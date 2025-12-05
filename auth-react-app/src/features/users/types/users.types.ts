export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'LOCKED' | 'PENDING_VERIFICATION';

export interface UserProfile {
    firstName?: string;
    lastName?: string;
    displayName?: string;
    phoneNumber?: string;
    address?: string;
    timezone?: string;
    locale?: string;
    avatarUrl?: string;
    attributes?: Record<string, unknown>;
}

export interface FederatedIdentity {
    providerId: string;
    providerUserId: string;
    providerUsername?: string;
    linkedAt: string;
}

export interface User {
    userId: string;
    username: string;
    email: string;
    status: UserStatus;
    profile: UserProfile;
    roles: string[]; // Role IDs or names
    federatedIdentities?: FederatedIdentity[];
    createdAt?: string;
    updatedAt?: string;
    lastLoginAt?: string;
}

export interface CreateUserRequest {
    username: string;
    email: string;
    password?: string; // Optional if using federated identity only, but usually required for local
    profile?: UserProfile;
    roles?: string[];
    status?: UserStatus;
}

export interface UpdateUserRequest {
    email?: string;
    status?: UserStatus;
    profile?: UserProfile;
    roles?: string[];
}

export interface UserFilters {
    search?: string;
    status?: UserStatus;
    role?: string;
    startDate?: string;
    endDate?: string;
}

// Re-export common types if needed
export interface PagedResponse<T> {
    content: T[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        totalElements: number;
        totalPages: number;
        last: boolean;
        first: boolean;
    };
}
