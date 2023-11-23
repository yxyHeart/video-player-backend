package com.yxy.videoplayerbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("users_works")
@Data
public class UsersWorks {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userId;
    private String work;

    // Constructors, getters, and setters
    public UsersWorks(String userId,String work){
        this.userId = userId;
        this.work = work;
    }
}