import { useEffect, useState } from "react";

import { useNavigate } from "react-router-dom";

import { useAuth } from "../../context/AuthContext";

import api from "../../services/api";

const AdminDashboard = () => {
    const navigate = useNavigate();

    const { user, logoutUser } = useAuth();

    const [stats, setStats] = useState({
        totalQuizzes: 0,
        totalAttempts: 0,
        totalUsers: 0
    });

    const [statsLoading, setStatsLoading] = useState(true);
    const [statsError, setStatsError] = useState("");

    useEffect(() => {
        const loadDashboardStats = async () => {
            try {
                setStatsLoading(true);
                setStatsError("");

                const response = await api.get(
                    "/admin/dashboard/stats"
                );

                setStats(response.data);
            } catch (err) {
                console.error(
                    "Failed to load dashboard stats:",
                    err
                );

                setStatsError(
                    err.response?.data?.message ||
                    "Unable to load dashboard statistics."
                );
            } finally {
                setStatsLoading(false);
            }
        };

        loadDashboardStats();

    }, []);

    const handleLogout = () => {
        logoutUser();
        navigate("/login");
    };

    return (
        <div className="admin-page">

            {/* SIDEBAR */}

            <aside className="admin-sidebar">

                <div className="admin-sidebar-brand">

                    <div className="admin-sidebar-logo">
                        Q
                    </div>

                    <div>
                        <h2>
                            Quiz App
                        </h2>

                        <span>
                            Administration
                        </span>
                    </div>

                </div>


                <div className="admin-sidebar-divider"></div>


                <nav className="admin-navigation">

                    {/* DASHBOARD */}

                    <button
                        className="admin-nav-item admin-nav-item-active"
                        onClick={() => navigate("/admin")}
                    >
                        <span className="admin-nav-icon">
                            ⌂
                        </span>

                        Dashboard
                    </button>


                    {/* MANAGE QUIZZES */}

                    <button
                        className="admin-nav-item"
                        onClick={() =>
                            navigate("/admin/quizzes")
                        }
                    >
                        <span className="admin-nav-icon">
                            ▣
                        </span>

                        Manage Quizzes
                    </button>


                    {/* ATTEMPTS */}

                    <button
                        className="admin-nav-item"
                        onClick={() =>
                            navigate("/admin/attempts")
                        }
                    >
                        <span className="admin-nav-icon">
                            ◷
                        </span>

                        Attempts
                    </button>

                </nav>


                <div className="admin-sidebar-bottom">

                    <div className="admin-user-card">

                        <div className="admin-user-avatar">

                            {(user?.name || "A")
                                .charAt(0)
                                .toUpperCase()}

                        </div>


                        <div className="admin-user-info">

                            <strong>
                                {user?.name || "Admin"}
                            </strong>

                            <span>
                                Administrator
                            </span>

                        </div>

                    </div>


                    <button
                        className="admin-logout-button"
                        onClick={handleLogout}
                    >
                        <span>
                            ↪
                        </span>

                        Logout
                    </button>

                </div>

            </aside>


            {/* MAIN CONTENT */}

            <main className="admin-main">

                <header className="admin-topbar">

                    <div>

                        <p className="admin-eyebrow">
                            ADMINISTRATION
                        </p>

                        <h1>
                            Dashboard
                        </h1>

                    </div>


                    <div className="admin-welcome">

                        <span>
                            Welcome back,
                        </span>

                        <strong>
                            {user?.name || "Admin"}
                        </strong>

                    </div>

                </header>


                <div className="admin-content">

                    {/* OVERVIEW */}

                    <section className="admin-section">

                        <div className="admin-section-heading">

                            <div>

                                <h2>
                                    Overview
                                </h2>

                                <p>
                                    A quick overview of your quiz
                                    platform.
                                </p>

                            </div>

                        </div>


                        {statsError && (

                            <div
                                style={{
                                    marginBottom: "16px",
                                    padding: "12px 15px",
                                    border: "1px solid #ead9d9",
                                    background: "#fcf7f7",
                                    borderRadius: "7px",
                                    color: "#8a4b4b",
                                    fontSize: "13px"
                                }}
                            >
                                {statsError}
                            </div>

                        )}


                        <div className="admin-overview-grid">

                            {/* TOTAL QUIZZES */}

                            <div className="admin-overview-card">

                                <div className="admin-card-icon">
                                    Q
                                </div>

                                <div>

                                    <span>
                                        Total Quizzes
                                    </span>

                                    <strong>
                                        {statsLoading
                                            ? "..."
                                            : stats.totalQuizzes}
                                    </strong>

                                </div>

                            </div>


                            {/* TOTAL ATTEMPTS */}

                            <div className="admin-overview-card">

                                <div className="admin-card-icon">
                                    ✓
                                </div>

                                <div>

                                    <span>
                                        Total Attempts
                                    </span>

                                    <strong>
                                        {statsLoading
                                            ? "..."
                                            : stats.totalAttempts}
                                    </strong>

                                </div>

                            </div>


                            {/* TOTAL USERS */}

                            <div className="admin-overview-card">

                                <div className="admin-card-icon">
                                    U
                                </div>

                                <div>

                                    <span>
                                        Total Users
                                    </span>

                                    <strong>
                                        {statsLoading
                                            ? "..."
                                            : stats.totalUsers}
                                    </strong>

                                </div>

                            </div>

                        </div>

                    </section>


                    {/* QUICK ACTIONS */}

                    <section className="admin-section">

                        <div className="admin-section-heading">

                            <div>

                                <h2>
                                    Quick Actions
                                </h2>

                                <p>
                                    Frequently used administration
                                    tools.
                                </p>

                            </div>

                        </div>


                        <div className="admin-actions-grid">

                            {/* CREATE QUIZ */}

                            <button
                                className="admin-action-card"
                                onClick={() =>
                                    navigate(
                                        "/admin/quizzes/create"
                                    )
                                }
                            >

                                <div className="admin-action-icon">
                                    +
                                </div>

                                <div>

                                    <strong>
                                        Create Quiz
                                    </strong>

                                    <span>
                                        Create a new quiz and add
                                        questions.
                                    </span>

                                </div>

                                <span className="admin-action-arrow">
                                    →
                                </span>

                            </button>


                            {/* MANAGE QUIZZES */}

                            <button
                                className="admin-action-card"
                                onClick={() =>
                                    navigate("/admin/quizzes")
                                }
                            >

                                <div className="admin-action-icon">
                                    Q
                                </div>

                                <div>

                                    <strong>
                                        Manage Quizzes
                                    </strong>

                                    <span>
                                        Edit, publish and manage
                                        quizzes.
                                    </span>

                                </div>

                                <span className="admin-action-arrow">
                                    →
                                </span>

                            </button>


                            {/* VIEW ATTEMPTS */}

                            <button
                                className="admin-action-card"
                                onClick={() =>
                                    navigate("/admin/attempts")
                                }
                            >

                                <div className="admin-action-icon">
                                    ◷
                                </div>

                                <div>

                                    <strong>
                                        View Attempts
                                    </strong>

                                    <span>
                                        Review quiz attempts and
                                        results.
                                    </span>

                                </div>

                                <span className="admin-action-arrow">
                                    →
                                </span>

                            </button>

                        </div>

                    </section>

                </div>

            </main>

        </div>
    );
};

export default AdminDashboard;