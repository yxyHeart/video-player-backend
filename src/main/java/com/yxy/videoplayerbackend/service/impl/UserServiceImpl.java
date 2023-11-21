package com.yxy.videoplayerbackend.service.impl;

import com.yxy.videoplayerbackend.common.CommonResult;
import com.yxy.videoplayerbackend.dto.LoginDto;
import com.yxy.videoplayerbackend.dto.RegisterDto;
import com.yxy.videoplayerbackend.mapper.UserMapper;
import com.yxy.videoplayerbackend.service.UserService;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import com.yxy.videoplayerbackend.entity.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private  UserMapper userMapper;


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;



    @Override
    public CommonResult<String> login(LoginDto loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        User user = userMapper.findByUsername(username);
        System.out.println(user);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // 生成JWT
        String token = this.generateToken(user);
        return CommonResult.success(token);
    }

    @Override
    public void register(RegisterDto registerDto) {
        String username = registerDto.getUsername();
        String password = registerDto.getPassword();
        User existingUser = userMapper.findByUsername(username);
        if (existingUser != null) {
            throw new RuntimeException("Username already exists");
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);

        userMapper.insert(user);
    }

    public void validateToken(String token) {
        try {
            Claims jwtClaims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            // 检查令牌是否过期
            Date tokenExpiration = jwtClaims.getExpiration();
            if (tokenExpiration.before(new Date())) {
                throw new RuntimeException("Token has expired");
            }

            // 其他验证逻辑...
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token has expired");
        } catch (MalformedJwtException | SignatureException e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpiration * 1000);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();

        return token;
    }
}