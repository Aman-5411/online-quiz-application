package com.quizapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizapp.entity.Option;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByQuestionId(Long questionId);

    List<Option> findByQuestionIdAndCorrectTrue(Long questionId);

    long countByQuestionIdAndCorrectTrue(Long questionId);
    
    long countByQuestionId(Long questionId);
}