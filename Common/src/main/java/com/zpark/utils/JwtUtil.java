package com.zpark.utils;

import cn.hutool.core.lang.UUID;
import com.zpark.entity.CustomUserDetails;
import com.zpark.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static long time = 1000 * 60 * 60 * 24;
    private static final long ACCESS_TOKEN_EXPIRE = 1000 * 60 * 30; // 30分钟
    private static final long REFRESH_TOKEN_EXPIRE = 1000 * 60 * 60 * 24 * 7; // 7天

    @Value("${jwt.secret-key}")
    private String secretString;

    private SecretKey getSecretKey() {
        return new SecretKeySpec(secretString.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public long getRemainingTime(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.getTime() - System.currentTimeMillis();
    }

    public  String createWebSocketToken(String userName,User user) {
        Map<String,Object> claims=new HashMap<>();
        claims.put("username",userName);
        claims.put("role",user.getRole());

        String jwtToken = Jwts.builder()
                //header
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setClaims(claims)
                //payload
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE))
                .setId(UUID.randomUUID().toString())
                //signature
                .signWith(getSecretKey(),SignatureAlgorithm.HS256)
                .compact();
        return jwtToken;
    }




    // 生成Access Token
    public  String createAccessToken(User user) {
        Map<String,Object> claims=new HashMap<>();
        claims.put("username",user.getUsername());
        claims.put("userId",user.getId());
        claims.put("role",user.getRole());

        if (!user.getAuthorities().isEmpty()) {
            claims.put("authorities", user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        }

        String jwtToken = Jwts.builder()
                //header
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setClaims(claims)
                //payload
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE))
                .setId(UUID.randomUUID().toString())
                //signature
                .signWith(getSecretKey(),SignatureAlgorithm.HS256)
                .compact();
        return jwtToken;
    }

    // 生成Refresh Token（只包含必要信息）
    public  String createRefreshToken(User user) {
        Map<String,Object> claims=new HashMap<>();
        claims.put("username",user.getUsername());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE))
                .signWith(getSecretKey())
                .compact();
    }


    public String createAccessToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // 基础信息
        claims.put("username", userDetails.getUsername());
        claims.put("userId",userDetails.getId());

        // 添加权限信息
        if (!userDetails.getAuthorities().isEmpty()) {
            claims.put("authorities", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        }


        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + time))
                .setId(UUID.randomUUID().toString())
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public  User getUserFromToken(String token){
        Map<String, Object> claims = new HashMap<>();
        claims=getClaimsFromToken(token);
        System.out.println("这里是jwtUtil");
        User user =new User();
        
        // 添加空值检查
        String username = claims.get("username") != null ? claims.get("username").toString() : null;
        Object userIdObj = claims.get("userId");
        Object roleObj = claims.get("role");
        Object authoritiesObj = claims.get("authorities");
        
        if (username == null) {
            throw new JwtException("Invalid token claims: missing required fields");
        }
        
        user.setUsername(username);

        if(userIdObj != null) {
            user.setId(Long.parseLong(userIdObj.toString()));
        }
        if (roleObj != null) {
            user.setRole(roleObj.toString());
        }
        
        if (authoritiesObj != null && authoritiesObj instanceof Collection<?>) {
            user.setAuthorities((Collection<? extends GrantedAuthority>) authoritiesObj);
        }

        return user;
    }
    public  String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        final String username = claims.getSubject();
        final Date expiration = claims.getExpiration();

        if (!username.equals(userDetails.getUsername())) {
            throw new JwtException("Token username mismatch: expected " + userDetails.getUsername() + " but found " + username);
        }
        if (expiration.before(new Date())) {
            throw new JwtException("Token expired at " + expiration);
        }
        return true;
    }
    public  boolean validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token);
        return true;
    }


    public  Claims getClaimsFromToken(String token){
        System.out.println("token"+token);
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
    // 新增方法：从Token获取Authentication对象
    public Authentication getAuthentication(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            // 1. 提取权限
            List<String> authoritiesList = claims.get("authorities", List.class);
            Collection<? extends GrantedAuthority> authorities = 
                authoritiesList == null 
                    ? Collections.emptyList() 
                    : authoritiesList.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());


            // 2. 构建UserDetails对象
            CustomUserDetails userDetails = new CustomUserDetails();
            userDetails.setUsername(claims.getSubject());
            userDetails.setAuthorities(authorities);

            // 3. 返回Authentication对象
            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    token,
                    authorities);
        } catch (JwtException e) {
            throw new JwtException("Failed to get authentication from token: " + e.getMessage(), e);
        }
    }

}
