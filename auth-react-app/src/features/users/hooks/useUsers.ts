import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { userService } from '../services/users.service';
import { type UserFilters, type CreateUserRequest, type UpdateUserRequest } from '../types/users.types';

export const userKeys = {
    all: ['users'] as const,
    lists: () => [...userKeys.all, 'list'] as const,
    list: (filters: UserFilters, page: number, size: number) =>
        [...userKeys.lists(), { filters, page, size }] as const,
    details: () => [...userKeys.all, 'detail'] as const,
    detail: (id: string) => [...userKeys.details(), id] as const,
    permissions: (id: string) => [...userKeys.detail(id), 'permissions'] as const,
};

// Hook to fetch paginated users
export function useUsers(page = 0, size = 10, filters: UserFilters = {}) {
    return useQuery({
        queryKey: userKeys.list(filters, page, size),
        queryFn: () => userService.getUsers(page, size, filters),
        placeholderData: keepPreviousData,
    });
}

// Hook to fetch single user
export function useUser(userId: string) {
    return useQuery({
        queryKey: userKeys.detail(userId),
        queryFn: () => userService.getUser(userId),
        enabled: !!userId,
    });
}

// Hook to create user
export function useCreateUser() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreateUserRequest) => userService.createUser(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: userKeys.lists() });
        },
    });
}

// Hook to update user
export function useUpdateUser() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ userId, data }: { userId: string; data: UpdateUserRequest }) =>
            userService.updateUser(userId, data),
        onSuccess: (data) => {
            queryClient.invalidateQueries({ queryKey: userKeys.detail(data.userId) });
            queryClient.invalidateQueries({ queryKey: userKeys.lists() });
        },
    });
}

// Hook to delete user
export function useDeleteUser() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (userId: string) => userService.deleteUser(userId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: userKeys.lists() });
        },
    });
}

// Hook to fetch user permissions
export function useUserPermissions(userId: string) {
    return useQuery({
        queryKey: userKeys.permissions(userId),
        queryFn: () => userService.getUserPermissions(userId),
        enabled: !!userId,
    });
}
