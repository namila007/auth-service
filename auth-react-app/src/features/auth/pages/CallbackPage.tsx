import { useRef, useEffect } from 'react';
import { useOIDCFlow } from '../hooks/useOIDCFlow';

interface CallbackPageProps {
    code: string;
    state: string;
}

export function CallbackPage({ code, state }: CallbackPageProps) {
    const { handleCallback, error } = useOIDCFlow();
    const processedRef = useRef(false);

    useEffect(() => {
        if (code && state && !processedRef.current) {
            processedRef.current = true;
            // We need providerId from somewhere. 
            // Usually state parameter or cookie can hold it, OR we iterate/lookup.
            // BUT current implementation of useOIDCFlow expects providerId.
            // For simplicity in this plan, I assumed providerId is known. 
            // Let's assume we stored providerId in sessionStorage during initiate.

            // FIX: Adjusting logic to retrieve providerId from storage or state
            // OIDC state param is often random, but we can encode data in it or map it.
            // Let's assume for now we persisted 'pending_provider_id' in storage.

            const providerId = sessionStorage.getItem('pending_provider_id');

            if (providerId) {
                handleCallback({ code, state, providerId }).catch(() => {
                    // Error handled by hook
                });
                sessionStorage.removeItem('pending_provider_id');
            } else {
                console.error("No pending provider ID found");
            }
        }
    }, [code, state, handleCallback]);

    if (error) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <div className="bg-white p-8 rounded-lg shadow-md max-w-md w-full text-center">
                    <div className="h-12 w-12 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-4">
                        <span className="text-red-600 text-xl font-bold">!</span>
                    </div>
                    <h3 className="text-lg font-medium text-gray-900 mb-2">Authentication Failed</h3>
                    <p className="text-sm text-gray-500 mb-6">{error.message || 'An unknown error occurred'}</p>
                    <a href="/login" className="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
                        Back to Login
                    </a>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="text-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto mb-4"></div>
                <h2 className="text-xl font-medium text-gray-900">Completing sign in...</h2>
                <p className="text-gray-500 mt-2">Please wait while we verify your credentials.</p>
            </div>
        </div>
    );
}
