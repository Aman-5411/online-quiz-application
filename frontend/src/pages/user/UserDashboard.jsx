import { useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext";
import { getPublishedQuizzes } from "../../services/quizService";
import { useNavigate } from "react-router-dom";
import { startQuizAttempt } from "../../services/quizAttemptService";

const UserDashboard = () => {

    const navigate = useNavigate();

    const { user, logoutUser } = useAuth();

    const [quizzes, setQuizzes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // =========================================================
    // FETCH QUIZZES
    // =========================================================

    useEffect(() => {

        const fetchQuizzes = async () => {

            try {

                setLoading(true);
                setError("");

                const data = await getPublishedQuizzes();

                setQuizzes(data);

            } catch (error) {

                console.error(
                    "Failed to fetch quizzes:",
                    error
                );

                setError(
                    "Unable to load quizzes."
                );

            } finally {

                setLoading(false);
            }
        };

        fetchQuizzes();

    }, []);

    // =========================================================
    // START QUIZ
    // =========================================================

    const handleStartQuiz = async (quizId) => {

        try {

            const attempt = await startQuizAttempt(
                user.id,
                quizId
            );

            navigate(
                `/quiz/${quizId}/attempt/${attempt.id}`
            );

        } catch (error) {

            console.error(
                "Failed to start quiz:",
                error
            );

            if (error.response?.data?.message) {

                setError(
                    error.response.data.message
                );

            } else {

                setError(
                    "Unable to start quiz."
                );
            }
        }
    };

    // =========================================================
    // LOGOUT
    // =========================================================

    const handleLogout = () => {

        logoutUser();

        navigate("/login", {
            replace: true
        });
    };

    return (
        <div className="dashboard-page">

            {/* =================================================
                HEADER
                ================================================= */}

            <header className="dashboard-header">

                <div className="dashboard-header-inner">

                    <div className="dashboard-brand">

                        <div className="dashboard-logo">
                            Q
                        </div>

                        <span>
                            Quiz App
                        </span>

                    </div>

                    <div className="dashboard-user">

                        <span>
                            Welcome, {user?.name}
                        </span>

                        <button
                            className="dashboard-logout"
                            onClick={handleLogout}
                        >
                            Logout
                        </button>

                    </div>

                </div>

            </header>


            {/* =================================================
                MAIN CONTENT
                ================================================= */}

            <main className="dashboard-container">

                {/* =================================================
                    WELCOME SECTION
                    ================================================= */}

                <section className="dashboard-welcome">

                    <div>

                        <p className="dashboard-eyebrow">
                            Dashboard
                        </p>

                        <h1>
                            Welcome back, {user?.name}
                        </h1>

                        <p className="dashboard-welcome-text">
                            Choose a quiz and test your knowledge.
                            Track your performance as you learn.
                        </p>

                    </div>

                    <div className="dashboard-welcome-icon">
                        ?
                    </div>

                </section>


                {/* =================================================
                    ERROR
                    ================================================= */}

                {error && (
                    <div className="dashboard-error">
                        {error}
                    </div>
                )}


                {/* =================================================
                    AVAILABLE QUIZZES
                    ================================================= */}

                <section className="dashboard-section">

                    <div className="section-heading">

                        <div>

                            <h2>
                                Available Quizzes
                            </h2>

                            <p>
                                Choose a quiz to begin your attempt.
                            </p>

                        </div>

                    </div>


                    {loading && (
                        <div className="dashboard-state">

                            <p>
                                Loading quizzes...
                            </p>

                        </div>
                    )}


                    {!loading &&
                        !error &&
                        quizzes.length === 0 && (

                            <div className="dashboard-state">

                                <div className="empty-state-icon">
                                    —
                                </div>

                                <h3>
                                    No quizzes available
                                </h3>

                                <p>
                                    There are currently no published
                                    quizzes available to take.
                                </p>

                            </div>
                        )
                    }


                    {!loading &&
                        !error &&
                        quizzes.length > 0 && (

                            <div className="quiz-grid">

                                {quizzes.map((quiz) => (

                                    <article
                                        className="quiz-card"
                                        key={quiz.id}
                                    >

                                        <div className="quiz-card-header">

                                            <span className="quiz-category">
                                                {quiz.category}
                                            </span>

                                            <span
                                                className={`quiz-difficulty difficulty-${quiz.difficulty?.toLowerCase()}`}
                                            >
                                                {quiz.difficulty}
                                            </span>

                                        </div>


                                        <h3>
                                            {quiz.title}
                                        </h3>


                                        <p className="quiz-description">
                                            {quiz.description ||
                                                "Test your knowledge with this quiz."}
                                        </p>


                                        <div className="quiz-card-footer">

                                            <button
                                                className="primary-button"
                                                onClick={() =>
                                                    handleStartQuiz(
                                                        quiz.id
                                                    )
                                                }
                                            >
                                                Start Quiz
                                            </button>

                                        </div>

                                    </article>

                                ))}

                            </div>
                        )}

                </section>


                {/*ATTEMPTS CARD*/}

                <section className="attempts-card">

                    <div className="attempts-content">

                        <div className="attempts-icon">
                            ✓
                        </div>

                        <div>

                            <h2>
                                My Attempts
                            </h2>

                            <p>
                                View your previous quiz attempts,
                                scores, and results.
                            </p>

                        </div>

                    </div>

                    <button
                        className="secondary-button"
                        onClick={() =>
                            navigate("/attempts")
                        }
                    >
                        View My Attempts
                    </button>

                </section>

            </main>

        </div>
    );
};

export default UserDashboard;