package com.quizapp.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.service.QuestionService;
import com.quizapp.dto.QuestionResponse;

@RestController
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /*
     * Create a question for a quiz.
     *
     * POST /api/quizzes/{quizId}/questions
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<Question> createQuestion(
            @PathVariable Long quizId,
            @RequestBody Question question
    ) {

        Question createdQuestion =
                questionService.createQuestion(
                        quizId,
                        question
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdQuestion);
    }

    /*
     * Get a question by ID.
     *
     * GET /api/questions/{id}
     */
    @GetMapping("/questions/{id}")
    public ResponseEntity<Question> getQuestionById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                questionService.getQuestionById(id)
        );
    }

    /*
     * Get all questions belonging to a quiz.
     *
     * GET /api/quizzes/{quizId}/questions
     */
    @GetMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<List<Question>> getQuestionsByQuizId(
            @PathVariable Long quizId
    ) {

        return ResponseEntity.ok(
                questionService.getQuestionsByQuizId(quizId)
        );
    }

    @GetMapping("/quizzes/{quizId}/questions/attempt")
    public ResponseEntity<List<QuestionResponse>> getQuestionsForQuizAttempt(
            @PathVariable Long quizId
    ) {

        return ResponseEntity.ok(
                questionService.getQuestionsForQuizAttempt(
                        quizId
                )
        );
    }

    /*
     * Update a question.
     *
     * PUT /api/questions/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/questions/{id}")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long id,
            @RequestBody Question question
    ) {

        return ResponseEntity.ok(
                questionService.updateQuestion(
                        id,
                        question
                )
        );
    }

    /*
     * Delete a question.
     *
     * DELETE /api/questions/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id
    ) {

        questionService.deleteQuestion(id);

        return ResponseEntity.noContent().build();
    }

    /*
     * Add an option to a question.
     *
     * POST /api/questions/{questionId}/options
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<Option> addOption(
            @PathVariable Long questionId,
            @RequestBody Option option
    ) {

        Option createdOption =
                questionService.addOption(
                        questionId,
                        option
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdOption);
    }

    /*
     * Update an option.
     *
     * PUT /api/options/{optionId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/options/{optionId}")
    public ResponseEntity<Option> updateOption(
            @PathVariable Long optionId,
            @RequestBody Option option
    ) {

        return ResponseEntity.ok(
                questionService.updateOption(
                        optionId,
                        option
                )
        );
    }

    /*
     * Delete an option.
     *
     * DELETE /api/options/{optionId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<Void> deleteOption(
            @PathVariable Long optionId
    ) {

        questionService.deleteOption(optionId);

        return ResponseEntity.noContent().build();
    }

    /*
     * Get all options belonging to a question.
     *
     * GET /api/questions/{questionId}/options
     */
    @GetMapping("/questions/{questionId}/options")
    public ResponseEntity<List<Option>> getOptionsByQuestionId(
            @PathVariable Long questionId
    ) {

        return ResponseEntity.ok(
                questionService.getOptionsByQuestionId(
                        questionId
                )
        );
    }
}