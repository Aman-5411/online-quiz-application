package com.quizapp.controller;

import com.quizapp.dto.QuestionResponse;
import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuestionType;
import com.quizapp.security.CustomUserDetailsService;
import com.quizapp.security.JwtAuthFilter;
import com.quizapp.security.JwtService;
import com.quizapp.service.QuestionService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(QuestionController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionService questionService;

    /*
     * Security beans required because the application security configuration
     * detects JwtAuthFilter during the WebMvcTest context.
     */
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;


    // =========================================================
    // CREATE QUESTION
    // =========================================================

    @Test
    void createQuestion_shouldReturnCreated() throws Exception {

        Question question = new Question();
        question.setId(1L);

        when(questionService.createQuestion(
                eq(10L),
                any(Question.class)
        )).thenReturn(question);

        mockMvc.perform(
                        post("/api/quizzes/10/questions")
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }


    // =========================================================
    // GET QUESTION BY ID
    // =========================================================

    @Test
    void getQuestionById_shouldReturnQuestion() throws Exception {

        Question question = new Question();
        question.setId(1L);

        when(questionService.getQuestionById(1L))
                .thenReturn(question);

        mockMvc.perform(
                        get("/api/questions/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }


    // =========================================================
    // GET QUESTIONS BY QUIZ
    // =========================================================

    @Test
    void getQuestionsByQuizId_shouldReturnQuestions() throws Exception {

        Question question1 = new Question();
        question1.setId(1L);

        Question question2 = new Question();
        question2.setId(2L);

        when(questionService.getQuestionsByQuizId(10L))
                .thenReturn(List.of(question1, question2));

        mockMvc.perform(
                        get("/api/quizzes/10/questions")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }


    // =========================================================
    // GET QUESTIONS FOR QUIZ ATTEMPT
    // =========================================================

    @Test
    void getQuestionsForQuizAttempt_shouldReturnQuestions() throws Exception {

        QuestionResponse response1 = new QuestionResponse(
                1L,
                "Which keyword is used to inherit a class?",
                1,
                QuestionType.SINGLE_CHOICE,
                Difficulty.EASY,
                List.of()
        );

        QuestionResponse response2 = new QuestionResponse(
                2L,
                "Which keyword is used to create an object?",
                2,
                QuestionType.SINGLE_CHOICE,
                Difficulty.EASY,
                List.of()
        );

        when(questionService.getQuestionsForQuizAttempt(10L))
                .thenReturn(List.of(response1, response2));

        mockMvc.perform(
                        get("/api/quizzes/10/questions/attempt")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }


    // =========================================================
    // UPDATE QUESTION
    // =========================================================

    @Test
    void updateQuestion_shouldReturnUpdatedQuestion() throws Exception {

        Question question = new Question();
        question.setId(1L);

        when(questionService.updateQuestion(
                eq(1L),
                any(Question.class)
        )).thenReturn(question);

        mockMvc.perform(
                        put("/api/questions/1")
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }


    // =========================================================
    // DELETE QUESTION
    // =========================================================

    @Test
    void deleteQuestion_shouldReturnNoContent() throws Exception {

        doNothing().when(questionService)
                .deleteQuestion(1L);

        mockMvc.perform(
                        delete("/api/questions/1")
                )
                .andExpect(status().isNoContent());
    }


    // =========================================================
    // ADD OPTION
    // =========================================================

    @Test
    void addOption_shouldReturnCreated() throws Exception {

        Option option = new Option();
        option.setId(1L);
        option.setOptionText("extends");
        option.setCorrect(true);

        when(questionService.addOption(
                eq(1L),
                any(Option.class)
        )).thenReturn(option);

        mockMvc.perform(
                        post("/api/questions/1/options")
                                .contentType("application/json")
                                .content("""
                                    {
                                        "optionText": "extends",
                                        "correct": true
                                    }
                                    """)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    // =========================================================
    // UPDATE OPTION
    // =========================================================

    @Test
    void updateOption_shouldReturnUpdatedOption() throws Exception {

        Option option = new Option();
        option.setId(1L);
        option.setOptionText("implements");
        option.setCorrect(false);

        when(questionService.updateOption(
                eq(1L),
                any(Option.class)
        )).thenReturn(option);

        mockMvc.perform(
                        put("/api/options/1")
                                .contentType("application/json")
                                .content("""
                                    {
                                        "optionText": "implements",
                                        "correct": false
                                    }
                                    """)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.optionText").value("implements"))
                .andExpect(jsonPath("$.correct").value(false));
    }


    // =========================================================
    // DELETE OPTION
    // =========================================================

    @Test
    void deleteOption_shouldReturnNoContent() throws Exception {

        doNothing().when(questionService)
                .deleteOption(1L);

        mockMvc.perform(
                        delete("/api/options/1")
                )
                .andExpect(status().isNoContent());
    }


    // =========================================================
    // GET OPTIONS BY QUESTION
    // =========================================================

    @Test
    void getOptionsByQuestionId_shouldReturnOptions() throws Exception {

        Option option1 = new Option();
        option1.setId(1L);

        Option option2 = new Option();
        option2.setId(2L);

        when(questionService.getOptionsByQuestionId(10L))
                .thenReturn(List.of(option1, option2));

        mockMvc.perform(
                        get("/api/questions/10/options")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }
}