package com.example.gymhibernatetask.service;

public interface LoginService {

    boolean login(String username, String password);

    boolean changeLogin(String username, String oldPassword, String newPassword);
}
