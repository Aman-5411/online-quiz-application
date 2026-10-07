import api from "./api";

export const getAllAdminAttempts = async () => {
    const response = await api.get("/admin/attempts");
    return response.data;
};