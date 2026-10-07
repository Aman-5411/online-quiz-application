package com.quizapp.service;

import java.util.List;

import com.quizapp.entity.QuizAnswer;

public interface QuizAnswerService {

    QuizAnswer submitAnswer(
            Long attemptId,
            Long questionId,
            Long optionId
    );

    QuizAnswer getAnswerById(
            Long answerId
    );

    List<QuizAnswer> getAnswersByAttempt(
            Long attemptId
    );

    QuizAnswer getAnswerForQuestion(
            Long attemptId,
            Long questionId
    );

    long countAnswersByAttempt(
            Long attemptId
    );

    long countCorrectAnswersByAttempt(
            Long attemptId
    );

    void deleteAnswer(
            Long answerId
    );
}