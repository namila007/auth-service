import { Card, CardContent, Typography, Grid, Box } from "@mui/material";
import { People as UsersIcon, AssignmentInd as UserCheck, Warning as ShieldAlert, Key } from "@mui/icons-material";

interface StatProps {
    title: string;
    value: string | number;
    description: string;
    icon: React.ComponentType<{ sx?: any }>;
    trend?: string;
    trendUp?: boolean;
}

function StatCard({ title, value, description, icon: Icon, trend, trendUp }: StatProps) {
    return (
        <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Typography variant="subtitle2" color="text.secondary" sx={{ fontWeight: 600 }}>
                        {title}
                    </Typography>
                    <Icon sx={{ color: 'text.secondary', fontSize: 20 }} />
                </Box>
                <Typography variant="h4" component="div" sx={{ fontWeight: 'bold', mb: 0.5 }}>
                    {value}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                    {description}
                </Typography>
                {trend && (
                    <Typography
                        variant="caption"
                        sx={{
                            display: 'block',
                            mt: 1,
                            color: trendUp ? 'success.main' : 'error.main',
                            fontWeight: 600
                        }}
                    >
                        {trend}
                    </Typography>
                )}
            </CardContent>
        </Card>
    )
}

export function DashboardStats() {
    // TODO: Replace with real data from API
    return (
        <Grid container spacing={3}>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <StatCard
                    title="Total Users"
                    value="12,345"
                    description="+180 from last month"
                    icon={UsersIcon}
                    trend="+1.5%"
                    trendUp={true}
                />
            </Grid>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <StatCard
                    title="Active Users"
                    value="10,234"
                    description="+19% active rate"
                    icon={UserCheck}
                    trend="+2.1%"
                    trendUp={true}
                />
            </Grid>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <StatCard
                    title="Pending Approvals"
                    value="23"
                    description="Needs attention"
                    icon={ShieldAlert}
                />
            </Grid>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <StatCard
                    title="Total Roles"
                    value="12"
                    description="System defined roles"
                    icon={Key}
                />
            </Grid>
        </Grid>
    )
}
