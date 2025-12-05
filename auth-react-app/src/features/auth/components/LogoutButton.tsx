import { useAuth } from '../hooks/useAuth';
import { cn } from '@/core/utils/cn';

interface LogoutButtonProps {
    className?: string;
    variant?: 'default' | 'ghost' | 'destructive';
}

export function LogoutButton({ className, variant = 'default' }: LogoutButtonProps) {
    const { logout, isLoggingOut } = useAuth();

    const baseStyles = "inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50";
    const variants = {
        default: "bg-primary text-primary-foreground shadow hover:bg-primary/90 h-9 px-4 py-2",
        destructive: "bg-destructive text-destructive-foreground shadow-sm hover:bg-destructive/90 h-9 px-4 py-2",
        ghost: "hover:bg-accent hover:text-accent-foreground h-9 px-4 py-2"
    };

    return (
        <button
            onClick={() => logout()}
            disabled={isLoggingOut}
            className={cn(baseStyles, variants[variant], className)}
        >
            {isLoggingOut ? 'Signing out...' : 'Sign out'}
        </button>
    );
}
