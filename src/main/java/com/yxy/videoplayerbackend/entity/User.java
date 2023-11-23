package com.yxy.videoplayerbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.util.List;

@TableName("users")
@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    @TableField(value = "avatar", fill = FieldFill.DEFAULT)
    private String avatar;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
    private String userId;
    @TableField(exist = false)
    private List<String> works;

    // Constructors, getters, and setters
}