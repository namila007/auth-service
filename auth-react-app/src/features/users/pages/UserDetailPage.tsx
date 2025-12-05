import { useNavigate, Link } from '@tanstack/react-router';
import { useUser, useDeleteUser } from '@/features/users/hooks/useUsers';
import { LoadingSpinner } from '@/components/feedback/LoadingSpinner';
import { UserProfileCard } from '@/features/users/components/UserProfileCard';
import { FederatedIdentityList } from '@/features/users/components/FederatedIdentityList';
import { Button } from '@/components/ui/button';
import { Edit, Trash2, Key, ArrowLeft, Shield } from 'lucide-react';
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "@/components/ui/alert-dialog";

interface UserDetailPageProps {
    userId: string;
}

export function UserDetailPage({ userId }: UserDetailPageProps) {
    const navigate = useNavigate();
    const { data: user, isLoading } = useUser(userId);
    const deleteUserMutation = useDeleteUser();

    const handleDelete = () => {
        deleteUserMutation.mutate(userId, {
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
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <Button variant="ghost" asChild className="mb-2 pl-0 hover:bg-transparent">
                        <Link to="/users" search={{ page: 1, size: 10 }}>
                            <ArrowLeft className="mr-2 h-4 w-4" /> Back to Users
                        </Link>
                    </Button>
                    <h2 className="text-3xl font-bold tracking-tight">User Details</h2>
                </div>
                <div className="flex gap-2">
                    <Button variant="outline" asChild>
                        <Link to="/users/$userId/edit" params={{ userId }} search={{ page: 1, size: 10 }}>
                            <Edit className="mr-2 h-4 w-4" /> Edit
                        </Link>
                    </Button>
                    <Button variant="outline" asChild>
                        {/* <Link to="/users/$userId/roles" params={{ userId }}>
                            <Key className="mr-2 h-4 w-4" /> Manage Roles
                        </Link> */}
                        <Button variant="outline" disabled>
                            <Key className="mr-2 h-4 w-4" /> Manage Roles
                        </Button>
                    </Button>

                    <AlertDialog>
                        <AlertDialogTrigger asChild>
                            <Button variant="destructive">
                                <Trash2 className="mr-2 h-4 w-4" /> Delete
                            </Button>
                        </AlertDialogTrigger>
                        <AlertDialogContent>
                            <AlertDialogHeader>
                                <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
                                <AlertDialogDescription>
                                    This action cannot be undone. This will permanently delete the user
                                    {user.username} and remove their data from our servers.
                                </AlertDialogDescription>
                            </AlertDialogHeader>
                            <AlertDialogFooter>
                                <AlertDialogCancel>Cancel</AlertDialogCancel>
                                <AlertDialogAction onClick={handleDelete} className="bg-destructive text-destructive-foreground hover:bg-destructive/90">
                                    Delete
                                </AlertDialogAction>
                            </AlertDialogFooter>
                        </AlertDialogContent>
                    </AlertDialog>
                </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <div className="lg:col-span-2 space-y-6">
                    <UserProfileCard user={user} />
                    <FederatedIdentityList identities={user.federatedIdentities} />
                </div>

                <div className="space-y-6">
                    {/* Placeholder for Perms/Roles Summary */}
                    <div className="border rounded-lg p-4 bg-card">
                        <div className="flex items-center gap-2 mb-4">
                            <Shield className="h-5 w-5 text-primary" />
                            <h3 className="font-semibold text-lg">Access Summary</h3>
                        </div>
                        <div className="space-y-3">
                            <div>
                                <div className="text-sm font-medium text-muted-foreground">Roles</div>
                                <div className="flex flex-wrap gap-1.5 mt-1.5">
                                    {user.roles && user.roles.length > 0 ? (
                                        user.roles.map((role: string) => (
                                            <span key={role} className="inline-flex items-center px-2 py-1 rounded bg-secondary text-secondary-foreground text-xs font-medium">
                                                {role}
                                            </span>
                                        ))
                                    ) : (
                                        <span className="text-sm text-muted-foreground italic">No roles assigned</span>
                                    )}
                                </div>
                            </div>
                            <Button variant="link" asChild className="px-0 h-auto">
                                {/* <Link to="/users/$userId/permissions" params={{ userId }}>View Effective Permissions</Link> */}
                                <span className="text-muted-foreground">View Effective Permissions (Coming Soon)</span>
                            </Button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
