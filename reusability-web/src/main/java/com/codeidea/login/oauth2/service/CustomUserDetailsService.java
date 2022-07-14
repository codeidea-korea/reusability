package com.codeidea.login.oauth2.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.codeidea.login.api.entity.user.User;
import com.codeidea.login.api.repository.user.UserRepository;
import com.codeidea.login.oauth2.entity.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUserId(username);
		if (user == null) {
			throw new UsernameNotFoundException("Can not find username.");
		}
		return UserPrincipal.create(user);
	}
}
