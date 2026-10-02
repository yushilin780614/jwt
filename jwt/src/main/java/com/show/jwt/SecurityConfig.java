package com.show.jwt;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.show.jwt.filter.JwtFilter;
import com.show.jwt.service.JwtService;

// 為了測試JWT，只做最簡單的設定
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 任意的三個使用者，以便能夠測試
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails u1=User.withUsername("user1").password("111").build();
        UserDetails u2=User.withUsername("user2").password("222").build();
        UserDetails u3=User.withUsername("user3").password("333").build();
        return new InMemoryUserDetailsManager(List.of(u1, u2, u3));
    }

    // 為了方便測試，所以不對密碼進行加密
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    // 為了測試，所以所有Request都被要求驗證身分
    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtFilter filter) throws Exception{
        // "/login"是公開API無需驗證身分
        return httpSecurity.authorizeHttpRequests(req-> req.requestMatchers(HttpMethod.POST, "/login").permitAll()
         .anyRequest().authenticated()).csrf(csrf-> csrf.disable())
         //  把客製用來驗證身分的Filter放進來
         .addFilterBefore(filter, BasicAuthenticationFilter.class).build();
    }

    // 處理JWT的工具
    @Bean 
    public JwtService jwtService(@Value("${jwt-secretKey}") String key, @Value("${jwt-limitSeconds}") int seconds){
        return new JwtService(key, seconds);
    }
}
