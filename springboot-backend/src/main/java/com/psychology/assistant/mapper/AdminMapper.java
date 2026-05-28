package com.psychology.assistant.mapper;

import com.psychology.assistant.model.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    @Select("SELECT id, username, nickname, avatar, password_hash AS passwordHash FROM admins WHERE username = #{username} LIMIT 1")
    Admin selectByUsername(@Param("username") String username);

    @Select("SELECT id, username, nickname, avatar FROM admins WHERE id = #{id} LIMIT 1")
    Admin selectById(@Param("id") Long id);
}
