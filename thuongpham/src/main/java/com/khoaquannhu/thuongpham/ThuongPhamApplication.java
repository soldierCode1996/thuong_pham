package com.khoaquannhu.thuongpham;

import com.khoaquannhu.thuongpham.entity.User;
import com.khoaquannhu.thuongpham.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ThuongPhamApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThuongPhamApplication.class, args);
	}

//	@Bean
//	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//		return args -> {
//			if (userRepository.findByUsername("admin").isEmpty()) {
//				User user = new User();
//				user.setUsername("admin");
//				user.setPassword(passwordEncoder.encode("123456"));
//				user.setRole("ADMIN");
//				userRepository.save(user);
//				System.out.println("Created default user: admin / 123456");
//			}
//		};
//	}
}
