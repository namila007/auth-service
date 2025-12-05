import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    IconButton,
    Menu,
    MenuItem,
    Chip,
    Box,
    Avatar,
    Typography,
    Pagination
} from "@mui/material"
import { MoreHoriz as MoreIcon, Edit as EditIcon, Delete as DeleteIcon, VpnKey as KeyIcon } from "@mui/icons-material"
import { type User } from "../types/users.types"
import { useState } from "react"
import { Link } from "@tanstack/react-router"
import { UserStatusBadge } from "./UserStatusBadge"

interface UserTableProps {
    data: User[];
    isLoading: boolean;
    pageCount: number;
    pagination: { pageIndex: number; pageSize: number };
    onPaginationChange: (pagination: { pageIndex: number; pageSize: number }) => void;
}

export function UserTable({ data, isLoading, pageCount, pagination, onPaginationChange }: UserTableProps) {
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);

    const handleMenuOpen = (event: React.MouseEvent<HTMLButtonElement>, user: User) => {
        setAnchorEl(event.currentTarget);
        setSelectedUser(user);
    };

    const handleMenuClose = () => {
        setAnchorEl(null);
        setSelectedUser(null);
    };

    const handlePageChange = (_: React.ChangeEvent<unknown>, value: number) => {
        onPaginationChange({ ...pagination, pageIndex: value - 1 });
    };

    if (isLoading) {
        return <Typography>Loading...</Typography>
    }

    return (
        <Box>
            <TableContainer component={Paper} elevation={0} sx={{ border: '1px solid #e2e8f0' }}>
                <Table sx={{ minWidth: 650 }} aria-label="user table">
                    <TableHead>
                        <TableRow>
                            <TableCell>User</TableCell>
                            <TableCell>Email</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Roles</TableCell>
                            <TableCell align="right">Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {data.map((user) => (
                            <TableRow
                                key={user.userId}
                                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                            >
                                <TableCell component="th" scope="row">
                                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                        <Avatar sx={{ width: 32, height: 32, mr: 1.5 }}>
                                            {user.profile.displayName?.[0] || user.username[0]}
                                        </Avatar>
                                        <Box>
                                            <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
                                                {user.profile.displayName || user.username}
                                            </Typography>
                                            <Typography variant="caption" color="text.secondary">
                                                {user.username}
                                            </Typography>
                                        </Box>
                                    </Box>
                                </TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell>
                                    <UserStatusBadge status={user.status} />
                                </TableCell>
                                <TableCell>
                                    <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                                        {user.roles && user.roles.length > 0 ? (
                                            user.roles.slice(0, 2).map((role) => (
                                                <Chip key={role} label={role} size="small" variant="outlined" />
                                            ))
                                        ) : (
                                            <Typography variant="caption" color="text.secondary">None</Typography>
                                        )}
                                        {user.roles && user.roles.length > 2 && (
                                            <Chip label={`+${user.roles.length - 2}`} size="small" variant="outlined" />
                                        )}
                                    </Box>
                                </TableCell>
                                <TableCell align="right">
                                    <IconButton
                                        aria-label="more"
                                        onClick={(e) => handleMenuOpen(e, user)}
                                        size="small"
                                    >
                                        <MoreIcon />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
                <Pagination
                    count={pageCount}
                    page={pagination.pageIndex + 1}
                    onChange={handlePageChange}
                    color="primary"
                    shape="rounded"
                />
            </Box>

            <Menu
                id="user-actions-menu"
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
                MenuListProps={{
                    'aria-labelledby': 'basic-button',
                }}
            >
                {selectedUser && (
                    <Box>
                        <MenuItem
                            component={Link as any}
                            to="/users/$userId"
                            params={{ userId: selectedUser.userId }}
                            onClick={handleMenuClose}
                        >
                            <EditIcon fontSize="small" sx={{ mr: 1.5 }} /> Edit Details
                        </MenuItem>
                        <MenuItem onClick={handleMenuClose} disabled>
                            <KeyIcon fontSize="small" sx={{ mr: 1.5 }} /> Manage Roles
                        </MenuItem>
                        <MenuItem onClick={handleMenuClose} sx={{ color: 'error.main' }}>
                            <DeleteIcon fontSize="small" sx={{ mr: 1.5 }} /> Delete User
                        </MenuItem>
                    </Box>
                )}
            </Menu>
        </Box>
    )
}
