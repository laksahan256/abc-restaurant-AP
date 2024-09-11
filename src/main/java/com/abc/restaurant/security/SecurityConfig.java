package com.abc.restaurant.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.abc.restaurant.models.AdminMyAppUserService;
import com.abc.restaurant.models.MyAppUserService;
import com.abc.restaurant.models.StaffMyAppUserService;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    @Autowired
    private final MyAppUserService appUserService;  // Regular user service

    @Autowired
    private final AdminMyAppUserService adminUserService;  // Admin service

    @Autowired
    private final StaffMyAppUserService staffMyAppUserService; // Staff service

    // UserDetailsService for regular users
    @Bean
    public UserDetailsService userDetailsService() {
        return appUserService;
    }

    // UserDetailsService for admins
    @Bean
    public UserDetailsService adminUserDetailsService() {
        return adminUserService;
    }

    // UserDetailsService for staff
    @Bean
    public UserDetailsService staffUserDetailsService() {
        return staffMyAppUserService;
    }

    // AuthenticationProvider for regular users
    @Bean
    public AuthenticationProvider userAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationProvider for admins
    @Bean
    public AuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminUserDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationProvider for staff
    @Bean
    public AuthenticationProvider staffAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(staffUserDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security filter chain for admins
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .securityMatcher("/admin/**", "/adminlogin") // Apply to admin URLs
            .authenticationProvider(adminAuthenticationProvider()) // Use admin auth provider
            .formLogin(httpForm -> {
                httpForm.loginPage("/adminlogin").permitAll();
                httpForm.defaultSuccessUrl("/admin", true);
            })
            .authorizeHttpRequests(registry -> {
                registry.requestMatchers("/admin/**").authenticated();
                registry.requestMatchers("/req/adminsignup", "/css/**", "/js/**", "/images/**").permitAll();
            })
            .build();
    }
    
    // Security filter chain for users
    @Bean
    @Order(2)
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .securityMatcher("/login", "/req/signup", "/css/**", "/js/**", "/images/**") // Apply to user URLs
            .authenticationProvider(userAuthenticationProvider()) // Use user auth provider
            .formLogin(httpForm -> {
                httpForm.loginPage("/login").permitAll();
                httpForm.defaultSuccessUrl("/index", true);
            })
            .authorizeHttpRequests(registry -> {
                registry.requestMatchers("/req/signup", "/css/**", "/js/**", "/images/**").permitAll();
                registry.anyRequest().authenticated();
            })
            .build();
    }

    // Security filter chain for staff
    @Bean
    @Order(3)
    public SecurityFilterChain staffSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .securityMatcher("/stafflogin", "/req/staffsignup", "/css/**", "/js/**", "/images/**") // Apply to staff URLs
            .authenticationProvider(staffAuthenticationProvider()) // Use staff auth provider
            .formLogin(httpForm -> {
                httpForm.loginPage("/stafflogin").permitAll();
                httpForm.defaultSuccessUrl("/index", true); // Ensure this is correct
            })
            .authorizeHttpRequests(registry -> {
                registry.requestMatchers("/req/staffsignup", "/css/**", "/js/**", "/images/**").permitAll();
                registry.anyRequest().authenticated();
            })
            .build();
    }
}
