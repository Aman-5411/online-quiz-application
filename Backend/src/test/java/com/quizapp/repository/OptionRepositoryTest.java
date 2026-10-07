package com.quizapp.repository;

import com.quizapp.entity.Option;
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
class OptionRepositoryTest {

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Quiz quiz;
    private Question question1;
    private Question question2;

    @BeforeEach
    void setUp() {
        optionRepository.deleteAll();
        questionRepository.deleteAll();
        quizRepository.deleteAll();
        userRepository.deleteAll();

        user = createUser();
        quiz = createQuiz();

        question1 = createQuestion(
                "What is Java?",
                1
        );

        question2 = createQuestion(
                "What is Spring Boot?",
                2
        );
    }

    private User createUser() {
        User user = new User();

        user.setName("Option Test User");
        user.setEmail("option-test@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    private Quiz createQuiz() {
        Quiz quiz = new Quiz();

        quiz.setTitle("Option Repository Test Quiz");
        quiz.setCategory("Programming");
        quiz.setDifficulty(Difficulty.EASY);
        quiz.setStatus(QuizStatus.PUBLISHED);
        quiz.setSource(QuizSource.MANUAL);
        quiz.setCreatedBy(user);

        return quizRepository.save(quiz);
    }

    private Question createQuestion(
            String text,
            int questionOrder) {

        Question question = new Question();

        question.setQuiz(quiz);
        question.setQuestionText(text);
        question.setQuestionOrder(questionOrder);
        question.setDifficulty(Difficulty.EASY);
        question.setQuestionType(QuestionType.SINGLE_CHOICE);

        return questionRepository.save(question);
    }

    private Option createOption(
            Question question,
            String text,
            boolean correct) {

        Option option = new Option();

        option.setQuestion(question);
        option.setOptionText(text);
        option.setCorrect(correct);

        return optionRepository.save(option);
    }

    @Test
    void findByQuestionId_shouldReturnAllOptionsForQuestion() {

        createOption(question1, "Java", true);
        createOption(question1, "Python", false);
        createOption(question1, "C++", false);

        createOption(question2, "Spring Boot", true);

        List<Option> options =
                optionRepository.findByQuestionId(question1.getId());

        assertThat(options).hasSize(3);

        assertThat(options)
                .allMatch(option ->
                        option.getQuestion().getId()
                                .equals(question1.getId()));
    }

    @Test
    void findByQuestionIdAndCorrectTrue_shouldReturnOnlyCorrectOptions() {

        createOption(question1, "Java", true);
        createOption(question1, "Python", false);
        createOption(question1, "C++", false);

        List<Option> correctOptions =
                optionRepository.findByQuestionIdAndCorrectTrue(
                        question1.getId());

        assertThat(correctOptions).hasSize(1);

        assertThat(correctOptions.get(0).isCorrect())
                .isTrue();

        assertThat(correctOptions.get(0).getOptionText())
                .isEqualTo("Java");
    }

    @Test
    void countByQuestionIdAndCorrectTrue_shouldReturnCorrectCount() {

        createOption(question1, "Java", true);
        createOption(question1, "Python", false);
        createOption(question1, "C++", false);

        createOption(question2, "Spring Boot", true);

        long count =
                optionRepository.countByQuestionIdAndCorrectTrue(
                        question1.getId());

        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByQuestionId_shouldReturnTotalOptionCount() {

        createOption(question1, "Java", true);
        createOption(question1, "Python", false);
        createOption(question1, "C++", false);

        createOption(question2, "Spring Boot", true);

        long count =
                optionRepository.countByQuestionId(question1.getId());

        assertThat(count).isEqualTo(3);
    }
}