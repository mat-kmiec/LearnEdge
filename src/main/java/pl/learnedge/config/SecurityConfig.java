package pl.learnedge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ CSRF włączony, ale pomijamy H2-console
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )

            // ✅ Uprawnienia
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/logowanie", "/rejestracja", "/error",
                        "/przypomnij-haslo", "/reset-hasla", "/reset-hasla/**",
                        "/oauth2/**", "/h2-console/**",
                        "/css/**", "/js/**", "/img/**", "/webjars/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/rejestracja", "/przypomnij-haslo", "/reset-hasla").permitAll()
                .anyRequest().authenticated()
            )

            // ✅ Dla H2
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // ✅ Logowanie formularzem
            .formLogin(form -> form
                .loginPage("/logowanie")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/panel", true)
                .failureUrl("/logowanie?error=true")
                .permitAll()
            )

            // ✅ Logowanie przez OAuth2 (Google/GitHub)
            .oauth2Login(oauth -> oauth
                .loginPage("/logowanie")
                .defaultSuccessUrl("/panel", true)
            )

            // ✅ Sesje użytkowników
            .sessionManagement(sm -> sm
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                .maximumSessions(3)
                .maxSessionsPreventsLogin(false)
            )

            // ✅ Wylogowanie
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logowanie?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            );

        return http.build();
    }
}
