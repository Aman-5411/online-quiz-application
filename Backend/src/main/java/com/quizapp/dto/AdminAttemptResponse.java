package com.quizapp.dto;

import java.time.LocalDateTime;

public class AdminAttemptResponse {

    private Long attemptId;

    private Long userId;
    private String userName;
    private String userEmail;

    private Long quizId;
    private String quizTitle;

    private Integer score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double percentage;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public AdminAttemptResponse() {
    }

    public AdminAttemptResponse(
            Long attemptId,
            Long userId,
            String userName,
            String userEmail,
            Long quizId,
            String quizTitle,
            Integer score,
            Integer totalQuestions,
            Integer correctAnswers,
            Double percentage,
            LocalDateTime startedAt,
            LocalDateTime completedAt
    ) {
        this.attemptId = attemptId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.percentage = percentage;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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
}