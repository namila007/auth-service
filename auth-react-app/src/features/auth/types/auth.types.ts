export interface User {
    userId: string;
    username: string;
    email: string;
    status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'LOCKED';
    profile: {
        firstName?: string;
        lastName?: string;
        displayName: string;
        attributes?: Record<string, unknown>;
    };
    roles: string[];
    permissions: string[];
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
    user: User;
}

export interface OIDCProvider {
    providerId: string;
    providerName: string;
    displayName: string;
    providerType: 'OIDC' | 'SAML';
    enabled: boolean;
}

export interface OIDCInitiateRequest {
    providerId: string;
    redirectUri: string;
    state?: string;
    nonce?: string;
}

export interface OIDCInitiateResponse {
    authorizationUrl: string;
    state: string;
    codeVerifier?: string;
}

export interface OIDCCallbackRequest {
    providerId: string;
    code: string;
    state: string;
    codeVerifier?: string;
}
