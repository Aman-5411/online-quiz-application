package com.quizapp.integration;

import com.quizapp.entity.User;
import com.quizapp.enums.Role;
import com.quizapp.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

        userRepository.findByEmail("integration@test.com")
                .ifPresent(user ->
                        userRepository.delete(user)
                );
    }

    @Test
    void register_shouldCreateUser() throws Exception {

        String requestBody = """
                {
                    "name": "Integration User",
                    "email": "integration@test.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());

        User user = userRepository
                .findByEmail("integration@test.com")
                .orElseThrow();

        assertThat(user.getName())
                .isEqualTo("Integration User");

        assertThat(user.getEmail())
                .isEqualTo("integration@test.com");

        assertThat(user.getRole())
                .isEqualTo(Role.USER);
    }

    @Test
    void login_shouldReturnJwtToken() throws Exception {

        String registerBody = """
                {
                    "name": "Integration User",
                    "email": "integration@test.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerBody)
                )
                .andExpect(status().isCreated());

        String loginBody = """
                {
                    "email": "integration@test.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginBody)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString())
                                .contains("\"token\"")
                );
    }

    @Test
    void protectedEndpoint_withoutJwt_shouldBeRejected()
            throws Exception {

        mockMvc.perform(
                        get("/api/attempts/user/1")
                )
                .andExpect(status().isForbidden());
    }
}