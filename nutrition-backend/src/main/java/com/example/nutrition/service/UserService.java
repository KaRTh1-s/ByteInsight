package com.example.nutrition.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.nutrition.entity.User;
import com.example.nutrition.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepo;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public User registerUser(String username,String email,String password) {
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		return userRepo.save(user);
	}
	
	public boolean authenticate(String username,String password) {
		return userRepo.findByUsername(username).map(user -> passwordEncoder.matches(password, user.getPassword())).orElse(false);
	}
}
       