package com.codeidea.login.config.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.codeidea.login.api.repository.user.UserRefreshTokenRepository;
import com.codeidea.login.config.properties.AppProperties;
import com.codeidea.login.config.properties.CorsProperties;
import com.codeidea.login.oauth2.entity.RoleType;
import com.codeidea.login.oauth2.exception.RestAuthenticationEntryPoint;
import com.codeidea.login.oauth2.filter.TokenAuthenticationFilter;
import com.codeidea.login.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.codeidea.login.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.codeidea.login.oauth2.handler.TokenAccessDeniedHandler;
import com.codeidea.login.oauth2.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.codeidea.login.oauth2.service.CustomOAuth2UserService;
import com.codeidea.login.oauth2.service.CustomUserDetailsService;
import com.codeidea.login.oauth2.token.AuthTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CorsProperties corsProperties;
	private final AppProperties appProperties;
	private final AuthTokenProvider tokenProvider;
	private final CustomUserDetailsService userDetailsService;
	private final CustomOAuth2UserService oAuth2UserService;
	private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
	private final UserRefreshTokenRepository userRefreshTokenRepository;

	/*
	 * UserDetailsService ??????
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors()
			.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.csrf().disable()
				.formLogin().disable()
				.httpBasic().disable()
				.exceptionHandling()
				.authenticationEntryPoint(new RestAuthenticationEntryPoint())
				.accessDeniedHandler(tokenAccessDeniedHandler)
			.and()
				.authorizeRequests()
				.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
//				.antMatchers("/api/**").hasAnyAuthority(RoleType.USER.getCode())
//				.antMatchers("/api/**/admin/**").hasAnyAuthority(RoleType.ADMIN.getCode())
				.antMatchers("/**").permitAll()
				.anyRequest().authenticated()
			.and()
				.oauth2Login()
				.authorizationEndpoint()
				.baseUri("/oauth2/authorization")
				.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
			.and()
				.redirectionEndpoint()
				.baseUri("/*/oauth2/code/*")
			.and()
				.userInfoEndpoint()
				.userService(oAuth2UserService)
			.and()
				.successHandler(oAuth2AuthenticationSuccessHandler())
				.failureHandler(oAuth2AuthenticationFailureHandler());

		http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	/*
	 * auth ????????? ??????
	 */
	@Override
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	/*
	 * security ?????? ???, ????????? ????????? ??????
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * ?????? ?????? ??????
	 */
	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter(tokenProvider);
	}

	/*
	 * ?????? ?????? ?????? Repository ?????? ????????? ?????? ?????? ????????? ??? ??????.
	 */
	@Bean
	public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
		return new OAuth2AuthorizationRequestBasedOnCookieRepository();
	}

	/*
	 * Oauth ?????? ?????? ?????????
	 */
	@Bean
	public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
		return new OAuth2AuthenticationSuccessHandler(tokenProvider, appProperties, userRefreshTokenRepository,
				oAuth2AuthorizationRequestBasedOnCookieRepository());
	}

	/*
	 * Oauth ?????? ?????? ?????????
	 */
	@Bean
	public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
		return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
	}

	/*
	 * Cors ??????
	 */
	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
		corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
		corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
		corsConfig.setAllowCredentials(true);
		corsConfig.setMaxAge(corsConfig.getMaxAge());

		corsConfigSource.registerCorsConfiguration("/**", corsConfig);
		return corsConfigSource;
	}
}
