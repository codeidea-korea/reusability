package com.codeidea.login.api.controller.redirect;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/redirect")
public class RedirectController {

	@GetMapping
	public void redirect(@RequestParam("token") String token) {

		System.out.println("token: " + token);
	}
}
