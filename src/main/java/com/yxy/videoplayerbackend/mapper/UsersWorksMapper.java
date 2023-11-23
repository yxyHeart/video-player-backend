package com.yxy.videoplayerbackend.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxy.videoplayerbackend.entity.User;
import com.yxy.videoplayerbackend.entity.UsersWorks;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface UsersWorksMapper extends BaseMapper<UsersWorks> {
    default List<String> findWorksByUserId(String userId){
        QueryWrapper<UsersWorks> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<UsersWorks> usersWorksList = this.selectList(queryWrapper);

        List<String> works = new ArrayList<>();
        for (UsersWorks usersWorks : usersWorksList) {
            works.add(usersWorks.getWork());

        }

        return works;
    }
}
