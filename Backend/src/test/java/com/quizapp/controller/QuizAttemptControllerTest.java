package com.quizapp.controller;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.service.QuizAttemptService;
import com.quizapp.security.CustomUserDetailsService;
import com.quizapp.security.JwtAuthFilter;
import com.quizapp.security.JwtService;

import org.junit.jupiter.api.Test;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizAttemptController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuizAttemptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuizAttemptService quizAttemptService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;


    // =========================================================
    // TEST DATA
    // =========================================================

    private QuizAttempt createAttempt() {

        Quiz quiz = new Quiz();

        quiz.setId(10L);
        quiz.setTitle("Java Basics Quiz");

        QuizAttempt attempt = new QuizAttempt();

        attempt.setId(1L);
        attempt.setQuiz(quiz);
        attempt.setScore(8);
        attempt.setTotalQuestions(10);
        attempt.setCorrectAnswers(8);
        attempt.setPercentage(80.0);

        attempt.setStartedAt(
                LocalDateTime.of(2026, 10, 6, 18, 0)
        );

        attempt.setCompletedAt(
                LocalDateTime.of(2026, 10, 6, 18, 8)
        );

        return attempt;
    }


    // =========================================================
    // START ATTEMPT
    // =========================================================

    @Test
    void startAttempt_shouldReturnCreated() throws Exception {

        QuizAttempt attempt = createAttempt();

        when(quizAttemptService.startAttempt(1L, 10L))
                .thenReturn(attempt);

        mockMvc.perform(
                        post("/api/attempts/user/1/quiz/10")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());
    }


    // =========================================================
    // GET ATTEMPT BY ID
    // =========================================================

    @Test
    void getAttemptById_shouldReturnAttempt() throws Exception {

        QuizAttempt attempt = createAttempt();

        when(
                quizAttemptService.getAttemptById(1L)
        ).thenReturn(attempt);

        mockMvc.perform(
                        get("/api/attempts/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quizId").value(10))
                .andExpect(jsonPath("$.quizTitle")
                        .value("Java Basics Quiz"))
                .andExpect(jsonPath("$.score").value(8))
                .andExpect(jsonPath("$.totalQuestions").value(10))
                .andExpect(jsonPath("$.correctAnswers").value(8))
                .andExpect(jsonPath("$.percentage").value(80.0))
                .andExpect(jsonPath("$.durationMinutes").value(10));

        verify(
                quizAttemptService
        ).getAttemptById(1L);
    }


    // =========================================================
    // GET ATTEMPTS BY USER
    // =========================================================

    @Test
    void getAttemptsByUser_shouldReturnAttempts() throws Exception {

        QuizAttempt attempt = createAttempt();

        when(
                quizAttemptService.getAttemptsByUser(1L)
        ).thenReturn(List.of(attempt));

        mockMvc.perform(
                        get("/api/attempts/user/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].quizId").value(10))
                .andExpect(jsonPath("$[0].quizTitle")
                        .value("Java Basics Quiz"))
                .andExpect(jsonPath("$[0].score").value(8))
                .andExpect(jsonPath("$[0].percentage").value(80.0))
                .andExpect(jsonPath("$[0].durationMinutes").value(10));

        verify(
                quizAttemptService
        ).getAttemptsByUser(1L);
    }


    // =========================================================
    // GET ATTEMPTS BY QUIZ
    // =========================================================

    @Test
    void getAttemptsByQuiz_shouldReturnAttempts() throws Exception {

        QuizAttempt attempt = createAttempt();

        when(
                quizAttemptService.getAttemptsByQuiz(10L)
        ).thenReturn(List.of(attempt));

        mockMvc.perform(
                        get("/api/attempts/quiz/10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].quizId").value(10))
                .andExpect(jsonPath("$[0].quizTitle")
                        .value("Java Basics Quiz"))
                .andExpect(jsonPath("$[0].score").value(8))
                .andExpect(jsonPath("$[0].correctAnswers").value(8));

        verify(
                quizAttemptService
        ).getAttemptsByQuiz(10L);
    }


    // =========================================================
    // GET ATTEMPTS BY USER + QUIZ
    // =========================================================

    @Test
    void getAttemptsByUserAndQuiz_shouldReturnAttempts()
            throws Exception {

        QuizAttempt attempt = createAttempt();

        when(
                quizAttemptService
                        .getAttemptsByUserAndQuiz(1L, 10L)
        ).thenReturn(List.of(attempt));

        mockMvc.perform(
                        get("/api/attempts/user/1/quiz/10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].quizId").value(10))
                .andExpect(jsonPath("$[0].quizTitle")
                        .value("Java Basics Quiz"));

        verify(
                quizAttemptService
        ).getAttemptsByUserAndQuiz(1L, 10L);
    }


    // =========================================================
    // SUBMIT ATTEMPT
    // =========================================================

    @Test
    void submitAttempt_shouldReturnSubmittedAttempt()
            throws Exception {

        QuizAttempt attempt = createAttempt();

        when(
                quizAttemptService.submitAttempt(1L)
        ).thenReturn(attempt);

        mockMvc.perform(
                        post("/api/attempts/1/submit")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.score").value(8))
                .andExpect(jsonPath("$.totalQuestions").value(10))
                .andExpect(jsonPath("$.correctAnswers").value(8))
                .andExpect(jsonPath("$.percentage").value(80.0))
                .andExpect(jsonPath("$.durationMinutes").value(10));

        verify(
                quizAttemptService
        ).submitAttempt(1L);
    }


    // =========================================================
    // DELETE ATTEMPT
    // =========================================================

    @Test
    void deleteAttempt_shouldReturnNoContent() throws Exception {

        doNothing()
                .when(quizAttemptService)
                .deleteAttempt(1L);

        mockMvc.perform(
                        delete("/api/attempts/1")
                )
                .andExpect(status().isNoContent());

        verify(
                quizAttemptService
        ).deleteAttempt(1L);
    }
}