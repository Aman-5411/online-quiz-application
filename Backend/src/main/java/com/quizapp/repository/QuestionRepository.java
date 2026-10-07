package com.quizapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizapp.entity.Question;
import com.quizapp.enums.Difficulty;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuizIdOrderByQuestionOrderAsc(Long quizId);

    List<Question> findByQuizId(Long quizId);

    List<Question> findByDifficulty(Difficulty difficulty);

    long countByQuizId(Long quizId);
}