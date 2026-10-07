package com.quizapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import com.quizapp.dto.QuizAttemptResponse;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.service.QuizAttemptService;

@RestController
@RequestMapping("/api/attempts")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;
    @Value("${quiz.attempt.duration-minutes:10}")
    private long durationMinutes;

    public QuizAttemptController(
            QuizAttemptService quizAttemptService
    ) {
        this.quizAttemptService = quizAttemptService;
    }

    // =========================================================
    // START QUIZ ATTEMPT
    // =========================================================

    /*
     * POST
     * /api/attempts/user/{userId}/quiz/{quizId}
     *
     * Starts a new attempt for a user.
     */
    @PostMapping("/user/{userId}/quiz/{quizId}")
    public ResponseEntity<QuizAttempt> startAttempt(
            @PathVariable Long userId,
            @PathVariable Long quizId
    ) {

        QuizAttempt attempt =
                quizAttemptService.startAttempt(
                        userId,
                        quizId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(attempt);
    }

    // =========================================================
    // GET ATTEMPT BY ID
    // =========================================================

    /*
     * GET
     * /api/attempts/{attemptId}
     *
     * Returns one attempt.
     */
    @GetMapping("/{attemptId}")
    public ResponseEntity<QuizAttemptResponse> getAttemptById(
            @PathVariable Long attemptId
    ) {

        QuizAttempt attempt =
                quizAttemptService.getAttemptById(
                        attemptId
                );

        return ResponseEntity.ok(toResponse(attempt));
    }

    // =========================================================
    // GET ATTEMPTS BY USER
    // =========================================================

    /*
     * GET
     * /api/attempts/user/{userId}
     *
     * Returns all attempts made by a user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizAttemptResponse>> getAttemptsByUser(
            @PathVariable Long userId
    ) {

        List<QuizAttemptResponse> attempts =
                quizAttemptService.getAttemptsByUser(userId)
                                        .stream()
                                        .map(this::toResponse)
                                        .toList();

        return ResponseEntity.ok(attempts);
    }

    // =========================================================
    // GET ATTEMPTS BY QUIZ
    // =========================================================

    /*
     * GET
     * /api/attempts/quiz/{quizId}
     *
     * Returns all attempts for a particular quiz.
     */
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizAttemptResponse>> getAttemptsByQuiz(
            @PathVariable Long quizId
    ) {

        List<QuizAttemptResponse> attempts =
                quizAttemptService.getAttemptsByQuiz(quizId)
                                        .stream()
                                        .map(this::toResponse)
                                        .toList();

        return ResponseEntity.ok(attempts);
    }

    // =========================================================
    // GET ATTEMPTS BY USER + QUIZ
    // =========================================================

    /*
     * GET
     * /api/attempts/user/{userId}/quiz/{quizId}
     *
     * Returns all attempts made by a specific user
     * for a specific quiz.
     */
    @GetMapping("/user/{userId}/quiz/{quizId}")
    public ResponseEntity<List<QuizAttemptResponse>>
            getAttemptsByUserAndQuiz(
                    @PathVariable Long userId,
                    @PathVariable Long quizId
            ) {

        List<QuizAttemptResponse> attempts =
                quizAttemptService
                        .getAttemptsByUserAndQuiz(
                                userId,
                                quizId)
                                .stream()
                                .map(this::toResponse)
                                .toList();

        return ResponseEntity.ok(attempts);
    }

    // =========================================================
    // SUBMIT ATTEMPT
    // =========================================================

    /*
     * POST
     * /api/attempts/{attemptId}/submit
     *
     * Calculates the final score and completes
     * the quiz attempt.
     */
    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<QuizAttemptResponse> submitAttempt(
            @PathVariable Long attemptId
    ) {

        QuizAttempt submittedAttempt =
                quizAttemptService.submitAttempt(
                        attemptId
                );

        return ResponseEntity.ok(toResponse(submittedAttempt));
    }

    // =========================================================
    // DELETE ATTEMPT
    // =========================================================

    /*
     * DELETE
     * /api/attempts/{attemptId}
     *
     * Deletes an attempt.
     */
    @DeleteMapping("/{attemptId}")
    public ResponseEntity<Void> deleteAttempt(
            @PathVariable Long attemptId
    ) {

            quizAttemptService.deleteAttempt(
                            attemptId);

            return ResponseEntity
                            .noContent()
                            .build();
    }
    private QuizAttemptResponse toResponse(
        QuizAttempt attempt
    ) {

        QuizAttemptResponse response =
                new QuizAttemptResponse(
                        attempt.getId(),
                        attempt.getQuiz().getId(),
                        attempt.getQuiz().getTitle(),
                        attempt.getScore(),
                        attempt.getTotalQuestions(),
                        attempt.getCorrectAnswers(),
                        attempt.getPercentage(),
                        attempt.getStartedAt(),
                        attempt.getCompletedAt()
                );

        response.setDurationMinutes(durationMinutes);

        return response;
    }

}