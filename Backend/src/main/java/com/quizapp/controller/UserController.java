package com.quizapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quizapp.entity.User;
import com.quizapp.security.CustomUserDetails;
import com.quizapp.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================================================
    // GET CURRENT AUTHENTICATED USER
    // =========================================================

    /*
     * GET
     * /api/users/me
     *
     * Returns the currently authenticated user.
     *
     * The user ID is obtained from the authenticated JWT
     * instead of being supplied by the client.
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(
            Authentication authentication
    ) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        Long userId = userDetails.getUserId();

        User user =
                userService.getUserById(userId);

        return ResponseEntity.ok(user);
    }
}