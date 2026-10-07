package com.quizapp.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuestionType;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;
import com.quizapp.enums.Role;
import com.quizapp.repository.OptionRepository;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.UserRepository;
import com.quizapp.security.CustomUserDetails;

@SpringBootTest
@AutoConfigureMockMvc
class QuizWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    private User user;
    private Quiz quiz;
    private Question question;
    private Option correctOption;
    private Option wrongOption;

    @BeforeEach
    void setUp() {

        /*
         * Reuse the test user if it already exists.
         * This avoids deleting existing data and causing
         * foreign-key constraint problems.
         */
        user = userRepository
                .findByEmail("quiz-workflow@test.com")
                .orElseGet(() -> {

                    User newUser = new User();

                    newUser.setEmail("quiz-workflow@test.com");
                    newUser.setName("Quiz Workflow User");
                    newUser.setPassword("$2a$10$test-password");
                    newUser.setRole(Role.USER);

                    return userRepository.save(newUser);
                });

        /*
         * Create a fresh quiz for every test.
         */
        quiz = new Quiz();

        quiz.setTitle("Integration Test Quiz");
        quiz.setDescription(
                "Quiz used for end-to-end integration testing."
        );
        quiz.setCategory("Java");
        quiz.setDifficulty(Difficulty.EASY);
        quiz.setSource(QuizSource.MANUAL);
        quiz.setStatus(QuizStatus.PUBLISHED);
        quiz.setCreatedBy(user);

        quiz = quizRepository.save(quiz);

        /*
         * Create one SINGLE_CHOICE question.
         */
        question = new Question();

        question.setQuestionText(
                "Which keyword is used to inherit a class in Java?"
        );
        question.setQuestionType(QuestionType.SINGLE_CHOICE);
        question.setDifficulty(Difficulty.EASY);
        question.setQuestionOrder(1);
        question.setQuiz(quiz);

        question = questionRepository.save(question);

        /*
         * Correct option.
         */
        correctOption = new Option();

        correctOption.setOptionText("extends");
        correctOption.setCorrect(true);
        correctOption.setQuestion(question);

        correctOption = optionRepository.save(correctOption);

        /*
         * Wrong option.
         */
        wrongOption = new Option();

        wrongOption.setOptionText("implements");
        wrongOption.setCorrect(false);
        wrongOption.setQuestion(question);

        wrongOption = optionRepository.save(wrongOption);
    }

    // =========================================================
    // COMPLETE QUIZ WORKFLOW
    // =========================================================

    @Test
    void completeQuizWorkflow_shouldStartAnswerSubmitAndPersistResult()
            throws Exception {

        /*
         * 1. START QUIZ
         */
        String startResponse =
                mockMvc.perform(
                                post(
                                        "/api/attempts/user/{userId}/quiz/{quizId}",
                                        user.getId(),
                                        quiz.getId()
                                )
                                        .with(authentication(createAuthentication()))
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.totalQuestions").value(1))
                        .andExpect(jsonPath("$.score").value(0))
                        .andExpect(jsonPath("$.correctAnswers").value(0))
                        .andExpect(jsonPath("$.percentage").value(0.0))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        /*
         * Extract attempt ID.
         */
        Long attemptId =
                new ObjectMapper()
                        .readTree(startResponse)
                        .get("id")
                        .asLong();

        /*
         * 2. SUBMIT CORRECT ANSWER
         */
        mockMvc.perform(
                        post("/api/answers")
                                .with(authentication(createAuthentication()))
                                .param("attemptId", attemptId.toString())
                                .param("questionId", question.getId().toString())
                                .param("optionId", correctOption.getId().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correct").value(true));

        /*
         * 3. VERIFY ANSWER WAS PERSISTED
         */

        mockMvc.perform(
                        get("/api/answers/attempt/{attemptId}", attemptId)
                                .with(authentication(createAuthentication()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].correct").value(true));

        mockMvc.perform(
                        get("/api/answers/attempt/{attemptId}/count", attemptId)
                                .with(authentication(createAuthentication()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));

        mockMvc.perform(
                        get(
                                "/api/answers/attempt/{attemptId}/correct-count",
                                attemptId
                        )
                                .with(authentication(createAuthentication()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
        /*
         * 4. SUBMIT QUIZ
         */
        mockMvc.perform(
                        post("/api/attempts/{attemptId}/submit", attemptId)
                                .with(authentication(createAuthentication()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(attemptId))
                .andExpect(jsonPath("$.score").value(1))
                .andExpect(jsonPath("$.totalQuestions").value(1))
                .andExpect(jsonPath("$.correctAnswers").value(1))
                .andExpect(jsonPath("$.percentage").value(100.0))
                .andExpect(jsonPath("$.completedAt").exists());

        /*
         * 5. VERIFY FINAL RESULT
         */
        mockMvc.perform(
                        get("/api/attempts/{attemptId}", attemptId)
                                .with(authentication(createAuthentication()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(attemptId))
                .andExpect(jsonPath("$.quizId").value(quiz.getId()))
                .andExpect(
                        jsonPath("$.quizTitle")
                                .value("Integration Test Quiz")
                )
                .andExpect(jsonPath("$.score").value(1))
                .andExpect(jsonPath("$.totalQuestions").value(1))
                .andExpect(jsonPath("$.correctAnswers").value(1))
                .andExpect(jsonPath("$.percentage").value(100.0))
                .andExpect(jsonPath("$.completedAt").exists());
    }

    // =========================================================
    // WRONG ANSWER WORKFLOW
    // =========================================================

    @Test
    void submittingWrongAnswer_shouldProduceZeroScore()
            throws Exception {

        /*
         * 1. START QUIZ
         */
        String startResponse =
                mockMvc.perform(
                                post(
                                        "/api/attempts/user/{userId}/quiz/{quizId}",
                                        user.getId(),
                                        quiz.getId()
                                )
                                        .with(authentication(createAuthentication()))
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Long attemptId =
                new ObjectMapper()
                        .readTree(startResponse)
                        .get("id")
                        .asLong();

        /*
         * 2. SUBMIT WRONG ANSWER
         */
        mockMvc.perform(
                        post("/api/answers")
                                .with(authentication(createAuthentication()))
                                .param("attemptId", attemptId.toString())
                                .param("questionId", question.getId().toString())
                                .param("optionId", wrongOption.getId().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correct").value(false));

        /*
         * 3. SUBMIT ATTEMPT
         */
        mockMvc.perform(
                        post("/api/attempts/{attemptId}/submit", attemptId)
                                .with(authentication(createAuthentication()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(0))
                .andExpect(jsonPath("$.correctAnswers").value(0))
                .andExpect(jsonPath("$.totalQuestions").value(1))
                .andExpect(jsonPath("$.percentage").value(0.0))
                .andExpect(jsonPath("$.completedAt").exists());
    }

    // =========================================================
    // AUTHENTICATION HELPER
    // =========================================================

    private UsernamePasswordAuthenticationToken createAuthentication() {

        CustomUserDetails userDetails =
                new CustomUserDetails(
                        user.getId(),
                        user.getEmail(),
                        user.getPassword(),
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + user.getRole().name()
                                )
                        )
                );

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}