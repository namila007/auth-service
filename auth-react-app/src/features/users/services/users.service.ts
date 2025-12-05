import { apiClient } from '@/core/api/client';
import {
    type User,
    type CreateUserRequest,
    type UpdateUserRequest,
    type UserFilters,
    type PagedResponse
} from '../types/users.types';

const BASE_URL = '/users';

export const userService = {
    // List users with pagination and filters
    getUsers: async (page = 0, size = 10, filters?: UserFilters): Promise<PagedResponse<User>> => {
        const params = {
            page,
            size,
            ...filters
        };
        const response = await apiClient.get<PagedResponse<User>>(BASE_URL, { params });
        return response.data;
    },

    // Get single user by ID
    getUser: async (userId: string): Promise<User> => {
        const response = await apiClient.get<User>(`${BASE_URL}/${userId}`);
        return response.data;
    },

    // Create new user
    createUser: async (data: CreateUserRequest): Promise<User> => {
        const response = await apiClient.post<User>(BASE_URL, data);
        return response.data;
    },

    // Update existing user
    updateUser: async (userId: string, data: UpdateUserRequest): Promise<User> => {
        const response = await apiClient.patch<User>(`${BASE_URL}/${userId}`, data);
        return response.data;
    },

    // Delete user
    deleteUser: async (userId: string): Promise<void> => {
        await apiClient.delete(`${BASE_URL}/${userId}`);
    },

    // Get user permissions (effective permissions)
    getUserPermissions: async (userId: string): Promise<string[]> => {
        const response = await apiClient.get<string[]>(`${BASE_URL}/${userId}/permissions`);
        return response.data;
    },

    // Assign roles to user (if separate endpoint, otherwise typically part of update)
    assignRoles: async (userId: string, roleIds: string[]): Promise<User> => {
        // Assuming a specific endpoint or using patch. 
        // Based on plan API: GET /api/v1/users/:userId/roles might exist, but usually update user handles it.
        // Let's assume patch for now.
        return userService.updateUser(userId, { roles: roleIds });
    }
};
