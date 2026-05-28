package com.psychology.assistant.service;

import com.psychology.assistant.common.exception.BusinessException;
import com.psychology.assistant.mapper.AdminMapper;
import com.psychology.assistant.model.dto.LoginRequest;
import com.psychology.assistant.model.entity.Admin;
import com.psychology.assistant.model.vo.LoginResponse;
import com.psychology.assistant.model.vo.ProfileVO;
import com.psychology.assistant.security.JwtUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuthService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private JwtUtil jwtUtil;

    public LoginResponse adminLogin(LoginRequest request) {
        Admin admin = adminMapper.selectByUsername(request.getUsername());

        if (admin == null) {
            throw new BusinessException(404, "账号不存在");
        }

        if (!"admin123".equals(request.getPassword())) {
            throw new BusinessException(401, "密码错误");
        }

        LoginResponse response = new LoginResponse();
        response.setToken(jwtUtil.generateToken(admin.getId(), admin.getUsername(), "ADMIN"));

        ProfileVO profile = new ProfileVO();
        profile.setId(admin.getId());
        profile.setUsername(admin.getUsername());
        profile.setNickname(admin.getNickname());
        profile.setAvatar(admin.getAvatar());
        response.setProfile(profile);
        return response;
    }

    public ProfileVO getProfile() {
        Long adminId = com.psychology.assistant.security.AuthContext.getUserId();
        Admin admin = adminMapper.selectById(adminId);

        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        ProfileVO profile = new ProfileVO();
        profile.setId(admin.getId());
        profile.setUsername(admin.getUsername());
        profile.setNickname(admin.getNickname());
        profile.setAvatar(admin.getAvatar());
        return profile;
    }
}
