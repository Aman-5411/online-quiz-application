import { createContext, useContext, useState } from "react";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {

    const [user, setUser] = useState(() => {

        const storedUser = localStorage.getItem("user");

        if (!storedUser) {
            return null;
        }

        try {
            return JSON.parse(storedUser);
        } catch {
            localStorage.removeItem("user");
            return null;
        }
    });

    const [authenticated, setAuthenticated] = useState(
        () => !!localStorage.getItem("token")
    );

    
    // SAVE AUTHENTICATION
    

    const saveAuthentication = (response) => {

        // Store JWT
        localStorage.setItem(
            "token",
            response.token
        );

        // Store user information
        const userData = {
            id: response.userId,
            name: response.name,
            email: response.email,
            role: response.role
        };

        localStorage.setItem(
            "user",
            JSON.stringify(userData)
        );

        // Update React state
        setUser(userData);
        setAuthenticated(true);
    };

    
    // LOGOUT

    const logoutUser = () => {

        localStorage.removeItem("token");
        localStorage.removeItem("user");

        setUser(null);
        setAuthenticated(false);
    };

    return (
        <AuthContext.Provider
            value={{
                user,
                authenticated,
                saveAuthentication,
                logoutUser
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

// USE AUTH

export const useAuth = () => {

    const context = useContext(AuthContext);

    if (!context) {
        throw new Error(
            "useAuth must be used inside AuthProvider"
        );
    }

    return context;
};