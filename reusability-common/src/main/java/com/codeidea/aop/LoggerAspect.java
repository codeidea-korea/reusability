package com.codeidea.aop;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggerAspect {

	@Around("within(com.codeidea.front..*) and "
			+ "@annotation(org.springframework.web.bind.annotation.GetMapping) or "
			+ "@annotation(org.springframework.web.bind.annotation.PostMapping)")
	public Object logAction(ProceedingJoinPoint joinPoint) throws Throwable {

		Object result = joinPoint.proceed();
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

		String controllerName = joinPoint.getSignature().getDeclaringType().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		Map<String, Object> params = new HashMap<>();

		try {
			params.put("controller", controllerName);
			params.put("method", methodName);
			params.put("params", getParams(request));
			params.put("request_uri", request.getRequestURI());
			params.put("http_method", request.getMethod());
			log.info("{}", params);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return result;
	}

	private JSONObject getParams(HttpServletRequest request) {

		JSONObject jsonObject = new JSONObject();

		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String param = params.nextElement();
			String replaceParam = param.replaceAll("\\.", "-");

			jsonObject.put(replaceParam, request.getParameter(param));
		}
		return jsonObject;
	}
}
