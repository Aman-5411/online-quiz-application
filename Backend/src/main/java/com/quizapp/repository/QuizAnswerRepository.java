package com.quizapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizapp.entity.QuizAnswer;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

    List<QuizAnswer> findByAttemptId(Long attemptId);

    Optional<QuizAnswer> findByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );

    long countByAttemptId(Long attemptId);

    long countByAttemptIdAndCorrectTrue(Long attemptId);
}