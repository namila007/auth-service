import { useNavigate } from '@tanstack/react-router';
import { useMutation, useQuery } from '@tanstack/react-query';
import { authService } from '../services/auth.service';
import { useAuthStore } from '@/core/auth/store';

import { nanoid } from 'nanoid';

export function useOIDCFlow() {
    const navigate = useNavigate();
    const setAuth = useAuthStore(state => state.setAuth);

    // Query to get providers
    const providersQuery = useQuery({
        queryKey: ['oidc-providers'],
        queryFn: authService.getProviders,
    });

    // Mutation to initiate login
    const initiateMutation = useMutation({
        mutationFn: async (providerId: string) => {
            const state = nanoid();
            const redirectUri = window.location.origin + '/auth/callback';

            const response = await authService.initiateLogin({
                providerId,
                redirectUri,
                state
            });

            return { ...response, redirectUri, providerId };
        },
        onSuccess: (data) => {
            // Store state and verifier in session storage/local storage for callback verification
            sessionStorage.setItem('oidc_state', data.state);
            if (data.codeVerifier) {
                sessionStorage.setItem('oidc_verifier', data.codeVerifier);
            }
            sessionStorage.setItem('pending_provider_id', data.providerId);

            // Redirect to IdP
            window.location.href = data.authorizationUrl;
        }
    });

    // Mutation to handle callback
    const callbackMutation = useMutation({
        mutationFn: async ({ code, state, providerId }: { code: string; state: string; providerId: string }) => {
            const storedState = sessionStorage.getItem('oidc_state');
            const storedVerifier = sessionStorage.getItem('oidc_verifier');

            if (state !== storedState) {
                throw new Error('Invalid state parameter');
            }

            const response = await authService.handleCallback({
                providerId,
                code,
                state,
                codeVerifier: storedVerifier || undefined
            });

            return response;
        },
        onSuccess: (data) => {
            // Update store
            setAuth(data.user, data.accessToken, data.refreshToken);

            // Cleanup
            sessionStorage.removeItem('oidc_state');
            sessionStorage.removeItem('oidc_verifier');

            // Navigate to dashboard
            navigate({ to: '/' });
        }
    });

    return {
        providers: providersQuery.data ?? [],
        isLoadingProviders: providersQuery.isLoading,
        initiateLogin: initiateMutation.mutate,
        isInitiating: initiateMutation.isPending,
        handleCallback: callbackMutation.mutateAsync,
        isHandlingCallback: callbackMutation.isPending,
        error: initiateMutation.error || callbackMutation.error,
    };
}
