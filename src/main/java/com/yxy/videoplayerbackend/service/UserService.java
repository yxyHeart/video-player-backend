package com.yxy.videoplayerbackend.service;


import com.yxy.videoplayerbackend.common.CommonResult;
import com.yxy.videoplayerbackend.dto.LoginDto;
import com.yxy.videoplayerbackend.dto.LoginResponseDto;
import com.yxy.videoplayerbackend.dto.RegisterDto;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface UserService {
    CommonResult<LoginResponseDto> login(LoginDto logindto);
    void register(RegisterDto registerDto);

    CommonResult<Map<String, String>> uploadBig(Long chunkSize,Integer totalNumber,Long chunkNumber,String md5,MultipartFile file) throws IOException;

    CommonResult<Map<String, String>> checkFile(String userId, String md5) throws Exception;
}