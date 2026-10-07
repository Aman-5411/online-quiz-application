package com.quizapp.controller;

import java.util.List;
import com.quizapp.dto.QuizSummaryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quizapp.entity.Quiz;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;
import com.quizapp.service.QuizService;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // =========================================================
    // CREATE QUIZ
    // =========================================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Quiz> createQuiz(
            @RequestBody Quiz quiz
    ) {

        Quiz createdQuiz = quizService.createQuiz(quiz);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdQuiz);
    }

    // =========================================================
    // GET QUIZ BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(
            @PathVariable Long id
    ) {

        Quiz quiz = quizService.getQuizById(id);

        return ResponseEntity.ok(quiz);
    }

    // =========================================================
    // GET ALL QUIZZES
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() {

        return ResponseEntity.ok(
                quizService.getAllQuizzes()
        );
    }

    // =========================================================
    // GET QUIZZES BY STATUS
    // =========================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getQuizzesByStatus(
            @PathVariable QuizStatus status
    ) {

        if (status == QuizStatus.PUBLISHED) {

            return ResponseEntity.ok(
                    quizService.getPublishedQuizSummaries()
            );
        }

        return ResponseEntity.ok(
                quizService.getQuizzesByStatus(status)
        );
    }

    // =========================================================
    // GET QUIZZES BY CATEGORY
    // =========================================================

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Quiz>> getQuizzesByCategory(
            @PathVariable String category
    ) {

        return ResponseEntity.ok(
                quizService.getQuizzesByCategory(category)
        );
    }

    // =========================================================
    // GET QUIZZES BY DIFFICULTY
    // =========================================================

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<Quiz>> getQuizzesByDifficulty(
            @PathVariable Difficulty difficulty
    ) {

        return ResponseEntity.ok(
                quizService.getQuizzesByDifficulty(difficulty)
        );
    }

    // =========================================================
    // GET QUIZZES BY SOURCE
    // =========================================================

    @GetMapping("/source/{source}")
    public ResponseEntity<List<Quiz>> getQuizzesBySource(
            @PathVariable QuizSource source
    ) {

        return ResponseEntity.ok(
                quizService.getQuizzesBySource(source)
        );
    }

    // =========================================================
    // GET QUIZZES CREATED BY USER
    // =========================================================

    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<Quiz>> getQuizzesCreatedByUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                quizService.getQuizzesCreatedByUser(userId)
        );
    }

    // =========================================================
    // GET QUIZZES BY STATUS + CATEGORY
    // =========================================================

    @GetMapping("/filter/status-category")
    public ResponseEntity<List<Quiz>> getQuizzesByStatusAndCategory(
            @RequestParam QuizStatus status,
            @RequestParam String category
    ) {

        return ResponseEntity.ok(
                quizService.getQuizzesByStatusAndCategory(
                        status,
                        category
                )
        );
    }

    // =========================================================
    // GET QUIZZES BY STATUS + DIFFICULTY
    // =========================================================

    @GetMapping("/filter/status-difficulty")
    public ResponseEntity<List<Quiz>> getQuizzesByStatusAndDifficulty(
            @RequestParam QuizStatus status,
            @RequestParam Difficulty difficulty
    ) {

        return ResponseEntity.ok(
                quizService.getQuizzesByStatusAndDifficulty(
                        status,
                        difficulty
                )
        );
    }

    // =========================================================
    // UPDATE QUIZ
    // =========================================================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(
            @PathVariable Long id,
            @RequestBody Quiz quiz
    ) {

        Quiz updatedQuiz = quizService.updateQuiz(
                id,
                quiz
        );

        return ResponseEntity.ok(updatedQuiz);
    }

    // =========================================================
    // PUBLISH QUIZ
    // =========================================================

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Quiz> publishQuiz(
            @PathVariable Long id
    ) {

        Quiz publishedQuiz = quizService.publishQuiz(id);

        return ResponseEntity.ok(publishedQuiz);
    }

    // =========================================================
    // DELETE QUIZ
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable Long id
    ) {

        quizService.deleteQuiz(id);

        return ResponseEntity.noContent().build();
    }
}