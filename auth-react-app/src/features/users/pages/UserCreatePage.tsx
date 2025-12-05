import { useNavigate } from '@tanstack/react-router';
import { useCreateUser } from '@/features/users/hooks/useUsers';
import { UserForm } from '@/features/users/components/UserForm';
import { type CreateUserRequest } from '@/features/users/types/users.types';
import { Container, Box, Typography } from '@mui/material';

export function UserCreatePage() {
    const navigate = useNavigate();
    const createUserMutation = useCreateUser();

    const handleSubmit = (values: any) => {
        const request: CreateUserRequest = {
            username: values.username,
            email: values.email,
            status: values.status,
            profile: {
                firstName: values.firstName,
                lastName: values.lastName,
                displayName: values.displayName
            }
        };

        createUserMutation.mutate(request, {
            onSuccess: () => {
                navigate({ to: '/users', search: { page: 1, size: 10 } });
            }
        });
    };

    return (
        <Container maxWidth="md">
            <Box sx={{ mb: 4 }}>
                <Typography variant="h4" fontWeight="bold" gutterBottom>
                    Create User
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    Add a new user to the system.
                </Typography>
            </Box>

            <UserForm onSubmit={handleSubmit} isLoading={createUserMutation.isPending} />
        </Container>
    );
}
