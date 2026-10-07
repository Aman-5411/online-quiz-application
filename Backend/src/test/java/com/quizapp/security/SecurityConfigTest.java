package com.quizapp.security;

import com.quizapp.entity.User;
import com.quizapp.enums.Role;
import com.quizapp.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private User admin;

    @BeforeEach
    void setUp() {

        user = userRepository.findByEmail("security-user@test.com")
                .orElseGet(() -> createUser(
                        "security-user@test.com",
                        "Security User",
                        Role.USER
                ));

        admin = userRepository.findByEmail("security-admin@test.com")
                .orElseGet(() -> createUser(
                        "security-admin@test.com",
                        "Security Admin",
                        Role.ADMIN
                ));
    }

    private User createUser(
            String email,
            String name,
            Role role) {

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode("password")
        );
        user.setRole(role);

        return userRepository.save(user);
    }

    @Test
    void protectedEndpointWithoutToken_shouldReturn403() throws Exception {

        mockMvc.perform(
                        get("/api/attempts/user/{userId}", user.getId())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpointWithUserToken_shouldBeAccessible() throws Exception {

        String token = jwtService.generateToken(user);

        mockMvc.perform(
                        get("/api/attempts/user/{userId}", user.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpointWithoutToken_shouldReturn403() throws Exception {

        mockMvc.perform(
                        get("/api/admin/attempts")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointWithUserToken_shouldReturn403() throws Exception {

        String token = jwtService.generateToken(user);

        mockMvc.perform(
                        post("/api/quizzes")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointWithAdminToken_shouldBeAccessible() throws Exception {

        String token = jwtService.generateToken(admin);

        mockMvc.perform(
                        get("/api/admin/attempts")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void adminQuizCreationWithoutToken_shouldReturn403() throws Exception {

        mockMvc.perform(
                        post("/api/quizzes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminQuizCreationWithUserToken_shouldReturn403() throws Exception {

        String token = jwtService.generateToken(user);

        mockMvc.perform(
                        post("/api/quizzes")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isForbidden());
    }
}