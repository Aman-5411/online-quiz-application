package com.quizapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.quizapp.dto.QuestionResponse;
import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuestionType;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;
import com.quizapp.repository.OptionRepository;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.service.impl.QuestionServiceImpl;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private Quiz draftQuiz;
    private Quiz publishedQuiz;
    private Question question;
    private Question secondQuestion;
    private Option correctOption;
    private Option wrongOption;

    @BeforeEach
    void setUp() {

        draftQuiz = new Quiz();
        draftQuiz.setId(1L);
        draftQuiz.setTitle("Java Basics");
        draftQuiz.setCategory("Java");
        draftQuiz.setDifficulty(Difficulty.EASY);
        draftQuiz.setSource(QuizSource.MANUAL);
        draftQuiz.setStatus(QuizStatus.DRAFT);

        publishedQuiz = new Quiz();
        publishedQuiz.setId(2L);
        publishedQuiz.setTitle("Published Java");
        publishedQuiz.setCategory("Java");
        publishedQuiz.setDifficulty(Difficulty.EASY);
        publishedQuiz.setSource(QuizSource.MANUAL);
        publishedQuiz.setStatus(QuizStatus.PUBLISHED);

        question = new Question();
        question.setId(1L);
        question.setQuestionText("Which keyword is used to inherit a class?");
        question.setQuestionType(QuestionType.SINGLE_CHOICE);
        question.setDifficulty(Difficulty.EASY);
        question.setQuestionOrder(1);
        question.setExplanation("The extends keyword is used for class inheritance.");
        question.setQuiz(draftQuiz);

        secondQuestion = new Question();
        secondQuestion.setId(2L);
        secondQuestion.setQuestionText("Which keyword creates an object?");
        secondQuestion.setQuestionType(QuestionType.SINGLE_CHOICE);
        secondQuestion.setDifficulty(Difficulty.EASY);
        secondQuestion.setQuestionOrder(2);
        secondQuestion.setQuiz(draftQuiz);

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

        question.addOption(correctOption);
        question.addOption(wrongOption);
    }

    // =========================================================
    // CREATE QUESTION
    // =========================================================

    @Test
    void createQuestion_shouldCreateValidQuestion() {

        Question newQuestion = new Question();
        newQuestion.setQuestionText("What is JVM?");
        newQuestion.setQuestionType(QuestionType.SINGLE_CHOICE);
        newQuestion.setDifficulty(Difficulty.EASY);

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(draftQuiz));

        when(questionRepository.countByQuizId(1L))
                .thenReturn(2L);

        when(questionRepository.save(newQuestion))
                .thenReturn(newQuestion);

        Question result =
                questionService.createQuestion(1L, newQuestion);

        assertNotNull(result);
        assertEquals(draftQuiz, result.getQuiz());
        assertEquals(3, result.getQuestionOrder());

        verify(quizRepository).findById(1L);
        verify(questionRepository).countByQuizId(1L);
        verify(questionRepository).save(newQuestion);
    }

    @Test
    void createQuestion_shouldKeepProvidedQuestionOrder() {

        Question newQuestion = new Question();
        newQuestion.setQuestionText("What is JVM?");
        newQuestion.setQuestionType(QuestionType.SINGLE_CHOICE);
        newQuestion.setDifficulty(Difficulty.EASY);
        newQuestion.setQuestionOrder(10);

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(draftQuiz));

        when(questionRepository.save(newQuestion))
                .thenReturn(newQuestion);

        Question result =
                questionService.createQuestion(1L, newQuestion);

        assertEquals(10, result.getQuestionOrder());

        verify(questionRepository, never())
                .countByQuizId(anyLong());

        verify(questionRepository).save(newQuestion);
    }

    @Test
    void createQuestion_shouldRejectMissingQuiz() {

        Question newQuestion = new Question();
        newQuestion.setQuestionText("What is JVM?");
        newQuestion.setQuestionType(QuestionType.SINGLE_CHOICE);
        newQuestion.setDifficulty(Difficulty.EASY);

        when(quizRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> questionService.createQuestion(
                                99L,
                                newQuestion
                        )
                );

        assertEquals(
                "Quiz not found with id: 99",
                exception.getMessage()
        );

        verifyNoInteractions(questionRepository);
    }

    @Test
    void createQuestion_shouldRejectPublishedQuiz() {

        Question newQuestion = new Question();
        newQuestion.setQuestionText("What is JVM?");
        newQuestion.setQuestionType(QuestionType.SINGLE_CHOICE);
        newQuestion.setDifficulty(Difficulty.EASY);

        when(quizRepository.findById(2L))
                .thenReturn(Optional.of(publishedQuiz));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> questionService.createQuestion(
                                2L,
                                newQuestion
                        )
                );

        assertEquals(
                "Published quiz cannot be modified",
                exception.getMessage()
        );

        verify(questionRepository, never()).save(any());
    }

    @Test
    void createQuestion_shouldRejectEmptyQuestionText() {

        Question newQuestion = new Question();
        newQuestion.setQuestionText("");
        newQuestion.setQuestionType(QuestionType.SINGLE_CHOICE);
        newQuestion.setDifficulty(Difficulty.EASY);

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(draftQuiz));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> questionService.createQuestion(
                                1L,
                                newQuestion
                        )
                );

        assertEquals(
                "Question text cannot be empty",
                exception.getMessage()
        );

        verify(questionRepository, never()).save(any());
    }

    @Test
    void createQuestion_shouldRejectMissingQuestionType() {

        Question newQuestion = new Question();
        newQuestion.setQuestionText("What is JVM?");
        newQuestion.setQuestionType(null);
        newQuestion.setDifficulty(Difficulty.EASY);

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(draftQuiz));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> questionService.createQuestion(
                                1L,
                                newQuestion
                        )
                );

        assertEquals(
                "Question type is required",
                exception.getMessage()
        );

        verify(questionRepository, never()).save(any());
    }

    @Test
    void createQuestion_shouldRejectMissingDifficulty() {

        Question newQuestion = new Question();
        newQuestion.setQuestionText("What is JVM?");
        newQuestion.setQuestionType(QuestionType.SINGLE_CHOICE);
        newQuestion.setDifficulty(null);

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(draftQuiz));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> questionService.createQuestion(
                                1L,
                                newQuestion
                        )
                );

        assertEquals(
                "Question difficulty is required",
                exception.getMessage()
        );

        verify(questionRepository, never()).save(any());
    }

    // =========================================================
    // GET QUESTION
    // =========================================================

    @Test
    void getQuestionById_shouldReturnQuestion() {

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        Question result =
                questionService.getQuestionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                "Which keyword is used to inherit a class?",
                result.getQuestionText()
        );

        verify(questionRepository).findById(1L);
    }

    @Test
    void getQuestionById_shouldThrowException_whenQuestionDoesNotExist() {

        when(questionRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> questionService.getQuestionById(99L)
                );

        assertEquals(
                "Question not found with id: 99",
                exception.getMessage()
        );
    }

    // =========================================================
    // GET QUESTIONS BY QUIZ
    // =========================================================

    @Test
    void getQuestionsByQuizId_shouldReturnQuestionsInOrder() {

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(draftQuiz));

        when(
                questionRepository.findByQuizIdOrderByQuestionOrderAsc(1L)
        ).thenReturn(List.of(question, secondQuestion));

        List<Question> result =
                questionService.getQuestionsByQuizId(1L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(quizRepository).findById(1L);
        verify(questionRepository)
                .findByQuizIdOrderByQuestionOrderAsc(1L);
    }

    @Test
    void getQuestionsByQuizId_shouldRejectMissingQuiz() {

        when(quizRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> questionService.getQuestionsByQuizId(99L)
                );

        assertEquals(
                "Quiz not found with id: 99",
                exception.getMessage()
        );

        verify(questionRepository, never())
                .findByQuizIdOrderByQuestionOrderAsc(anyLong());
    }

    // =========================================================
    // GET QUESTIONS FOR QUIZ ATTEMPT
    // =========================================================

    @Test
    void getQuestionsForQuizAttempt_shouldReturnSafeQuestionResponses() {

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(draftQuiz));

        when(
                questionRepository.findByQuizIdOrderByQuestionOrderAsc(1L)
        ).thenReturn(List.of(question));

        List<QuestionResponse> result =
                questionService.getQuestionsForQuizAttempt(1L);

        assertEquals(1, result.size());

        QuestionResponse response = result.get(0);

        assertEquals(1L, response.getId());
        assertEquals(
                "Which keyword is used to inherit a class?",
                response.getQuestionText()
        );
        assertEquals(1, response.getQuestionOrder());
        assertEquals(
                QuestionType.SINGLE_CHOICE,
                response.getQuestionType()
        );
        assertEquals(Difficulty.EASY, response.getDifficulty());

        assertEquals(2, response.getOptions().size());

        assertEquals(
                "extends",
                response.getOptions().get(0).getOptionText()
        );

        assertEquals(
                "implements",
                response.getOptions().get(1).getOptionText()
        );
    }

    @Test
    void getQuestionsForQuizAttempt_shouldRejectMissingQuiz() {

        when(quizRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> questionService
                                .getQuestionsForQuizAttempt(99L)
                );

        assertEquals(
                "Quiz not found with id: 99",
                exception.getMessage()
        );

        verify(questionRepository, never())
                .findByQuizIdOrderByQuestionOrderAsc(anyLong());
    }

    // =========================================================
    // UPDATE QUESTION
    // =========================================================

    @Test
    void updateQuestion_shouldUpdateProvidedFields() {

        Question updateRequest = new Question();
        updateRequest.setQuestionText("Updated question");
        updateRequest.setQuestionType(QuestionType.SINGLE_CHOICE);
        updateRequest.setDifficulty(Difficulty.MEDIUM);
        updateRequest.setQuestionOrder(5);
        updateRequest.setExplanation("Updated explanation");

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(questionRepository.save(question))
                .thenReturn(question);

        Question result =
                questionService.updateQuestion(1L, updateRequest);

        assertEquals(
                "Updated question",
                result.getQuestionText()
        );

        assertEquals(
                QuestionType.SINGLE_CHOICE,
                result.getQuestionType()
        );

        assertEquals(
                Difficulty.MEDIUM,
                result.getDifficulty()
        );

        assertEquals(5, result.getQuestionOrder());

        assertEquals(
                "Updated explanation",
                result.getExplanation()
        );

        verify(questionRepository).save(question);
    }

    @Test
    void updateQuestion_shouldPreserveFields_whenNotProvided() {

        question.setExplanation("Original explanation");

        Question updateRequest = new Question();

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(questionRepository.save(question))
                .thenReturn(question);

        Question result =
                questionService.updateQuestion(1L, updateRequest);

        assertEquals(
                "Which keyword is used to inherit a class?",
                result.getQuestionText()
        );

        assertEquals(
                QuestionType.SINGLE_CHOICE,
                result.getQuestionType()
        );

        assertEquals(Difficulty.EASY, result.getDifficulty());
        assertEquals(1, result.getQuestionOrder());
        assertEquals("Original explanation", result.getExplanation());

        verify(questionRepository).save(question);
    }

    @Test
    void updateQuestion_shouldRejectPublishedQuiz() {

        question.setQuiz(publishedQuiz);

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> questionService.updateQuestion(
                                1L,
                                new Question()
                        )
                );

        assertEquals(
                "Published quiz cannot be modified",
                exception.getMessage()
        );

        verify(questionRepository, never()).save(any());
    }

    // =========================================================
    // DELETE QUESTION
    // =========================================================

    @Test
    void deleteQuestion_shouldDeleteQuestion() {

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        questionService.deleteQuestion(1L);

        verify(questionRepository).delete(question);
    }

    @Test
    void deleteQuestion_shouldRejectPublishedQuiz() {

        question.setQuiz(publishedQuiz);

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> questionService.deleteQuestion(1L)
                );

        assertEquals(
                "Published quiz cannot be modified",
                exception.getMessage()
        );

        verify(questionRepository, never())
                .delete(any(Question.class));
    }

    // =========================================================
    // ADD OPTION
    // =========================================================

    @Test
    void addOption_shouldAddOptionToQuestion() {

        Option option = new Option();
        option.setOptionText("extends");
        option.setCorrect(true);

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(optionRepository.countByQuestionIdAndCorrectTrue(1L))
                .thenReturn(0L);

        when(optionRepository.save(option))
                .thenReturn(option);

        Option result =
                questionService.addOption(1L, option);

        assertNotNull(result);
        assertEquals("extends", result.getOptionText());
        assertTrue(result.isCorrect());
        assertEquals(question, result.getQuestion());

        verify(optionRepository).save(option);
    }

    @Test
    void addOption_shouldAllowIncorrectOption() {

        Option option = new Option();
        option.setOptionText("implements");
        option.setCorrect(false);

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(optionRepository.save(option))
                .thenReturn(option);

        Option result =
                questionService.addOption(1L, option);

        assertEquals("implements", result.getOptionText());
        assertFalse(result.isCorrect());
        assertEquals(question, result.getQuestion());

        verify(optionRepository).save(option);
        verify(
                optionRepository,
                never()
        ).countByQuestionIdAndCorrectTrue(anyLong());
    }

    @Test
    void addOption_shouldRejectEmptyOptionText() {

        Option option = new Option();
        option.setOptionText("");
        option.setCorrect(false);

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> questionService.addOption(1L, option)
                );

        assertEquals(
                "Option text cannot be empty",
                exception.getMessage()
        );

        verify(optionRepository, never()).save(any());
    }

    @Test
    void addOption_shouldRejectSecondCorrectOptionForSingleChoice() {

        Option option = new Option();
        option.setOptionText("another correct option");
        option.setCorrect(true);

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(optionRepository.countByQuestionIdAndCorrectTrue(1L))
                .thenReturn(1L);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> questionService.addOption(1L, option)
                );

        assertEquals(
                "A SINGLE_CHOICE question can have only one correct option",
                exception.getMessage()
        );

        verify(optionRepository, never()).save(any());
    }

    @Test
    void addOption_shouldRejectPublishedQuiz() {

        question.setQuiz(publishedQuiz);

        Option option = new Option();
        option.setOptionText("extends");
        option.setCorrect(true);

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> questionService.addOption(1L, option)
                );

        assertEquals(
                "Published quiz cannot be modified",
                exception.getMessage()
        );

        verify(optionRepository, never()).save(any());
    }

    // =========================================================
    // UPDATE OPTION
    // =========================================================

    @Test
    void updateOption_shouldUpdateOption() {

        Option updateRequest = new Option();
        updateRequest.setOptionText("super");
        updateRequest.setCorrect(false);

        when(optionRepository.findById(1L))
                .thenReturn(Optional.of(correctOption));

        when(optionRepository.save(correctOption))
                .thenReturn(correctOption);

        Option result =
                questionService.updateOption(1L, updateRequest);

        assertEquals("super", result.getOptionText());
        assertFalse(result.isCorrect());

        verify(optionRepository).save(correctOption);
    }

    @Test
    void updateOption_shouldRejectMissingOption() {

        when(optionRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> questionService.updateOption(
                                99L,
                                new Option()
                        )
                );

        assertEquals(
                "Option not found with id: 99",
                exception.getMessage()
        );
    }

    @Test
    void updateOption_shouldRejectPublishedQuiz() {

        question.setQuiz(publishedQuiz);
        correctOption.setQuestion(question);

        when(optionRepository.findById(1L))
                .thenReturn(Optional.of(correctOption));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> questionService.updateOption(
                                1L,
                                new Option()
                        )
                );

        assertEquals(
                "Published quiz cannot be modified",
                exception.getMessage()
        );

        verify(optionRepository, never()).save(any());
    }

    @Test
    void updateOption_shouldRejectSecondCorrectOption() {

        Option updateRequest = new Option();
        updateRequest.setOptionText("another");
        updateRequest.setCorrect(true);

        correctOption.setCorrect(false);

        when(optionRepository.findById(1L))
                .thenReturn(Optional.of(correctOption));

        when(optionRepository.countByQuestionIdAndCorrectTrue(1L))
                .thenReturn(1L);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> questionService.updateOption(
                                1L,
                                updateRequest
                        )
                );

        assertEquals(
                "A SINGLE_CHOICE question can have only one correct option",
                exception.getMessage()
        );

        verify(optionRepository, never())
                .save(any());
    }

    // =========================================================
    // DELETE OPTION
    // =========================================================

    @Test
    void deleteOption_shouldDeleteOption() {

        when(optionRepository.findById(1L))
                .thenReturn(Optional.of(correctOption));

        questionService.deleteOption(1L);

        verify(optionRepository).delete(correctOption);
    }

    @Test
    void deleteOption_shouldRejectMissingOption() {

        when(optionRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> questionService.deleteOption(99L)
                );

        assertEquals(
                "Option not found with id: 99",
                exception.getMessage()
        );

        verify(optionRepository, never())
                .delete(any());
    }

    @Test
    void deleteOption_shouldRejectPublishedQuiz() {

        question.setQuiz(publishedQuiz);
        correctOption.setQuestion(question);

        when(optionRepository.findById(1L))
                .thenReturn(Optional.of(correctOption));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> questionService.deleteOption(1L)
                );

        assertEquals(
                "Published quiz cannot be modified",
                exception.getMessage()
        );

        verify(optionRepository, never())
                .delete(any());
    }

    // =========================================================
    // GET OPTIONS
    // =========================================================

    @Test
    void getOptionsByQuestionId_shouldReturnOptions() {

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(optionRepository.findByQuestionId(1L))
                .thenReturn(List.of(correctOption, wrongOption));

        List<Option> result =
                questionService.getOptionsByQuestionId(1L);

        assertEquals(2, result.size());
        assertEquals("extends", result.get(0).getOptionText());
        assertEquals("implements", result.get(1).getOptionText());

        verify(questionRepository).findById(1L);
        verify(optionRepository).findByQuestionId(1L);
    }

    @Test
    void getOptionsByQuestionId_shouldRejectMissingQuestion() {

        when(questionRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> questionService.getOptionsByQuestionId(99L)
                );

        assertEquals(
                "Question not found with id: 99",
                exception.getMessage()
        );

        verify(optionRepository, never())
                .findByQuestionId(anyLong());
    }
}