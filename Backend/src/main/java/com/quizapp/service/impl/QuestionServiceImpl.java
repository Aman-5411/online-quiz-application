package com.quizapp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.quizapp.dto.OptionResponse;
import com.quizapp.dto.QuestionResponse;
import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.enums.QuizStatus;
import com.quizapp.repository.OptionRepository;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.service.QuestionService;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final QuizRepository quizRepository;

    public QuestionServiceImpl(
            QuestionRepository questionRepository,
            OptionRepository optionRepository,
            QuizRepository quizRepository
    ) {
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.quizRepository = quizRepository;
    }

    // =========================================================
    // CREATE QUESTION
    // =========================================================

    @Override
    public Question createQuestion(
            Long quizId,
            Question question
    ) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Quiz not found with id: " + quizId
                        )
                );

        /*
         * Published quizzes are immutable.
         */
        validateQuizIsEditable(quiz);

        validateQuestion(question);

        question.setQuiz(quiz);

        /*
         * Automatically assign the next question order
         * when the client does not provide one.
         */
        if (question.getQuestionOrder() == null) {

            long questionCount =
                    questionRepository.countByQuizId(quizId);

            question.setQuestionOrder(
                    (int) questionCount + 1
            );
        }

        return questionRepository.save(question);
    }

    // =========================================================
    // GET QUESTION BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Question getQuestionById(Long id) {

        return questionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Question not found with id: " + id
                        )
                );
    }

    // =========================================================
    // GET QUESTIONS BY QUIZ
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Question> getQuestionsByQuizId(
            Long quizId
    ) {

        /*
         * Make sure the quiz exists.
         */
        quizRepository.findById(quizId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Quiz not found with id: " + quizId
                        )
                );

        return questionRepository
                .findByQuizIdOrderByQuestionOrderAsc(quizId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsForQuizAttempt(
            Long quizId
    ) {

        // Make sure the quiz exists
        quizRepository.findById(quizId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Quiz not found with id: " + quizId
                        )
                );

        List<Question> questions =
                questionRepository
                        .findByQuizIdOrderByQuestionOrderAsc(quizId);

        return questions.stream()
                .map(question -> {

                    List<OptionResponse> options =
                            question.getOptions()
                                    .stream()
                                    .map(option ->
                                            new OptionResponse(
                                                    option.getId(),
                                                    option.getOptionText()
                                            )
                                    )
                                    .collect(Collectors.toList());

                    return new QuestionResponse(
                            question.getId(),
                            question.getQuestionText(),
                            question.getQuestionOrder(),
                            question.getQuestionType(),
                            question.getDifficulty(),
                            options
                    );
                })
                .collect(Collectors.toList());
    }


    // =========================================================
    // UPDATE QUESTION
    // =========================================================

    @Override
    public Question updateQuestion(
            Long id,
            Question question
    ) {

        Question existingQuestion =
                getQuestionById(id);

        Quiz quiz = existingQuestion.getQuiz();

        /*
         * Published quizzes cannot be modified.
         */
        validateQuizIsEditable(quiz);

        if (question.getQuestionText() != null
                && !question.getQuestionText().isBlank()) {

            existingQuestion.setQuestionText(
                    question.getQuestionText()
            );
        }

        if (question.getQuestionType() != null) {
            existingQuestion.setQuestionType(
                    question.getQuestionType()
            );
        }

        if (question.getDifficulty() != null) {
            existingQuestion.setDifficulty(
                    question.getDifficulty()
            );
        }

        if (question.getQuestionOrder() != null) {
            existingQuestion.setQuestionOrder(
                    question.getQuestionOrder()
            );
        }

        if (question.getExplanation() != null) {
            existingQuestion.setExplanation(
                    question.getExplanation()
            );
        }

        return questionRepository.save(
                existingQuestion
        );
    }

    // =========================================================
    // DELETE QUESTION
    // =========================================================

    @Override
    public void deleteQuestion(Long id) {

        Question question =
                getQuestionById(id);

        /*
         * Published quizzes cannot be modified.
         */
        validateQuizIsEditable(
                question.getQuiz()
        );

        questionRepository.delete(question);
    }

    // =========================================================
    // ADD OPTION
    // =========================================================

    @Override
    public Option addOption(
            Long questionId,
            Option option
    ) {

        Question question =
                getQuestionById(questionId);

        /*
         * Published quizzes cannot be modified.
         */
        validateQuizIsEditable(
                question.getQuiz()
        );

        validateOptionForQuestion(
                question,
                option
        );

        option.setQuestion(question);

        return optionRepository.save(option);
    }

    // =========================================================
    // UPDATE OPTION
    // =========================================================

    @Override
    public Option updateOption(
            Long optionId,
            Option option
    ) {

        Option existingOption =
                optionRepository.findById(optionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Option not found with id: "
                                                + optionId
                                )
                        );

        Question question =
                existingOption.getQuestion();

        /*
         * Published quizzes cannot be modified.
         */
        validateQuizIsEditable(
                question.getQuiz()
        );

        if (option.getOptionText() != null
                && !option.getOptionText().isBlank()) {

            existingOption.setOptionText(
                    option.getOptionText()
            );
        }

        /*
         * Only validate SINGLE_CHOICE when changing
         * an option from incorrect -> correct.
         */
        if (option.isCorrect()
                && !existingOption.isCorrect()) {

            validateOptionForQuestion(
                    question,
                    option
            );
        }

        existingOption.setCorrect(
                option.isCorrect()
        );

        return optionRepository.save(
                existingOption
        );
    }

    // =========================================================
    // DELETE OPTION
    // =========================================================

    @Override
    public void deleteOption(Long optionId) {

        Option option =
                optionRepository.findById(optionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Option not found with id: "
                                                + optionId
                                )
                        );

        /*
         * Published quizzes cannot be modified.
         */
        validateQuizIsEditable(
                option.getQuestion().getQuiz()
        );

        optionRepository.delete(option);
    }

    // =========================================================
    // GET OPTIONS BY QUESTION
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Option> getOptionsByQuestionId(
            Long questionId
    ) {

        /*
         * Ensures the question actually exists.
         */
        getQuestionById(questionId);

        return optionRepository
                .findByQuestionId(questionId);
    }

    // =========================================================
    // QUESTION VALIDATION
    // =========================================================

    private void validateQuestion(
            Question question
    ) {

        if (question.getQuestionText() == null
                || question.getQuestionText().isBlank()) {

            throw new IllegalArgumentException(
                    "Question text cannot be empty"
            );
        }

        if (question.getQuestionType() == null) {

            throw new IllegalArgumentException(
                    "Question type is required"
            );
        }

        if (question.getDifficulty() == null) {

            throw new IllegalArgumentException(
                    "Question difficulty is required"
            );
        }
    }

    // =========================================================
    // OPTION VALIDATION
    // =========================================================

    private void validateOptionForQuestion(
            Question question,
            Option option
    ) {

        if (option.getOptionText() == null
                || option.getOptionText().isBlank()) {

            throw new IllegalArgumentException(
                    "Option text cannot be empty"
            );
        }

        /*
         * An incorrect option does not require
         * any additional validation.
         */
        if (!option.isCorrect()) {
            return;
        }

        /*
         * SINGLE_CHOICE questions can have only
         * one correct option.
         */
        if (question.getQuestionType()
                == com.quizapp.enums.QuestionType.SINGLE_CHOICE) {

            long correctCount =
                    optionRepository
                            .countByQuestionIdAndCorrectTrue(
                                    question.getId()
                            );

            if (correctCount > 0) {

                throw new IllegalArgumentException(
                        "A SINGLE_CHOICE question can have only one correct option"
                );
            }
        }

        /*
         * MULTIPLE_CHOICE questions are allowed
         * to have multiple correct options.
         */
    }

    // =========================================================
    // QUIZ EDITABILITY VALIDATION
    // =========================================================

    private void validateQuizIsEditable(
            Quiz quiz
    ) {

        if (quiz == null) {

            throw new IllegalStateException(
                    "Question is not associated with a quiz"
            );
        }

        if (quiz.getStatus() == QuizStatus.PUBLISHED) {

            throw new IllegalStateException(
                    "Published quiz cannot be modified"
            );
        }
    }
}