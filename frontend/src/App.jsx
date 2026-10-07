import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Register from "./pages/auth/Register";
import Login from "./pages/auth/Login";
import UserDashboard from "./pages/user/UserDashboard";
import QuizAttempt from "./pages/quiz/QuizAttempt";
import QuizResult from "./pages/quiz/QuizResult";
import MyAttempts from "./pages/quiz/MyAttempts";
import AdminQuizManagement from "./pages/admin/AdminQuizManagement";
import CreateQuiz from "./pages/admin/CreateQuiz";
import AdminDashboard from "./pages/admin/AdminDashboard";
import EditQuiz from "./pages/admin/EditQuiz";
import AdminAttempts from "./pages/admin/AdminAttempts";
function App() {

    return (
        <BrowserRouter>

            <Routes>
                {/* REGISTER */}

                <Route
                    path="/register"
                    element={<Register />} />

                {/* LOGIN */}

                <Route
                    path="/login"
                    element={<Login />}
                />


                {/* DEFAULT */}

                <Route
                    path="/"
                    element={
                        <Navigate
                            to="/login"
                            replace
                        />
                    }
                />
                
                {/* USER DASHBOARD */}

                <Route
                    path="/dashboard"
                    element={<UserDashboard />}
                />
                
                <Route
                    path="/quiz/:quizId/attempt/:attemptId"
                    element={<QuizAttempt />}
                />

                <Route
                    path="/quiz/result/:attemptId"
                    element={<QuizResult />}
                />

                <Route
                    path="/attempts"
                    element={<MyAttempts />}
                />

                {/* ADMIN DASHBOARD */}

                <Route
                    path="/admin"
                    element={<AdminDashboard />}
                />

                <Route
                    path="/admin/quizzes"
                    element={<AdminQuizManagement />}
                />

                <Route
                    path="/admin/quizzes/create"
                    element={<CreateQuiz />}
                />

                <Route
                    path="/admin/quizzes"
                    element={<AdminQuizManagement />}
                />

                <Route
                    path="/admin/quizzes/create"
                    element={<CreateQuiz />}
                />
                
                <Route
                    path="/admin/quizzes/edit/:quizId"
                    element={<EditQuiz />}
                />

                <Route
                    path="/admin/attempts"
                    element={<AdminAttempts />}
                />

                {/* UNKNOWN ROUTES */}

                <Route
                    path="*"
                    element={
                        <Navigate
                            to="/login"
                            replace
                        />
                    }
                />

            </Routes>

        </BrowserRouter>
    );
}

export default App;