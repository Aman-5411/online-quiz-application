package com.quizapp.service;

import java.util.List;

import com.quizapp.entity.QuizAttempt;

public interface QuizAttemptService {

    QuizAttempt startAttempt(
            Long userId,
            Long quizId
    );

    QuizAttempt getAttemptById(
            Long attemptId
    );

    List<QuizAttempt> getAttemptsByUser(
            Long userId
    );

    List<QuizAttempt> getAttemptsByQuiz(
            Long quizId
    );

    List<QuizAttempt> getAttemptsByUserAndQuiz(
            Long userId,
            Long quizId
    );

    List<QuizAttempt> getAllAttempts();

    QuizAttempt submitAttempt(
            Long attemptId
    );

    QuizAttempt autoSubmitAttempt(
            Long attemptId
    );

    void deleteAttempt(
            Long attemptId
    );
}