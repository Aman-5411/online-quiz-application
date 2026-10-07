import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import {
    getUserAttempts,
    getQuizAttemptResult
} from "../../services/quizAttemptService";

const MyAttempts = () => {
    const { user } = useAuth();
    const navigate = useNavigate();

    const [attempts, setAttempts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadAttempts = async () => {
            try {
                setLoading(true);
                setError("");

                const data = await getUserAttempts(user.id);

                setAttempts(data);
            } catch (err) {
                console.error("Failed to load attempts:", err);
                setError("Failed to load quiz attempts.");
            } finally {
                setLoading(false);
            }
        };

        if (user?.id) {
            loadAttempts();
        }
    }, [user]);

    const handleViewResult = async (attemptId) => {
        try {
            await getQuizAttemptResult(attemptId);

            navigate(`/quiz/result/${attemptId}`);
        } catch (err) {
            console.error("Failed to load attempt result:", err);
            alert("Unable to load quiz result.");
        }
    };

    if (loading) {
        return (
            <div className="attempts-page">
                <div className="attempts-state-card">
                    <div className="attempts-loading-spinner"></div>
                    <h2>Loading attempts...</h2>
                    <p>Please wait while we load your quiz history.</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="attempts-page">
                <div className="attempts-state-card attempts-error">
                    <div className="attempts-state-icon">!</div>
                    <h2>Unable to Load Attempts</h2>
                    <p>{error}</p>

                    <button
                        className="attempts-primary-button"
                        onClick={() => navigate("/dashboard")}
                    >
                        Back to Dashboard
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="attempts-page">

            {/* Header */}
            <header className="attempts-header">
                <div className="attempts-header-inner">

                    <div className="attempts-brand">
                        <div className="attempts-logo">Q</div>

                        <div>
                            <h1>Quiz App</h1>
                            <span>My Attempts</span>
                        </div>
                    </div>

                    <button
                        className="attempts-back-button"
                        onClick={() => navigate("/dashboard")}
                    >
                        Dashboard
                    </button>

                </div>
            </header>

            {/* Main Content */}
            <main className="attempts-container">

                <section className="attempts-intro">
                    <p className="attempts-eyebrow">QUIZ HISTORY</p>

                    <h2>Your Attempts</h2>

                    <p>
                        Review your previous quiz attempts and view detailed
                        results for completed quizzes.
                    </p>
                </section>

                {attempts.length === 0 ? (
                    <div className="attempts-empty-card">

                        <div className="attempts-empty-icon">
                            ✓
                        </div>

                        <h2>No Attempts Yet</h2>

                        <p>
                            You haven't attempted any quizzes yet.
                            Start a quiz from your dashboard to see your
                            results here.
                        </p>

                        <button
                            className="attempts-primary-button"
                            onClick={() => navigate("/dashboard")}
                        >
                            Browse Quizzes
                        </button>

                    </div>
                ) : (
                    <section className="attempts-list">

                        {attempts.map((attempt) => {

                            const completed = Boolean(attempt.completedAt);

                            return (
                                <article
                                    className="attempt-card"
                                    key={attempt.id}
                                >

                                    <div className="attempt-card-main">

                                        <div className="attempt-card-heading">
                                            <p className="attempt-card-label">
                                                QUIZ ATTEMPT
                                            </p>

                                            <h2>
                                                {attempt.quizTitle}
                                            </h2>
                                        </div>

                                        <span
                                            className={`attempt-status ${
                                                completed
                                                    ? "attempt-status-completed"
                                                    : "attempt-status-progress"
                                            }`}
                                        >
                                            {completed
                                                ? "Completed"
                                                : "In Progress"}
                                        </span>

                                    </div>

                                    <div className="attempt-stats">

                                        <div className="attempt-stat">
                                            <span className="attempt-stat-label">
                                                Score
                                            </span>

                                            <strong>
                                                {attempt.score} /{" "}
                                                {attempt.totalQuestions}
                                            </strong>
                                        </div>

                                        <div className="attempt-stat">
                                            <span className="attempt-stat-label">
                                                Correct Answers
                                            </span>

                                            <strong>
                                                {attempt.correctAnswers}
                                            </strong>
                                        </div>

                                        <div className="attempt-stat">
                                            <span className="attempt-stat-label">
                                                Percentage
                                            </span>

                                            <strong>
                                                {Math.round(
                                                    attempt.percentage
                                                )}
                                                %
                                            </strong>
                                        </div>

                                    </div>

                                    <div className="attempt-card-footer">

                                        <span className="attempt-id">
                                            Attempt #{attempt.id}
                                        </span>

                                        {completed && (
                                            <button
                                                className="attempt-result-button"
                                                onClick={() =>
                                                    handleViewResult(
                                                        attempt.id
                                                    )
                                                }
                                            >
                                                View Result
                                                <span>→</span>
                                            </button>
                                        )}

                                    </div>

                                </article>
                            );
                        })}

                    </section>
                )}

                <div className="attempts-footer">
                    <button
                        className="attempts-secondary-button"
                        onClick={() => navigate("/dashboard")}
                    >
                        ← Back to Dashboard
                    </button>
                </div>

            </main>
        </div>
    );
};

export default MyAttempts;