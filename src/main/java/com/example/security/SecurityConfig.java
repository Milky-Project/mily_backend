package com.example.security;

import com.example.service.PrincipalOauthDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private PrincipalOauthDetailsService principalOauthDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin(login -> {
            try {
                login
                    .loginPage("/loginview")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .loginProcessingUrl("/login_ok")
                    .permitAll()
                        .and()
                        .oauth2Login()
                        .loginPage("/index")
                        .defaultSuccessUrl("http://localhost:3000")
                        .failureUrl("http://localhost:3000")
                        .userInfoEndpoint()
                        .userService(principalOauthDetailsService);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        http
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/member/**").authenticated()
                        .antMatchers("/admin/**", "/api/admin/**").access("hasRole('ROLE_ADMIN')")
                        .anyRequest().permitAll()
                );

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index")
                .deleteCookies("JSESSIONID");

        http.csrf().disable();

        return http.build();
    }
}
