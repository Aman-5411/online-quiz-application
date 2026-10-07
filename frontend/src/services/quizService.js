import api from "./api";

// =========================================================
// GET ALL QUIZZES
// =========================================================

export const getAllQuizzes = async () => {

    const response = await api.get("/quizzes");

    return response.data;
};


// =========================================================
// GET QUIZ BY ID
// =========================================================

export const getQuizById = async (quizId) => {

    const response = await api.get(
        `/quizzes/${quizId}`
    );

    return response.data;
};


// =========================================================
// GET PUBLISHED QUIZZES
// =========================================================

export const getPublishedQuizzes = async () => {

    const response = await api.get(
        "/quizzes/status/PUBLISHED"
    );

    return response.data;
};