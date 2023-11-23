package com.yxy.videoplayerbackend.controller;

import com.yxy.videoplayerbackend.common.CommonResult;
import com.yxy.videoplayerbackend.dto.LoginDto;
import com.yxy.videoplayerbackend.dto.LoginResponseDto;
import com.yxy.videoplayerbackend.dto.RegisterDto;
import com.yxy.videoplayerbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public CommonResult<LoginResponseDto> login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterDto registerDto) {
        userService.register(registerDto);
    }



    /**
     * @param chunkSize   每个分片大小
     * @param chunkNumber 当前分片
     * @param md5         文件总MD5
     * @param file        当前分片文件数据
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadBig")
    public CommonResult<Map<String, String>> uploadBig(@RequestParam Long chunkSize, @RequestParam Integer totalNumber, @RequestParam Long chunkNumber, @RequestParam String md5, @RequestParam MultipartFile file) throws IOException {
        return this.userService.uploadBig(chunkSize,totalNumber,chunkNumber,md5,file);
    }


    /**
     * 获取文件分片状态，检测文件MD5合法性
     *
     * @param md5
     * @return
     * @throws Exception
     */
    @GetMapping("/checkFile")
    public CommonResult<Map<String, String>> checkFile(@RequestParam String userId, @RequestParam String md5) throws Exception {
        return this.userService.checkFile(userId, md5);
    }

}