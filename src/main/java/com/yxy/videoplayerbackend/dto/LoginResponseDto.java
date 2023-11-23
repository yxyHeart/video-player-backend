package com.yxy.videoplayerbackend.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private String username;
    private String avatar;
    private String token;
    public LoginResponseDto(String username,String avatar,String token){
        this.username = username;
        this.avatar = avatar;
        this.token = token;
    }
}
