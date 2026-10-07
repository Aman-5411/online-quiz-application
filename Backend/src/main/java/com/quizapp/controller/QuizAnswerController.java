package com.quizapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quizapp.entity.QuizAnswer;
import com.quizapp.service.QuizAnswerService;

@RestController
@RequestMapping("/api/answers")
public class QuizAnswerController {

    private final QuizAnswerService quizAnswerService;

    public QuizAnswerController(
            QuizAnswerService quizAnswerService
    ) {
        this.quizAnswerService = quizAnswerService;
    }

    // =========================================================
    // SUBMIT ANSWER
    // =========================================================

    /*
     * POST
     * /api/answers?attemptId=1&questionId=1&optionIds=1
     *
     * SINGLE_CHOICE example:
     *
     * /api/answers?attemptId=1&questionId=1&optionIds=1
     *
     * MULTIPLE_CHOICE example:
     *
     * /api/answers?attemptId=1&questionId=1&optionIds=1&optionIds=3
     */
    @PostMapping
        public ResponseEntity<QuizAnswer> submitAnswer(
                @RequestParam Long attemptId,
                @RequestParam Long questionId,
                @RequestParam Long optionId
        ) {

        System.out.println(
                        ">>> QuizAnswerController.submitAnswer() CALLED");

        System.out.println(
                        "attemptId = " + attemptId);

        System.out.println(
                        "questionId = " + questionId);

        System.out.println(
                        "optionId = " + optionId);

        QuizAnswer answer = quizAnswerService.submitAnswer(
                        attemptId,
                        questionId,
                        optionId);

        return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(answer);
        }


    // =========================================================
    // GET ANSWER BY ID
    // =========================================================

    /*
     * GET /api/answers/{answerId}
     */
    @GetMapping("/{answerId}")
    public ResponseEntity<QuizAnswer> getAnswerById(
            @PathVariable Long answerId
    ) {

        return ResponseEntity.ok(
                quizAnswerService.getAnswerById(
                        answerId
                )
        );
    }

    // =========================================================
    // GET ANSWERS BY ATTEMPT
    // =========================================================

    /*
     * GET /api/answers/attempt/{attemptId}
     */
    @GetMapping("/attempt/{attemptId}")
    public ResponseEntity<List<QuizAnswer>> getAnswersByAttempt(
            @PathVariable Long attemptId
    ) {

        return ResponseEntity.ok(
                quizAnswerService.getAnswersByAttempt(
                        attemptId
                )
        );
    }

    // =========================================================
    // GET ANSWER FOR SPECIFIC QUESTION
    // =========================================================

    /*
     * GET
     * /api/answers/attempt/{attemptId}/question/{questionId}
     */
    @GetMapping(
            "/attempt/{attemptId}/question/{questionId}"
    )
    public ResponseEntity<QuizAnswer> getAnswerForQuestion(
            @PathVariable Long attemptId,
            @PathVariable Long questionId
    ) {

        return ResponseEntity.ok(
                quizAnswerService.getAnswerForQuestion(
                        attemptId,
                        questionId
                )
        );
    }

    // =========================================================
    // COUNT ANSWERS
    // =========================================================

    /*
     * GET /api/answers/attempt/{attemptId}/count
     */
    @GetMapping("/attempt/{attemptId}/count")
    public ResponseEntity<Long> countAnswersByAttempt(
            @PathVariable Long attemptId
    ) {

        return ResponseEntity.ok(
                quizAnswerService.countAnswersByAttempt(
                        attemptId
                )
        );
    }

    // =========================================================
    // COUNT CORRECT ANSWERS
    // =========================================================

    /*
     * GET /api/answers/attempt/{attemptId}/correct-count
     */
    @GetMapping(
            "/attempt/{attemptId}/correct-count"
    )
    public ResponseEntity<Long> countCorrectAnswersByAttempt(
            @PathVariable Long attemptId
    ) {

        return ResponseEntity.ok(
                quizAnswerService.countCorrectAnswersByAttempt(
                        attemptId
                )
        );
    }

    // =========================================================
    // DELETE ANSWER
    // =========================================================

    /*
     * DELETE /api/answers/{answerId}
     */
    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long answerId
    ) {

        quizAnswerService.deleteAnswer(answerId);

        return ResponseEntity.noContent().build();
    }
}