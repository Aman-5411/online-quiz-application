package com.quizapp.dto;

import com.quizapp.enums.Difficulty;

public class QuizSummaryResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private Difficulty difficulty;
    private int questionCount;

    public QuizSummaryResponse(
            Long id,
            String title,
            String description,
            String category,
            Difficulty difficulty,
            int questionCount
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getQuestionCount() {
        return questionCount;
    }
}