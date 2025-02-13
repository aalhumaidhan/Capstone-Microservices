package Capstone.Users.bo.Register.Response;

import Capstone.Users.entity.UserEntity;

public class RegisterResponse {

    private String token;

    private UserEntity user;

    private String message;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

