package com.quizapp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.quizapp.entity.QuizAnswer;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.enums.QuizStatus;
import com.quizapp.exception.ResourceNotFoundException;
import com.quizapp.repository.QuizAnswerRepository;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.UserRepository;
import com.quizapp.security.CustomUserDetails;
import com.quizapp.service.QuizAttemptService;

@Service
@Transactional
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public QuizAttemptServiceImpl(
            QuizAttemptRepository quizAttemptRepository,
            QuizAnswerRepository quizAnswerRepository,
            QuizRepository quizRepository,
            UserRepository userRepository
    ) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // AUTHENTICATED USER
    // =========================================================

    private CustomUserDetails getAuthenticatedUser() {

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

        return (CustomUserDetails)
                authentication.getPrincipal();
    }

    // =========================================================
    // CHECK ADMIN
    // =========================================================

    private boolean isAdmin() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication != null
                && authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );
    }

    // =========================================================
    // VERIFY USER ACCESS
    // =========================================================

    private void verifyUserAccess(Long requestedUserId) {

        CustomUserDetails currentUser =
                getAuthenticatedUser();

        Long currentUserId =
                currentUser.getUserId();

        /*
         * Admins are allowed to access
         * other users' attempt information.
         */
        if (isAdmin()) {
            return;
        }

        /*
         * Normal users can only access
         * their own attempts.
         */
        if (!currentUserId.equals(requestedUserId)) {

            throw new SecurityException(
                    "You are not allowed to access another user's quiz attempts"
            );
        }
    }

    // =========================================================
    // START ATTEMPT
    // =========================================================

    @Override
    public QuizAttempt startAttempt(
            Long userId,
            Long quizId
    ) {

        CustomUserDetails currentUser =
                getAuthenticatedUser();

        /*
         * For normal users, ignore the userId
         * supplied by the client.
         *
         * The authenticated JWT determines
         * the actual user.
         */
        Long authenticatedUserId =
                currentUser.getUserId();

        if (!isAdmin()
                && !authenticatedUserId.equals(userId)) {

            throw new SecurityException(
                    "You cannot start a quiz attempt for another user"
            );
        }

        User user =
                userRepository.findById(authenticatedUserId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Authenticated user not found"
                                )
                        );

        Quiz quiz =
                quizRepository.findById(quizId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Quiz not found with id: "
                                                + quizId
                                )
                        );

        /*
         * Only published quizzes can be attempted.
         */
        if (quiz.getStatus() != QuizStatus.PUBLISHED) {

            throw new IllegalStateException(
                    "Only published quizzes can be attempted"
            );
        }

        /*
         * Quiz must contain questions.
         */
        if (quiz.getQuestions() == null
                || quiz.getQuestions().isEmpty()) {

            throw new IllegalStateException(
                    "Quiz does not contain any questions"
            );
        }

        QuizAttempt attempt =
                new QuizAttempt();

        attempt.setUser(user);
        attempt.setQuiz(quiz);

        attempt.setTotalQuestions(
                quiz.getQuestions().size()
        );

        attempt.setScore(0);
        attempt.setCorrectAnswers(0);
        attempt.setPercentage(0.0);

        attempt.setStartedAt(
                LocalDateTime.now()
        );

        return quizAttemptRepository.save(
                attempt
        );
    }

    // =========================================================
    // GET ATTEMPT BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public QuizAttempt getAttemptById(
            Long attemptId
    ) {

        QuizAttempt attempt =
                quizAttemptRepository.findById(attemptId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Quiz attempt not found with id: "
                                                + attemptId
                                )
                        );

        /*
         * Verify that the authenticated user
         * is allowed to access this attempt.
         */
        verifyUserAccess(
                attempt.getUser().getId()
        );

        return attempt;
    }

    // =========================================================
    // GET ATTEMPTS BY USER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getAttemptsByUser(
            Long userId
    ) {

        verifyUserAccess(userId);

        return quizAttemptRepository
                .findByUserIdOrderByStartedAtDesc(
                        userId
                );
    }

    // =========================================================
    // GET ATTEMPTS BY QUIZ
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getAttemptsByQuiz(
            Long quizId
    ) {

        /*
         * This endpoint is intended for
         * administrative access because it
         * exposes attempts from multiple users.
         */
        if (!isAdmin()) {

            throw new SecurityException(
                    "Only administrators can view all attempts for a quiz"
            );
        }

        return quizAttemptRepository
                .findByQuizIdOrderByStartedAtDesc(
                        quizId
                );
    }

    // =========================================================
    // GET ATTEMPTS BY USER + QUIZ
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getAttemptsByUserAndQuiz(
            Long userId,
            Long quizId
    ) {

        verifyUserAccess(userId);

        return quizAttemptRepository
                .findByUserIdAndQuizIdOrderByStartedAtDesc(
                        userId,
                        quizId
                );
    }

    // =========================================================
    // GET ALL ATTEMPTS — ADMIN
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getAllAttempts() {

        return quizAttemptRepository
                .findAllByOrderByStartedAtDesc();
    }

    // =========================================================
    // SUBMIT ATTEMPT
    // =========================================================

    @Override
    public QuizAttempt submitAttempt(
            Long attemptId
    ) {

        /*
         * getAttemptById() already verifies
         * ownership.
         */
        QuizAttempt attempt =
                getAttemptById(attemptId);

        /*
         * Prevent submitting an already
         * completed attempt.
         */
        if (attempt.getCompletedAt() != null) {

             return attempt;
        }

        long totalAnswers =
                quizAnswerRepository
                        .countByAttemptId(
                                attemptId
                        );

        long correctAnswers =
                quizAnswerRepository
                        .countByAttemptIdAndCorrectTrue(
                                attemptId
                        );

        int totalQuestions =
                attempt.getTotalQuestions();

        int correct =
                (int) correctAnswers;

        int score =
                correct;

        double percentage =
                0.0;

        if (totalQuestions > 0) {

            percentage =
                    ((double) correct
                            / totalQuestions)
                            * 100;
        }

        attempt.setScore(score);

        attempt.setCorrectAnswers(
                correct
        );

        attempt.setPercentage(
                percentage
        );

        attempt.setCompletedAt(
                LocalDateTime.now()
        );

        return quizAttemptRepository.save(
                attempt
        );
    }

    @Override
    @Transactional
    public QuizAttempt autoSubmitAttempt(Long attemptId) {

        QuizAttempt attempt =
                quizAttemptRepository.findById(attemptId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Quiz attempt not found with id: "
                                                + attemptId
                                )
                        );

        // Prevent submitting an already completed attempt.
        if (attempt.getCompletedAt() != null) {
            return attempt;
        }

        long correctAnswers =
                quizAnswerRepository
                        .countByAttemptIdAndCorrectTrue(
                                attemptId
                        );

        int totalQuestions =
                attempt.getTotalQuestions();

        int correct =
                (int) correctAnswers;

        int score =
                correct;

        double percentage =
                0.0;

        if (totalQuestions > 0) {
            percentage =
                    ((double) correct
                            / totalQuestions)
                            * 100;
        }

        attempt.setScore(score);

        attempt.setCorrectAnswers(
                correct
        );

        attempt.setPercentage(
                percentage
        );

        attempt.setCompletedAt(
                LocalDateTime.now()
        );

        return quizAttemptRepository.save(
                attempt
        );
    }

    // =========================================================
    // DELETE ATTEMPT
    // =========================================================

    @Override
    public void deleteAttempt(
            Long attemptId
    ) {

        /*
         * getAttemptById() verifies ownership.
         */
        QuizAttempt attempt =
                getAttemptById(attemptId);

        /*
         * Do not allow modification of
         * a completed attempt.
         */
        if (attempt.getCompletedAt() != null) {

            throw new IllegalStateException(
                    "Completed quiz attempt cannot be deleted"
            );
        }

        quizAttemptRepository.delete(
                attempt
        );
    }
}