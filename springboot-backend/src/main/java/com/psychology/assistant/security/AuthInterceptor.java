package com.psychology.assistant.security;

import com.psychology.assistant.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (uri.startsWith("/api/articles") && "GET".equalsIgnoreCase(method)) {
            return true;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录或登录已失效");
        }

        String token = authorization.substring(7);
        Claims claims = jwtUtil.parseToken(token);
        Object userId = claims.get("userId");
        String role = String.valueOf(claims.get("role"));

        if (userId == null) {
            throw new BusinessException(401, "登录状态无效，请重新登录");
        }

        if (uri.startsWith("/api/articles")
            || uri.startsWith("/api/auth/profile")
            || uri.startsWith("/api/consultations")
            || uri.startsWith("/api/dashboard")
            || (uri.startsWith("/api/emotions") && "GET".equalsIgnoreCase(method))) {
            if (!"ADMIN".equals(role)) {
                throw new BusinessException(403, "无权访问管理员接口");
            }
        }

        if ((uri.startsWith("/api/emotions") && "POST".equalsIgnoreCase(method))
            || uri.startsWith("/api/user")) {
            if (!"USER".equals(role)) {
                throw new BusinessException(403, "请先登录用户账户");
            }
        }

        AuthContext.set(Long.valueOf(String.valueOf(userId)), role);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}
