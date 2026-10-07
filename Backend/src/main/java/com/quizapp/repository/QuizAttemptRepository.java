package com.quizapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizapp.entity.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByUserIdOrderByStartedAtDesc(Long userId);

    List<QuizAttempt> findByQuizIdOrderByStartedAtDesc(Long quizId);

    List<QuizAttempt> findByUserIdAndQuizIdOrderByStartedAtDesc(
            Long userId,
            Long quizId
    );
    List<QuizAttempt> findByCompletedAtIsNullAndStartedAtBefore(
            LocalDateTime cutoff
    );
    List<QuizAttempt> findAllByOrderByStartedAtDesc();

    long countByUserId(Long userId);

    long countByQuizId(Long quizId);
}