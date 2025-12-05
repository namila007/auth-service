import { apiClient } from '@/core/api/client';
import {
    type AuthResponse,
    type OIDCInitiateRequest,
    type OIDCInitiateResponse,
    type OIDCCallbackRequest,
    type OIDCProvider
} from '../types/auth.types';

export const authService = {
    // Get available OIDC providers
    getProviders: async (): Promise<OIDCProvider[]> => {
        const response = await apiClient.get<OIDCProvider[]>('/oidc-providers', {
            params: { enabled: true }
        });
        return response.data; // Assumes ApiResponse wrapper is handled or direct array
    },

    // Initiate OIDC login
    initiateLogin: async (data: OIDCInitiateRequest): Promise<OIDCInitiateResponse> => {
        const response = await apiClient.post<OIDCInitiateResponse>('/auth/oidc/initiate', data);
        return response.data;
    },

    // Handle OIDC callback
    handleCallback: async (data: OIDCCallbackRequest): Promise<AuthResponse> => {
        const response = await apiClient.post<AuthResponse>('/auth/oidc/callback', data);
        return response.data;
    },

    // Refresh token
    refreshToken: async (refreshToken: string): Promise<AuthResponse> => {
        const response = await apiClient.post<AuthResponse>('/auth/token/refresh', { refreshToken });
        return response.data;
    },

    // Logout
    logout: async (refreshToken?: string): Promise<void> => {
        if (refreshToken) {
            await apiClient.post('/auth/logout', { refreshToken });
        }
    }
};
