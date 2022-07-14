package com.codeidea.login.api.service;

import org.springframework.stereotype.Service;

import com.codeidea.login.api.entity.user.User;
import com.codeidea.login.api.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public User getUser(String userId) {
		return userRepository.findByUserId(userId);
	}
}
