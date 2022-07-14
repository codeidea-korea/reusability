package com.codeidea.login.oauth2.info;

import java.util.Map;

import com.codeidea.login.oauth2.entity.ProviderType;
import com.codeidea.login.oauth2.info.impl.FacebookOAuth2UserInfo;
import com.codeidea.login.oauth2.info.impl.GoogleOAuth2UserInfo;
import com.codeidea.login.oauth2.info.impl.KakaoOAuth2UserInfo;
import com.codeidea.login.oauth2.info.impl.NaverOAuth2UserInfo;

public class OAuth2UserInfoFactory {

	public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {

		switch (providerType) {
			case GOOGLE:
				return new GoogleOAuth2UserInfo(attributes);
			case FACEBOOK:
				return new FacebookOAuth2UserInfo(attributes);
			case NAVER:
				return new NaverOAuth2UserInfo(attributes);
			case KAKAO:
				return new KakaoOAuth2UserInfo(attributes);
			default:
				throw new IllegalArgumentException("Invalid Provider Type.");
		}
	}
}
