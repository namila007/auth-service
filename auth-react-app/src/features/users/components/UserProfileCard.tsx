import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { type User } from "@/features/users/types/users.types";
import { UserStatusBadge } from "./UserStatusBadge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { format } from "date-fns";

interface UserProfileCardProps {
    user: User;
}

export function UserProfileCard({ user }: UserProfileCardProps) {
    const initials = user.profile.displayName
        ?.split(' ')
        .map((n: string) => n[0])
        .join('')
        .toUpperCase()
        .slice(0, 2) || user.username.slice(0, 2).toUpperCase();

    return (
        <Card>
            <CardHeader className="flex flex-row items-start gap-4 space-y-0">
                <Avatar className="h-16 w-16">
                    <AvatarImage src={user.profile.avatarUrl} alt={user.username} />
                    <AvatarFallback className="text-lg">{initials}</AvatarFallback>
                </Avatar>
                <div className="flex-1 space-y-1">
                    <div className="flex items-center gap-2">
                        <CardTitle className="text-2xl">{user.profile.displayName || user.username}</CardTitle>
                        <UserStatusBadge status={user.status} />
                    </div>
                    <CardDescription>{user.email}</CardDescription>
                    <div className="text-sm text-muted-foreground pt-1">
                        @{user.username}
                    </div>
                </div>
            </CardHeader>
            <CardContent>
                <dl className="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
                    <div>
                        <dt className="text-sm font-medium text-muted-foreground">First Name</dt>
                        <dd className="text-sm font-semibold">{user.profile.firstName || '-'}</dd>
                    </div>
                    <div>
                        <dt className="text-sm font-medium text-muted-foreground">Last Name</dt>
                        <dd className="text-sm font-semibold">{user.profile.lastName || '-'}</dd>
                    </div>
                    <div>
                        <dt className="text-sm font-medium text-muted-foreground">Created At</dt>
                        <dd className="text-sm font-semibold">
                            {user.createdAt ? format(new Date(user.createdAt), 'PPP p') : '-'}
                        </dd>
                    </div>
                    <div>
                        <dt className="text-sm font-medium text-muted-foreground">Last Login</dt>
                        <dd className="text-sm font-semibold">
                            {user.lastLoginAt ? format(new Date(user.lastLoginAt), 'PPP p') : 'Never'}
                        </dd>
                    </div>
                    {user.profile.timezone && (
                        <div>
                            <dt className="text-sm font-medium text-muted-foreground">Timezone</dt>
                            <dd className="text-sm font-semibold">{user.profile.timezone}</dd>
                        </div>
                    )}
                </dl>
            </CardContent>
        </Card>
    );
}
