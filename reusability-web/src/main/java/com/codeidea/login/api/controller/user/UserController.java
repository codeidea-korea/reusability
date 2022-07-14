package com.codeidea.login.api.controller.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeidea.login.api.entity.user.User;
import com.codeidea.login.api.service.UserService;
import com.codeidea.login.common.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public ApiResponse getUser() {

		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		return ApiResponse.success("user", user);
	}
}
