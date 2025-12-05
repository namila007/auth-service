import { createFileRoute } from '@tanstack/react-router'
import { CallbackPage } from '@/features/auth/pages/CallbackPage'
import { z } from 'zod'

const searchSchema = z.object({
    code: z.string(),
    state: z.string(),
})

export const Route = createFileRoute('/auth/callback')({
    validateSearch: (search) => searchSchema.parse(search),
    component: () => {
        const { code, state } = Route.useSearch()
        return <CallbackPage code={code} state={state} />
    },
})
