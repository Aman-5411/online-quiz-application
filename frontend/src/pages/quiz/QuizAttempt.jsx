import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useRef, useState } from "react";

import {
    getQuizQuestionsForAttempt,
    submitQuizAnswer,
    submitQuizAttempt,
    getQuizAttemptResult
} from "../../services/quizAttemptService";

const QuizAttempt = () => {

    const navigate = useNavigate();

    const { quizId, attemptId } = useParams();

    const [questions, setQuestions] = useState([]);
    const [currentQuestionIndex, setCurrentQuestionIndex] =
        useState(0);

    const [selectedOptions, setSelectedOptions] =
        useState({});

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [remainingSeconds, setRemainingSeconds] =
        useState(null);

    const [durationMinutes, setDurationMinutes] =
        useState(null);

    // Prevent automatic submission from happening more than once
    const autoSubmitTriggered = useRef(false);

    // =========================================================
    // LOAD QUIZ QUESTIONS + ATTEMPT
    // =========================================================

    useEffect(() => {

        const loadQuiz = async () => {

            try {

                setLoading(true);
                setError("");

                const [
                    questionsData,
                    attemptData
                ] = await Promise.all([
                    getQuizQuestionsForAttempt(quizId),
                    getQuizAttemptResult(attemptId)
                ]);

                setQuestions(questionsData);
                setDurationMinutes(
                    attemptData.durationMinutes
                );

                // If the attempt was already completed,
                // go directly to the result page.
                if (attemptData.completedAt) {

                    navigate(
                        `/quiz/result/${attemptId}`
                    );

                    return;
                }

                /*
                 * Calculate remaining time from the actual
                 * attempt start time.
                 *
                 * This prevents refreshing the page from
                 * resetting the timer.
                 */

                const startedAt = new Date(
                    attemptData.startedAt
                ).getTime();

                const durationMilliseconds =
                    attemptData.durationMinutes *
                    60 *
                    1000;

                const expirationTime =
                    startedAt +
                    durationMilliseconds;

                const calculateRemainingTime = () => {

                    const now = Date.now();

                    const remainingMilliseconds =
                        expirationTime - now;

                    const seconds = Math.max(
                        0,
                        Math.ceil(
                            remainingMilliseconds / 1000
                        )
                    );

                    setRemainingSeconds(seconds);

                    return seconds;
                };

                calculateRemainingTime();

            } catch (err) {

                console.error(
                    "Failed to load quiz:",
                    err
                );

                setError(
                    "Failed to load quiz."
                );

            } finally {

                setLoading(false);
            }
        };

        loadQuiz();

    }, [quizId, attemptId, navigate]);

    // =========================================================
    // AUTOMATIC SUBMISSION
    // =========================================================

    const handleAutomaticSubmit = async () => {

        // Prevent duplicate automatic submission
        if (autoSubmitTriggered.current) {
            return;
        }

        autoSubmitTriggered.current = true;

        try {

            const currentQuestion =
                questions[currentQuestionIndex];

            /*
             * If the current question has a selected answer,
             * save it before submitting the attempt.
             */

            if (currentQuestion) {

                const selectedOptionId =
                    selectedOptions[currentQuestion.id];

                if (selectedOptionId) {

                    try {

                        await submitQuizAnswer(
                            attemptId,
                            currentQuestion.id,
                            selectedOptionId
                        );

                    } catch (answerError) {

                        console.error(
                            "Failed to save current answer before automatic submission:",
                            answerError
                        );
                    }
                }
            }

            // Submit the complete attempt
            await submitQuizAttempt(attemptId);

            navigate(
                `/quiz/result/${attemptId}`
            );

        } catch (err) {

            console.error(
                "Failed to automatically submit quiz:",
                err
            );

            /*
             * The backend scheduler may have already
             * submitted the attempt.
             *
             * Try loading the result page anyway.
             */

            navigate(
                `/quiz/result/${attemptId}`
            );
        }
    };

    // =========================================================
    // COUNTDOWN TIMER
    // =========================================================

    useEffect(() => {

        if (remainingSeconds === null) {
            return;
        }

        if (remainingSeconds <= 0) {

            handleAutomaticSubmit();

            return;
        }

        const timer = setInterval(() => {

            setRemainingSeconds((previous) => {

                if (previous <= 1) {
                    return 0;
                }

                return previous - 1;
            });

        }, 1000);

        return () => {
            clearInterval(timer);
        };

    }, [remainingSeconds]);

    // =========================================================
    // SELECT OPTION
    // =========================================================

    const handleOptionSelect = (optionId) => {

        const currentQuestion =
            questions[currentQuestionIndex];

        setSelectedOptions((previous) => ({
            ...previous,
            [currentQuestion.id]: optionId
        }));
    };

    // =========================================================
    // NEXT QUESTION
    // =========================================================

    const handleNext = async () => {

        const currentQuestion =
            questions[currentQuestionIndex];

        const selectedOptionId =
            selectedOptions[currentQuestion.id];

        // Don't move forward without selecting an option
        if (!selectedOptionId) {

            alert(
                "Please select an option before continuing."
            );

            return;
        }

        try {

            await submitQuizAnswer(
                attemptId,
                currentQuestion.id,
                selectedOptionId
            );

            if (
                currentQuestionIndex <
                questions.length - 1
            ) {

                setCurrentQuestionIndex(
                    (previous) => previous + 1
                );
            }

        } catch (err) {

            console.error(
                "Failed to submit answer:",
                err
            );

            alert(
                "Failed to save your answer. Please try again."
            );
        }
    };

    // =========================================================
    // MANUAL SUBMIT
    // =========================================================

    const handleSubmitQuiz = async () => {

        const currentQuestion =
            questions[currentQuestionIndex];

        const selectedOptionId =
            selectedOptions[currentQuestion.id];

        // Make sure the final question is answered
        if (!selectedOptionId) {

            alert(
                "Please select an option before submitting the quiz."
            );

            return;
        }

        try {

            // Save the answer for the final question
            await submitQuizAnswer(
                attemptId,
                currentQuestion.id,
                selectedOptionId
            );

            // Submit the complete attempt
            await submitQuizAttempt(attemptId);

            navigate(
                `/quiz/result/${attemptId}`
            );

        } catch (err) {

            console.error(
                "Failed to submit quiz:",
                err
            );

            alert(
                "Failed to submit quiz. Please try again."
            );
        }
    };

    // =========================================================
    // PREVIOUS QUESTION
    // =========================================================

    const handlePrevious = () => {

        if (currentQuestionIndex > 0) {

            setCurrentQuestionIndex(
                (previous) => previous - 1
            );
        }
    };

    // =========================================================
    // LOADING / ERROR
    // =========================================================

    if (loading) {

        return (
            <div className="quiz-attempt-page">
                <div className="quiz-state-card">
                    <div className="quiz-loading-spinner">
                        Q
                    </div>

                    <h2>
                        Loading quiz...
                    </h2>

                    <p>
                        Preparing your questions.
                    </p>
                </div>
            </div>
        );
    }

    if (error) {

        return (
            <div className="quiz-attempt-page">
                <div className="quiz-state-card">

                    <div className="quiz-state-icon error-state">
                        !
                    </div>

                    <h2>
                        Unable to load quiz
                    </h2>

                    <p>
                        {error}
                    </p>

                    <button
                        className="secondary-button"
                        onClick={() =>
                            navigate("/dashboard")
                        }
                    >
                        Back to Dashboard
                    </button>

                </div>
            </div>
        );
    }

    if (questions.length === 0) {

        return (
            <div className="quiz-attempt-page">
                <div className="quiz-state-card">

                    <div className="quiz-state-icon">
                        ?
                    </div>

                    <h2>
                        No questions available
                    </h2>

                    <p>
                        There are no questions available
                        for this quiz.
                    </p>

                    <button
                        className="secondary-button"
                        onClick={() =>
                            navigate("/dashboard")
                        }
                    >
                        Back to Dashboard
                    </button>

                </div>
            </div>
        );
    }

    // =========================================================
    // CURRENT QUESTION
    // =========================================================

    const currentQuestion =
        questions[currentQuestionIndex];

    // =========================================================
    // FORMAT TIMER
    // =========================================================

    const minutes =
        Math.floor(
            (remainingSeconds ?? 0) / 60
        );

    const seconds =
        (remainingSeconds ?? 0) % 60;

    const formattedTime =
        `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;

    const isTimeRunningLow =
        remainingSeconds !== null &&
        remainingSeconds <= 60;

    const progress =
        ((currentQuestionIndex + 1) /
            questions.length) *
        100;

    // =========================================================
    // UI
    // =========================================================

    return (
        <div className="quiz-attempt-page">

            {/* =================================================
                HEADER
                ================================================= */}

            <header className="quiz-attempt-header">

                <div className="quiz-attempt-header-inner">

                    <div className="quiz-attempt-brand">

                        <div className="quiz-attempt-logo">
                            Q
                        </div>

                        <div>
                            <strong>
                                Quiz App
                            </strong>

                            <span>
                                Quiz Attempt
                            </span>
                        </div>

                    </div>

                    <div
                        className={`quiz-timer ${
                            isTimeRunningLow
                                ? "quiz-timer-warning"
                                : ""
                        }`}
                    >

                        <span className="timer-label">
                            Time Remaining
                        </span>

                        <span className="timer-value">
                            {formattedTime}
                        </span>

                    </div>

                </div>

            </header>


            {/* =================================================
                MAIN CONTENT
                ================================================= */}

            <main className="quiz-attempt-container">

                {/* PROGRESS */}

                <div className="quiz-progress-section">

                    <div className="quiz-progress-info">

                        <span>
                            Question{" "}
                            <strong>
                                {currentQuestionIndex + 1}
                            </strong>{" "}
                            of{" "}
                            <strong>
                                {questions.length}
                            </strong>
                        </span>

                        <span>
                            {Math.round(progress)}%
                        </span>

                    </div>

                    <div className="quiz-progress-track">

                        <div
                            className="quiz-progress-bar"
                            style={{
                                width: `${progress}%`
                            }}
                        />

                    </div>

                </div>


                {/* QUESTION CARD */}

                <section className="question-card">

                    <div className="question-number">
                        Question {currentQuestionIndex + 1}
                    </div>

                    <h1 className="question-text">
                        {currentQuestion.questionText}
                    </h1>

                    <p className="question-instruction">
                        Select one answer.
                    </p>


                    {/* OPTIONS */}

                    <div className="question-options">

                        {currentQuestion.options.map(
                            (option, index) => {

                                const isSelected =
                                    selectedOptions[
                                        currentQuestion.id
                                    ] === option.id;

                                return (
                                    <label
                                        key={option.id}
                                        className={`option-card ${
                                            isSelected
                                                ? "option-card-selected"
                                                : ""
                                        }`}
                                    >

                                        <input
                                            type="radio"
                                            name={`question-${currentQuestion.id}`}
                                            value={option.id}
                                            checked={isSelected}
                                            onChange={() =>
                                                handleOptionSelect(
                                                    option.id
                                                )
                                            }
                                        />

                                        <span className="option-marker">
                                            {String.fromCharCode(
                                                65 + index
                                            )}
                                        </span>

                                        <span className="option-text">
                                            {option.optionText}
                                        </span>

                                    </label>
                                );
                            }
                        )}

                    </div>

                </section>


                {/* NAVIGATION */}

                <div className="quiz-navigation">

                    <button
                        className="secondary-button"
                        onClick={handlePrevious}
                        disabled={
                            currentQuestionIndex === 0
                        }
                    >
                        ← Previous
                    </button>


                    {currentQuestionIndex ===
                    questions.length - 1 ? (

                        <button
                            className="primary-button submit-quiz-button"
                            onClick={handleSubmitQuiz}
                        >
                            Submit Quiz
                        </button>

                    ) : (

                        <button
                            className="primary-button next-button"
                            onClick={handleNext}
                        >
                            Next →
                        </button>

                    )}

                </div>

            </main>

        </div>
    );
};

export default QuizAttempt;