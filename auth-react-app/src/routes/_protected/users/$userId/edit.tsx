import { createFileRoute } from '@tanstack/react-router'
import { UserEditPage } from '@/features/users/pages/UserEditPage'

export const Route = createFileRoute('/_protected/users/$userId/edit')({
    component: () => {
        const { userId } = Route.useParams()
        return <UserEditPage userId={userId} />
    },
})
