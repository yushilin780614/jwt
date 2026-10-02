package com.show.jwt.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.show.jwt.service.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 驗證JWT，確定有成功登入
@Component 
public class JwtFilter extends OncePerRequestFilter{

    @Autowired
    private JwtService jwtService;

    // 根據JWT驗證身分
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain)
            throws ServletException, IOException {
        // 取得Request Header裡的"Authorization"值
        String authHeader=req.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader==null || authHeader.isBlank() || authHeader.length()<"Bearer ".length()){ filterChain.doFilter(req, resp);  return;}

        // 基本驗證JWT
        String token=authHeader.substring("Bearer ".length());
        try{
            Claims claims=jwtService.parseToken(token);

            // JWT是否過期
            Date expiredDate=claims.getExpiration();
            if(new Date().before(expiredDate)){
                // 放入Security Context裡
                UserDetails user=new User(claims.get("username").toString(), claims.get("psd").toString(), new ArrayList<>());
                Authentication userToken = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    new ArrayList<>()
                );
                SecurityContextHolder.getContext().setAuthentication(userToken);
            }else{ resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE); }
        }catch(Exception e){
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        filterChain.doFilter(req, resp);
    }

    // 排除URL中有"/login"的Request，因為這是登入用的API
    @Override
    public boolean shouldNotFilter(HttpServletRequest req){
        return req.getServletPath().contains("/login");
    }
}
