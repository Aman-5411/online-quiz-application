package com.quizapp.repository;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;
import com.quizapp.enums.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class QuizAttemptRepositoryTest {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    private Quiz quiz1;
    private Quiz quiz2;

    @BeforeEach
    void setUp() {
        quizAttemptRepository.deleteAll();
        quizRepository.deleteAll();
        userRepository.deleteAll();

        user1 = createUser("attempt-user1@test.com");
        user2 = createUser("attempt-user2@test.com");

        quiz1 = createQuiz("Java Basics");
        quiz2 = createQuiz("Spring Boot Basics");
    }

    private User createUser(String email) {
        User user = new User();
        user.setName("Test User");
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    private Quiz createQuiz(String title) {
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setCategory("Programming");
        quiz.setDifficulty(Difficulty.EASY);
        quiz.setStatus(QuizStatus.PUBLISHED);
        quiz.setSource(QuizSource.MANUAL);
        quiz.setCreatedBy(user1);
        return quizRepository.save(quiz);
    }

    private QuizAttempt createAttempt(
            User user,
            Quiz quiz,
            LocalDateTime startedAt,
            LocalDateTime completedAt) {

        QuizAttempt attempt = new QuizAttempt();

        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setStartedAt(startedAt);
        attempt.setCompletedAt(completedAt);

        attempt.setTotalQuestions(10);
        attempt.setCorrectAnswers(8);
        attempt.setScore(8);
        attempt.setPercentage(80.0);

        return quizAttemptRepository.save(attempt);
    }

    @Test
    void findByUserIdOrderByStartedAtDesc_shouldReturnUserAttemptsNewestFirst() {

        LocalDateTime now = LocalDateTime.now();

        createAttempt(user1, quiz1, now.minusHours(3), now.minusHours(2));
        createAttempt(user1, quiz2, now.minusHours(1), now.minusMinutes(30));
        createAttempt(user2, quiz1, now.minusMinutes(10), now);

        List<QuizAttempt> attempts =
                quizAttemptRepository.findByUserIdOrderByStartedAtDesc(user1.getId());

        assertThat(attempts).hasSize(2);
        assertThat(attempts.get(0).getStartedAt())
                .isAfter(attempts.get(1).getStartedAt());
        assertThat(attempts)
                .allMatch(attempt -> attempt.getUser().getId().equals(user1.getId()));
    }

    @Test
    void findByQuizIdOrderByStartedAtDesc_shouldReturnQuizAttemptsNewestFirst() {

        LocalDateTime now = LocalDateTime.now();

        createAttempt(user1, quiz1, now.minusHours(2), now.minusHours(1));
        createAttempt(user2, quiz1, now.minusHours(1), now.minusMinutes(30));
        createAttempt(user1, quiz2, now, null);

        List<QuizAttempt> attempts =
                quizAttemptRepository.findByQuizIdOrderByStartedAtDesc(quiz1.getId());

        assertThat(attempts).hasSize(2);
        assertThat(attempts.get(0).getStartedAt())
                .isAfter(attempts.get(1).getStartedAt());
        assertThat(attempts)
                .allMatch(attempt -> attempt.getQuiz().getId().equals(quiz1.getId()));
    }

    @Test
    void findByUserIdAndQuizIdOrderByStartedAtDesc_shouldReturnMatchingAttempts() {

        LocalDateTime now = LocalDateTime.now();

        createAttempt(user1, quiz1, now.minusHours(3), now.minusHours(2));
        createAttempt(user1, quiz1, now.minusHours(1), now.minusMinutes(30));
        createAttempt(user1, quiz2, now, null);
        createAttempt(user2, quiz1, now, null);

        List<QuizAttempt> attempts =
                quizAttemptRepository.findByUserIdAndQuizIdOrderByStartedAtDesc(
                        user1.getId(),
                        quiz1.getId());

        assertThat(attempts).hasSize(2);
        assertThat(attempts.get(0).getStartedAt())
                .isAfter(attempts.get(1).getStartedAt());
        assertThat(attempts)
                .allMatch(attempt ->
                        attempt.getUser().getId().equals(user1.getId())
                                && attempt.getQuiz().getId().equals(quiz1.getId()));
    }

    @Test
    void findByCompletedAtIsNullAndStartedAtBefore_shouldReturnExpiredIncompleteAttempts() {

        LocalDateTime now = LocalDateTime.now();

        QuizAttempt expiredIncomplete =
                createAttempt(
                        user1,
                        quiz1,
                        now.minusMinutes(30),
                        null);

        createAttempt(
                user1,
                quiz2,
                now.minusMinutes(30),
                now.minusMinutes(5));

        createAttempt(
                user2,
                quiz1,
                now.plusMinutes(10),
                null);

        List<QuizAttempt> attempts =
                quizAttemptRepository.findByCompletedAtIsNullAndStartedAtBefore(
                        now);

        assertThat(attempts).hasSize(1);
        assertThat(attempts.get(0).getId())
                .isEqualTo(expiredIncomplete.getId());
        assertThat(attempts.get(0).getCompletedAt())
                .isNull();
        assertThat(attempts.get(0).getStartedAt())
                .isBefore(now);
    }

    @Test
    void findAllByOrderByStartedAtDesc_shouldReturnAllAttemptsNewestFirst() {

        LocalDateTime now = LocalDateTime.now();

        createAttempt(user1, quiz1, now.minusHours(3), now.minusHours(2));
        createAttempt(user2, quiz2, now.minusHours(2), now.minusHours(1));
        createAttempt(user1, quiz2, now.minusHours(1), now);

        List<QuizAttempt> attempts =
                quizAttemptRepository.findAllByOrderByStartedAtDesc();

        assertThat(attempts).hasSize(3);
        assertThat(attempts.get(0).getStartedAt())
                .isAfter(attempts.get(1).getStartedAt());
        assertThat(attempts.get(1).getStartedAt())
                .isAfter(attempts.get(2).getStartedAt());
    }

    @Test
    void countByUserId_shouldReturnCorrectCount() {

        LocalDateTime now = LocalDateTime.now();

        createAttempt(user1, quiz1, now.minusHours(3), now.minusHours(2));
        createAttempt(user1, quiz2, now.minusHours(2), now.minusHours(1));
        createAttempt(user2, quiz1, now.minusHours(1), now);

        long count = quizAttemptRepository.countByUserId(user1.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    void countByQuizId_shouldReturnCorrectCount() {

        LocalDateTime now = LocalDateTime.now();

        createAttempt(user1, quiz1, now.minusHours(3), now.minusHours(2));
        createAttempt(user2, quiz1, now.minusHours(2), now.minusHours(1));
        createAttempt(user1, quiz2, now.minusHours(1), now);

        long count = quizAttemptRepository.countByQuizId(quiz1.getId());

        assertThat(count).isEqualTo(2);
    }
}