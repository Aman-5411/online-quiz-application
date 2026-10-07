import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../services/api";

const EditQuiz = () => {

    const navigate = useNavigate();
    const { quizId } = useParams();

    const [quiz, setQuiz] = useState(null);
    const [questions, setQuestions] = useState([]);
    const [options, setOptions] = useState({});

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [questionText, setQuestionText] = useState("");
    const [questionDifficulty, setQuestionDifficulty] =
        useState("EASY");

    const [addingQuestion, setAddingQuestion] =
        useState(false);


    // =========================================================
    // LOAD OPTIONS FOR A QUESTION
    // =========================================================

    const loadOptionsForQuestion = async (questionId) => {

        try {

            const response = await api.get(
                `/questions/${questionId}/options`
            );

            setOptions((previous) => ({
                ...previous,
                [questionId]: response.data
            }));

        } catch (err) {

            console.error(
                "Failed to load options:",
                err
            );

        }
    };


    // =========================================================
    // LOAD QUIZ
    // =========================================================

    const loadQuiz = async () => {

        try {

            setLoading(true);
            setError("");

            const [
                quizResponse,
                questionsResponse
            ] = await Promise.all([
                api.get(`/quizzes/${quizId}`),
                api.get(`/quizzes/${quizId}/questions`)
            ]);

            setQuiz(quizResponse.data);
            setQuestions(questionsResponse.data);

            // Load options for every question
            const optionsData = {};

            for (const question of questionsResponse.data) {

                try {

                    const response = await api.get(
                        `/questions/${question.id}/options`
                    );

                    optionsData[question.id] =
                        response.data;

                } catch (err) {

                    console.error(
                        `Failed to load options for question ${question.id}:`,
                        err
                    );

                    optionsData[question.id] = [];
                }
            }

            setOptions(optionsData);

        } catch (err) {

            console.error(
                "Failed to load quiz:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Failed to load quiz."
            );

        } finally {

            setLoading(false);
        }
    };


    // =========================================================
    // INITIAL LOAD
    // =========================================================

    useEffect(() => {

        loadQuiz();

    }, [quizId]);


    // =========================================================
    // ADD QUESTION
    // =========================================================

    const handleAddQuestion = async (event) => {

        event.preventDefault();

        if (!questionText.trim()) {

            alert(
                "Please enter a question."
            );

            return;
        }

        try {

            setAddingQuestion(true);

            await api.post(
                `/quizzes/${quizId}/questions`,
                {
                    questionText:
                        questionText.trim(),

                    questionType:
                        "SINGLE_CHOICE",

                    difficulty:
                        questionDifficulty
                }
            );

            setQuestionText("");
            setQuestionDifficulty("EASY");

            await loadQuiz();

        } catch (err) {

            console.error(
                "Failed to add question:",
                err
            );

            alert(
                err.response?.data?.message ||
                "Failed to add question."
            );

        } finally {

            setAddingQuestion(false);
        }
    };


    // =========================================================
    // DELETE QUESTION
    // =========================================================

    const handleDeleteQuestion = async (questionId) => {

        const confirmed = window.confirm(
            "Are you sure you want to delete this question?"
        );

        if (!confirmed) {
            return;
        }

        try {

            await api.delete(
                `/questions/${questionId}`
            );

            await loadQuiz();

        } catch (err) {

            console.error(
                "Failed to delete question:",
                err
            );

            alert(
                err.response?.data?.message ||
                "Failed to delete question."
            );
        }
    };


    // =========================================================
    // ADD OPTION
    // =========================================================

    const handleAddOption = async (
        questionId,
        optionText
    ) => {

        if (!optionText.trim()) {

            alert(
                "Please enter option text."
            );

            return;
        }

        try {

            await api.post(
                `/questions/${questionId}/options`,
                {
                    optionText:
                        optionText.trim(),

                    correct: false
                }
            );

            await loadOptionsForQuestion(
                questionId
            );

        } catch (err) {

            console.error(
                "Failed to add option:",
                err
            );

            alert(
                err.response?.data?.message ||
                "Failed to add option."
            );
        }
    };


    // =========================================================
    // SET CORRECT OPTION
    // =========================================================

    const handleSetCorrectOption = async (
        questionId,
        option
    ) => {

        const questionOptions =
            options[questionId] || [];

        try {

            /*
             * First remove the existing correct
             * option.
             */
            for (const currentOption of questionOptions) {

                if (
                    currentOption.correct &&
                    currentOption.id !== option.id
                ) {

                    await api.put(
                        `/options/${currentOption.id}`,
                        {
                            optionText:
                                currentOption.optionText,

                            correct: false
                        }
                    );
                }
            }


            /*
             * Then mark the selected option
             * as correct.
             */
            if (!option.correct) {

                await api.put(
                    `/options/${option.id}`,
                    {
                        optionText:
                            option.optionText,

                        correct: true
                    }
                );
            }


            await loadOptionsForQuestion(
                questionId
            );

        } catch (err) {

            console.error(
                "Failed to update correct option:",
                err
            );

            alert(
                err.response?.data?.message ||
                "Failed to update correct option."
            );
        }
    };


    // =========================================================
    // DELETE OPTION
    // =========================================================

    const handleDeleteOption = async (
        optionId,
        questionId
    ) => {

        const confirmed = window.confirm(
            "Are you sure you want to delete this option?"
        );

        if (!confirmed) {
            return;
        }

        try {

            await api.delete(
                `/options/${optionId}`
            );

            await loadOptionsForQuestion(
                questionId
            );

        } catch (err) {

            console.error(
                "Failed to delete option:",
                err
            );

            alert(
                err.response?.data?.message ||
                "Failed to delete option."
            );
        }
    };


    // =========================================================
    // LOADING / ERROR STATES
    // =========================================================

    if (loading) {

        return (
            <h2>
                Loading quiz...
            </h2>
        );
    }


    if (error) {

        return (
            <h2>
                {error}
            </h2>
        );
    }


    if (!quiz) {

        return (
            <h2>
                Quiz not found.
            </h2>
        );
    }


    // =========================================================
    // UI
    // =========================================================

    return (
        <div
            style={{
                padding: "30px"
            }}
        >

            {/* =================================================
                HEADER
            ================================================= */}

            <div
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center"
                }}
            >

                <div>

                    <h1>
                        Edit Quiz
                    </h1>

                    <h2>
                        {quiz.title}
                    </h2>

                    <p>
                        {quiz.description ||
                            "No description"}
                    </p>

                    <p>
                        <strong>
                            Category:
                        </strong>{" "}
                        {quiz.category}
                    </p>

                    <p>
                        <strong>
                            Difficulty:
                        </strong>{" "}
                        {quiz.difficulty}
                    </p>

                    <p>
                        <strong>
                            Status:
                        </strong>{" "}
                        {quiz.status}
                    </p>

                </div>


                <button
                    onClick={() =>
                        navigate(
                            "/admin/quizzes"
                        )
                    }
                >
                    Back to Quizzes
                </button>

            </div>


            <hr />


            {/* =================================================
                QUESTIONS
            ================================================= */}

            <section>

                <h2>
                    Questions
                </h2>


                {questions.length === 0 ? (

                    <p>
                        No questions added yet.
                    </p>

                ) : (

                    questions.map((question) => (

                        <div
                            key={question.id}
                            style={{
                                border:
                                    "1px solid #ddd",

                                borderRadius:
                                    "8px",

                                padding:
                                    "20px",

                                marginBottom:
                                    "20px"
                            }}
                        >

                            <h3>
                                Question{" "}
                                {question.questionOrder}
                            </h3>


                            <p>
                                {question.questionText}
                            </p>


                            <p>
                                <strong>
                                    Type:
                                </strong>{" "}
                                {question.questionType}
                            </p>


                            <p>
                                <strong>
                                    Difficulty:
                                </strong>{" "}
                                {question.difficulty}
                            </p>


                            {/* =================================
                                OPTIONS
                            ================================= */}

                            <div
                                style={{
                                    marginTop:
                                        "20px",

                                    marginBottom:
                                        "20px"
                                }}
                            >

                                <h4>
                                    Options
                                </h4>


                                {(options[question.id] || [])
                                    .length === 0 ? (

                                    <p>
                                        No options
                                        added yet.
                                    </p>

                                ) : (

                                    (
                                        options[
                                            question.id
                                        ] || []
                                    ).map(
                                        (
                                            option,
                                            index
                                        ) => (

                                            <div
                                                key={
                                                    option.id
                                                }
                                                style={{
                                                    border:
                                                        "1px solid #eee",

                                                    padding:
                                                        "10px",

                                                    marginBottom:
                                                        "8px"
                                                }}
                                            >

                                                <strong>
                                                    Option{" "}
                                                    {index +
                                                        1}
                                                    :
                                                </strong>{" "}

                                                {
                                                    option.optionText
                                                }


                                                {" "}


                                                {option.correct && (
                                                    <strong>
                                                        ✓ Correct
                                                    </strong>
                                                )}


                                                <div
                                                    style={{
                                                        marginTop:
                                                            "8px"
                                                    }}
                                                >

                                                    <button
                                                        type="button"
                                                        onClick={() =>
                                                            handleSetCorrectOption(
                                                                question.id,
                                                                option
                                                            )
                                                        }
                                                        disabled={
                                                            option.correct
                                                        }
                                                    >
                                                        {option.correct
                                                            ? "Correct"
                                                            : "Mark Correct"}
                                                    </button>


                                                    {" "}


                                                    <button
                                                        type="button"
                                                        onClick={() =>
                                                            handleDeleteOption(
                                                                option.id,
                                                                question.id
                                                            )
                                                        }
                                                    >
                                                        Delete
                                                    </button>

                                                </div>

                                            </div>
                                        )
                                    )

                                )}


                                {/* =============================
                                    ADD OPTION
                                ============================= */}

                                <div
                                    style={{
                                        marginTop:
                                            "15px"
                                    }}
                                >

                                    <input
                                        type="text"
                                        placeholder="Enter option text"
                                        id={`option-${question.id}`}
                                    />


                                    {" "}


                                    <button
                                        type="button"
                                        onClick={() => {

                                            const input =
                                                document.getElementById(
                                                    `option-${question.id}`
                                                );

                                            handleAddOption(
                                                question.id,
                                                input.value
                                            );

                                            input.value =
                                                "";

                                        }}
                                    >
                                        Add Option
                                    </button>

                                </div>

                            </div>


                            {/* =================================
                                DELETE QUESTION
                            ================================= */}

                            <button
                                type="button"
                                onClick={() =>
                                    handleDeleteQuestion(
                                        question.id
                                    )
                                }
                            >
                                Delete Question
                            </button>

                        </div>

                    ))

                )}

            </section>


            <hr />


            {/* =================================================
                ADD QUESTION
            ================================================= */}

            <section>

                <h2>
                    Add Question
                </h2>


                <form
                    onSubmit={
                        handleAddQuestion
                    }
                >

                    {/* QUESTION TEXT */}

                    <div
                        style={{
                            marginBottom:
                                "15px"
                        }}
                    >

                        <label>
                            <strong>
                                Question
                            </strong>
                        </label>

                        <br />

                        <textarea
                            value={
                                questionText
                            }
                            onChange={(
                                event
                            ) =>
                                setQuestionText(
                                    event.target.value
                                )
                            }
                            rows="4"
                            cols="60"
                            placeholder="Enter question text"
                            required
                        />

                    </div>


                    {/* QUESTION TYPE */}

                    <div
                        style={{
                            marginBottom:
                                "15px"
                        }}
                    >

                        <label>
                            <strong>
                                Question Type
                            </strong>
                        </label>

                        <br />

                        <input
                            type="text"
                            value="SINGLE_CHOICE"
                            disabled
                        />

                    </div>


                    {/* DIFFICULTY */}

                    <div
                        style={{
                            marginBottom:
                                "20px"
                        }}
                    >

                        <label>
                            <strong>
                                Difficulty
                            </strong>
                        </label>

                        <br />

                        <select
                            value={
                                questionDifficulty
                            }
                            onChange={(
                                event
                            ) =>
                                setQuestionDifficulty(
                                    event.target.value
                                )
                            }
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


                    <button
                        type="submit"
                        disabled={
                            addingQuestion
                        }
                    >
                        {addingQuestion
                            ? "Adding..."
                            : "Add Question"}
                    </button>

                </form>

            </section>

        </div>
    );
};

export default EditQuiz;