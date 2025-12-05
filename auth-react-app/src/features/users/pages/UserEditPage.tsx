import { useNavigate } from '@tanstack/react-router';
import { useUser, useUpdateUser } from '@/features/users/hooks/useUsers';
import { UserForm } from '@/features/users/components/UserForm';
import { type UpdateUserRequest } from '@/features/users/types/users.types';
import { LoadingSpinner } from '@/components/feedback/LoadingSpinner';

interface UserEditPageProps {
    userId: string;
}

export function UserEditPage({ userId }: UserEditPageProps) {
    const navigate = useNavigate();
    const { data: user, isLoading } = useUser(userId);
    const updateUserMutation = useUpdateUser();

    const handleSubmit = (values: any) => {
        const request: UpdateUserRequest = {
            email: values.email,
            status: values.status,
            profile: {
                firstName: values.firstName,
                lastName: values.lastName,
                displayName: values.displayName
            }
        };

        updateUserMutation.mutate({ userId, data: request }, {
            onSuccess: () => {
                navigate({ to: '/users', search: { page: 1, size: 10 } });
            }
        });
    };

    if (isLoading) {
        return <LoadingSpinner />;
    }

    if (!user) {
        return <div>User not found</div>;
    }

    return (
        <div className="space-y-6 max-w-2xl mx-auto">
            <div>
                <h2 className="text-3xl font-bold tracking-tight">Edit User</h2>
                <p className="text-muted-foreground">
                    Update user details for {user.username}.
                </p>
            </div>

            <div className="border rounded-lg p-6 bg-card">
                <UserForm
                    initialData={user}
                    onSubmit={handleSubmit}
                    isLoading={updateUserMutation.isPending}
                />
            </div>
        </div>
    );
}
