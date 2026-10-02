package com.show.jwt.service;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// 處理JWT的工具
public class JwtService {

    // 產生JWT的金鑰
    private SecretKey key;
    // JWT的存活時間
    private int limitSeconds;
    // 解析Token的工具
    private JwtParser jwtParser;

    public JwtService(String key, int limitSeconds){
        this.key=Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
        this.limitSeconds=limitSeconds;
        this.jwtParser=Jwts.parser().verifyWith(this.key).build();
    }

    // 產生JWT
    public String createToken(UserDetails user){
        // Token期限
        long expiredTime=Instant.now().plusSeconds(limitSeconds).getEpochSecond()*1000;

        // JWT的Payload
        Claims body=Jwts.claims().issuedAt(new Date())
         .expiration(new Date(expiredTime))
         .add("username", user.getUsername())
         .add("psd", user.getPassword()).build();

        // JWT的結果
        String token=Jwts.builder().claims(body).signWith(key).compact();

        return token;
    }

    // 解析JWT的Payload
    public Claims parseToken(String token) throws JwtException {
        return jwtParser.parseSignedClaims(token).getPayload();
    }
}
