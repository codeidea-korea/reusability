package com.codeidea.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.codeidea.bind.DataArgumentResolver;
import com.codeidea.interceptor.ServletInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

	private final DataArgumentResolver dataResolver;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(new ServletInterceptor())
				.excludePathPatterns("/css/**", "/fonts/**", "/plugin/**", "/scripts/**", "/favicon.ico");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

		resolvers.add(dataResolver);
	}
}
