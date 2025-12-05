import { useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';

export function LogoutPage() {
    const { logout, isAuthenticated } = useAuth();

    useEffect(() => {
        if (isAuthenticated) {
            logout();
        }
    }, [isAuthenticated, logout]);

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="text-center">
                <h2 className="text-xl font-medium text-gray-900">Signing out...</h2>
            </div>
        </div>
    );
}
