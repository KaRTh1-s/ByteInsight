package com.example.nutrition.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.nutrition.entity.User;
import com.example.nutrition.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired UserService userService;
	
	public AuthController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user) {
		User saved = userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
		return ResponseEntity.ok("User registered successfull  -->  "+saved.getUsername());
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User user) {
		boolean valid = userService.authenticate(user.getUsername(), user.getPassword());
		
		if(valid) return ResponseEntity.ok("Login successfull");
		return ResponseEntity.status(401).body("Invalid credentials");
	}
	
}
