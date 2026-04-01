package TKPMHDT.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/error", "/login", "/register", "/quen-mat-khau").permitAll()
                        .requestMatchers("/", "/ui/san-pham", "/ui/khuyen-mai").permitAll()
                        .requestMatchers("/api/nguoi-dung/dang-ky-khach-hang").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/san-pham/**").permitAll()
                        .requestMatchers("/api/khuyen-mai/ma/**", "/api/khuyen-mai/tinh-tien-giam").permitAll()
                        .requestMatchers("/api/nguoi-dung/me").authenticated()
                        .requestMatchers("/api/gio-hang/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}

