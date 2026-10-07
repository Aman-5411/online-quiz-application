import { useEffect, useMemo, useState } from "react";
import { getAllAdminAttempts } from "../../services/adminAttemptService";

const AdminAttempts = () => {
    const [attempts, setAttempts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [search, setSearch] = useState("");
    const [resultFilter, setResultFilter] = useState("ALL");

    useEffect(() => {
        const loadAttempts = async () => {
            try {
                setLoading(true);
                setError("");

                const data = await getAllAdminAttempts();

                setAttempts(Array.isArray(data) ? data : []);
            } catch (err) {
                console.error(
                    "Failed to load admin attempts:",
                    err
                );

                setError(
                    err.response?.data?.message ||
                    "Unable to load quiz attempts. Please try again."
                );
            } finally {
                setLoading(false);
            }
        };

        loadAttempts();
    }, []);

    const filteredAttempts = useMemo(() => {
        const searchValue = search.trim().toLowerCase();

        return attempts.filter((attempt) => {
            const matchesSearch =
                !searchValue ||
                String(attempt.attemptId)
                    .toLowerCase()
                    .includes(searchValue) ||
                attempt.userName
                    ?.toLowerCase()
                    .includes(searchValue) ||
                attempt.userEmail
                    ?.toLowerCase()
                    .includes(searchValue) ||
                attempt.quizTitle
                    ?.toLowerCase()
                    .includes(searchValue);

            const percentage = Number(
                attempt.percentage ?? 0
            );

            const matchesResult =
                resultFilter === "ALL" ||
                (resultFilter === "PASSED" &&
                    percentage >= 50) ||
                (resultFilter === "FAILED" &&
                    percentage < 50);

            return matchesSearch && matchesResult;
        });
    }, [attempts, search, resultFilter]);

    const formatDateTime = (value) => {
        if (!value) return "—";

        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return "—";
        }

        return date.toLocaleString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const getResultClass = (percentage) => {
        const value = Number(percentage ?? 0);

        if (value >= 75) {
            return "attempt-result-good";
        }

        if (value >= 50) {
            return "attempt-result-average";
        }

        return "attempt-result-low";
    };

    /* =========================
       LOADING
       ========================= */

    if (loading) {
        return (
            <div className="attempts-page">

                <div className="attempts-page-header">
                    <div>
                        <p className="attempts-page-eyebrow">
                            Administration
                        </p>

                        <h1>
                            Quiz Attempts
                        </h1>

                        <p>
                            Review and monitor quiz attempts
                            submitted by users.
                        </p>
                    </div>
                </div>

                <div className="admin-state-card">

                    <div className="admin-loading-spinner"></div>

                    <h3>
                        Loading attempts...
                    </h3>

                    <p>
                        Please wait while the attempts are
                        being loaded.
                    </p>

                </div>

            </div>
        );
    }

    /* =========================
       ERROR
       ========================= */

    if (error) {
        return (
            <div className="attempts-page">

                <div className="attempts-page-header">
                    <div>
                        <p className="attempts-page-eyebrow">
                            Administration
                        </p>

                        <h1>
                            Quiz Attempts
                        </h1>

                        <p>
                            Review and monitor quiz attempts
                            submitted by users.
                        </p>
                    </div>
                </div>

                <div className="admin-state-card admin-error-state">

                    <div className="admin-state-icon">
                        !
                    </div>

                    <h3>
                        Unable to load attempts
                    </h3>

                    <p>
                        {error}
                    </p>

                </div>

            </div>
        );
    }

    return (
        <div className="attempts-page">

            {/* =========================
                HEADER
                ========================= */}

            <div className="attempts-page-header">

                <div>

                    <p className="attempts-page-eyebrow">
                        Administration
                    </p>

                    <h1>
                        Quiz Attempts
                    </h1>

                    <p>
                        Review and monitor quiz attempts
                        submitted by users.
                    </p>

                </div>

                <div className="admin-attempt-count">
                    {filteredAttempts.length}{" "}
                    {filteredAttempts.length === 1
                        ? "attempt"
                        : "attempts"}
                </div>

            </div>


            {/* =========================
                SEARCH + FILTER
                FULL WIDTH ABOVE TABLE
                ========================= */}

            <div className="admin-filter-card">

                <div className="admin-search-wrapper">

                    <label htmlFor="attempt-search">
                        Search
                    </label>

                    <input
                        id="attempt-search"
                        type="text"
                        placeholder="Search by user, email, quiz or attempt ID..."
                        value={search}
                        onChange={(event) =>
                            setSearch(event.target.value)
                        }
                    />

                </div>


                <div className="admin-filter-wrapper">

                    <label htmlFor="result-filter">
                        Result
                    </label>

                    <select
                        id="result-filter"
                        value={resultFilter}
                        onChange={(event) =>
                            setResultFilter(event.target.value)
                        }
                    >

                        <option value="ALL">
                            All Results
                        </option>

                        <option value="PASSED">
                            50% and above
                        </option>

                        <option value="FAILED">
                            Below 50%
                        </option>

                    </select>

                </div>

            </div>


            {/* =========================
                EMPTY STATE
                ========================= */}

            {filteredAttempts.length === 0 ? (

                <div className="admin-state-card">

                    <div className="admin-state-icon">
                        —
                    </div>

                    <h3>
                        No attempts found
                    </h3>

                    <p>
                        No quiz attempts match your current
                        search or filter.
                    </p>

                </div>

            ) : (

                /* =========================
                   TABLE
                   ========================= */

                <div className="admin-attempts-card">

                    <div className="admin-table-wrapper">

                        <table className="admin-attempts-table">

                            <thead>
                                <tr>

                                    <th>
                                        Attempt
                                    </th>

                                    <th>
                                        User
                                    </th>

                                    <th>
                                        Email
                                    </th>

                                    <th>
                                        Quiz
                                    </th>

                                    <th>
                                        Score
                                    </th>

                                    <th>
                                        Result
                                    </th>

                                    <th>
                                        Started
                                    </th>

                                    <th>
                                        Completed
                                    </th>

                                </tr>
                            </thead>

                            <tbody>

                                {filteredAttempts.map(
                                    (attempt) => {

                                        const percentage =
                                            Number(
                                                attempt.percentage ??
                                                0
                                            );

                                        return (
                                            <tr
                                                key={
                                                    attempt.attemptId
                                                }
                                            >

                                                {/* ATTEMPT */}

                                                <td>
                                                    <span className="attempt-id">
                                                        #
                                                        {
                                                            attempt.attemptId
                                                        }
                                                    </span>
                                                </td>


                                                {/* USER */}

                                                <td>
                                                    <strong className="attempt-user-name">
                                                        {
                                                            attempt.userName ||
                                                            "Unknown"
                                                        }
                                                    </strong>
                                                </td>


                                                {/* EMAIL */}

                                                <td>
                                                    <span className="attempt-email">
                                                        {
                                                            attempt.userEmail ||
                                                            "—"
                                                        }
                                                    </span>
                                                </td>


                                                {/* QUIZ */}

                                                <td>

                                                    <div className="attempt-quiz">

                                                        <strong>
                                                            {
                                                                attempt.quizTitle ||
                                                                "Unknown Quiz"
                                                            }
                                                        </strong>

                                                        <span>
                                                            Quiz #
                                                            {
                                                                attempt.quizId
                                                            }
                                                        </span>

                                                    </div>

                                                </td>


                                                {/* SCORE */}

                                                <td>

                                                    <strong className="attempt-score">
                                                        {
                                                            attempt.score ??
                                                            0
                                                        }
                                                        /
                                                        {
                                                            attempt.totalQuestions ??
                                                            0
                                                        }
                                                    </strong>

                                                </td>


                                                {/* RESULT */}

                                                <td>

                                                    <span
                                                        className={`attempt-result ${getResultClass(
                                                            percentage
                                                        )}`}
                                                    >
                                                        {Math.round(
                                                            percentage
                                                        )}
                                                        %
                                                    </span>

                                                </td>


                                                {/* STARTED */}

                                                <td>
                                                    <span className="attempt-date">
                                                        {formatDateTime(
                                                            attempt.startedAt
                                                        )}
                                                    </span>
                                                </td>


                                                {/* COMPLETED */}

                                                <td>
                                                    <span className="attempt-date">
                                                        {formatDateTime(
                                                            attempt.completedAt
                                                        )}
                                                    </span>
                                                </td>

                                            </tr>
                                        );
                                    }
                                )}

                            </tbody>

                        </table>

                    </div>

                </div>

            )}

        </div>
    );
};

export default AdminAttempts;