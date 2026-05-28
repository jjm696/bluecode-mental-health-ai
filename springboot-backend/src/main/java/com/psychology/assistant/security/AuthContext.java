package com.psychology.assistant.security;

public class AuthContext {

    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<Long>();
    private static final ThreadLocal<String> ROLE_HOLDER = new ThreadLocal<String>();

    private AuthContext() {
    }

    public static void set(Long userId, String role) {
        USER_ID_HOLDER.set(userId);
        ROLE_HOLDER.set(role);
    }

    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    public static String getRole() {
        return ROLE_HOLDER.get();
    }

    public static void clear() {
        USER_ID_HOLDER.remove();
        ROLE_HOLDER.remove();
    }
}
