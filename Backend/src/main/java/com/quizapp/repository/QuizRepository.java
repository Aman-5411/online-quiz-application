package com.quizapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizapp.entity.Quiz;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByStatus(QuizStatus status);

    List<Quiz> findByCategory(String category);

    List<Quiz> findByDifficulty(Difficulty difficulty);

    List<Quiz> findBySource(QuizSource source);

    List<Quiz> findByCreatedById(Long userId);

    List<Quiz> findByStatusAndCategory(
            QuizStatus status,
            String category
    );

    List<Quiz> findByStatusAndDifficulty(
            QuizStatus status,
            Difficulty difficulty
    );
}