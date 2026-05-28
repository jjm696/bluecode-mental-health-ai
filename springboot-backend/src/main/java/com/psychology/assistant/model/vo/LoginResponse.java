package com.psychology.assistant.model.vo;

public class LoginResponse {

    private String token;
    private ProfileVO profile;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ProfileVO getProfile() {
        return profile;
    }

    public void setProfile(ProfileVO profile) {
        this.profile = profile;
    }
}
