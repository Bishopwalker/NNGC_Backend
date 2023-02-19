package com.northernneckgarbage.nngc.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity/*(debug = true)*/
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
    private final AuthUserService authUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf().disable()

                .authorizeHttpRequests((authz) -> {
                            try {
                                authz
                                         .requestMatchers("/auth/nngc/register").authenticated()
                                        .anyRequest().permitAll()
                                        .and()
                                        .formLogin();


                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )


                .httpBasic(withDefaults());
           // @formatter:on
        return http.build();


    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(authUserService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authProvider;
    }

}
