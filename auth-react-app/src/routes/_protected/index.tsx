import { createFileRoute } from '@tanstack/react-router'
import { AppShell } from '@/components/layout/AppShell'
import { DashboardStats } from '@/features/dashboard/components/DashboardStats'

export const Route = createFileRoute('/_protected/')({
    component: Index,
})

function Index() {
    return (
        <AppShell>
            <div className="p-4 space-y-6">
                <div>
                    <h1 className="text-2xl font-bold">Welcome to Auth Admin</h1>
                    <p className="mt-2 text-muted-foreground">Select a module from the sidebar to get started.</p>
                </div>

                <DashboardStats />
            </div>
        </AppShell>
    )
}
