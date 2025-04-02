package com.mygo.mapper;

import com.mygo.domain.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
public interface UserMapper {
    @Insert("insert into user(email,password) values (#{email},#{password})")
    void addUser(@Param("email") String email, @Param("password") String password);

    @Select("select * from user where email=#{email}")
    User selectUserByEmail(@Param("email") String email);
}
