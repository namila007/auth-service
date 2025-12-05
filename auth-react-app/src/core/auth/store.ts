import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export interface User {
    userId: string;
    username: string;
    email: string;
    roles: string[];
    profile?: {
        displayName?: string;
        avatarUrl?: string;
        firstName?: string;
        lastName?: string;
    };
}

interface AuthState {
    user: User | null;
    accessToken: string | null;
    refreshToken: string | null;
    isAuthenticated: boolean;

    setAuth: (user: User, accessToken: string, refreshToken: string) => void;
    clearAuth: () => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            // TODO: [OAUTH-BYPASS] Remove mock data and restore nulls when connecting real OAuth
            user: {
                userId: 'dev-user-001',
                username: 'Developer',
                email: 'dev@example.com',
                roles: ['ADMIN'],
                profile: {
                    displayName: 'Dev User',
                    avatarUrl: ''
                }
            },
            accessToken: 'mock-token',
            refreshToken: 'mock-refresh',
            isAuthenticated: true,

            setAuth: (user, accessToken, refreshToken) =>
                set({ user, accessToken, refreshToken, isAuthenticated: true }),

            clearAuth: () =>
                set({ user: null, accessToken: null, refreshToken: null, isAuthenticated: false }),
        }),
        {
            name: 'auth-storage',
        }
    )
);
