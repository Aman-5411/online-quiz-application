package com.quizapp.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.entity.QuizAnswer;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.repository.OptionRepository;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.QuizAnswerRepository;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.security.CustomUserDetails;
import com.quizapp.service.QuizAnswerService;

@Service
@Transactional
public class QuizAnswerServiceImpl implements QuizAnswerService {

    private final QuizAnswerRepository quizAnswerRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    public QuizAnswerServiceImpl(
            QuizAnswerRepository quizAnswerRepository,
            QuizAttemptRepository quizAttemptRepository,
            QuestionRepository questionRepository,
            OptionRepository optionRepository
    ) {
        this.quizAnswerRepository = quizAnswerRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    // =========================================================
    // SUBMIT ANSWER
    // =========================================================

    @Override
    public QuizAnswer submitAnswer(
            Long attemptId,
            Long questionId,
            Long optionId
    ) {

        // -----------------------------------------------------
        // 1. Get authenticated user
        // -----------------------------------------------------

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal()
                        instanceof CustomUserDetails)) {

            throw new SecurityException(
                    "User is not authenticated"
            );
        }

        CustomUserDetails currentUser =
                (CustomUserDetails)
                        authentication.getPrincipal();

        Long currentUserId =
                currentUser.getUserId();

        // -----------------------------------------------------
        // 2. Validate option
        // -----------------------------------------------------

        if (optionId == null) {

            throw new IllegalArgumentException(
                    "An option must be selected"
            );
        }

        // -----------------------------------------------------
        // 3. Find attempt
        // -----------------------------------------------------

        QuizAttempt attempt =
                quizAttemptRepository.findById(attemptId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Quiz attempt not found with id: "
                                                + attemptId
                                )
                        );

        // -----------------------------------------------------
        // 4. Verify attempt ownership
        // -----------------------------------------------------

        if (!attempt.getUser()
                .getId()
                .equals(currentUserId)) {

            throw new SecurityException(
                    "You are not allowed to submit an answer "
                            + "for this quiz attempt"
            );
        }

        // -----------------------------------------------------
        // 5. Prevent answers after completion
        // -----------------------------------------------------

        if (attempt.getCompletedAt() != null) {

            throw new IllegalStateException(
                    "Cannot submit answer after the quiz "
                            + "has been completed"
            );
        }

        // -----------------------------------------------------
        // 6. Find question
        // -----------------------------------------------------

        Question question =
                questionRepository.findById(questionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question not found with id: "
                                                + questionId
                                )
                        );

        // -----------------------------------------------------
        // 7. Question must belong to attempted quiz
        // -----------------------------------------------------

        if (!question.getQuiz()
                .getId()
                .equals(attempt.getQuiz().getId())) {

            throw new IllegalArgumentException(
                    "Question does not belong to "
                            + "the quiz being attempted"
            );
        }

        // -----------------------------------------------------
        // 8. Only SINGLE_CHOICE is supported
        // -----------------------------------------------------

        if (question.getQuestionType()
                != com.quizapp.enums.QuestionType.SINGLE_CHOICE) {

            throw new IllegalStateException(
                    "Only SINGLE_CHOICE questions are supported"
            );
        }

        // -----------------------------------------------------
        // 9. Prevent duplicate answer
        // -----------------------------------------------------

        if (quizAnswerRepository
                .findByAttemptIdAndQuestionId(
                        attemptId,
                        questionId
                )
                .isPresent()) {

            throw new IllegalStateException(
                    "An answer has already been submitted "
                            + "for this question"
            );
        }

        // -----------------------------------------------------
        // 10. Find selected option
        // -----------------------------------------------------

        Option selectedOption =
                optionRepository.findById(optionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Option not found with id: "
                                                + optionId
                                )
                        );

        // -----------------------------------------------------
        // 11. Verify option belongs to question
        // -----------------------------------------------------

        if (!selectedOption.getQuestion()
                .getId()
                .equals(questionId)) {

            throw new IllegalArgumentException(
                    "Selected option does not belong "
                            + "to the submitted question"
            );
        }

        // -----------------------------------------------------
        // 12. Determine correctness
        // -----------------------------------------------------

        boolean isCorrect =
                selectedOption.isCorrect();

        // -----------------------------------------------------
        // 13. Create QuizAnswer
        // -----------------------------------------------------

        QuizAnswer answer =
                new QuizAnswer();

        answer.setAttempt(attempt);
        answer.setQuestion(question);
        answer.setSelectedOption(selectedOption);
        answer.setCorrect(isCorrect);

        // -----------------------------------------------------
        // 14. Save
        // -----------------------------------------------------

        return quizAnswerRepository.save(answer);
    }

    // =========================================================
    // GET ANSWER BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public QuizAnswer getAnswerById(
            Long answerId
    ) {

        return quizAnswerRepository.findById(answerId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Quiz answer not found with id: "
                                        + answerId
                        )
                );
    }

    // =========================================================
    // GET ANSWERS BY ATTEMPT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<QuizAnswer> getAnswersByAttempt(
            Long attemptId
    ) {

        if (!quizAttemptRepository
                .existsById(attemptId)) {

            throw new RuntimeException(
                    "Quiz attempt not found with id: "
                            + attemptId
            );
        }

        return quizAnswerRepository
                .findByAttemptId(attemptId);
    }

    // =========================================================
    // GET ANSWER FOR QUESTION
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public QuizAnswer getAnswerForQuestion(
            Long attemptId,
            Long questionId
    ) {

        return quizAnswerRepository
                .findByAttemptIdAndQuestionId(
                        attemptId,
                        questionId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Answer not found for attempt "
                                        + attemptId
                                        + " and question "
                                        + questionId
                        )
                );
    }

    // =========================================================
    // COUNT ANSWERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long countAnswersByAttempt(
            Long attemptId
    ) {

        return quizAnswerRepository
                .countByAttemptId(attemptId);
    }

    // =========================================================
    // COUNT CORRECT ANSWERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long countCorrectAnswersByAttempt(
            Long attemptId
    ) {

        return quizAnswerRepository
                .countByAttemptIdAndCorrectTrue(
                        attemptId
                );
    }

    // =========================================================
    // DELETE ANSWER
    // =========================================================

    @Override
    public void deleteAnswer(
            Long answerId
    ) {

        QuizAnswer answer =
                getAnswerById(answerId);

        if (answer.getAttempt()
                .getCompletedAt() != null) {

            throw new IllegalStateException(
                    "Cannot delete an answer from "
                            + "a completed quiz attempt"
            );
        }

        quizAnswerRepository.delete(answer);
    }
}