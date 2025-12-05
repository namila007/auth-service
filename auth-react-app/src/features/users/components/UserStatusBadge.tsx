import { cn } from "@/core/utils/cn";
import { type UserStatus } from "@/features/users/types/users.types";

interface UserStatusBadgeProps {
    status: UserStatus;
    className?: string;
}

export function UserStatusBadge({ status, className }: UserStatusBadgeProps) {
    const variants: Record<UserStatus, string> = {
        ACTIVE: "bg-green-100 text-green-800 border-green-200",
        INACTIVE: "bg-gray-100 text-gray-800 border-gray-200",
        SUSPENDED: "bg-red-100 text-red-800 border-red-200",
        LOCKED: "bg-orange-100 text-orange-800 border-orange-200",
        PENDING_VERIFICATION: "bg-blue-100 text-blue-800 border-blue-200",
    };

    return (
        <span className={cn(
            "inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border",
            variants[status] || variants.INACTIVE,
            className
        )}>
            {status.replace('_', ' ')}
        </span>
    );
}
