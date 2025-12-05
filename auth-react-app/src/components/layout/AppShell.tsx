import { useState } from 'react';
import {
    Box,
    Drawer,
    AppBar,
    Toolbar,
    List,
    Typography,
    Divider,
    IconButton,
    ListItem,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Avatar,
    Menu,
    MenuItem,
    useTheme,
    Collapse
} from '@mui/material';
import {
    Menu as MenuIcon,
    Dashboard as DashboardIcon,
    People as PeopleIcon,
    Security as SecurityIcon,
    Settings as SettingsIcon,
    ExpandLess,
    ExpandMore,
    VpnKey,
    AssignmentInd
} from '@mui/icons-material';
import { Link, useRouterState, useNavigate } from '@tanstack/react-router';
import { useAuthStore } from '@/core/auth/store';

const drawerWidth = 260;

interface AppShellProps {
    children: React.ReactNode;
}

export function AppShell({ children }: AppShellProps) {
    const theme = useTheme();
    const [mobileOpen, setMobileOpen] = useState(false);
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const [usersOpen, setUsersOpen] = useState(true);

    const navigate = useNavigate();
    const { user, clearAuth } = useAuthStore();
    const router = useRouterState();

    const handleDrawerToggle = () => {
        setMobileOpen(!mobileOpen);
    };

    const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleLogout = () => {
        handleClose();
        clearAuth();
        navigate({ to: '/login' });
    }

    const handleUsersClick = () => {
        setUsersOpen(!usersOpen);
    };

    const drawerContent = (
        <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <Toolbar sx={{ display: 'flex', alignItems: 'center', px: 2 }}>
                <VpnKey sx={{ mr: 1, color: theme.palette.primary.main }} />
                <Typography variant="h6" noWrap component="div" sx={{ fontWeight: 'bold' }}>
                    Auth Admin
                </Typography>
            </Toolbar>
            <Divider sx={{ borderColor: 'rgba(255,255,255,0.1)' }} />
            <Box sx={{ flexGrow: 1, overflowY: 'auto', mt: 2 }}>
                <List>
                    {/* Dashboard */}
                    <ListItem disablePadding>
                        <ListItemButton
                            component={Link}
                            to="/"
                            selected={router.location.pathname === '/'}
                            sx={{ my: 0.5, mx: 1, borderRadius: 1 }}
                        >
                            <ListItemIcon>
                                <DashboardIcon />
                            </ListItemIcon>
                            <ListItemText primary="Dashboard" />
                        </ListItemButton>
                    </ListItem>

                    {/* Users Group */}
                    <ListItemButton onClick={handleUsersClick} sx={{ my: 0.5, mx: 1, borderRadius: 1 }}>
                        <ListItemIcon>
                            <PeopleIcon />
                        </ListItemIcon>
                        <ListItemText primary="User Management" />
                        {usersOpen ? <ExpandLess /> : <ExpandMore />}
                    </ListItemButton>
                    <Collapse in={usersOpen} timeout="auto" unmountOnExit>
                        <List component="div" disablePadding>
                            <ListItemButton
                                component={Link}
                                to="/users"
                                selected={router.location.pathname.startsWith('/users')}
                                sx={{ pl: 4, my: 0.5, mx: 1, borderRadius: 1 }}
                            >
                                <ListItemIcon>
                                    <PeopleIcon fontSize="small" />
                                </ListItemIcon>
                                <ListItemText primary="All Users" />
                            </ListItemButton>
                            {/* Placeholder for Roles */}
                            <ListItemButton sx={{ pl: 4, my: 0.5, mx: 1, borderRadius: 1 }}>
                                <ListItemIcon>
                                    <AssignmentInd fontSize="small" />
                                </ListItemIcon>
                                <ListItemText primary="Roles (Planned)" />
                            </ListItemButton>
                        </List>
                    </Collapse>

                    {/* Other items */}
                    <ListItem disablePadding>
                        <ListItemButton sx={{ my: 0.5, mx: 1, borderRadius: 1 }}>
                            <ListItemIcon>
                                <SecurityIcon />
                            </ListItemIcon>
                            <ListItemText primary="Security Log" />
                        </ListItemButton>
                    </ListItem>
                    <ListItem disablePadding>
                        <ListItemButton sx={{ my: 0.5, mx: 1, borderRadius: 1 }}>
                            <ListItemIcon>
                                <SettingsIcon />
                            </ListItemIcon>
                            <ListItemText primary="Settings" />
                        </ListItemButton>
                    </ListItem>
                </List>
            </Box>

            {/* User Profile Footer in Sidebar */}
            <Divider sx={{ borderColor: 'rgba(255,255,255,0.1)' }} />
            <Box sx={{ p: 2, display: 'flex', alignItems: 'center' }}>
                <Avatar sx={{ width: 32, height: 32, mr: 1, bgcolor: theme.palette.primary.main }}>
                    {user?.profile?.displayName?.[0] || 'U'}
                </Avatar>
                <Box>
                    <Typography variant="body2" sx={{ fontWeight: 500 }}>
                        {user?.profile?.displayName || 'Admin User'}
                    </Typography>
                    <Typography variant="caption" sx={{ color: 'text.secondary', display: 'block' }}>
                        {user?.email || 'admin@example.com'}
                    </Typography>
                </Box>
            </Box>
        </Box>
    );

    return (
        <Box sx={{ display: 'flex' }}>
            <AppBar
                position="fixed"
                sx={{
                    width: { md: `calc(100% - ${drawerWidth}px)` },
                    ml: { md: `${drawerWidth}px` },
                }}
            >
                <Toolbar>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        edge="start"
                        onClick={handleDrawerToggle}
                        sx={{ mr: 2, display: { md: 'none' } }}
                    >
                        <MenuIcon />
                    </IconButton>

                    <Box sx={{ flexGrow: 1 }} />

                    {/* Top Right Actions */}
                    <Typography variant="body2" sx={{ mr: 2, color: 'text.secondary' }}>
                        v1.0.0
                    </Typography>

                    <IconButton
                        size="large"
                        aria-label="account of current user"
                        aria-controls="menu-appbar"
                        aria-haspopup="true"
                        onClick={handleMenu}
                        color="inherit"
                    >
                        <Avatar sx={{ width: 32, height: 32 }} src={user?.profile?.avatarUrl} />
                    </IconButton>
                    <Menu
                        id="menu-appbar"
                        anchorEl={anchorEl}
                        anchorOrigin={{
                            vertical: 'top',
                            horizontal: 'right',
                        }}
                        keepMounted
                        transformOrigin={{
                            vertical: 'top',
                            horizontal: 'right',
                        }}
                        open={Boolean(anchorEl)}
                        onClose={handleClose}
                    >
                        <MenuItem onClick={handleClose}>Profile</MenuItem>
                        <MenuItem onClick={handleLogout}>Logout</MenuItem>
                    </Menu>
                </Toolbar>
            </AppBar>

            <Box
                component="nav"
                sx={{ width: { md: drawerWidth }, flexShrink: { md: 0 } }}
                aria-label="mailbox folders"
            >
                {/* Mobile Drawer */}
                <Drawer
                    variant="temporary"
                    open={mobileOpen}
                    onClose={handleDrawerToggle}
                    ModalProps={{
                        keepMounted: true, // Better open performance on mobile.
                    }}
                    sx={{
                        display: { xs: 'block', md: 'none' },
                        '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
                    }}
                >
                    {drawerContent}
                </Drawer>

                {/* Desktop Drawer */}
                <Drawer
                    variant="permanent"
                    sx={{
                        display: { xs: 'none', md: 'block' },
                        '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
                    }}
                    open
                >
                    {drawerContent}
                </Drawer>
            </Box>

            <Box
                component="main"
                sx={{
                    flexGrow: 1,
                    p: 3,
                    width: { md: `calc(100% - ${drawerWidth}px)` },
                    mt: 8 // Toolbar height spacing
                }}
            >
                {children}
            </Box>
        </Box>
    );
}
