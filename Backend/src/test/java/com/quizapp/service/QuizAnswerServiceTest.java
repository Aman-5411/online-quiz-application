package com.quizapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAnswer;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuestionType;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;
import com.quizapp.enums.Role;
import com.quizapp.repository.OptionRepository;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.QuizAnswerRepository;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.security.CustomUserDetails;
import com.quizapp.service.impl.QuizAnswerServiceImpl;

@ExtendWith(MockitoExtension.class)
class QuizAnswerServiceTest {

    @Mock
    private QuizAnswerRepository quizAnswerRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionRepository optionRepository;

    @InjectMocks
    private QuizAnswerServiceImpl quizAnswerService;

    private User user;
    private User anotherUser;

    private Quiz quiz;
    private Quiz anotherQuiz;

    private QuizAttempt attempt;
    private QuizAttempt completedAttempt;

    private Question question;
    private Question anotherQuestion;

    private Option correctOption;
    private Option wrongOption;
    private Option optionFromAnotherQuestion;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setName("Test User");
        user.setPassword("password");
        user.setRole(Role.USER);

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@test.com");
        anotherUser.setName("Another User");
        anotherUser.setPassword("password");
        anotherUser.setRole(Role.USER);

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Java Basics");
        quiz.setCategory("Java");
        quiz.setDifficulty(Difficulty.EASY);
        quiz.setSource(QuizSource.MANUAL);
        quiz.setStatus(QuizStatus.PUBLISHED);

        anotherQuiz = new Quiz();
        anotherQuiz.setId(2L);
        anotherQuiz.setTitle("Spring Boot");
        anotherQuiz.setCategory("Spring");
        anotherQuiz.setDifficulty(Difficulty.EASY);
        anotherQuiz.setSource(QuizSource.MANUAL);
        anotherQuiz.setStatus(QuizStatus.PUBLISHED);

        attempt = new QuizAttempt();
        attempt.setId(1L);
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setScore(0);
        attempt.setTotalQuestions(1);
        attempt.setCorrectAnswers(0);
        attempt.setPercentage(0.0);
        attempt.setStartedAt(LocalDateTime.now());

        completedAttempt = new QuizAttempt();
        completedAttempt.setId(2L);
        completedAttempt.setUser(user);
        completedAttempt.setQuiz(quiz);
        completedAttempt.setScore(1);
        completedAttempt.setTotalQuestions(1);
        completedAttempt.setCorrectAnswers(1);
        completedAttempt.setPercentage(100.0);
        completedAttempt.setStartedAt(LocalDateTime.now());
        completedAttempt.setCompletedAt(LocalDateTime.now());

        question = new Question();
        question.setId(1L);
        question.setQuestionText("Which keyword is used to inherit a class?");
        question.setQuestionType(QuestionType.SINGLE_CHOICE);
        question.setDifficulty(Difficulty.EASY);
        question.setQuestionOrder(1);
        question.setQuiz(quiz);

        anotherQuestion = new Question();
        anotherQuestion.setId(2L);
        anotherQuestion.setQuestionText("What is Spring?");
        anotherQuestion.setQuestionType(QuestionType.SINGLE_CHOICE);
        anotherQuestion.setDifficulty(Difficulty.EASY);
        anotherQuestion.setQuestionOrder(1);
        anotherQuestion.setQuiz(anotherQuiz);

        correctOption = new Option();
        correctOption.setId(1L);
        correctOption.setOptionText("extends");
        correctOption.setCorrect(true);
        correctOption.setQuestion(question);

        wrongOption = new Option();
        wrongOption.setId(2L);
        wrongOption.setOptionText("implements");
        wrongOption.setCorrect(false);
        wrongOption.setQuestion(question);

        optionFromAnotherQuestion = new Option();
        optionFromAnotherQuestion.setId(3L);
        optionFromAnotherQuestion.setOptionText("Spring");
        optionFromAnotherQuestion.setCorrect(true);
        optionFromAnotherQuestion.setQuestion(anotherQuestion);

        authenticateAs(user);
    }

    // =========================================================
    // SUBMIT ANSWER - SUCCESS
    // =========================================================

    @Test
    void submitAnswer_shouldSaveCorrectAnswer() {

        when(quizAttemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(quizAnswerRepository.findByAttemptIdAndQuestionId(1L, 1L))
                .thenReturn(Optional.empty());

        when(optionRepository.findById(1L))
                .thenReturn(Optional.of(correctOption));

        QuizAnswer savedAnswer = new QuizAnswer();
        savedAnswer.setId(10L);
        savedAnswer.setAttempt(attempt);
        savedAnswer.setQuestion(question);
        savedAnswer.setSelectedOption(correctOption);
        savedAnswer.setCorrect(true);

        when(quizAnswerRepository.save(any(QuizAnswer.class)))
                .thenReturn(savedAnswer);

        QuizAnswer result =
                quizAnswerService.submitAnswer(1L, 1L, 1L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertTrue(result.getCorrect());
        assertEquals(attempt, result.getAttempt());
        assertEquals(question, result.getQuestion());
        assertEquals(correctOption, result.getSelectedOption());

        verify(quizAnswerRepository).save(any(QuizAnswer.class));
    }

    @Test
    void submitAnswer_shouldSaveIncorrectAnswer() {

        when(quizAttemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(quizAnswerRepository.findByAttemptIdAndQuestionId(1L, 1L))
                .thenReturn(Optional.empty());

        when(optionRepository.findById(2L))
                .thenReturn(Optional.of(wrongOption));

        QuizAnswer savedAnswer = new QuizAnswer();
        savedAnswer.setId(11L);
        savedAnswer.setAttempt(attempt);
        savedAnswer.setQuestion(question);
        savedAnswer.setSelectedOption(wrongOption);
        savedAnswer.setCorrect(false);

        when(quizAnswerRepository.save(any(QuizAnswer.class)))
                .thenReturn(savedAnswer);

        QuizAnswer result =
                quizAnswerService.submitAnswer(1L, 1L, 2L);

        assertNotNull(result);
        assertFalse(result.getCorrect());

        verify(quizAnswerRepository).save(any(QuizAnswer.class));
    }

    // =========================================================
    // SUBMIT ANSWER - AUTHENTICATION
    // =========================================================

    @Test
    void submitAnswer_shouldRejectUnauthenticatedUser() {

        SecurityContextHolder.clearContext();

        SecurityException exception =
                assertThrows(
                        SecurityException.class,
                        () -> quizAnswerService.submitAnswer(1L, 1L, 1L)
                );

        assertEquals(
                "User is not authenticated",
                exception.getMessage()
        );

        verifyNoInteractions(
                quizAttemptRepository,
                questionRepository,
                optionRepository,
                quizAnswerRepository
        );
    }

    @Test
    void submitAnswer_shouldRejectNullOption() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> quizAnswerService.submitAnswer(1L, 1L, null)
                );

        assertEquals(
                "An option must be selected",
                exception.getMessage()
        );

        verifyNoInteractions(
                quizAttemptRepository,
                questionRepository,
                optionRepository,
                quizAnswerRepository
        );
    }

    // =========================================================
    // SUBMIT ANSWER - ATTEMPT VALIDATION
    // =========================================================

    @Test
    void submitAnswer_shouldRejectMissingAttempt() {

        when(quizAttemptRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> quizAnswerService.submitAnswer(99L, 1L, 1L)
                );

        assertEquals(
                "Quiz attempt not found with id: 99",
                exception.getMessage()
        );
    }

    @Test
    void submitAnswer_shouldRejectAnotherUsersAttempt() {

        attempt.setUser(anotherUser);

        when(quizAttemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        SecurityException exception =
                assertThrows(
                        SecurityException.class,
                        () -> quizAnswerService.submitAnswer(1L, 1L, 1L)
                );

        assertEquals(
                "You are not allowed to submit an answer for this quiz attempt",
                exception.getMessage()
        );

        verifyNoInteractions(questionRepository, optionRepository);
        verify(quizAnswerRepository, never())
                .save(any(QuizAnswer.class));
    }

    @Test
    void submitAnswer_shouldRejectCompletedAttempt() {

        when(quizAttemptRepository.findById(2L))
                .thenReturn(Optional.of(completedAttempt));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizAnswerService.submitAnswer(2L, 1L, 1L)
                );

        assertEquals(
                "Cannot submit answer after the quiz has been completed",
                exception.getMessage()
        );

        verifyNoInteractions(questionRepository, optionRepository);
    }

    // =========================================================
    // SUBMIT ANSWER - QUESTION VALIDATION
    // =========================================================

    @Test
    void submitAnswer_shouldRejectMissingQuestion() {

        when(quizAttemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(questionRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> quizAnswerService.submitAnswer(1L, 99L, 1L)
                );

        assertEquals(
                "Question not found with id: 99",
                exception.getMessage()
        );
    }

    @Test
    void submitAnswer_shouldRejectQuestionFromAnotherQuiz() {

        when(quizAttemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(questionRepository.findById(2L))
                .thenReturn(Optional.of(anotherQuestion));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> quizAnswerService.submitAnswer(1L, 2L, 1L)
                );

        assertEquals(
                "Question does not belong to the quiz being attempted",
                exception.getMessage()
        );

        verifyNoInteractions(optionRepository);
    }

    @Test
    void submitAnswer_shouldRejectUnsupportedQuestionType() {

        question.setQuestionType(null);

        when(quizAttemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizAnswerService.submitAnswer(1L, 1L, 1L)
                );

        assertEquals(
                "Only SINGLE_CHOICE questions are supported",
                exception.getMessage()
        );

        verifyNoInteractions(optionRepository);
    }

    // =========================================================
    // SUBMIT ANSWER - DUPLICATE
    // =========================================================

    @Test
    void submitAnswer_shouldRejectDuplicateAnswer() {

        QuizAnswer existingAnswer = new QuizAnswer();
        existingAnswer.setId(20L);
        existingAnswer.setAttempt(attempt);
        existingAnswer.setQuestion(question);
        existingAnswer.setSelectedOption(correctOption);
        existingAnswer.setCorrect(true);

        when(quizAttemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(quizAnswerRepository.findByAttemptIdAndQuestionId(1L, 1L))
                .thenReturn(Optional.of(existingAnswer));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizAnswerService.submitAnswer(1L, 1L, 1L)
                );

        assertEquals(
                "An answer has already been submitted for this question",
                exception.getMessage()
        );

        verify(optionRepository, never()).findById(anyLong());
        verify(quizAnswerRepository, never())
                .save(any(QuizAnswer.class));
    }

    // =========================================================
    // SUBMIT ANSWER - OPTION VALIDATION
    // =========================================================

    @Test
    void submitAnswer_shouldRejectMissingOption() {

        when(quizAttemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(quizAnswerRepository.findByAttemptIdAndQuestionId(1L, 1L))
                .thenReturn(Optional.empty());

        when(optionRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> quizAnswerService.submitAnswer(1L, 1L, 99L)
                );

        assertEquals(
                "Option not found with id: 99",
                exception.getMessage()
        );
    }

    @Test
    void submitAnswer_shouldRejectOptionFromAnotherQuestion() {

        when(quizAttemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(quizAnswerRepository.findByAttemptIdAndQuestionId(1L, 1L))
                .thenReturn(Optional.empty());

        when(optionRepository.findById(3L))
                .thenReturn(Optional.of(optionFromAnotherQuestion));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> quizAnswerService.submitAnswer(1L, 1L, 3L)
                );

        assertEquals(
                "Selected option does not belong to the submitted question",
                exception.getMessage()
        );

        verify(quizAnswerRepository, never())
                .save(any(QuizAnswer.class));
    }

    // =========================================================
    // GET ANSWER BY ID
    // =========================================================

    @Test
    void getAnswerById_shouldReturnAnswer() {

        QuizAnswer answer = new QuizAnswer();
        answer.setId(10L);
        answer.setAttempt(attempt);
        answer.setQuestion(question);
        answer.setSelectedOption(correctOption);
        answer.setCorrect(true);

        when(quizAnswerRepository.findById(10L))
                .thenReturn(Optional.of(answer));

        QuizAnswer result =
                quizAnswerService.getAnswerById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertTrue(result.getCorrect());

        verify(quizAnswerRepository).findById(10L);
    }

    @Test
    void getAnswerById_shouldThrowException_whenAnswerDoesNotExist() {

        when(quizAnswerRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> quizAnswerService.getAnswerById(99L)
                );

        assertEquals(
                "Quiz answer not found with id: 99",
                exception.getMessage()
        );
    }

    // =========================================================
    // GET ANSWERS BY ATTEMPT
    // =========================================================

    @Test
    void getAnswersByAttempt_shouldReturnAnswers() {

        QuizAnswer answer = new QuizAnswer();
        answer.setId(10L);
        answer.setAttempt(attempt);
        answer.setQuestion(question);
        answer.setSelectedOption(correctOption);
        answer.setCorrect(true);

        when(quizAttemptRepository.existsById(1L))
                .thenReturn(true);

        when(quizAnswerRepository.findByAttemptId(1L))
                .thenReturn(List.of(answer));

        List<QuizAnswer> result =
                quizAnswerService.getAnswersByAttempt(1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());

        verify(quizAttemptRepository).existsById(1L);
        verify(quizAnswerRepository).findByAttemptId(1L);
    }

    @Test
    void getAnswersByAttempt_shouldRejectMissingAttempt() {

        when(quizAttemptRepository.existsById(99L))
                .thenReturn(false);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> quizAnswerService.getAnswersByAttempt(99L)
                );

        assertEquals(
                "Quiz attempt not found with id: 99",
                exception.getMessage()
        );

        verify(quizAnswerRepository, never())
                .findByAttemptId(anyLong());
    }

    // =========================================================
    // GET ANSWER FOR QUESTION
    // =========================================================

    @Test
    void getAnswerForQuestion_shouldReturnAnswer() {

        QuizAnswer answer = new QuizAnswer();
        answer.setId(10L);
        answer.setAttempt(attempt);
        answer.setQuestion(question);
        answer.setSelectedOption(correctOption);
        answer.setCorrect(true);

        when(
                quizAnswerRepository.findByAttemptIdAndQuestionId(
                        1L,
                        1L
                )
        ).thenReturn(Optional.of(answer));

        QuizAnswer result =
                quizAnswerService.getAnswerForQuestion(1L, 1L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertTrue(result.getCorrect());
    }

    @Test
    void getAnswerForQuestion_shouldThrowException_whenAnswerDoesNotExist() {

        when(
                quizAnswerRepository.findByAttemptIdAndQuestionId(
                        1L,
                        1L
                )
        ).thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> quizAnswerService.getAnswerForQuestion(1L, 1L)
                );

        assertEquals(
                "Answer not found for attempt 1 and question 1",
                exception.getMessage()
        );
    }

    // =========================================================
    // COUNTS
    // =========================================================

    @Test
    void countAnswersByAttempt_shouldReturnCount() {

        when(quizAnswerRepository.countByAttemptId(1L))
                .thenReturn(5L);

        long result =
                quizAnswerService.countAnswersByAttempt(1L);

        assertEquals(5L, result);

        verify(quizAnswerRepository)
                .countByAttemptId(1L);
    }

    @Test
    void countCorrectAnswersByAttempt_shouldReturnCount() {

        when(quizAnswerRepository.countByAttemptIdAndCorrectTrue(1L))
                .thenReturn(3L);

        long result =
                quizAnswerService.countCorrectAnswersByAttempt(1L);

        assertEquals(3L, result);

        verify(quizAnswerRepository)
                .countByAttemptIdAndCorrectTrue(1L);
    }

    // =========================================================
    // DELETE ANSWER
    // =========================================================

    @Test
    void deleteAnswer_shouldDeleteAnswer_whenAttemptIsIncomplete() {

        QuizAnswer answer = new QuizAnswer();
        answer.setId(10L);
        answer.setAttempt(attempt);
        answer.setQuestion(question);
        answer.setSelectedOption(correctOption);
        answer.setCorrect(true);

        when(quizAnswerRepository.findById(10L))
                .thenReturn(Optional.of(answer));

        quizAnswerService.deleteAnswer(10L);

        verify(quizAnswerRepository).findById(10L);
        verify(quizAnswerRepository).delete(answer);
    }

    @Test
    void deleteAnswer_shouldRejectCompletedAttempt() {

        QuizAnswer answer = new QuizAnswer();
        answer.setId(10L);
        answer.setAttempt(completedAttempt);
        answer.setQuestion(question);
        answer.setSelectedOption(correctOption);
        answer.setCorrect(true);

        when(quizAnswerRepository.findById(10L))
                .thenReturn(Optional.of(answer));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizAnswerService.deleteAnswer(10L)
                );

        assertEquals(
                "Cannot delete an answer from a completed quiz attempt",
                exception.getMessage()
        );

        verify(quizAnswerRepository, never())
                .delete(any(QuizAnswer.class));
    }

    // =========================================================
    // SECURITY HELPER
    // =========================================================

    private void authenticateAs(User authenticatedUser) {

        CustomUserDetails userDetails =
                new CustomUserDetails(
                        authenticatedUser.getId(),
                        authenticatedUser.getEmail(),
                        authenticatedUser.getPassword(),
                        List.of()
                );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }
}