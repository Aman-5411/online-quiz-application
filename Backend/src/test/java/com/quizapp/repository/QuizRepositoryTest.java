package com.quizapp.repository;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;

import com.quizapp.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class QuizRepositoryTest {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        quizRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("Test User");
        user.setEmail("quizrepo@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        user = userRepository.save(user);
    }

    private Quiz createQuiz(
            String title,
            String category,
            Difficulty difficulty,
            QuizStatus status,
            QuizSource source
    ) {

        Quiz quiz = new Quiz();

        quiz.setTitle(title);
        quiz.setCategory(category);
        quiz.setDifficulty(difficulty);
        quiz.setStatus(status);
        quiz.setSource(source);
        quiz.setCreatedBy(user);

        return quizRepository.save(quiz);
    }

    @Test
    void findByStatus_shouldReturnMatchingQuizzes() {

        createQuiz(
                "Published Java",
                "Java",
                Difficulty.EASY,
                QuizStatus.PUBLISHED,
                QuizSource.MANUAL
        );

        createQuiz(
                "Draft Java",
                "Java",
                Difficulty.MEDIUM,
                QuizStatus.DRAFT,
                QuizSource.MANUAL
        );

        List<Quiz> result =
                quizRepository.findByStatus(QuizStatus.PUBLISHED);

        assertThat(result)
                .hasSize(1)
                .extracting(Quiz::getTitle)
                .containsExactly("Published Java");
    }

    @Test
    void findByCategory_shouldReturnMatchingQuizzes() {

        createQuiz(
                "Java Quiz",
                "Java",
                Difficulty.EASY,
                QuizStatus.PUBLISHED,
                QuizSource.MANUAL
        );

        createQuiz(
                "SQL Quiz",
                "SQL",
                Difficulty.EASY,
                QuizStatus.PUBLISHED,
                QuizSource.MANUAL
        );

        List<Quiz> result =
                quizRepository.findByCategory("Java");

        assertThat(result)
                .hasSize(1)
                .extracting(Quiz::getTitle)
                .containsExactly("Java Quiz");
    }

    @Test
    void findByDifficulty_shouldReturnMatchingQuizzes() {

        createQuiz(
                "Easy Quiz",
                "Java",
                Difficulty.EASY,
                QuizStatus.PUBLISHED,
                QuizSource.MANUAL
        );

        createQuiz(
                "Hard Quiz",
                "Java",
                Difficulty.HARD,
                QuizStatus.PUBLISHED,
                QuizSource.MANUAL
        );

        List<Quiz> result =
                quizRepository.findByDifficulty(Difficulty.EASY);

        assertThat(result)
                .hasSize(1)
                .extracting(Quiz::getTitle)
                .containsExactly("Easy Quiz");
    }

    @Test
    void findBySource_shouldReturnMatchingQuizzes() {

        createQuiz(
                "Manual Quiz",
                "Java",
                Difficulty.EASY,
                QuizStatus.PUBLISHED,
                QuizSource.MANUAL
        );

        createQuiz(
                "AI Quiz",
                "Java",
                Difficulty.EASY,
                QuizStatus.PUBLISHED,
                QuizSource.AI_GENERATED
        );

        List<Quiz> result =
                quizRepository.findBySource(QuizSource.MANUAL);

        assertThat(result)
                .hasSize(1)
                .extracting(Quiz::getTitle)
                .containsExactly("Manual Quiz");
    }

    @Test
    void findByCreatedById_shouldReturnQuizzesCreatedByUser() {

        createQuiz(
                "User Quiz 1",
                "Java",
                Difficulty.EASY,
                QuizStatus.DRAFT,
                QuizSource.MANUAL
        );

        createQuiz(
                "User Quiz 2",
                "SQL",
                Difficulty.MEDIUM,
                QuizStatus.PUBLISHED,
                QuizSource.MANUAL
        );

        List<Quiz> result =
                quizRepository.findByCreatedById(user.getId());

        assertThat(result)
                .hasSize(2)
                .extracting(Quiz::getTitle)
                .containsExactlyInAnyOrder(
                        "User Quiz 1",
                        "User Quiz 2"
                );
    }

    @Test
    void findByStatusAndCategory_shouldReturnMatchingQuizzes() {

        createQuiz(
                "Published Java",
                "Java",
                Difficulty.EASY,
                QuizStatus.PUBLISHED,
                QuizSource.MANUAL
        );

        createQuiz(
                "Draft Java",
                "Java",
                Difficulty.EASY,
                QuizStatus.DRAFT,
                QuizSource.MANUAL
        );

        createQuiz(
                "Published SQL",
                "SQL",
                Difficulty.EASY,
                QuizStatus.PUBLISHED,
                QuizSource.MANUAL
        );

        List<Quiz> result =
                quizRepository.findByStatusAndCategory(
                        QuizStatus.PUBLISHED,
                        "Java"
                );

        assertThat(result)
                .hasSize(1)
                .extracting(Quiz::getTitle)
                .containsExactly("Published Java");
    }

    @Test
    void findByStatusAndDifficulty_shouldReturnMatchingQuizzes() {

        createQuiz(
                "Published Easy",
                "Java",
                Difficulty.EASY,
                QuizStatus.PUBLISHED,
                QuizSource.MANUAL
        );

        createQuiz(
                "Published Hard",
                "Java",
                Difficulty.HARD,
                QuizStatus.PUBLISHED,
                QuizSource.MANUAL
        );

        createQuiz(
                "Draft Easy",
                "Java",
                Difficulty.EASY,
                QuizStatus.DRAFT,
                QuizSource.MANUAL
        );

        List<Quiz> result =
                quizRepository.findByStatusAndDifficulty(
                        QuizStatus.PUBLISHED,
                        Difficulty.EASY
                );

        assertThat(result)
                .hasSize(1)
                .extracting(Quiz::getTitle)
                .containsExactly("Published Easy");
    }
}