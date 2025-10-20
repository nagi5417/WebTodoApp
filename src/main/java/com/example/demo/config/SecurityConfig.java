package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * AuthenticationManagerのBean定義
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * CORS設定
     */
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://127.0.0.1:3000");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * セキュリティ設定 - SecurityFilterChain スタイル
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("SecurityConfig filterChain method called");

        http
            .cors()
            .and()
            .authorizeRequests()
            .antMatchers("/h2-console/**", "/users/login", "/users/register",
                        "/css/**", "/js/**", "/*.png",
                        "/*.ico", "/images/**", "/api/auth/**", "/error",
                        "/oauth2/**", "/login/oauth2/**", "/api/auth/oauth2/success")
            .permitAll()
            .antMatchers("/api/tasks/**").authenticated() // タスクAPIは認証必須
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .loginPage("/users/login")
            .defaultSuccessUrl("/api/auth/oauth2/success", true)
            .redirectionEndpoint()
            .baseUri("/login/oauth2/code/*")
            .and()
            .userInfoEndpoint()
            .and()
            .and()
            .formLogin()
            .loginPage("/users/login")
            .defaultSuccessUrl("http://localhost:3000/tasks", true) // フロントエンドにリダイレクト
            .permitAll()
            .and()
            .logout()
            .logoutSuccessUrl("http://localhost:3000/users/login") // フロントエンドにリダイレクト
            .permitAll()
            .and()
            .csrf().disable()
            .headers().frameOptions().disable()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED);

        return http.build();
    }

}