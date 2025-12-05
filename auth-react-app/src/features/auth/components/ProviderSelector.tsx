
import { useOIDCFlow } from '../hooks/useOIDCFlow';
import { cn } from '@/core/utils/cn';

interface ProviderSelectorProps {
    className?: string;
}

export function ProviderSelector({ className }: ProviderSelectorProps) {
    const { providers, isLoadingProviders, initiateLogin, isInitiating } = useOIDCFlow();

    if (isLoadingProviders) {
        return <div className="text-sm text-muted-foreground animate-pulse">Loading providers...</div>;
    }

    if (!providers || providers.length === 0) {
        return <div className="text-sm text-yellow-600">No identity providers configured.</div>;
    }

    return (
        <div className={cn("flex flex-col gap-3", className)}>
            {providers.map((provider) => (
                <button
                    key={provider.providerId}
                    onClick={() => initiateLogin(provider.providerId)}
                    disabled={isInitiating}
                    className="flex items-center justify-center gap-2 w-full py-2.5 px-4 rounded-lg bg-white border border-gray-200 text-gray-700 hover:bg-gray-50 hover:border-gray-300 transition-all font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
                >
                    {isInitiating ? (
                        <span className="w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full animate-spin"></span>
                    ) : null}
                    <span>Sign in with {provider.displayName}</span>
                </button>
            ))}
        </div>
    );
}
