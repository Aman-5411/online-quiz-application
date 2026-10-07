package com.quizapp.repository;

import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuestionType;
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
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Quiz quiz1;
    private Quiz quiz2;

    @BeforeEach
    void setUp() {
        questionRepository.deleteAll();
        quizRepository.deleteAll();
        userRepository.deleteAll();

        user = createUser();

        quiz1 = createQuiz("Java Basics");
        quiz2 = createQuiz("Spring Boot Basics");
    }

    private User createUser() {
        User user = new User();

        user.setName("Question Test User");
        user.setEmail("question-test@test.com");
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
        quiz.setCreatedBy(user);

        return quizRepository.save(quiz);
    }

    private Question createQuestion(
            Quiz quiz,
            String text,
            int questionOrder,
            Difficulty difficulty) {

        Question question = new Question();

        question.setQuiz(quiz);
        question.setQuestionText(text);
        question.setQuestionOrder(questionOrder);
        question.setDifficulty(difficulty);
        question.setQuestionType(QuestionType.SINGLE_CHOICE);

        return questionRepository.save(question);
    }

    @Test
    void findByQuizIdOrderByQuestionOrderAsc_shouldReturnQuestionsInOrder() {

        createQuestion(
                quiz1,
                "Question 3",
                3,
                Difficulty.HARD);

        createQuestion(
                quiz1,
                "Question 1",
                1,
                Difficulty.EASY);

        createQuestion(
                quiz1,
                "Question 2",
                2,
                Difficulty.MEDIUM);

        createQuestion(
                quiz2,
                "Other Quiz Question",
                1,
                Difficulty.EASY);

        List<Question> questions =
                questionRepository.findByQuizIdOrderByQuestionOrderAsc(
                        quiz1.getId());

        assertThat(questions).hasSize(3);

        assertThat(questions.get(0).getQuestionOrder())
                .isEqualTo(1);

        assertThat(questions.get(1).getQuestionOrder())
                .isEqualTo(2);

        assertThat(questions.get(2).getQuestionOrder())
                .isEqualTo(3);

        assertThat(questions)
                .allMatch(question ->
                        question.getQuiz().getId().equals(quiz1.getId()));
    }

    @Test
    void findByQuizId_shouldReturnOnlyQuestionsForQuiz() {

        createQuestion(
                quiz1,
                "Java Question 1",
                1,
                Difficulty.EASY);

        createQuestion(
                quiz1,
                "Java Question 2",
                2,
                Difficulty.MEDIUM);

        createQuestion(
                quiz2,
                "Spring Question",
                1,
                Difficulty.HARD);

        List<Question> questions =
                questionRepository.findByQuizId(quiz1.getId());

        assertThat(questions).hasSize(2);

        assertThat(questions)
                .allMatch(question ->
                        question.getQuiz().getId().equals(quiz1.getId()));
    }

    @Test
    void findByDifficulty_shouldReturnQuestionsWithMatchingDifficulty() {

        createQuestion(
                quiz1,
                "Easy Java Question",
                1,
                Difficulty.EASY);

        createQuestion(
                quiz1,
                "Hard Java Question",
                2,
                Difficulty.HARD);

        createQuestion(
                quiz2,
                "Easy Spring Question",
                1,
                Difficulty.EASY);

        List<Question> questions =
                questionRepository.findByDifficulty(Difficulty.EASY);

        assertThat(questions).hasSize(2);

        assertThat(questions)
                .allMatch(question ->
                        question.getDifficulty() == Difficulty.EASY);
    }

    @Test
    void countByQuizId_shouldReturnCorrectCount() {

        createQuestion(
                quiz1,
                "Question 1",
                1,
                Difficulty.EASY);

        createQuestion(
                quiz1,
                "Question 2",
                2,
                Difficulty.MEDIUM);

        createQuestion(
                quiz1,
                "Question 3",
                3,
                Difficulty.HARD);

        createQuestion(
                quiz2,
                "Other Quiz Question",
                1,
                Difficulty.EASY);

        long count = questionRepository.countByQuizId(quiz1.getId());

        assertThat(count).isEqualTo(3);
    }
}