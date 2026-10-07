package com.quizapp.service;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;
import com.quizapp.enums.Role;
import com.quizapp.exception.ResourceNotFoundException;
import com.quizapp.repository.QuizAnswerRepository;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.UserRepository;
import com.quizapp.security.CustomUserDetails;
import com.quizapp.service.impl.QuizAttemptServiceImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class QuizAttemptOwnershipSecurityTest {

    @Autowired
    private QuizAttemptServiceImpl quizAttemptService;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    private User userA;
    private User userB;
    private Quiz quiz;

    private QuizAttempt attemptA;
    private QuizAttempt attemptB;

    @BeforeEach
    void setUp() {

        userA = userRepository.findByEmail(
                "ownership-user-a@test.com"
        ).orElseGet(() ->
                createUser(
                        "ownership-user-a@test.com",
                        "User A",
                        Role.USER
                )
        );

        userB = userRepository.findByEmail(
                "ownership-user-b@test.com"
        ).orElseGet(() ->
                createUser(
                        "ownership-user-b@test.com",
                        "User B",
                        Role.USER
                )
        );

        quiz = createQuiz();

        attemptA = createAttempt(userA);
        attemptB = createAttempt(userB);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private User createUser(
            String email,
            String name,
            Role role) {

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(role);

        return userRepository.save(user);
    }

    private Quiz createQuiz() {

        Quiz quiz = new Quiz();

        quiz.setTitle("Ownership Security Test Quiz");
        quiz.setCategory("Testing");
        quiz.setDifficulty(Difficulty.EASY);
        quiz.setStatus(QuizStatus.PUBLISHED);
        quiz.setSource(QuizSource.MANUAL);
        quiz.setCreatedBy(userA);

        return quizRepository.save(quiz);
    }

    private QuizAttempt createAttempt(User user) {

        QuizAttempt attempt = new QuizAttempt();

        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setStartedAt(
                LocalDateTime.now().minusMinutes(5)
        );

        attempt.setTotalQuestions(10);
        attempt.setCorrectAnswers(0);
        attempt.setScore(0);
        attempt.setPercentage(0.0);

        return quizAttemptRepository.save(attempt);
    }

    private void authenticateAs(User user) {

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

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @Test
    void userShouldAccessOwnAttempt() {

        authenticateAs(userA);

        QuizAttempt result =
                quizAttemptService.getAttemptById(
                        attemptA.getId()
                );

        assertThat(result).isNotNull();
        assertThat(result.getId())
                .isEqualTo(attemptA.getId());
        assertThat(result.getUser().getId())
                .isEqualTo(userA.getId());
    }

    @Test
    void userShouldNotAccessAnotherUsersAttempt() {

        authenticateAs(userA);

        assertThatThrownBy(() ->
                quizAttemptService.getAttemptById(
                        attemptB.getId()
                )
        )
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining(
                        "not allowed to access another user's quiz attempts"
                );
    }

    @Test
    void userShouldAccessOwnAttemptHistory() {

        authenticateAs(userA);

        List<QuizAttempt> attempts =
                quizAttemptService.getAttemptsByUser(
                        userA.getId()
                );

        assertThat(attempts)
                .allMatch(attempt ->
                        attempt.getUser().getId()
                                .equals(userA.getId()));
    }

    @Test
    void userShouldNotAccessAnotherUsersAttemptHistory() {

        authenticateAs(userA);

        assertThatThrownBy(() ->
                quizAttemptService.getAttemptsByUser(
                        userB.getId()
                )
        )
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining(
                        "not allowed to access another user's quiz attempts"
                );
    }

    @Test
    void userShouldAccessOwnQuizAttemptHistory() {

        authenticateAs(userA);

        List<QuizAttempt> attempts =
                quizAttemptService.getAttemptsByUserAndQuiz(
                        userA.getId(),
                        quiz.getId()
                );

        assertThat(attempts)
                .allMatch(attempt ->
                        attempt.getUser().getId()
                                .equals(userA.getId())
                                && attempt.getQuiz().getId()
                                .equals(quiz.getId()));
    }

    @Test
    void userShouldNotAccessAnotherUsersQuizAttemptHistory() {

        authenticateAs(userA);

        assertThatThrownBy(() ->
                quizAttemptService.getAttemptsByUserAndQuiz(
                        userB.getId(),
                        quiz.getId()
                )
        )
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining(
                        "not allowed to access another user's quiz attempts"
                );
    }

    @Test
    void userShouldNotSubmitAnotherUsersAttempt() {

        authenticateAs(userA);

        assertThatThrownBy(() ->
                quizAttemptService.submitAttempt(
                        attemptB.getId()
                )
        )
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining(
                        "not allowed to access another user's quiz attempts"
                );
    }

    @Test
    void userShouldNotDeleteAnotherUsersAttempt() {

        authenticateAs(userA);

        assertThatThrownBy(() ->
                quizAttemptService.deleteAttempt(
                        attemptB.getId()
                )
        )
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining(
                        "not allowed to access another user's quiz attempts"
                );
    }

    @Test
    void userShouldDeleteOwnIncompleteAttempt() {

        authenticateAs(userA);

        quizAttemptService.deleteAttempt(
                attemptA.getId()
        );

        assertThat(
                quizAttemptRepository.findById(
                        attemptA.getId()
                )
        ).isEmpty();
    }

    @Test
    void userShouldNotAccessAnotherUsersAttemptsByQuiz() {

        authenticateAs(userA);

        assertThatThrownBy(() ->
                quizAttemptService.getAttemptsByQuiz(
                        quiz.getId()
                )
        )
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining(
                        "Only administrators can view all attempts for a quiz"
                );
    }
}