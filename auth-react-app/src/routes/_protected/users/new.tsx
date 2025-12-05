import { createFileRoute } from '@tanstack/react-router'
import { UserCreatePage } from '@/features/users/pages/UserCreatePage'

export const Route = createFileRoute('/_protected/users/new')({
    component: UserCreatePage,
})
