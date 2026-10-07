package com.quizapp.repository;

import com.quizapp.entity.*;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuestionType;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;
import com.quizapp.enums.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class QuizAnswerRepositoryTest {

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Quiz quiz;
    private QuizAttempt attempt;
    private Question question1;
    private Question question2;

    @BeforeEach
    void setUp() {
        quizAnswerRepository.deleteAll();
        quizAttemptRepository.deleteAll();
        optionRepository.deleteAll();
        questionRepository.deleteAll();
        quizRepository.deleteAll();
        userRepository.deleteAll();

        user = createUser();
        quiz = createQuiz();
        attempt = createAttempt();

        question1 = createQuestion("What is Java?");
        question2 = createQuestion("What is Spring Boot?");
    }

    private User createUser() {
        User user = new User();

        user.setName("Test User");
        user.setEmail("quiz-answer-test@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    private Quiz createQuiz() {
        Quiz quiz = new Quiz();

        quiz.setTitle("Repository Test Quiz");
        quiz.setCategory("Programming");
        quiz.setDifficulty(Difficulty.EASY);
        quiz.setStatus(QuizStatus.PUBLISHED);
        quiz.setSource(QuizSource.MANUAL);
        quiz.setCreatedBy(user);

        return quizRepository.save(quiz);
    }

    private QuizAttempt createAttempt() {
        QuizAttempt attempt = new QuizAttempt();

        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setStartedAt(LocalDateTime.now().minusMinutes(10));
        attempt.setCompletedAt(null);

        attempt.setTotalQuestions(2);
        attempt.setCorrectAnswers(1);
        attempt.setScore(1);
        attempt.setPercentage(50.0);

        return quizAttemptRepository.save(attempt);
    }

    private Question createQuestion(String text) {
        Question question = new Question();

        question.setQuiz(quiz);
        question.setQuestionText(text);
        question.setQuestionOrder(
                (int) questionRepository.countByQuizId(quiz.getId()) + 1
        );
        question.setDifficulty(Difficulty.EASY);
        question.setQuestionType(QuestionType.SINGLE_CHOICE);

        return questionRepository.save(question);
    }

    private QuizAnswer createAnswer(
            Question question,
            boolean correct) {

        Option option = new Option();

        option.setQuestion(question);
        option.setOptionText(correct ? "Correct Answer" : "Wrong Answer");
        option.setCorrect(correct);

        option = optionRepository.save(option);

        QuizAnswer answer = new QuizAnswer();

        answer.setAttempt(attempt);
        answer.setQuestion(question);
        answer.setSelectedOption(option);
        answer.setCorrect(correct);

        return quizAnswerRepository.save(answer);
    }

    @Test
    void findByAttemptId_shouldReturnAnswersForAttempt() {

        createAnswer(question1, true);
        createAnswer(question2, false);

        List<QuizAnswer> answers =
                quizAnswerRepository.findByAttemptId(attempt.getId());

        assertThat(answers).hasSize(2);

        assertThat(answers)
                .allMatch(answer ->
                        answer.getAttempt().getId().equals(attempt.getId()));
    }

    @Test
    void findByAttemptIdAndQuestionId_shouldReturnMatchingAnswer() {

        QuizAnswer answer = createAnswer(question1, true);
        createAnswer(question2, false);

        Optional<QuizAnswer> result =
                quizAnswerRepository.findByAttemptIdAndQuestionId(
                        attempt.getId(),
                        question1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(answer.getId());
        assertThat(result.get().getQuestion().getId())
                .isEqualTo(question1.getId());
        assertThat(result.get().getAttempt().getId())
                .isEqualTo(attempt.getId());
    }

    @Test
    void findByAttemptIdAndQuestionId_shouldReturnEmptyWhenNotFound() {

        createAnswer(question1, true);

        Optional<QuizAnswer> result =
                quizAnswerRepository.findByAttemptIdAndQuestionId(
                        attempt.getId(),
                        question2.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void countByAttemptId_shouldReturnCorrectCount() {

        createAnswer(question1, true);
        createAnswer(question2, false);

        long count =
                quizAnswerRepository.countByAttemptId(attempt.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    void countByAttemptIdAndCorrectTrue_shouldReturnCorrectAnswerCount() {

        createAnswer(question1, true);
        createAnswer(question2, false);

        long count =
                quizAnswerRepository.countByAttemptIdAndCorrectTrue(
                        attempt.getId());

        assertThat(count).isEqualTo(1);
    }
}