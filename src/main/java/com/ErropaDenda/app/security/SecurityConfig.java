package com.ErropaDenda.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	private final CustomUserDetailsService userDetailsService;

	public SecurityConfig(CustomUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/saskia/ikusi", "/produktua/produktuak")
				.hasRole("USER")
				.requestMatchers("/erabiltzaileak/**", "/produktua/admin/**", "/saskia/admin/**", "/perfil/admin/**")
				.hasRole("ADMIN").requestMatchers("/css/**", "/home/**", "/perfil/**", "/uploads/**").permitAll()
				.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/home").defaultSuccessUrl("/home/futbolWear", true)
						.failureUrl("/home?error").permitAll())

				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/home").permitAll())
				.userDetailsService(userDetailsService);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
