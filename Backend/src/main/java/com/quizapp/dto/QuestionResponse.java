package com.quizapp.dto;

import java.util.List;

import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuestionType;

public class QuestionResponse {

    private Long id;
    private String questionText;
    private Integer questionOrder;
    private QuestionType questionType;
    private Difficulty difficulty;
    private List<OptionResponse> options;

    public QuestionResponse(
            Long id,
            String questionText,
            Integer questionOrder,
            QuestionType questionType,
            Difficulty difficulty,
            List<OptionResponse> options
    ) {
        this.id = id;
        this.questionText = questionText;
        this.questionOrder = questionOrder;
        this.questionType = questionType;
        this.difficulty = difficulty;
        this.options = options;
    }

    public Long getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<OptionResponse> getOptions() {
        return options;
    }
}