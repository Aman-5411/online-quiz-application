package com.quizapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAnswer;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;

import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuestionType;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;

import com.quizapp.exception.ResourceNotFoundException;

import com.quizapp.repository.QuizAnswerRepository;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.UserRepository;

import com.quizapp.security.CustomUserDetails;
import com.quizapp.service.impl.QuizAttemptServiceImpl;

@ExtendWith(MockitoExtension.class)
class QuizAttemptServiceTest {

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private QuizAnswerRepository quizAnswerRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuizAttemptServiceImpl quizAttemptService;

    private User user;
    private User otherUser;
    private Quiz quiz;
    private QuizAttempt attempt;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");

        quiz = new Quiz();
        quiz.setId(10L);
        quiz.setTitle("Java Basics");
        quiz.setCategory("Java");
        quiz.setDifficulty(Difficulty.EASY);
        quiz.setSource(QuizSource.MANUAL);
        quiz.setStatus(QuizStatus.PUBLISHED);

        addQuestionToQuiz();

        attempt = new QuizAttempt();
        attempt.setId(100L);
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setTotalQuestions(2);
        attempt.setScore(0);
        attempt.setCorrectAnswers(0);
        attempt.setPercentage(0.0);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================
    // AUTHENTICATION HELPERS
    // =========================================================

    private void authenticateUser(Long userId) {

        CustomUserDetails userDetails =
                new CustomUserDetails(
                        userId,
                        "test@example.com",
                        "password",
                        List.of(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )
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

    private void authenticateAdmin(Long userId) {

        CustomUserDetails userDetails =
                new CustomUserDetails(
                        userId,
                        "admin@example.com",
                        "password",
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
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

    private void addQuestionToQuiz() {

        Question question1 = new Question();
        question1.setQuestionText("What is Java?");
        question1.setQuestionOrder(1);
        question1.setDifficulty(Difficulty.EASY);
        question1.setQuestionType(QuestionType.SINGLE_CHOICE);

        Question question2 = new Question();
        question2.setQuestionText("What is JVM?");
        question2.setQuestionOrder(2);
        question2.setDifficulty(Difficulty.EASY);
        question2.setQuestionType(QuestionType.SINGLE_CHOICE);

        quiz.addQuestion(question1);
        quiz.addQuestion(question2);
    }

    // =========================================================
    // START ATTEMPT
    // =========================================================

    @Test
    void startAttempt_shouldCreateAttemptForAuthenticatedUser() {

        authenticateUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(quizRepository.findById(10L))
                .thenReturn(Optional.of(quiz));

        when(quizAttemptRepository.save(any(QuizAttempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        QuizAttempt result =
                quizAttemptService.startAttempt(1L, 10L);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(quiz, result.getQuiz());
        assertEquals(2, result.getTotalQuestions());
        assertEquals(0, result.getScore());
        assertEquals(0, result.getCorrectAnswers());
        assertEquals(0.0, result.getPercentage());
        assertNotNull(result.getStartedAt());

        verify(quizAttemptRepository).save(any(QuizAttempt.class));
    }

    @Test
    void startAttempt_shouldRejectUnauthenticatedUser() {

        SecurityContextHolder.clearContext();

        SecurityException exception =
                assertThrows(
                        SecurityException.class,
                        () -> quizAttemptService.startAttempt(1L, 10L)
                );

        assertEquals(
                "User is not authenticated",
                exception.getMessage()
        );

        verifyNoInteractions(userRepository);
        verifyNoInteractions(quizRepository);
        verifyNoInteractions(quizAttemptRepository);
    }

    @Test
    void startAttempt_shouldRejectAnotherUsersAttempt() {

        authenticateUser(1L);

        SecurityException exception =
                assertThrows(
                        SecurityException.class,
                        () -> quizAttemptService.startAttempt(2L, 10L)
                );

        assertEquals(
                "You cannot start a quiz attempt for another user",
                exception.getMessage()
        );

        verifyNoInteractions(userRepository);
        verifyNoInteractions(quizRepository);
        verifyNoInteractions(quizAttemptRepository);
    }

    @Test
    void startAttempt_shouldRejectMissingAuthenticatedUser() {

        authenticateUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> quizAttemptService.startAttempt(1L, 10L)
                );

        assertEquals(
                "Authenticated user not found",
                exception.getMessage()
        );
    }

    @Test
    void startAttempt_shouldRejectMissingQuiz() {

        authenticateUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(quizRepository.findById(10L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> quizAttemptService.startAttempt(1L, 10L)
                );

        assertEquals(
                "Quiz not found with id: 10",
                exception.getMessage()
        );
    }

    @Test
    void startAttempt_shouldRejectUnpublishedQuiz() {

        authenticateUser(1L);

        quiz.setStatus(QuizStatus.DRAFT);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(quizRepository.findById(10L))
                .thenReturn(Optional.of(quiz));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizAttemptService.startAttempt(1L, 10L)
                );

        assertEquals(
                "Only published quizzes can be attempted",
                exception.getMessage()
        );

        verify(quizAttemptRepository, never())
                .save(any());
    }

    @Test
    void startAttempt_shouldRejectQuizWithoutQuestions() {

        authenticateUser(1L);

        quiz.getQuestions().clear();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(quizRepository.findById(10L))
                .thenReturn(Optional.of(quiz));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizAttemptService.startAttempt(1L, 10L)
                );

        assertEquals(
                "Quiz does not contain any questions",
                exception.getMessage()
        );

        verify(quizAttemptRepository, never())
                .save(any());
    }

    // =========================================================
    // GET ATTEMPT BY ID
    // =========================================================

    @Test
    void getAttemptById_shouldReturnOwnedAttempt() {

        authenticateUser(1L);

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        QuizAttempt result =
                quizAttemptService.getAttemptById(100L);

        assertSame(attempt, result);

        verify(quizAttemptRepository)
                .findById(100L);
    }

    @Test
    void getAttemptById_shouldRejectMissingAttempt() {

        authenticateUser(1L);

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> quizAttemptService.getAttemptById(100L)
                );

        assertEquals(
                "Quiz attempt not found with id: 100",
                exception.getMessage()
        );
    }

    @Test
    void getAttemptById_shouldRejectAnotherUsersAttempt() {

        authenticateUser(1L);

        attempt.setUser(otherUser);

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        SecurityException exception =
                assertThrows(
                        SecurityException.class,
                        () -> quizAttemptService.getAttemptById(100L)
                );

        assertEquals(
                "You are not allowed to access another user's quiz attempts",
                exception.getMessage()
        );
    }

    @Test
    void getAttemptById_shouldAllowAdminToAccessAnotherUsersAttempt() {

        authenticateAdmin(1L);

        attempt.setUser(otherUser);

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        QuizAttempt result =
                quizAttemptService.getAttemptById(100L);

        assertSame(attempt, result);
    }

    // =========================================================
    // GET ATTEMPTS BY USER
    // =========================================================

    @Test
    void getAttemptsByUser_shouldReturnOwnAttempts() {

        authenticateUser(1L);

        when(
                quizAttemptRepository
                        .findByUserIdOrderByStartedAtDesc(1L)
        ).thenReturn(List.of(attempt));

        List<QuizAttempt> result =
                quizAttemptService.getAttemptsByUser(1L);

        assertEquals(1, result.size());
        assertSame(attempt, result.get(0));

        verify(quizAttemptRepository)
                .findByUserIdOrderByStartedAtDesc(1L);
    }

    @Test
    void getAttemptsByUser_shouldRejectAnotherUser() {

        authenticateUser(1L);

        SecurityException exception =
                assertThrows(
                        SecurityException.class,
                        () -> quizAttemptService.getAttemptsByUser(2L)
                );

        assertEquals(
                "You are not allowed to access another user's quiz attempts",
                exception.getMessage()
        );

        verify(
                quizAttemptRepository,
                never()
        ).findByUserIdOrderByStartedAtDesc(anyLong());
    }

    // =========================================================
    // GET ATTEMPTS BY QUIZ
    // =========================================================

    @Test
    void getAttemptsByQuiz_shouldAllowAdmin() {

        authenticateAdmin(1L);

        when(
                quizAttemptRepository
                        .findByQuizIdOrderByStartedAtDesc(10L)
        ).thenReturn(List.of(attempt));

        List<QuizAttempt> result =
                quizAttemptService.getAttemptsByQuiz(10L);

        assertEquals(1, result.size());
        assertSame(attempt, result.get(0));

        verify(quizAttemptRepository)
                .findByQuizIdOrderByStartedAtDesc(10L);
    }

    @Test
    void getAttemptsByQuiz_shouldRejectNormalUser() {

        authenticateUser(1L);

        SecurityException exception =
                assertThrows(
                        SecurityException.class,
                        () -> quizAttemptService.getAttemptsByQuiz(10L)
                );

        assertEquals(
                "Only administrators can view all attempts for a quiz",
                exception.getMessage()
        );

        verify(
                quizAttemptRepository,
                never()
        ).findByQuizIdOrderByStartedAtDesc(anyLong());
    }

    // =========================================================
    // GET ATTEMPTS BY USER + QUIZ
    // =========================================================

    @Test
    void getAttemptsByUserAndQuiz_shouldReturnAttempts() {

        authenticateUser(1L);

        when(
                quizAttemptRepository
                        .findByUserIdAndQuizIdOrderByStartedAtDesc(
                                1L,
                                10L
                        )
        ).thenReturn(List.of(attempt));

        List<QuizAttempt> result =
                quizAttemptService.getAttemptsByUserAndQuiz(
                        1L,
                        10L
                );

        assertEquals(1, result.size());
        assertSame(attempt, result.get(0));

        verify(
                quizAttemptRepository
        ).findByUserIdAndQuizIdOrderByStartedAtDesc(
                1L,
                10L
        );
    }

    // =========================================================
    // GET ALL ATTEMPTS
    // =========================================================

    @Test
    void getAllAttempts_shouldReturnAllAttempts() {

        when(
                quizAttemptRepository
                        .findAllByOrderByStartedAtDesc()
        ).thenReturn(List.of(attempt));

        List<QuizAttempt> result =
                quizAttemptService.getAllAttempts();

        assertEquals(1, result.size());
        assertSame(attempt, result.get(0));

        verify(quizAttemptRepository)
                .findAllByOrderByStartedAtDesc();
    }

    // =========================================================
    // SUBMIT ATTEMPT
    // =========================================================

    @Test
    void submitAttempt_shouldCalculateScoreAndPercentage() {

        authenticateUser(1L);

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        when(
                quizAnswerRepository
                        .countByAttemptId(100L)
        ).thenReturn(2L);

        when(
                quizAnswerRepository
                        .countByAttemptIdAndCorrectTrue(100L)
        ).thenReturn(1L);

        when(quizAttemptRepository.save(attempt))
                .thenReturn(attempt);

        QuizAttempt result =
                quizAttemptService.submitAttempt(100L);

        assertEquals(1, result.getScore());
        assertEquals(1, result.getCorrectAnswers());
        assertEquals(50.0, result.getPercentage());
        assertNotNull(result.getCompletedAt());

        verify(quizAttemptRepository)
                .save(attempt);
    }

    @Test
    void submitAttempt_shouldCalculate100PercentForAllCorrectAnswers() {

        authenticateUser(1L);

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        when(
                quizAnswerRepository
                        .countByAttemptIdAndCorrectTrue(100L)
        ).thenReturn(2L);

        when(quizAttemptRepository.save(attempt))
                .thenReturn(attempt);

        QuizAttempt result =
                quizAttemptService.submitAttempt(100L);

        assertEquals(2, result.getScore());
        assertEquals(2, result.getCorrectAnswers());
        assertEquals(100.0, result.getPercentage());
        assertNotNull(result.getCompletedAt());
    }

    @Test
    void submitAttempt_shouldReturnExistingAttemptIfAlreadyCompleted() {

        authenticateUser(1L);

        attempt.setCompletedAt(
                java.time.LocalDateTime.now()
        );

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        QuizAttempt result =
                quizAttemptService.submitAttempt(100L);

        assertSame(attempt, result);

        verify(
                quizAnswerRepository,
                never()
        ).countByAttemptIdAndCorrectTrue(anyLong());

        verify(
                quizAttemptRepository,
                never()
        ).save(any());
    }

    @Test
    void submitAttempt_shouldRejectAnotherUsersAttempt() {

        authenticateUser(1L);

        attempt.setUser(otherUser);

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        SecurityException exception =
                assertThrows(
                        SecurityException.class,
                        () -> quizAttemptService.submitAttempt(100L)
                );

        assertEquals(
                "You are not allowed to access another user's quiz attempts",
                exception.getMessage()
        );

        verify(
                quizAttemptRepository,
                never()
        ).save(any());
    }

    // =========================================================
    // AUTO SUBMIT
    // =========================================================

    @Test
    void autoSubmitAttempt_shouldCalculateScoreAndPercentage() {

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        when(
                quizAnswerRepository
                        .countByAttemptIdAndCorrectTrue(100L)
        ).thenReturn(1L);

        when(quizAttemptRepository.save(attempt))
                .thenReturn(attempt);

        QuizAttempt result =
                quizAttemptService.autoSubmitAttempt(100L);

        assertEquals(1, result.getScore());
        assertEquals(1, result.getCorrectAnswers());
        assertEquals(50.0, result.getPercentage());
        assertNotNull(result.getCompletedAt());

        verify(quizAttemptRepository)
                .save(attempt);
    }

    @Test
    void autoSubmitAttempt_shouldReturnExistingCompletedAttempt() {

        attempt.setCompletedAt(
                java.time.LocalDateTime.now()
        );

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        QuizAttempt result =
                quizAttemptService.autoSubmitAttempt(100L);

        assertSame(attempt, result);

        verify(
                quizAnswerRepository,
                never()
        ).countByAttemptIdAndCorrectTrue(anyLong());

        verify(
                quizAttemptRepository,
                never()
        ).save(any());
    }

    @Test
    void autoSubmitAttempt_shouldRejectMissingAttempt() {

        when(quizAttemptRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> quizAttemptService.autoSubmitAttempt(999L)
                );

        assertEquals(
                "Quiz attempt not found with id: 999",
                exception.getMessage()
        );
    }

    // =========================================================
    // DELETE ATTEMPT
    // =========================================================

    @Test
    void deleteAttempt_shouldDeleteIncompleteAttempt() {

        authenticateUser(1L);

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        quizAttemptService.deleteAttempt(100L);

        verify(quizAttemptRepository)
                .delete(attempt);
    }

    @Test
    void deleteAttempt_shouldRejectCompletedAttempt() {

        authenticateUser(1L);

        attempt.setCompletedAt(
                java.time.LocalDateTime.now()
        );

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizAttemptService.deleteAttempt(100L)
                );

        assertEquals(
                "Completed quiz attempt cannot be deleted",
                exception.getMessage()
        );

        verify(
                quizAttemptRepository,
                never()
        ).delete(any());
    }

    @Test
    void deleteAttempt_shouldRejectAnotherUsersAttempt() {

        authenticateUser(1L);

        attempt.setUser(otherUser);

        when(quizAttemptRepository.findById(100L))
                .thenReturn(Optional.of(attempt));

        SecurityException exception =
                assertThrows(
                        SecurityException.class,
                        () -> quizAttemptService.deleteAttempt(100L)
                );

        assertEquals(
                "You are not allowed to access another user's quiz attempts",
                exception.getMessage()
        );

        verify(
                quizAttemptRepository,
                never()
        ).delete(any());
    }
}