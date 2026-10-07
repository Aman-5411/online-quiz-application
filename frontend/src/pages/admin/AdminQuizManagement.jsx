import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";

const AdminQuizManagement = () => {
    const navigate = useNavigate();

    const [quizzes, setQuizzes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const loadQuizzes = async () => {
        try {
            setLoading(true);
            setError("");

            const response = await api.get("/quizzes");

            setQuizzes(response.data);
        } catch (err) {
            console.error("Failed to load quizzes:", err);
            setError("Failed to load quizzes.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadQuizzes();
    }, []);

    const handleDelete = async (quizId) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this quiz?"
        );

        if (!confirmed) {
            return;
        }

        try {
            await api.delete(`/quizzes/${quizId}`);

            await loadQuizzes();
        } catch (err) {
            console.error("Failed to delete quiz:", err);

            alert(
                err.response?.data?.message ||
                "Failed to delete quiz."
            );
        }
    };

    const handlePublish = async (quizId) => {
        const confirmed = window.confirm(
            "Are you sure you want to publish this quiz?"
        );

        if (!confirmed) {
            return;
        }

        try {
            await api.post(`/quizzes/${quizId}/publish`);

            await loadQuizzes();
        } catch (err) {
            console.error("Failed to publish quiz:", err);

            alert(
                err.response?.data?.message ||
                "Failed to publish quiz."
            );
        }
    };

    if (loading) {
        return (
            <div className="admin-quiz-page">
                <div className="admin-quiz-state-card">
                    <div className="admin-quiz-loading-spinner"></div>

                    <h2>Loading quizzes...</h2>

                    <p>
                        Please wait while we load the quiz management
                        data.
                    </p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="admin-quiz-page">
                <div className="admin-quiz-state-card admin-quiz-error">

                    <div className="admin-quiz-state-icon">
                        !
                    </div>

                    <h2>Unable to Load Quizzes</h2>

                    <p>{error}</p>

                    <button
                        className="admin-quiz-primary-button"
                        onClick={loadQuizzes}
                    >
                        Try Again
                    </button>

                </div>
            </div>
        );
    }

    return (
        <div className="admin-quiz-page">

            {/* HEADER */}

            <header className="admin-quiz-header">

                <div className="admin-quiz-header-inner">

                    <div className="admin-quiz-brand">
                        <div className="admin-quiz-logo">
                            Q
                        </div>

                        <div>
                            <h1>Quiz App</h1>
                            <span>Administration</span>
                        </div>
                    </div>

                    <button
                        className="admin-quiz-dashboard-button"
                        onClick={() => navigate("/admin")}
                    >
                        ← Dashboard
                    </button>

                </div>

            </header>


            {/* MAIN */}

            <main className="admin-quiz-container">

                <section className="admin-quiz-intro">

                    <div>
                        <p className="admin-quiz-eyebrow">
                            QUIZ MANAGEMENT
                        </p>

                        <h2>Manage Quizzes</h2>

                        <p>
                            Create, update, publish and manage quizzes
                            from one place.
                        </p>
                    </div>

                    <button
                        className="admin-quiz-create-button"
                        onClick={() =>
                            navigate("/admin/quizzes/create")
                        }
                    >
                        <span>+</span>
                        Create Quiz
                    </button>

                </section>


                {/* SUMMARY */}

                <section className="admin-quiz-summary">

                    <div className="admin-quiz-summary-item">
                        <span>Total Quizzes</span>
                        <strong>{quizzes.length}</strong>
                    </div>

                    <div className="admin-quiz-summary-item">
                        <span>Drafts</span>
                        <strong>
                            {
                                quizzes.filter(
                                    (quiz) => quiz.status === "DRAFT"
                                ).length
                            }
                        </strong>
                    </div>

                    <div className="admin-quiz-summary-item">
                        <span>Published</span>
                        <strong>
                            {
                                quizzes.filter(
                                    (quiz) =>
                                        quiz.status === "PUBLISHED"
                                ).length
                            }
                        </strong>
                    </div>

                </section>


                {/* QUIZ LIST */}

                {quizzes.length === 0 ? (

                    <div className="admin-quiz-empty-card">

                        <div className="admin-quiz-empty-icon">
                            Q
                        </div>

                        <h2>No Quizzes Found</h2>

                        <p>
                            There are currently no quizzes in the system.
                            Create your first quiz to get started.
                        </p>

                        <button
                            className="admin-quiz-primary-button"
                            onClick={() =>
                                navigate("/admin/quizzes/create")
                            }
                        >
                            Create Your First Quiz
                        </button>

                    </div>

                ) : (

                    <section className="admin-quiz-list">

                        {quizzes.map((quiz) => (

                            <article
                                key={quiz.id}
                                className="admin-quiz-card"
                            >

                                {/* Quiz Heading */}

                                <div className="admin-quiz-card-top">

                                    <div className="admin-quiz-card-title">

                                        <span className="admin-quiz-number">
                                            #{quiz.id}
                                        </span>

                                        <div>
                                            <h3>
                                                {quiz.title}
                                            </h3>

                                            <p>
                                                {quiz.description ||
                                                    "No description provided."}
                                            </p>
                                        </div>

                                    </div>

                                    <span
                                        className={`admin-quiz-status ${
                                            quiz.status === "PUBLISHED"
                                                ? "admin-quiz-status-published"
                                                : "admin-quiz-status-draft"
                                        }`}
                                    >
                                        {quiz.status}
                                    </span>

                                </div>


                                {/* Quiz Details */}

                                <div className="admin-quiz-details">

                                    <div className="admin-quiz-detail">

                                        <span>Category</span>

                                        <strong>
                                            {quiz.category || "—"}
                                        </strong>

                                    </div>

                                    <div className="admin-quiz-detail">

                                        <span>Difficulty</span>

                                        <strong
                                            className={`admin-quiz-difficulty admin-quiz-difficulty-${String(
                                                quiz.difficulty || ""
                                            ).toLowerCase()}`}
                                        >
                                            {quiz.difficulty || "—"}
                                        </strong>

                                    </div>

                                    <div className="admin-quiz-detail">

                                        <span>Source</span>

                                        <strong>
                                            {quiz.source || "—"}
                                        </strong>

                                    </div>

                                </div>


                                {/* Actions */}

                                <div className="admin-quiz-card-footer">

                                    <span className="admin-quiz-management-label">
                                        Quiz #{quiz.id}
                                    </span>

                                    <div className="admin-quiz-actions">

                                        <button
                                            className="admin-quiz-edit-button"
                                            onClick={() =>
                                                navigate(
                                                    `/admin/quizzes/edit/${quiz.id}`
                                                )
                                            }
                                        >
                                            Edit
                                        </button>

                                        {quiz.status === "DRAFT" && (
                                            <button
                                                className="admin-quiz-publish-button"
                                                onClick={() =>
                                                    handlePublish(
                                                        quiz.id
                                                    )
                                                }
                                            >
                                                Publish
                                            </button>
                                        )}

                                        {quiz.status !== "PUBLISHED" && (
                                            <button
                                                className="admin-quiz-delete-button"
                                                onClick={() =>
                                                    handleDelete(
                                                        quiz.id
                                                    )
                                                }
                                            >
                                                Delete
                                            </button>
                                        )}

                                    </div>

                                </div>

                            </article>

                        ))}

                    </section>

                )}

            </main>

        </div>
    );
};

export default AdminQuizManagement;