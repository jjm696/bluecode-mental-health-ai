package com.psychology.assistant.service;

import com.psychology.assistant.common.exception.BusinessException;
import com.psychology.assistant.mapper.UserMapper;
import com.psychology.assistant.model.dto.UserLoginRequest;
import com.psychology.assistant.model.dto.UserRegisterRequest;
import com.psychology.assistant.model.entity.User;
import com.psychology.assistant.model.vo.LoginResponse;
import com.psychology.assistant.model.vo.ProfileVO;
import com.psychology.assistant.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserAuthService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse register(UserRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(400, "两次输入的密码不一致");
        }

        if (userMapper.countByUsername(request.getUsername()) > 0) {
            throw new BusinessException(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userMapper.insert(user);

        return buildLoginResponse(user);
    }

    public LoginResponse login(UserLoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());

        if (user == null) {
            throw new BusinessException(404, "账号不存在");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(401, "密码错误");
        }

        return buildLoginResponse(user);
    }

    private LoginResponse buildLoginResponse(User user) {
        LoginResponse response = new LoginResponse();
        response.setToken(jwtUtil.generateToken(user.getId(), user.getUsername(), "USER"));

        ProfileVO profile = new ProfileVO();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setNickname(user.getNickname());
        profile.setAvatar(user.getAvatar());
        response.setProfile(profile);
        return response;
    }
}
