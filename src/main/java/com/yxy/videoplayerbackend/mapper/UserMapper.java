package com.yxy.videoplayerbackend.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxy.videoplayerbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;



@Mapper
public interface UserMapper extends BaseMapper<User> {
    default User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return selectOne(queryWrapper);
    }

    default User findByUserId(String userId){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return selectOne(queryWrapper);
    }
}