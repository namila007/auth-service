import axios from 'axios';
import { env } from '../config/env';
import { useAuthStore } from '../auth/store';
import { authService } from '../../features/auth/services/auth.service';

export const apiClient = axios.create({
    baseURL: env.VITE_API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

apiClient.interceptors.request.use((config) => {
    const token = useAuthStore.getState().accessToken;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const refreshToken = useAuthStore.getState().refreshToken;
                if (refreshToken) {
                    // Try to refresh token
                    const { accessToken, refreshToken: newRefreshToken, user } = await authService.refreshToken(refreshToken);

                    // Update store
                    useAuthStore.getState().setAuth(user, accessToken, newRefreshToken);

                    // Retry original request with new token
                    originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                    return apiClient(originalRequest);
                }
            } catch (refreshError) {
                // Refresh failed, logout user
                useAuthStore.getState().clearAuth();
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);
