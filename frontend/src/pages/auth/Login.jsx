import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { loginUser } from "../../services/authService";
import { useAuth } from "../../context/AuthContext";

const Login = () => {

    const navigate = useNavigate();

    const { saveAuthentication } = useAuth();

    const [formData, setFormData] = useState({
        email: "",
        password: ""
    });

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    // =========================================================
    // HANDLE INPUT
    // =========================================================

    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData((previous) => ({
            ...previous,
            [name]: value
        }));
    };

    // =========================================================
    // HANDLE LOGIN
    // =========================================================

    const handleSubmit = async (event) => {

        event.preventDefault();

        setError("");
        setLoading(true);

        try {

            const response = await loginUser(formData);

            /*
             * Store JWT + user information
             * inside AuthContext/localStorage.
             */
            saveAuthentication(response);

            /*
             * Redirect according to role.
             */
            if (response.role === "ADMIN") {

                navigate("/admin");

            } else {

                navigate("/dashboard");
            }

        } catch (error) {

            console.error("Login failed:", error);

            if (error.response?.data?.message) {

                setError(
                    error.response.data.message
                );

            } else {

                setError(
                    "Invalid email or password."
                );
            }

        } finally {

            setLoading(false);
        }
    };

    return (
        <div className="login-page">

            <div className="login-card">

                {/* BRAND */}

                <div className="login-header">

                    <div className="login-logo">
                        Q
                    </div>

                    <h1>Quiz App</h1>

                    <p>
                        Test your knowledge. Track your progress.
                    </p>

                </div>

                {/* ERROR */}

                {error && (
                    <div className="login-error">
                        {error}
                    </div>
                )}

                {/* LOGIN FORM */}

                <form
                    className="login-form"
                    onSubmit={handleSubmit}
                >

                    <div className="form-group">

                        <label htmlFor="email">
                            Email
                        </label>

                        <input
                            id="email"
                            name="email"
                            type="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="Enter your email"
                            autoComplete="email"
                            required
                        />

                    </div>

                    <div className="form-group">

                        <label htmlFor="password">
                            Password
                        </label>

                        <input
                            id="password"
                            name="password"
                            type="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Enter your password"
                            autoComplete="current-password"
                            required
                        />

                    </div>

                    <button
                        className="login-button"
                        type="submit"
                        disabled={loading}
                    >
                        {loading
                            ? "Logging in..."
                            : "Login"}
                    </button>

                </form>
                <div className="auth-switch">

                    <span>
                        Don't have an account?
                    </span>

                    <button
                        type="button"
                        className="text-button"
                        onClick={() => navigate("/register")}
                    >
                        Register
                    </button>

                </div>

            </div>

        </div>
    );
};

export default Login;