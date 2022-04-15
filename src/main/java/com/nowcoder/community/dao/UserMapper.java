package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(int id);    //根据用户id查找
    User selectByName(String username); //根据用户名字查找
    User selectByEmail(String email);   //根据用户邮箱查找
    int insertUser(User user);  //增加用户
    int updateStatus(int id, int status);   //根据id更新状态
    int updateHeader(int id, String headerUrl); //根据id更新头像路径
    int updatePassword(int id, String password);    //根据id修改密码
}
