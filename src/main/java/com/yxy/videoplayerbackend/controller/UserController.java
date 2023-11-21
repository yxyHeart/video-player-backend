package com.yxy.videoplayerbackend.controller;

import com.yxy.videoplayerbackend.common.CommonResult;
import com.yxy.videoplayerbackend.dto.LoginDto;
import com.yxy.videoplayerbackend.dto.RegisterDto;
import com.yxy.videoplayerbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public CommonResult<String> login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterDto registerDto) {
        userService.register(registerDto);
    }
}