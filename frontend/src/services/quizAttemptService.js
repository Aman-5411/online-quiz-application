import api from "./api";

export const startQuizAttempt = async (userId, quizId) => {
    const response = await api.post(
        `/attempts/user/${userId}/quiz/${quizId}`
    );

    return response.data;
};

export const getQuizQuestionsForAttempt = async (quizId) => {
    const response = await api.get(
        `/quizzes/${quizId}/questions/attempt`
    );

    return response.data;
};

export const submitQuizAnswer = async (
    attemptId,
    questionId,
    optionId
) => {
    const response = await api.post(
        `/answers?attemptId=${attemptId}&questionId=${questionId}&optionId=${optionId}`
    );

    return response.data;
};

export const submitQuizAttempt = async (attemptId) => {
    const response = await api.post(
        `/attempts/${attemptId}/submit`
    );

    return response.data;
};

export const getQuizAttemptResult = async (attemptId) => {
    const response = await api.get(`/attempts/${attemptId}`);
    return response.data;
};

export const getUserAttempts = async (userId) => {
    const response = await api.get(`/attempts/user/${userId}`);
    return response.data;
};