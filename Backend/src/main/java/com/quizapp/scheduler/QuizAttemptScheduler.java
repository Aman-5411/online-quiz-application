package com.quizapp.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.quizapp.entity.QuizAttempt;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.service.QuizAttemptService;

@Component
public class QuizAttemptScheduler {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAttemptService quizAttemptService;

    @Value("${quiz.attempt.duration-minutes:10}")
    private long durationMinutes;

    public QuizAttemptScheduler(
            QuizAttemptRepository quizAttemptRepository,
            QuizAttemptService quizAttemptService
    ) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizAttemptService = quizAttemptService;
    }

    @Scheduled(fixedRateString = "${quiz.attempt.scheduler-rate:600000}")
    @Transactional
    public void autoSubmitExpiredAttempts() {

        LocalDateTime cutoff =
                LocalDateTime.now()
                        .minusMinutes(durationMinutes);

        List<QuizAttempt> expiredAttempts =
                quizAttemptRepository
                        .findByCompletedAtIsNullAndStartedAtBefore(
                                cutoff
                        );

        for (QuizAttempt attempt : expiredAttempts) {

            quizAttemptService.autoSubmitAttempt(
                    attempt.getId()
            );

            System.out.println(
                    "Automatically submitted expired quiz attempt: "
                            + attempt.getId()
            );
        }
    }
}