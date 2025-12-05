import { useNavigate } from '@tanstack/react-router'
import { useUsers } from '@/features/users/hooks/useUsers'
import { UserTable } from '@/features/users/components/UserTable'
import {
    Button,
    Typography,
    Box,
    Stack,
    TextField,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    type SelectChangeEvent
} from '@mui/material'
import { Add as PlusIcon } from '@mui/icons-material'
import { useState } from 'react'
import { Link } from '@tanstack/react-router'
import { type UserStatus } from '../types/users.types'

interface UserListPageProps {
    page: number;
    size: number;
    search?: string;
    status?: UserStatus | 'ALL';
}

export function UserListPage({ page, size, search, status = 'ALL' }: UserListPageProps) {
    const navigate = useNavigate({ from: '/users' })

    // Local state for search input
    const [searchTerm, setSearchTerm] = useState(search || '')

    const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(event.target.value);
    }

    const handleSearchSubmit = () => {
        navigate({
            search: (prev: any) => ({ ...prev, search: searchTerm, page: 1 })
        })
    }

    const handleStatusChange = (event: SelectChangeEvent) => {
        const val = event.target.value;
        navigate({
            search: (prev: any) => ({
                ...prev,
                status: val === 'ALL' ? undefined : val,
                page: 1
            })
        })
    }

    const { data, isLoading } = useUsers(
        page - 1, // API is 0-indexed
        size,
        {
            search,
            status: status === 'ALL' ? undefined : status
        }
    )

    const handlePaginationChange = (pagination: { pageIndex: number; pageSize: number }) => {
        navigate({
            search: (prev: any) => ({ ...prev, page: pagination.pageIndex + 1, size: pagination.pageSize })
        })
    }

    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Box>
                    <Typography variant="h4" gutterBottom fontWeight="bold">
                        Users
                    </Typography>
                    <Typography variant="body1" color="text.secondary">
                        Manage system users and their identities.
                    </Typography>
                </Box>
                <Button
                    variant="contained"
                    color="primary"
                    component={Link}
                    to="/users/new"
                    startIcon={<PlusIcon />}
                >
                    Create User
                </Button>
            </Box>

            <Stack direction="row" spacing={2} sx={{ mb: 3 }} alignItems="center">
                <TextField
                    placeholder="Search users..."
                    size="small"
                    value={searchTerm}
                    onChange={handleSearchChange}
                    onKeyDown={(e) => e.key === 'Enter' && handleSearchSubmit()}
                    onBlur={handleSearchSubmit}
                    sx={{ width: 300 }}
                />

                <FormControl size="small" sx={{ minWidth: 200 }}>
                    <InputLabel id="status-select-label">Status</InputLabel>
                    <Select
                        labelId="status-select-label"
                        id="status-select"
                        value={status}
                        label="Status"
                        onChange={handleStatusChange}
                    >
                        <MenuItem value="ALL">All Statuses</MenuItem>
                        <MenuItem value="ACTIVE">Active</MenuItem>
                        <MenuItem value="INACTIVE">Inactive</MenuItem>
                        <MenuItem value="SUSPENDED">Suspended</MenuItem>
                        <MenuItem value="LOCKED">Locked</MenuItem>
                        <MenuItem value="PENDING_VERIFICATION">Pending Verification</MenuItem>
                    </Select>
                </FormControl>
            </Stack>

            <UserTable
                data={data?.content || []}
                isLoading={isLoading}
                pageCount={data?.pageable?.totalPages || 0}
                pagination={{
                    pageIndex: page - 1,
                    pageSize: size
                }}
                onPaginationChange={handlePaginationChange}
            />
        </Box>
    )
}
