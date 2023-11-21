package com.yxy.videoplayerbackend.service;


import com.yxy.videoplayerbackend.common.CommonResult;
import com.yxy.videoplayerbackend.dto.LoginDto;
import com.yxy.videoplayerbackend.dto.RegisterDto;

public interface UserService {
    CommonResult<String> login(LoginDto logindto);
    void register(RegisterDto registerDto);
}