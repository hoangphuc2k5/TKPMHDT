package com.example.nuocuong.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/",
					"/auth/**",
					"/css/**",
					"/js/**",
					"/images/**",
					"/error/**"
				).permitAll()
				.requestMatchers("/admin/**").hasRole("QUAN_TRI_VIEN")
				.requestMatchers("/staff/**").hasAnyRole("NHAN_VIEN_BAN_HANG", "QUAN_LY_KHO", "NHAN_VIEN_GIAO_HANG")
				.requestMatchers("/customer/**").hasRole("KHACH_HANG")
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/auth/login")
				.loginProcessingUrl("/auth/login")
				.defaultSuccessUrl("/", true)
				.failureUrl("/auth/login?error")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/auth/logout")
				.logoutSuccessUrl("/auth/login?logout")
			)
			.csrf(Customizer.withDefaults());

		return http.build();
	}
}

