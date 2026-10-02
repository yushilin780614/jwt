package com.show.jwt.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.show.jwt.service.JwtService;


// 證明登入成功的RESTful API
@RestController 
public class TestRestController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired 
    private UserDetailsService userService;
    @Autowired
    private JwtService jwtService;


    // 證明以JWT，通過驗證
    @GetMapping("/test")
    public String getMethodName() {
        return "登入成功";
    }
    
    @PostMapping("/login")
    public String login(@RequestBody LoginVo vo) {
        UserDetails user=userService.loadUserByUsername(vo.username);
        if(user==null || !passwordEncoder.matches(vo.psd, user.getPassword())){
            throw new BadCredentialsException("認證失敗");
        }
        
        // 回傳JWT
        return jwtService.createToken(user);
    }
    
    // 使用者登入的資料格式
    public static class LoginVo{
        // 帳號
        public String username;
        // 密碼
        public String psd;
    }
}
