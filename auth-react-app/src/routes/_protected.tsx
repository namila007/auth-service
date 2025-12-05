import { createFileRoute, Outlet } from '@tanstack/react-router'
// import { useAuthStore } from '@/core/auth/store'
import { AppShell } from '@/components/layout/AppShell'

export const Route = createFileRoute('/_protected')({
    beforeLoad: ({ /* location */ }) => {
        // TODO: [OAUTH-BYPASS] Uncomment this check to re-enable authentication guard
        /*
        if (!useAuthStore.getState().isAuthenticated) {
            throw redirect({
                to: '/login',
                search: {
                    redirect: location.href,
                },
            })
        }
        */
    },
    component: () => (
        <AppShell>
            <Outlet />
        </AppShell>
    ),
})
