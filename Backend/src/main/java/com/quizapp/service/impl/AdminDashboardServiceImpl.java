package com.quizapp.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quizapp.dto.AdminDashboardStatsResponse;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.UserRepository;
import com.quizapp.service.AdminDashboardService;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;

    public AdminDashboardServiceImpl(
            QuizRepository quizRepository,
            QuizAttemptRepository quizAttemptRepository,
            UserRepository userRepository
    ) {
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AdminDashboardStatsResponse getDashboardStats() {

        long totalQuizzes = quizRepository.count();

        long totalAttempts = quizAttemptRepository.count();

        long totalUsers = userRepository.count();

        return new AdminDashboardStatsResponse(
                totalQuizzes,
                totalAttempts,
                totalUsers
        );
    }
}