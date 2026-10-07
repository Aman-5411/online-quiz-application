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

import com.quizapp.dto.QuizSummaryResponse;
import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.UserRepository;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;
import com.quizapp.service.impl.QuizServiceImpl;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Java Basics");
        quiz.setDescription("Basic Java quiz");
        quiz.setCategory("Java");
        quiz.setDifficulty(Difficulty.EASY);
        quiz.setSource(QuizSource.MANUAL);
        quiz.setStatus(QuizStatus.DRAFT);
    }

    // =========================================================
    // CREATE QUIZ - VALIDATION
    // =========================================================

    @Test
    void createQuiz_shouldRejectEmptyTitle() {

        quiz.setTitle("");

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> quizService.createQuiz(quiz)
                );

        assertEquals(
                "Quiz title cannot be empty",
                exception.getMessage()
        );

        verifyNoInteractions(quizRepository);
    }

    @Test
    void createQuiz_shouldRejectEmptyCategory() {

        quiz.setCategory("");

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> quizService.createQuiz(quiz)
                );

        assertEquals(
                "Quiz category cannot be empty",
                exception.getMessage()
        );

        verifyNoInteractions(quizRepository);
    }

    @Test
    void createQuiz_shouldRejectMissingDifficulty() {

        quiz.setDifficulty(null);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> quizService.createQuiz(quiz)
                );

        assertEquals(
                "Quiz difficulty is required",
                exception.getMessage()
        );

        verifyNoInteractions(quizRepository);
    }

    // =========================================================
    // GET QUIZ
    // =========================================================

    @Test
    void getQuizById_shouldReturnQuiz_whenQuizExists() {

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        Quiz result = quizService.getQuizById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java Basics", result.getTitle());

        verify(quizRepository).findById(1L);
    }

    @Test
    void getQuizById_shouldThrowException_whenQuizDoesNotExist() {

        when(quizRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> quizService.getQuizById(99L)
                );

        assertEquals(
                "Quiz not found with id: 99",
                exception.getMessage()
        );

        verify(quizRepository).findById(99L);
    }

    // =========================================================
    // GET ALL QUIZZES
    // =========================================================

    @Test
    void getAllQuizzes_shouldReturnAllQuizzes() {

        Quiz secondQuiz = new Quiz();
        secondQuiz.setId(2L);
        secondQuiz.setTitle("Spring Boot");

        when(quizRepository.findAll())
                .thenReturn(List.of(quiz, secondQuiz));

        List<Quiz> result = quizService.getAllQuizzes();

        assertEquals(2, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        assertEquals("Spring Boot", result.get(1).getTitle());

        verify(quizRepository).findAll();
    }

    // =========================================================
    // FILTERING
    // =========================================================

    @Test
    void getQuizzesByStatus_shouldReturnMatchingQuizzes() {

        when(quizRepository.findByStatus(QuizStatus.DRAFT))
                .thenReturn(List.of(quiz));

        List<Quiz> result =
                quizService.getQuizzesByStatus(QuizStatus.DRAFT);

        assertEquals(1, result.size());
        assertEquals(QuizStatus.DRAFT, result.get(0).getStatus());

        verify(quizRepository)
                .findByStatus(QuizStatus.DRAFT);
    }

    @Test
    void getQuizzesByCategory_shouldReturnMatchingQuizzes() {

        when(quizRepository.findByCategory("Java"))
                .thenReturn(List.of(quiz));

        List<Quiz> result =
                quizService.getQuizzesByCategory("Java");

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getCategory());

        verify(quizRepository)
                .findByCategory("Java");
    }

    @Test
    void getQuizzesByDifficulty_shouldReturnMatchingQuizzes() {

        when(quizRepository.findByDifficulty(Difficulty.EASY))
                .thenReturn(List.of(quiz));

        List<Quiz> result =
                quizService.getQuizzesByDifficulty(Difficulty.EASY);

        assertEquals(1, result.size());
        assertEquals(
                Difficulty.EASY,
                result.get(0).getDifficulty()
        );

        verify(quizRepository)
                .findByDifficulty(Difficulty.EASY);
    }

    @Test
    void getQuizzesBySource_shouldReturnMatchingQuizzes() {

        when(quizRepository.findBySource(QuizSource.MANUAL))
                .thenReturn(List.of(quiz));

        List<Quiz> result =
                quizService.getQuizzesBySource(QuizSource.MANUAL);

        assertEquals(1, result.size());
        assertEquals(
                QuizSource.MANUAL,
                result.get(0).getSource()
        );

        verify(quizRepository)
                .findBySource(QuizSource.MANUAL);
    }

    @Test
    void getQuizzesCreatedByUser_shouldReturnMatchingQuizzes() {

        when(quizRepository.findByCreatedById(1L))
                .thenReturn(List.of(quiz));

        List<Quiz> result =
                quizService.getQuizzesCreatedByUser(1L);

        assertEquals(1, result.size());

        verify(quizRepository)
                .findByCreatedById(1L);
    }

    @Test
    void getQuizzesByStatusAndCategory_shouldReturnMatchingQuizzes() {

        when(
                quizRepository.findByStatusAndCategory(
                        QuizStatus.DRAFT,
                        "Java"
                )
        ).thenReturn(List.of(quiz));

        List<Quiz> result =
                quizService.getQuizzesByStatusAndCategory(
                        QuizStatus.DRAFT,
                        "Java"
                );

        assertEquals(1, result.size());

        verify(quizRepository)
                .findByStatusAndCategory(
                        QuizStatus.DRAFT,
                        "Java"
                );
    }

    @Test
    void getQuizzesByStatusAndDifficulty_shouldReturnMatchingQuizzes() {

        when(
                quizRepository.findByStatusAndDifficulty(
                        QuizStatus.DRAFT,
                        Difficulty.EASY
                )
        ).thenReturn(List.of(quiz));

        List<Quiz> result =
                quizService.getQuizzesByStatusAndDifficulty(
                        QuizStatus.DRAFT,
                        Difficulty.EASY
                );

        assertEquals(1, result.size());

        verify(quizRepository)
                .findByStatusAndDifficulty(
                        QuizStatus.DRAFT,
                        Difficulty.EASY
                );
    }

    // =========================================================
    // PUBLISHED QUIZ SUMMARIES
    // =========================================================

    @Test
    void getPublishedQuizSummaries_shouldReturnSummaries() {

        quiz.setStatus(QuizStatus.PUBLISHED);

        when(quizRepository.findByStatus(QuizStatus.PUBLISHED))
                .thenReturn(List.of(quiz));

        List<QuizSummaryResponse> result =
                quizService.getPublishedQuizSummaries();

        assertEquals(1, result.size());
        assertEquals(
                "Java Basics",
                result.get(0).getTitle()
        );
        assertEquals(
                "Java",
                result.get(0).getCategory()
        );
        assertEquals(
                Difficulty.EASY,
                result.get(0).getDifficulty()
        );

        verify(quizRepository)
                .findByStatus(QuizStatus.PUBLISHED);
    }

    // =========================================================
    // UPDATE QUIZ
    // =========================================================

    @Test
    void updateQuiz_shouldUpdateDraftQuiz() {

        Quiz updateRequest = new Quiz();
        updateRequest.setTitle("Updated Java Basics");
        updateRequest.setDescription("Updated description");
        updateRequest.setCategory("Core Java");
        updateRequest.setDifficulty(Difficulty.MEDIUM);
        updateRequest.setSource(QuizSource.MANUAL);

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        when(quizRepository.save(quiz))
                .thenReturn(quiz);

        Quiz result =
                quizService.updateQuiz(1L, updateRequest);

        assertEquals(
                "Updated Java Basics",
                result.getTitle()
        );
        assertEquals(
                "Updated description",
                result.getDescription()
        );
        assertEquals(
                "Core Java",
                result.getCategory()
        );
        assertEquals(
                Difficulty.MEDIUM,
                result.getDifficulty()
        );

        verify(quizRepository).save(quiz);
    }

    @Test
    void updateQuiz_shouldRejectPublishedQuiz() {

        quiz.setStatus(QuizStatus.PUBLISHED);

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizService.updateQuiz(
                                1L,
                                new Quiz()
                        )
                );

        assertEquals(
                "Published quiz cannot be updated",
                exception.getMessage()
        );

        verify(quizRepository, never()).save(any());
    }

    // =========================================================
    // PUBLISH QUIZ
    // =========================================================

    @Test
    void publishQuiz_shouldPublishDraftQuizWithQuestions() {

        Question question = new Question();
        quiz.addQuestion(question);

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        when(quizRepository.save(quiz))
                .thenReturn(quiz);

        Quiz result = quizService.publishQuiz(1L);

        assertEquals(
                QuizStatus.PUBLISHED,
                result.getStatus()
        );

        assertNotNull(result.getPublishedAt());

        verify(quizRepository).save(quiz);
    }

    @Test
    void publishQuiz_shouldRejectAlreadyPublishedQuiz() {

        quiz.setStatus(QuizStatus.PUBLISHED);

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizService.publishQuiz(1L)
                );

        assertEquals(
                "Quiz is already published",
                exception.getMessage()
        );

        verify(quizRepository, never()).save(any());
    }

    @Test
    void publishQuiz_shouldRejectQuizWithoutQuestions() {

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizService.publishQuiz(1L)
                );

        assertEquals(
                "A quiz must contain at least one question before publishing",
                exception.getMessage()
        );

        verify(quizRepository, never()).save(any());
    }

    @Test
    void publishQuiz_shouldRejectNonDraftQuiz() {

        quiz.setStatus(QuizStatus.PUBLISHED);

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizService.publishQuiz(1L)
                );

        assertEquals(
                "Quiz is already published",
                exception.getMessage()
        );

        verify(quizRepository, never()).save(any());
    }

    // =========================================================
    // DELETE QUIZ
    // =========================================================

    @Test
    void deleteQuiz_shouldDeleteDraftQuiz() {

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        quizService.deleteQuiz(1L);

        verify(quizRepository).delete(quiz);
    }

    @Test
    void deleteQuiz_shouldRejectPublishedQuiz() {

        quiz.setStatus(QuizStatus.PUBLISHED);

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> quizService.deleteQuiz(1L)
                );

        assertEquals(
                "Published quiz cannot be deleted",
                exception.getMessage()
        );

        verify(quizRepository, never()).delete(any());
    }
}