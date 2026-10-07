package com.quizapp.service;

import java.util.List;

import com.quizapp.entity.User;

public interface UserService {

    User createUser(User user);

    User getUserById(Long id);

    User getUserByEmail(String email);

    List<User> getAllUsers();

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    boolean existsByEmail(String email);
}