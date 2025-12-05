import { createFileRoute } from '@tanstack/react-router'
import { z } from 'zod'
import { UserListPage } from '@/features/users/pages/UserListPage'

const usersSearchSchema = z.object({
    page: z.number().catch(1),
    size: z.number().catch(10),
    search: z.string().optional(),
})

export const Route = createFileRoute('/_protected/users/')({
    validateSearch: (search) => usersSearchSchema.parse(search),
    component: () => {
        const { page, size, search } = Route.useSearch()
        return <UserListPage page={page} size={size} search={search} />
    },
})
