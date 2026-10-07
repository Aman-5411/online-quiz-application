package com.quizapp.dto;

import java.time.LocalDateTime;

public class QuizAttemptResponse {

    private Long id;

    private Long quizId;

    private String quizTitle;

    private Integer score;

    private Integer totalQuestions;

    private Integer correctAnswers;

    private Double percentage;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private Long durationMinutes;

    public QuizAttemptResponse() {
    }

    public QuizAttemptResponse(
            Long id,
            Long quizId,
            String quizTitle,
            Integer score,
            Integer totalQuestions,
            Integer correctAnswers,
            Double percentage,
            LocalDateTime startedAt,
            LocalDateTime completedAt
    ) {
        this.id = id;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.percentage = percentage;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Long getDurationMinutes() { return durationMinutes; }

    public void setDurationMinutes(Long durationMinutes) { this.durationMinutes = durationMinutes; }
}