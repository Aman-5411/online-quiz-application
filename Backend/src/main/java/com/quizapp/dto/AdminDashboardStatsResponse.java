package com.quizapp.dto;

public class AdminDashboardStatsResponse {

    private long totalQuizzes;
    private long totalAttempts;
    private long totalUsers;

    public AdminDashboardStatsResponse() {
    }

    public AdminDashboardStatsResponse(
            long totalQuizzes,
            long totalAttempts,
            long totalUsers
    ) {
        this.totalQuizzes = totalQuizzes;
        this.totalAttempts = totalAttempts;
        this.totalUsers = totalUsers;
    }

    public long getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(long totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public long getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(long totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }
}