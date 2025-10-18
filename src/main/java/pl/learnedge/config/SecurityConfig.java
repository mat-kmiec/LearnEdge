//package pl.learnedge.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/",
//                                "/logowanie",       // widok logowania (GET)
//                                "/rejestracja",
//                                "/error",
//                                "/oauth2/**",
//                                "/css/**", "/js/**", "/img/**", "/webjars/**"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/logowanie")          // GET — Twój widok HTML
//                        .loginProcessingUrl("/login")     // POST — Spring obsługuje logowanie
//                        .defaultSuccessUrl("/dashboard", true)
//                        .failureUrl("/logowanie?error=true")
//                        .permitAll()
//                )
//                .oauth2Login(oauth -> oauth
//                        .loginPage("/logowanie")          // ten sam widok dla OAuth
//                        .defaultSuccessUrl("/", true)
//                )
////                .rememberMe(rm -> rm
////                        .rememberMeParameter("remember-me")
////                        .tokenValiditySeconds(60 * 60 * 24 * 14)
////                )
//                .sessionManagement(sm -> sm
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                        .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
//                        .maximumSessions(3)
//                        .maxSessionsPreventsLogin(false)
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/logowanie?logout")
//                        .invalidateHttpSession(true)
//                        .clearAuthentication(true)
//                        .deleteCookies("JSESSIONID", "remember-me")
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//}

package pl.learnedge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**") //
                        .disable()
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/logowanie", "/rejestracja", "/error", "/przypomnij-haslo",
                                "/oauth2/**", "/h2-console/**", //
                                "/css/**", "/js/**", "/img/**", "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()) // 🔧 H2 działa w iframe
                )
                .formLogin(form -> form
                        .loginPage("/logowanie")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/panel", true)
                        .failureUrl("/logowanie?error=true")
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/logowanie")
                        .defaultSuccessUrl("/", true)
                )
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                        .maximumSessions(3)
                        .maxSessionsPreventsLogin(false)
                )
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
