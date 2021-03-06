package com.codeidea.login.api.controller.auth;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeidea.login.api.entity.auth.AuthReqModel;
import com.codeidea.login.api.entity.user.UserRefreshToken;
import com.codeidea.login.api.repository.user.UserRefreshTokenRepository;
import com.codeidea.login.common.ApiResponse;
import com.codeidea.login.config.properties.AppProperties;
import com.codeidea.login.oauth2.entity.RoleType;
import com.codeidea.login.oauth2.entity.UserPrincipal;
import com.codeidea.login.oauth2.token.AuthToken;
import com.codeidea.login.oauth2.token.AuthTokenProvider;
import com.codeidea.login.utils.CookieUtil;
import com.codeidea.login.utils.HeaderUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AppProperties appProperties;

	private final AuthTokenProvider tokenProvider;

	private final AuthenticationManager authenticationManager;

	private final UserRefreshTokenRepository userRefreshTokenRepository;

	private final static long THREE_DAYS_MSEC = 259200000;

	private final static String REFRESH_TOKEN = "refresh_token";

	@PostMapping("/login")
	public ApiResponse login(HttpServletRequest request, HttpServletResponse response,
			@RequestBody AuthReqModel authReqModel) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authReqModel.getId(), authReqModel.getPassword()));

		String userId = authReqModel.getId();
		SecurityContextHolder.getContext().setAuthentication(authentication);

		Date now = new Date();
		AuthToken accessToken = tokenProvider.createAuthToken(userId,
				((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
				new Date(now.getTime() + appProperties.getAuth().getTokenExpiry()));

		long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
		AuthToken refreshToken = tokenProvider.createAuthToken(appProperties.getAuth().getTokenSecret(),
				new Date(now.getTime() + refreshTokenExpiry));

		// userId refresh token ?????? DB ??????
		UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserId(userId);
		if (userRefreshToken == null) {
			// ?????? ?????? ?????? ??????
			userRefreshToken = new UserRefreshToken(userId, refreshToken.getToken());
			userRefreshTokenRepository.saveAndFlush(userRefreshToken);
		} else {
			// DB??? refresh ?????? ????????????
			userRefreshToken.setRefreshToken(refreshToken.getToken());
		}

		int cookieMaxAge = (int) refreshTokenExpiry / 60;
		CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
		CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

		return ApiResponse.success("token", accessToken.getToken());
	}

	@GetMapping("/refresh")
	public ApiResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
		// access token ??????
		String accessToken = HeaderUtil.getAccessToken(request);
		AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
		if (!authToken.validate()) {
			return ApiResponse.invalidAccessToken();
		}

		// expired access token ?????? ??????
		Claims claims = authToken.getExpiredTokenClaims();
		if (claims == null) {
			return ApiResponse.notExpiredTokenYet();
		}

		String userId = claims.getSubject();
		RoleType roleType = RoleType.of(claims.get("role", String.class));

		// refresh token
		String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN).map(Cookie::getValue).orElse((null));
		AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

		if (authRefreshToken.validate()) {
			return ApiResponse.invalidRefreshToken();
		}

		// userId refresh token ?????? DB ??????
		UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserIdAndRefreshToken(userId,
				refreshToken);
		if (userRefreshToken == null) {
			return ApiResponse.invalidRefreshToken();
		}

		Date now = new Date();
		AuthToken newAccessToken = tokenProvider.createAuthToken(userId, roleType.getCode(),
				new Date(now.getTime() + appProperties.getAuth().getTokenExpiry()));

		long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

		// refresh ?????? ????????? 3??? ????????? ?????? ??????, refresh ?????? ??????
		if (validTime <= THREE_DAYS_MSEC) {
			// refresh ?????? ??????
			long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

			authRefreshToken = tokenProvider.createAuthToken(appProperties.getAuth().getTokenSecret(),
					new Date(now.getTime() + refreshTokenExpiry));

			// DB??? refresh ?????? ????????????
			userRefreshToken.setRefreshToken(authRefreshToken.getToken());

			int cookieMaxAge = (int) refreshTokenExpiry / 60;
			CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
			CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
		}

		return ApiResponse.success("token", newAccessToken.getToken());
	}
}
