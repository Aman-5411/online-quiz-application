package com.quizapp.service;

import java.util.List;

import com.quizapp.entity.Quiz;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;
import com.quizapp.dto.QuizSummaryResponse;

public interface QuizService {

    Quiz createQuiz(Quiz quiz);

    Quiz getQuizById(Long id);

    List<Quiz> getAllQuizzes();

    List<Quiz> getQuizzesByStatus(QuizStatus status);

    List<QuizSummaryResponse> getPublishedQuizSummaries();

    List<Quiz> getQuizzesByCategory(String category);

    List<Quiz> getQuizzesByDifficulty(Difficulty difficulty);

    List<Quiz> getQuizzesBySource(QuizSource source);

    List<Quiz> getQuizzesCreatedByUser(Long userId);

    List<Quiz> getQuizzesByStatusAndCategory(
            QuizStatus status,
            String category
    );

    List<Quiz> getQuizzesByStatusAndDifficulty(
            QuizStatus status,
            Difficulty difficulty
    );

    Quiz updateQuiz(Long id, Quiz quiz);

    Quiz publishQuiz(Long id);

    void deleteQuiz(Long id);
}