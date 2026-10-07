import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getQuizAttemptResult } from "../../services/quizAttemptService";

const QuizResult = () => {

    const { attemptId } = useParams();
    const navigate = useNavigate();

    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        const loadResult = async () => {

            try {

                setLoading(true);
                setError("");

                const data =
                    await getQuizAttemptResult(attemptId);

                setResult(data);

            } catch (err) {

                console.error(
                    "Failed to load quiz result:",
                    err
                );

                setError(
                    "Failed to load quiz result."
                );

            } finally {

                setLoading(false);
            }
        };

        loadResult();

    }, [attemptId]);

    if (loading) {

        return (
            <div className="result-page">

                <div className="result-state-card">

                    <div className="result-state-icon">
                        Q
                    </div>

                    <h2>
                        Loading result...
                    </h2>

                    <p>
                        Calculating your quiz performance.
                    </p>

                </div>

            </div>
        );
    }

    if (error) {

        return (
            <div className="result-page">

                <div className="result-state-card">

                    <div className="result-state-icon result-error-icon">
                        !
                    </div>

                    <h2>
                        Unable to load result
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

    if (!result) {

        return (
            <div className="result-page">

                <div className="result-state-card">

                    <div className="result-state-icon">
                        ?
                    </div>

                    <h2>
                        No result found
                    </h2>

                    <p>
                        We couldn't find the requested quiz result.
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

    return (
        <div className="result-page">

            {/* =================================================
                HEADER
                ================================================= */}

            <header className="result-header">

                <div className="result-header-inner">

                    <div className="result-brand">

                        <div className="result-logo">
                            Q
                        </div>

                        <span>
                            Quiz App
                        </span>

                    </div>

                </div>

            </header>


            {/* =================================================
                RESULT CONTENT
                ================================================= */}

            <main className="result-container">

                <section className="result-card">

                    {/* COMPLETION ICON */}

                    <div className="result-completion-icon">
                        ✓
                    </div>

                    <p className="result-eyebrow">
                        Quiz Completed
                    </p>

                    <h1>
                        Well done!
                    </h1>

                    <h2 className="result-quiz-title">
                        {result.quizTitle}
                    </h2>

                    <p className="result-subtitle">
                        Here is a summary of your performance.
                    </p>


                    {/* =================================================
                        SCORE
                        ================================================= */}

                    <div className="result-score">

                        <span className="result-score-value">
                            {Math.round(result.percentage)}%
                        </span>

                        <span className="result-score-label">
                            Overall Score
                        </span>

                    </div>


                    {/* =================================================
                        STATISTICS
                        ================================================= */}

                    <div className="result-statistics">

                        <div className="result-stat">

                            <span className="result-stat-value">
                                {result.score}
                            </span>

                            <span className="result-stat-label">
                                Score
                            </span>

                        </div>


                        <div className="result-stat">

                            <span className="result-stat-value">
                                {result.correctAnswers}
                            </span>

                            <span className="result-stat-label">
                                Correct
                            </span>

                        </div>


                        <div className="result-stat">

                            <span className="result-stat-value">
                                {result.totalQuestions}
                            </span>

                            <span className="result-stat-label">
                                Questions
                            </span>

                        </div>

                    </div>


                    {/* =================================================
                        ACTIONS
                        ================================================= */}

                    <div className="result-actions">

                        <button
                            className="primary-button"
                            onClick={() =>
                                navigate("/dashboard")
                            }
                        >
                            Back to Dashboard
                        </button>

                        <button
                            className="secondary-button"
                            onClick={() =>
                                navigate("/attempts")
                            }
                        >
                            View My Attempts
                        </button>

                    </div>

                </section>

            </main>

        </div>
    );
};

export default QuizResult;