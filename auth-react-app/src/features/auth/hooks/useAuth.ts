import { useAuthStore } from '@/core/auth/store';
import { authService } from '../services/auth.service';
import { useNavigate } from '@tanstack/react-router';
import { useMutation } from '@tanstack/react-query';

export function useAuth() {
    const { user, isAuthenticated, accessToken, refreshToken, clearAuth } = useAuthStore();
    const navigate = useNavigate();

    const logoutMutation = useMutation({
        mutationFn: async () => {
            // Best effort logout from server
            if (refreshToken) {
                try {
                    await authService.logout(refreshToken);
                } catch (e) {
                    console.error("Logout failed on server", e);
                }
            }
        },
        onSettled: () => {
            // Always clear local state
            clearAuth();
            navigate({ to: '/login' });
        }
    });

    return {
        user,
        isAuthenticated,
        token: accessToken,
        logout: logoutMutation.mutate,
        isLoggingOut: logoutMutation.isPending
    };
}
