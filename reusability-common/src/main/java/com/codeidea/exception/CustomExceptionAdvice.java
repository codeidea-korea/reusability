package com.codeidea.exception;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionAdvice {

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public void handler(HttpServletRequest req, Exception e) {

		log.error(String.format("%s", logging(req)), e);
	}

	@ExceptionHandler(CustomException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handler(HttpServletRequest req, CustomException e) {

		log.error(String.format("%s", logging(req)), e);
	}

	private Map<String, Object> logging(HttpServletRequest req) {

		Map<String, Object> params = new HashMap<>();
		params.put("params", getParams(req));
		params.put("request_uri", req.getRequestURI());
		params.put("http_method", req.getMethod());

		return params;
	}

	private JSONObject getParams(HttpServletRequest req) {

		JSONObject jsonObject = new JSONObject();

		Enumeration<String> params = req.getParameterNames();
		while (params.hasMoreElements()) {
			String param = params.nextElement();
			String replaceParam = param.replaceAll("\\.", "-");

			jsonObject.put(replaceParam, req.getParameter(param));
		}
		return jsonObject;
	}
}
