package com.psychology.assistant.mapper;

import com.psychology.assistant.model.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, nickname, avatar, password_hash AS passwordHash, phone FROM users WHERE username = #{username} LIMIT 1")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    long countByUsername(@Param("username") String username);

    @Insert("INSERT INTO users(username, password_hash, nickname, phone, avatar, status) VALUES(#{username}, #{passwordHash}, #{nickname}, #{phone}, #{avatar}, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(User user);
}
