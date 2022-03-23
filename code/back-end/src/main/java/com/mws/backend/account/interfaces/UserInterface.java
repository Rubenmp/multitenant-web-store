package com.mws.backend.account.interfaces;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInterface {

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

}
