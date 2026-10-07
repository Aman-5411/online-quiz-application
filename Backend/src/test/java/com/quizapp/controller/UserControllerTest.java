package com.quizapp.controller;

import com.quizapp.entity.User;
import com.quizapp.security.CustomUserDetails;
import com.quizapp.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class UserControllerTest {

    private UserService userService;

    private UserController userController;


    @BeforeEach
    void setUp() {

        userService = mock(UserService.class);

        userController =
                new UserController(userService);
    }


    // =========================================================
    // GET CURRENT AUTHENTICATED USER
    // =========================================================

    @Test
    void getCurrentUser_shouldReturnAuthenticatedUser() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("Aman");
        user.setEmail("aman@example.com");

        when(userService.getUserById(userId))
                .thenReturn(user);


        CustomUserDetails userDetails =
                new CustomUserDetails(
                        userId,
                        "aman@example.com",
                        "encoded-password",
                        List.of(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )
                );


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );


        ResponseEntity<User> response =
                userController.getCurrentUser(authentication);


        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        assertNotNull(response.getBody());

        assertEquals(userId, response.getBody().getId());
        assertEquals("Aman", response.getBody().getName());
        assertEquals(
                "aman@example.com",
                response.getBody().getEmail()
        );


        verify(userService).getUserById(userId);
    }
}