import { z } from 'zod';

const envSchema = z.object({
    VITE_API_BASE_URL: z.string().url().default('http://localhost:8080/api/v1'),
    VITE_OIDC_REDIRECT_URI: z.string().url().default('http://localhost:5173/auth/callback'),
});

const _env = envSchema.safeParse(import.meta.env);

if (!_env.success) {
    console.error('Invalid environment variables:', _env.error.format());
    throw new Error('Invalid environment variables');
}

export const env = _env.data;
