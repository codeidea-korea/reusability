package com.codeidea.bind;

import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.codeidea.bind.annotation.SupportData;
import com.codeidea.vo.Data;

@Component
public class DataArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		return parameter.getParameterAnnotation(SupportData.class) != null && Data.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		Data data = new Data();

		HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();
		Enumeration<String> keys = req.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String[] values = req.getParameterValues(key);

			if (values == null) {
				continue;
			}

			if (values.length > 1) {
				data.put(key, Arrays.asList(values));
			} else {
				String value = values[0];

				if (StringUtils.isNumeric(value)) {
					data.put(key, Integer.parseInt(value));
				} else {
					data.put(key, value);
				}
			}
		}

		String contentType = req.getContentType();
		if (StringUtils.isNotBlank(contentType) && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
			try {
				String body = IOUtils.toString(req.getInputStream());
				data.putJsonString(body);
			} catch (Exception e) {}
		}

		return data;
	}
}
