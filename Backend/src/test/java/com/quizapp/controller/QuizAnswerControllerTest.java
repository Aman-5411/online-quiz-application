package com.quizapp.controller;

import com.quizapp.entity.QuizAnswer;
import com.quizapp.service.QuizAnswerService;
import com.quizapp.security.CustomUserDetailsService;
import com.quizapp.security.JwtAuthFilter;
import com.quizapp.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizAnswerController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuizAnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuizAnswerService quizAnswerService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    // =========================================================
    // SUBMIT ANSWER
    // =========================================================

    @Test
    void submitAnswer_shouldReturnCreated() throws Exception {

        QuizAnswer answer = new QuizAnswer();
        answer.setId(1L);
        answer.setCorrect(true);

        when(quizAnswerService.submitAnswer(1L, 2L, 3L))
                .thenReturn(answer);

        mockMvc.perform(
                        post("/api/answers")
                                .param("attemptId", "1")
                                .param("questionId", "2")
                                .param("optionId", "3")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.correct").value(true));
    }

    // =========================================================
    // GET ANSWER BY ID
    // =========================================================

    @Test
    void getAnswerById_shouldReturnAnswer() throws Exception {

        QuizAnswer answer = new QuizAnswer();
        answer.setId(1L);
        answer.setCorrect(true);

        when(quizAnswerService.getAnswerById(1L))
                .thenReturn(answer);

        mockMvc.perform(
                        get("/api/answers/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.correct").value(true));
    }

    // =========================================================
    // GET ANSWERS BY ATTEMPT
    // =========================================================

    @Test
    void getAnswersByAttempt_shouldReturnAnswers() throws Exception {

        QuizAnswer answer1 = new QuizAnswer();
        answer1.setId(1L);
        answer1.setCorrect(true);

        QuizAnswer answer2 = new QuizAnswer();
        answer2.setId(2L);
        answer2.setCorrect(false);

        when(quizAnswerService.getAnswersByAttempt(10L))
                .thenReturn(List.of(answer1, answer2));

        mockMvc.perform(
                        get("/api/answers/attempt/10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].correct").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].correct").value(false));
    }

    // =========================================================
    // GET ANSWER FOR QUESTION
    // =========================================================

    @Test
    void getAnswerForQuestion_shouldReturnAnswer() throws Exception {

        QuizAnswer answer = new QuizAnswer();
        answer.setId(5L);
        answer.setCorrect(true);

        when(quizAnswerService.getAnswerForQuestion(10L, 20L))
                .thenReturn(answer);

        mockMvc.perform(
                        get("/api/answers/attempt/10/question/20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.correct").value(true));
    }

    // =========================================================
    // COUNT ANSWERS
    // =========================================================

    @Test
    void countAnswersByAttempt_shouldReturnCount() throws Exception {

        when(quizAnswerService.countAnswersByAttempt(10L))
                .thenReturn(5L);

        mockMvc.perform(
                        get("/api/answers/attempt/10/count")
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        org.junit.jupiter.api.Assertions.assertEquals(
                                "5",
                                result.getResponse().getContentAsString()
                        )
                );
    }

    // =========================================================
    // COUNT CORRECT ANSWERS
    // =========================================================

    @Test
    void countCorrectAnswersByAttempt_shouldReturnCount() throws Exception {

        when(quizAnswerService.countCorrectAnswersByAttempt(10L))
                .thenReturn(3L);

        mockMvc.perform(
                        get("/api/answers/attempt/10/correct-count")
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        org.junit.jupiter.api.Assertions.assertEquals(
                                "3",
                                result.getResponse().getContentAsString()
                        )
                );
    }

    // =========================================================
    // DELETE ANSWER
    // =========================================================

    @Test
    void deleteAnswer_shouldReturnNoContent() throws Exception {

        doNothing().when(quizAnswerService)
                .deleteAnswer(1L);

        mockMvc.perform(
                        delete("/api/answers/1")
                )
                .andExpect(status().isNoContent());
    }
}