package com.quizapp.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.quizapp.dto.QuizSummaryResponse;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.enums.Difficulty;
import com.quizapp.enums.QuizSource;
import com.quizapp.enums.QuizStatus;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.UserRepository;
import com.quizapp.security.CustomUserDetails;
import com.quizapp.service.QuizService;

@Service
@Transactional
public class QuizServiceImpl implements QuizService {
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    public QuizServiceImpl(QuizRepository quizRepository,
                UserRepository userRepository
    ) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Quiz createQuiz(Quiz quiz) {

        if (quiz.getTitle() == null
                || quiz.getTitle().isBlank()) {

            throw new IllegalArgumentException(
                    "Quiz title cannot be empty");
        }

        if (quiz.getCategory() == null
                || quiz.getCategory().isBlank()) {

            throw new IllegalArgumentException(
                    "Quiz category cannot be empty");
        }

        if (quiz.getDifficulty() == null) {

            throw new IllegalArgumentException(
                    "Quiz difficulty is required");
        }

        /*
         * Get the currently authenticated user
         * from the JWT.
         */
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof CustomUserDetails)) {

            throw new SecurityException(
                    "User is not authenticated");
        }

        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

        Long currentUserId = currentUser.getUserId();

        /*
         * Load the actual User from the database.
         */
        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException(
                        "Authenticated user not found"));

        /*
         * The backend determines the creator.
         * We do NOT trust createdBy from the request body.
         */
        quiz.setCreatedBy(creator);

        return quizRepository.save(quiz);
    }


    @Override
    @Transactional(readOnly = true)
    public Quiz getQuizById(Long id) {

        return quizRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Quiz not found with id: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesByStatus(QuizStatus status) {
        return quizRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSummaryResponse> getPublishedQuizSummaries() {

        return quizRepository
                .findByStatus(QuizStatus.PUBLISHED)
                .stream()
                .map(quiz -> new QuizSummaryResponse(
                        quiz.getId(),
                        quiz.getTitle(),
                        quiz.getDescription(),
                        quiz.getCategory(),
                        quiz.getDifficulty(),
                        quiz.getQuestions().size()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesByCategory(String category) {
        return quizRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesByDifficulty(
            Difficulty difficulty
    ) {
        return quizRepository.findByDifficulty(difficulty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesBySource(
            QuizSource source
    ) {
        return quizRepository.findBySource(source);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesCreatedByUser(Long userId) {
        return quizRepository.findByCreatedById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesByStatusAndCategory(
            QuizStatus status,
            String category
    ) {
        return quizRepository.findByStatusAndCategory(
                status,
                category
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesByStatusAndDifficulty(
            QuizStatus status,
            Difficulty difficulty
    ) {
        return quizRepository.findByStatusAndDifficulty(
                status,
                difficulty
        );
    }

    @Override
    public Quiz updateQuiz(Long id, Quiz quiz) {

        Quiz existingQuiz = getQuizById(id);

        /*
         * Do not allow editing a published quiz through
         * the generic update operation.
         *
         * We will later enforce the complete admin
         * authorization through Spring Security.
         */
        if (existingQuiz.getStatus() == QuizStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Published quiz cannot be updated"
            );
        }

        if (quiz.getTitle() != null
                && !quiz.getTitle().isBlank()) {
            existingQuiz.setTitle(quiz.getTitle());
        }

        if (quiz.getDescription() != null) {
            existingQuiz.setDescription(
                    quiz.getDescription()
            );
        }

        if (quiz.getCategory() != null
                && !quiz.getCategory().isBlank()) {
            existingQuiz.setCategory(
                    quiz.getCategory()
            );
        }

        if (quiz.getDifficulty() != null) {
            existingQuiz.setDifficulty(
                    quiz.getDifficulty()
            );
        }

        if (quiz.getSource() != null) {
            existingQuiz.setSource(
                    quiz.getSource()
            );
        }

        return quizRepository.save(existingQuiz);
    }

    @Override
public Quiz publishQuiz(Long id) {

    Quiz quiz = getQuizById(id);

    /*
     * A quiz can only be published once.
     */
    if (quiz.getStatus() == QuizStatus.PUBLISHED) {

        throw new IllegalStateException(
                "Quiz is already published"
        );
    }

    /*
     * A quiz must contain at least one question.
     */
    if (quiz.getQuestions() == null
            || quiz.getQuestions().isEmpty()) {

        throw new IllegalStateException(
                "A quiz must contain at least one question before publishing"
        );
    }

    /*
     * The quiz must currently be in DRAFT state.
     */
    if (quiz.getStatus() != QuizStatus.DRAFT) {

        throw new IllegalStateException(
                "Only draft quizzes can be published"
        );
    }

    /*
     * Publish the quiz.
     */
    quiz.setStatus(QuizStatus.PUBLISHED);
    quiz.setPublishedAt(LocalDateTime.now());

    return quizRepository.save(quiz);
}

    @Override
    public void deleteQuiz(Long id) {

        Quiz quiz = getQuizById(id);

        if (quiz.getStatus() == QuizStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Published quiz cannot be deleted"
            );
        }

        quizRepository.delete(quiz);
    }
}