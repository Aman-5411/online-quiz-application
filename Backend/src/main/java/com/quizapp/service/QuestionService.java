package com.quizapp.service;

import java.util.List;

import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.dto.QuestionResponse;

public interface QuestionService {

    Question createQuestion(Long quizId, Question question);

    Question getQuestionById(Long id);

    List<Question> getQuestionsByQuizId(Long quizId);

    List<QuestionResponse> getQuestionsForQuizAttempt(Long quizId);

    Question updateQuestion(Long id, Question question);

    void deleteQuestion(Long id);

    Option addOption(Long questionId, Option option);

    Option updateOption(Long optionId, Option option);

    void deleteOption(Long optionId);

    List<Option> getOptionsByQuestionId(Long questionId);
}