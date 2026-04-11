package TKPMHDT.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/uploads/**", "/error", "/login", "/register", "/quen-mat-khau").permitAll()
                        .requestMatchers("/", "/ui/san-pham/**", "/ui/khuyen-mai").permitAll()
                        .requestMatchers("/ui/admin/**").hasAnyRole("QUAN_TRI_VIEN", "QUAN_TRI_VIEN_CAP_CAO")
                        .requestMatchers("/ui/pos/**").hasAnyRole("NHAN_VIEN_BAN_HANG", "QUAN_TRI_VIEN", "QUAN_TRI_VIEN_CAP_CAO", "QUAN_LY_KHO")
                        .requestMatchers("/api/nguoi-dung/dang-ky-khach-hang").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/san-pham/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/san-pham/*/tuy-chinh").hasAuthority("product:customize")
                        .requestMatchers("/api/khuyen-mai/ma/**", "/api/khuyen-mai/tinh-tien-giam").permitAll()
                        .requestMatchers("/api/nguoi-dung/me").authenticated()
                        .requestMatchers("/api/gio-hang/**").hasAuthority("cart:manage")
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self' https: data: 'unsafe-inline'"))
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String redirect = request.getParameter("redirect");
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_QUAN_TRI_VIEN") || authority.getAuthority().equals("ROLE_QUAN_TRI_VIEN_CAP_CAO"));

            if (redirect != null && !redirect.isBlank() && redirect.startsWith("/") && !redirect.startsWith("//") && !isAdmin) {
                response.sendRedirect(redirect);
                return;
            }

            boolean isPosUser = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_NHAN_VIEN_BAN_HANG") || authority.getAuthority().equals("ROLE_QUAN_LY_KHO"));

            if (isAdmin) {
                response.sendRedirect("/ui/admin/dashboard");
                return;
            }

            if (isPosUser) {
                response.sendRedirect("/ui/pos");
                return;
            }

            response.sendRedirect("/");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new CompatiblePasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "https://drinkhub.vn"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
