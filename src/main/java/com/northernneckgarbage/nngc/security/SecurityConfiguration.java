package com.northernneckgarbage.nngc.security;

import com.northernneckgarbage.nngc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {

    @Value("${spring.profiles.active}")
    private String env;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
 private final CustomerRepository customerRepository;



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false); // Set to false as credentials are not required
        if (isProduction()) {
            configuration.addAllowedOriginPattern("https://*.northernneckgarbage.com");
            configuration.addAllowedOriginPattern("http://localhost:[*]");
            configuration.addAllowedOriginPattern("http://*:5173");
            configuration.addAllowedOriginPattern("https://*:5173");
        } else {
            configuration.addAllowedOriginPattern("http://localhost:[*]");
            configuration.addAllowedOriginPattern("http://*:5173");
            configuration.addAllowedOriginPattern("https://*:5173");
            configuration.addAllowedOriginPattern("https://*.northernneckgarbage.com");
        }
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider);

        return http.build();
    }


        private boolean isProduction() {
            // Implement your logic to determine if the application is running in production
            // For example, you can check an environment variable
            return "prod".equals(env);
        }
//
//        private Customer verifyOrCreateUser(String email, Map<String, Object> attributes) {
//            // Implement logic to verify or create a user in your database
//            // Return the user object
//            var user = customerRepository.findByEmail(email).orElse(null);
//            String password = passwordEncoder.encode("password1");
//            if (user == null) {
//                var customer = new Customer();
//                customer.setEmail(email);
//                customer.setFirstName((String) attributes.get("given_name"));
//                customer.setLastName((String) attributes.get("family_name"));
//                customer.setPassword(password);
//                customer.setEnabled(true);
//                customer.setAppUserRoles(AppUserRoles.valueOf("USER"));
//                customer.setChangePassword(false);
//                customerRepository.save(customer);
//                return customer;
//            }
//            return user;
//
//        }
//
//
//        private Authentication createAuthenticationForUser(Customer user) {
//            // Check if the user's role is not null
//            if (user.getAppUserRoles() != null) {
//                // Create a GrantedAuthority from the user's role
//                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getAppUserRoles().name());
//
//                // Return an Authentication object with the user and their authority
//                return new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(authority));
//            }
//
//
//            // Handle the case where the user doesn't have a role
//            // You might throw an exception or handle it based on your application's requirements
//            throw new IllegalArgumentException("User does not have a role assigned");
//        }

    }

//
//                .oauth2Login()
//                .successHandler(new SimpleUrlAuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                                        Authentication authentication) throws IOException {
//                        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
//                            OAuth2User user = oauthToken.getPrincipal();
//                            String email = user.getAttribute("email");
//                            String name = user.getAttribute("name");
//                          log.info(user.toString());
//                            // Log the user's email
//                            System.out.println("Authenticated user's email: " + email);
//                        Customer customer = verifyOrCreateUser(email, user.getAttributes());
//                            // Create an Authentication object
//                            Authentication auth = createAuthenticationForUser(customer);
//
//                            // Set the authentication object on the SecurityContext
//                            SecurityContextHolder.getContext().setAuthentication(auth);
//                        }
//
//                        // Redirect based on the environment
//                        String redirectUrl = isProduction() ? "https://www.northernneckgarbage.com" : "http://localhost:5173";
//                        response.sendRedirect(redirectUrl);
//                    }
//                })
//                .and()
