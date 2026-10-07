import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";

const CreateQuiz = () => {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        title: "",
        description: "",
        category: "",
        difficulty: "EASY",
        source: "MANUAL"
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((previous) => ({
            ...previous,
            [name]: value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        try {
            setLoading(true);
            setError("");

            const response = await api.post(
                "/quizzes",
                formData
            );

            const createdQuiz = response.data;

            navigate(
                `/admin/quizzes/edit/${createdQuiz.id}`
            );
        } catch (err) {
            console.error("Failed to create quiz:", err);

            setError(
                err.response?.data?.message ||
                "Failed to create quiz."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="create-quiz-page">

            {/* Header */}

            <header className="create-quiz-header">

                <div className="create-quiz-header-inner">

                    <div className="create-quiz-brand">
                        <div className="create-quiz-logo">
                            Q
                        </div>

                        <div>
                            <h1>Quiz App</h1>
                            <span>Administration</span>
                        </div>
                    </div>

                    <button
                        type="button"
                        className="create-quiz-dashboard-button"
                        onClick={() =>
                            navigate("/admin/quizzes")
                        }
                        disabled={loading}
                    >
                        ← Quiz Management
                    </button>

                </div>

            </header>


            {/* Main */}

            <main className="create-quiz-container">

                <div className="create-quiz-heading">

                    <p className="create-quiz-eyebrow">
                        QUIZ MANAGEMENT
                    </p>

                    <h2>Create Quiz</h2>

                    <p>
                        Enter the basic information for your new quiz.
                        You can add questions and options after creating it.
                    </p>

                </div>


                {/* Form Card */}

                <section className="create-quiz-card">

                    {error && (
                        <div className="create-quiz-error">
                            <span>!</span>

                            <div>
                                <strong>
                                    Unable to create quiz
                                </strong>

                                <p>{error}</p>
                            </div>
                        </div>
                    )}


                    <form onSubmit={handleSubmit}>

                        {/* Title */}

                        <div className="create-quiz-field">

                            <label htmlFor="title">
                                Quiz Title
                            </label>

                            <input
                                id="title"
                                type="text"
                                name="title"
                                value={formData.title}
                                onChange={handleChange}
                                placeholder="e.g. Java Fundamentals"
                                required
                                disabled={loading}
                            />

                            <span className="create-quiz-help">
                                Give your quiz a clear and descriptive title.
                            </span>

                        </div>


                        {/* Description */}

                        <div className="create-quiz-field">

                            <label htmlFor="description">
                                Description
                            </label>

                            <textarea
                                id="description"
                                name="description"
                                value={formData.description}
                                onChange={handleChange}
                                placeholder="Briefly describe what this quiz covers..."
                                rows="4"
                                disabled={loading}
                            />

                            <span className="create-quiz-help">
                                A short description helps users understand
                                the purpose of the quiz.
                            </span>

                        </div>


                        {/* Category */}

                        <div className="create-quiz-field">

                            <label htmlFor="category">
                                Category
                            </label>

                            <input
                                id="category"
                                type="text"
                                name="category"
                                value={formData.category}
                                onChange={handleChange}
                                placeholder="e.g. Java, SQL, Spring Boot"
                                required
                                disabled={loading}
                            />

                        </div>


                        {/* Difficulty + Source */}

                        <div className="create-quiz-form-row">

                            <div className="create-quiz-field">

                                <label htmlFor="difficulty">
                                    Difficulty
                                </label>

                                <select
                                    id="difficulty"
                                    name="difficulty"
                                    value={formData.difficulty}
                                    onChange={handleChange}
                                    disabled={loading}
                                >
                                    <option value="EASY">
                                        Easy
                                    </option>

                                    <option value="MEDIUM">
                                        Medium
                                    </option>

                                    <option value="HARD">
                                        Hard
                                    </option>
                                </select>

                            </div>


                            <div className="create-quiz-field">

                                <label htmlFor="source">
                                    Source
                                </label>

                                <select
                                    id="source"
                                    name="source"
                                    value={formData.source}
                                    onChange={handleChange}
                                    disabled={loading}
                                >
                                    <option value="MANUAL">
                                        Manual
                                    </option>

                                    <option value="AI_GENERATED">
                                        AI Generated
                                    </option>
                                </select>

                            </div>

                        </div>


                        {/* Actions */}

                        <div className="create-quiz-actions">

                            <button
                                type="button"
                                className="create-quiz-cancel-button"
                                onClick={() =>
                                    navigate("/admin/quizzes")
                                }
                                disabled={loading}
                            >
                                Cancel
                            </button>

                            <button
                                type="submit"
                                className="create-quiz-submit-button"
                                disabled={loading}
                            >
                                {loading
                                    ? "Creating..."
                                    : "Create Quiz"}
                            </button>

                        </div>

                    </form>

                </section>


                {/* Information */}

                <div className="create-quiz-note">

                    <span>i</span>

                    <p>
                        After creating the quiz, you'll be taken to the
                        quiz editor where you can add questions and options.
                    </p>

                </div>

            </main>

        </div>
    );
};

export default CreateQuiz;