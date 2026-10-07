package com.quizapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quizapp.dto.AdminAttemptResponse;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.service.QuizAttemptService;

@RestController
@RequestMapping("/api/admin/attempts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAttemptController {

    private final QuizAttemptService quizAttemptService;

    public AdminAttemptController(
            QuizAttemptService quizAttemptService
    ) {
        this.quizAttemptService = quizAttemptService;
    }

    @GetMapping
    public ResponseEntity<List<AdminAttemptResponse>> getAllAttempts() {

        List<AdminAttemptResponse> attempts =
                quizAttemptService
                        .getAllAttempts()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(attempts);
    }

    private AdminAttemptResponse toResponse(
            QuizAttempt attempt
    ) {

        return new AdminAttemptResponse(
                attempt.getId(),

                attempt.getUser().getId(),
                attempt.getUser().getName(),
                attempt.getUser().getEmail(),

                attempt.getQuiz().getId(),
                attempt.getQuiz().getTitle(),

                attempt.getScore(),
                attempt.getTotalQuestions(),
                attempt.getCorrectAnswers(),
                attempt.getPercentage(),

                attempt.getStartedAt(),
                attempt.getCompletedAt()
        );
    }
}