# Auth React App - Complete Implementation Plan
## React 19 + Node 22 | Auth Service Consumer | Enterprise Admin Console

---

## Table of Contents
1. [Executive Summary](#1-executive-summary)
2. [Technology Stack](#2-technology-stack)
3. [Application Architecture](#3-application-architecture)
4. [Feature Modules](#4-feature-modules)
5. [API Integration](#5-api-integration)
6. [State Management](#6-state-management)
7. [Routing & Navigation](#7-routing--navigation)
8. [UI/UX Design System](#8-uiux-design-system)
9. [Security Implementation](#9-security-implementation)
10. [Implementation Phases](#10-implementation-phases)
11. [Project Structure](#11-project-structure)
12. [Getting Started](#12-getting-started)

---

## 1. Executive Summary

### 1.1 Purpose
This React application serves as the **administrative console and user portal** for the Auth Service backend. It provides a comprehensive interface for:

| Domain | Capabilities |
|--------|--------------|
| **Authentication** | OIDC-based login with multiple identity providers |
| **User Management** | CRUD operations for users, profiles, and federated identities |
| **Authorization** | Roles, permissions, policies, and role assignments management |
| **OIDC Configuration** | Configure and manage external identity providers |
| **Governance & Audit** | View audit logs, compliance reports, and access certifications |

### 1.2 Target Users

| User Type | Capabilities |
|-----------|--------------|
| **System Administrator** | Full access to all features, OIDC provider configuration, policy management |
| **Security Admin** | User management, role assignments, audit log access |
| **Auditor** | Read-only access to audit logs and compliance reports |
| **End User** | Self-service profile management, view assigned roles |

### 1.3 Backend API Reference
This app consumes the Auth Service API as documented in `auth-service-plan.md`:
- **Base URL**: `http://localhost:8080/api/v1`
- **Authentication**: JWT Bearer tokens
- **OIDC Providers**: External IdP integration (Azure AD, Okta, Google, etc.)

---

## 2. Technology Stack

### 2.1 Core Technologies
| Technology | Version | Purpose |
|------------|---------|---------|
| **Node.js** | 22.x LTS | Runtime environment |
| **React** | 19.x | UI framework with concurrent features |
| **TypeScript** | 5.6+ | Type safety and DX |
| **Vite** | 6.x | Build tool & dev server |

### 2.2 Dependencies Matrix

#### State Management & Data Fetching
| Library | Version | Purpose |
|---------|---------|---------|
| **@tanstack/react-query** | ^5.x | Server state management, caching |
| **@tanstack/react-router** | ^1.x | Type-safe file-based routing |
| **zustand** | ^5.x | Lightweight client state (auth) |
| **react-hook-form** | ^7.x | Form management |
| **zod** | ^3.x | Schema validation |

#### UI & Styling
| Library | Version | Purpose |
|---------|---------|---------|
| **tailwindcss** | ^4.x | Utility-first CSS |
| **@radix-ui/***  | Latest | Headless UI primitives |
| **lucide-react** | Latest | Icon library |
| **framer-motion** | ^11.x | Animations |
| **class-variance-authority** | Latest | Variant utilities |
| **clsx** + **tailwind-merge** | Latest | Class utilities |

#### API & Networking
| Library | Version | Purpose |
|---------|---------|---------|
| **axios** | ^1.x | HTTP client |
| **oidc-client-ts** | ^3.x | OIDC authentication |

#### Utilities
| Library | Version | Purpose |
|---------|---------|---------|
| **date-fns** | ^3.x | Date manipulation |
| **@hookform/resolvers** | Latest | Form + Zod integration |

#### Testing
| Library | Version | Purpose |
|---------|---------|---------|
| **vitest** | ^2.x | Unit testing |
| **@testing-library/react** | ^15.x | Component testing |
| **playwright** | ^1.x | E2E testing |
| **msw** | ^2.x | API mocking |

---

## 3. Application Architecture

### 3.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              APP SHELL                                   │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                       Navigation & Layout                           │ │
│  │    [Header] ──── [Sidebar] ──── [Main Content Area]                │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                        FEATURE MODULES                              │ │
│  │  ┌──────────────┐ ┌──────────────┐ ┌────────────────────────────┐  │ │
│  │  │     Auth     │ │    Users     │ │       Authorization        │  │ │
│  │  │    Module    │ │   Module     │ │         Module             │  │ │
│  │  │              │ │              │ │                            │  │ │
│  │  │ • Login      │ │ • List       │ │ • Roles Management         │  │ │
│  │  │ • Callback   │ │ • Create     │ │ • Permissions Management   │  │ │
│  │  │ • Logout     │ │ • Detail     │ │ • Role Assignments         │  │ │
│  │  │ • Profile    │ │ • Edit       │ │ • Policy Management        │  │ │
│  │  │              │ │ • Delete     │ │ • Authorization Simulator  │  │ │
│  │  └──────────────┘ └──────────────┘ └────────────────────────────┘  │ │
│  │                                                                     │ │
│  │  ┌──────────────┐ ┌──────────────────────────────────────────────┐ │ │
│  │  │     OIDC     │ │            Audit & Governance                │ │ │
│  │  │   Config     │ │               Module                         │ │ │
│  │  │   Module     │ │                                              │ │ │
│  │  │              │ │ • Audit Logs          • Compliance Reports   │ │ │
│  │  │ • Providers  │ │ • User Activity       • Certifications       │ │ │
│  │  │ • Mapping    │ │ • Dashboard           • Export               │ │ │
│  │  │ • Testing    │ │                                              │ │ │
│  │  └──────────────┘ └──────────────────────────────────────────────┘ │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                           CORE LAYER                                │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐  │ │
│  │  │  API Client │ │ Auth Store  │ │ UI Library  │ │   Shared    │  │ │
│  │  │  (Axios)    │ │ (Zustand)   │ │ (shadcn/ui) │ │  Utilities  │  │ │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Module Design Principles

Each feature module is **self-contained** with:
- **Components/**: UI components specific to the feature
- **hooks/**: Custom hooks for data fetching and business logic
- **services/**: API integration layer
- **types/**: TypeScript interfaces
- **pages/**: Route-level page components

### 3.3 Key Architectural Decisions

| Decision | Rationale |
|----------|-----------|
| **Feature-based structure** | Colocation improves maintainability |
| **TanStack Query for server state** | Automatic caching, background refetch, optimistic updates |
| **Zustand for auth only** | Minimal client state, avoid over-engineering |
| **TanStack Router** | Type-safe routing with file-based organization |
| **Lazy loading modules** | Optimal initial load performance |
| **Zod for validation** | Runtime + compile-time type safety |

---

## 4. Feature Modules

### 4.1 Authentication Module (`features/auth`)

| Screen | Route | Description |
|--------|-------|-------------|
| Login | `/login` | Provider selection & OIDC initiation |
| OIDC Callback | `/auth/callback` | Handle OIDC redirect |
| Logout | `/logout` | Session termination |
| Profile | `/profile` | Self-service user profile |

**Components**:
- `ProviderSelector` - List of configured OIDC providers
- `OIDCLoginButton` - Initiates OIDC flow for selected provider
- `AuthCallback` - Handles authorization code exchange
- `UserProfile` - Display and edit current user profile

---

### 4.2 User Management Module (`features/users`)

| Screen | Route | Description |
|--------|-------|-------------|
| User List | `/users` | Paginated, searchable user list |
| User Create | `/users/new` | Create new user form |
| User Detail | `/users/:userId` | View user details |
| User Edit | `/users/:userId/edit` | Edit user details |
| User Roles | `/users/:userId/roles` | Manage user role assignments |
| User Permissions | `/users/:userId/permissions` | View effective permissions |

**Components**:
- `UserTable` - Sortable, filterable data table
- `UserForm` - Create/Edit user form with validation
- `UserProfileCard` - Display user profile information
- `FederatedIdentityList` - Show linked external identities
- `UserRoleAssignments` - Manage role assignments
- `EffectivePermissions` - Display computed permissions
- `UserStatusBadge` - Visual status indicator

---

### 4.3 Authorization Module (`features/authorization`)

#### Roles Sub-Module
| Screen | Route | Description |
|--------|-------|-------------|
| Role List | `/roles` | All roles with permission counts |
| Role Create | `/roles/new` | Create new role |
| Role Detail | `/roles/:roleId` | View role with permissions |
| Role Edit | `/roles/:roleId/edit` | Edit role details |

#### Permissions Sub-Module
| Screen | Route | Description |
|--------|-------|-------------|
| Permission List | `/permissions` | All permissions by resource |
| Permission Create | `/permissions/new` | Create new permission |

#### Role Assignments Sub-Module
| Screen | Route | Description |
|--------|-------|-------------|
| Assignment List | `/assignments` | All role assignments |
| Bulk Assignment | `/assignments/bulk` | Bulk assign roles |

#### Policy Management Sub-Module
| Screen | Route | Description |
|--------|-------|-------------|
| Policy List | `/policies` | All policies |
| Policy Create | `/policies/new` | Create new policy |
| Policy Edit | `/policies/:policyId/edit` | Edit policy |
| Policy Simulator | `/policies/simulate` | Test policy decisions |

**Key Components**:
- `RoleTable`, `RoleForm`, `RoleHierarchyTree`
- `PermissionTable`, `PermissionForm`, `PermissionPicker`
- `AssignmentTable`, `AssignmentForm`, `BulkAssignmentWizard`
- `PolicyEditor`, `PolicyConditionBuilder`, `PolicySimulator`

---

### 4.4 OIDC Configuration Module (`features/oidc`)

| Screen | Route | Description |
|--------|-------|-------------|
| Provider List | `/oidc/providers` | All configured providers |
| Provider Create | `/oidc/providers/new` | Add new OIDC provider (wizard) |
| Provider Detail | `/oidc/providers/:providerId` | View configuration |
| Provider Edit | `/oidc/providers/:providerId/edit` | Edit configuration |
| Provider Test | `/oidc/providers/:providerId/test` | Test connection |

**Components**:
- `ProviderTable` - Providers with status & user counts
- `ProviderWizard` - Multi-step configuration wizard
- `OIDCEndpointConfig` - Configure OAuth endpoints
- `AttributeMappingEditor` - Map external to internal attributes
- `RoleMappingEditor` - Map external groups to internal roles
- `JITProvisioningConfig` - Configure auto-provisioning
- `ProviderTestResult` - Display connection test results

---

### 4.5 Audit & Governance Module (`features/governance`)

| Screen | Route | Description |
|--------|-------|-------------|
| Audit Logs | `/audit/logs` | Searchable audit log viewer |
| User Activity | `/audit/users/:userId` | User-specific activity |
| Compliance Report | `/audit/compliance` | Generate compliance reports |
| Certifications | `/audit/certifications` | Access certification campaigns |
| Dashboard | `/audit/dashboard` | Governance metrics dashboard |

**Components**:
- `AuditLogTable` - Advanced search & filtering
- `AuditLogDetail` - Full log entry details
- `AuditTimeline` - Visual activity timeline
- `ComplianceReportGenerator` - Report configuration
- `CertificationCampaign` - Manage certification reviews
- `GovernanceDashboard` - Metrics & KPIs
- `AuditExport` - Export to CSV/PDF

---

## 5. API Integration

### 5.1 Backend API Endpoints Summary

```typescript
// Authentication
POST /api/v1/auth/oidc/initiate
POST /api/v1/auth/oidc/callback
POST /api/v1/auth/token/refresh
POST /api/v1/auth/logout

// Users
GET    /api/v1/users
GET    /api/v1/users/:userId
POST   /api/v1/users
PATCH  /api/v1/users/:userId
DELETE /api/v1/users/:userId
GET    /api/v1/users/:userId/permissions

// Roles
GET    /api/v1/roles
GET    /api/v1/roles/:roleId
POST   /api/v1/roles
PUT    /api/v1/roles/:roleId
DELETE /api/v1/roles/:roleId
POST   /api/v1/roles/:roleId/permissions
DELETE /api/v1/roles/:roleId/permissions/:permissionId

// Permissions
GET    /api/v1/permissions
POST   /api/v1/permissions
DELETE /api/v1/permissions/:permissionId

// Assignments
GET    /api/v1/assignments
POST   /api/v1/assignments
DELETE /api/v1/assignments/:assignmentId
POST   /api/v1/assignments/bulk

// Policies
GET    /api/v1/policies
GET    /api/v1/policies/:policyId
POST   /api/v1/policies
PUT    /api/v1/policies/:policyId
DELETE /api/v1/policies/:policyId

// Authorization Check
POST   /api/v1/access-control/check
POST   /api/v1/access-control/batch-check

// OIDC Providers
GET    /api/v1/oidc-providers
GET    /api/v1/oidc-providers/:providerId
POST   /api/v1/oidc-providers
PUT    /api/v1/oidc-providers/:providerId
DELETE /api/v1/oidc-providers/:providerId
POST   /api/v1/oidc-providers/:providerId/test

// Audit
GET    /api/v1/audit/logs
GET    /api/v1/audit/user-activity/:userId
GET    /api/v1/audit/compliance-report
POST   /api/v1/audit/access-certifications
```

### 5.2 API Response Types

```typescript
// Standard wrapper
interface ApiResponse<T> {
  status: 'success' | 'error';
  data?: T;
  error?: ApiError;
  metadata?: { timestamp: string; version: string };
}

interface ApiError {
  code: string;
  message: string;
  details?: string;
  field?: string;
  correlationId: string;
}

// Pagination
interface PagedResponse<T> {
  content: T[];
  pageable: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    hasNext: boolean;
    hasPrevious: boolean;
  };
}
```

### 5.3 Error Handling Strategy

| Status | Action |
|--------|--------|
| **401** | Attempt token refresh → redirect to login if failed |
| **403** | Show permission denied message |
| **404** | Show not found page |
| **422** | Show field-level validation errors |
| **500** | Show generic error with retry option |

---

## 6. State Management

### 6.1 State Strategy

| State Type | Solution | Examples |
|------------|----------|----------|
| **Server State** | TanStack Query | Users, Roles, Permissions, Audit Logs |
| **Auth State** | Zustand | Current user, tokens, session |
| **UI State** | React useState | Modal open, sidebar collapsed |
| **Form State** | React Hook Form | Form inputs, validation |
| **URL State** | TanStack Router | Filters, pagination, sort |

### 6.2 Auth Store Shape

```typescript
interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  
  // Actions
  setAuth: (user: User, tokens: TokenPair) => void;
  clearAuth: () => void;
  refreshTokens: () => Promise<boolean>;
  checkSession: () => Promise<void>;
}
```

### 6.3 React Query Key Structure

```typescript
export const queryKeys = {
  users: {
    all: ['users'] as const,
    lists: () => [...queryKeys.users.all, 'list'] as const,
    list: (filters: UserFilters) => [...queryKeys.users.lists(), filters] as const,
    details: () => [...queryKeys.users.all, 'detail'] as const,
    detail: (id: string) => [...queryKeys.users.details(), id] as const,
    permissions: (id: string) => [...queryKeys.users.detail(id), 'permissions'] as const,
  },
  roles: { /* similar structure */ },
  permissions: { /* similar structure */ },
  policies: { /* similar structure */ },
  oidcProviders: { /* similar structure */ },
  audit: { /* similar structure */ },
};
```

---

## 7. Routing & Navigation

### 7.1 Complete Route Structure

```
/                           → Dashboard (redirect based on role)
/login                      → Login page
/auth/callback              → OIDC callback handler
/logout                     → Logout page

/dashboard                  → Admin dashboard

/users                      → User list
/users/new                  → Create user
/users/:userId              → User detail
/users/:userId/edit         → Edit user
/users/:userId/roles        → User role assignments
/users/:userId/permissions  → User effective permissions

/roles                      → Role list
/roles/new                  → Create role
/roles/:roleId              → Role detail
/roles/:roleId/edit         → Edit role

/permissions                → Permission list
/permissions/new            → Create permission
/permissions/:permissionId  → Permission detail

/assignments                → Role assignment list
/assignments/bulk           → Bulk assignment wizard

/policies                   → Policy list
/policies/new               → Create policy
/policies/:policyId         → Policy detail
/policies/:policyId/edit    → Edit policy
/policies/simulate          → Policy simulator

/oidc/providers             → OIDC provider list
/oidc/providers/new         → Add provider wizard
/oidc/providers/:providerId → Provider detail
/oidc/providers/:providerId/edit → Edit provider
/oidc/providers/:providerId/test → Test provider

/audit/logs                 → Audit log viewer
/audit/users/:userId        → User activity
/audit/compliance           → Compliance reports
/audit/certifications       → Certifications
/audit/dashboard            → Governance dashboard

/profile                    → Current user profile
/settings                   → User preferences
```

### 7.2 Navigation Sidebar Structure

```
┌─────────────────────────────────┐
│ Dashboard                       │
├─────────────────────────────────┤
│ IDENTITY                        │
│ ├── Users                       │
│ └── Profiles                    │
├─────────────────────────────────┤
│ ACCESS MANAGEMENT               │
│ ├── Roles                       │
│ ├── Permissions                 │
│ ├── Assignments                 │
│ └── Policies                    │
├─────────────────────────────────┤
│ CONFIGURATION                   │
│ └── OIDC Providers              │
├─────────────────────────────────┤
│ GOVERNANCE                      │
│ ├── Audit Logs                  │
│ ├── Compliance Reports          │
│ └── Certifications              │
└─────────────────────────────────┘
```

---

## 8. UI/UX Design System

### 8.1 Design Principles

1. **Dark Mode First** - Professional, modern appearance
2. **Glassmorphism** - Subtle transparency and blur effects
3. **Micro-animations** - Smooth transitions and feedback
4. **Accessible** - WCAG 2.1 AA compliance
5. **Responsive** - Desktop-first with tablet support

### 8.2 Color Palette

```css
/* Primary */
--color-primary: oklch(0.45 0.24 264);      /* Deep blue */
--color-primary-light: oklch(0.65 0.18 264);

/* Semantic */
--color-success: oklch(0.70 0.18 145);      /* Green */
--color-warning: oklch(0.80 0.18 80);       /* Amber */
--color-error: oklch(0.60 0.22 25);         /* Red */
--color-info: oklch(0.65 0.18 220);         /* Light blue */

/* Dark Mode Surfaces */
--bg-primary: oklch(0.15 0.01 260);
--bg-secondary: oklch(0.18 0.01 260);
--bg-tertiary: oklch(0.22 0.02 260);
--text-primary: oklch(0.95 0.01 260);
--text-secondary: oklch(0.75 0.01 260);
```

### 8.3 Component Library (shadcn/ui)

Building on shadcn/ui with these customizations:
- **DataTable** - Advanced sorting, filtering, row actions
- **Card** - Glassmorphism effect
- **Button** - Gradient variants, loading states
- **Form** - Integrated with React Hook Form + Zod
- **Dialog** - Slide-in animations
- **Toast** - Success/Error variants with icons

---

## 9. Security Implementation

### 9.1 OIDC Authentication Flow

```
1. User navigates to /login
   └─→ Fetch OIDC providers: GET /api/v1/oidc-providers?enabled=true

2. User selects provider
   └─→ POST /api/v1/auth/oidc/initiate
       Response: { authorizationUrl, state, codeVerifier }

3. Store state + codeVerifier in sessionStorage
   └─→ Redirect to IdP authorizationUrl

4. User authenticates with external IdP
   └─→ IdP redirects to /auth/callback?code=xxx&state=yyy

5. Callback handler
   └─→ Verify state
   └─→ POST /api/v1/auth/oidc/callback
       Response: { accessToken, refreshToken, user }

6. Store tokens in memory (Zustand)
   └─→ Redirect to dashboard
```

### 9.2 Token Management

- **Access Token**: Stored in memory (Zustand)
- **Refresh Token**: Stored in localStorage (with XSS protection)
- **Token Refresh**: Automatic refresh 5 minutes before expiry
- **Logout**: Clear all tokens, redirect to login

### 9.3 Security Measures

- **CSRF Protection**: State parameter in OIDC flow
- **XSS Protection**: Content sanitization, CSP headers
- **Token Injection**: Axios interceptor for Bearer token
- **Error Handling**: Never expose sensitive info in errors

---

## 10. Implementation Phases

### Phase 1: Project Foundation (Week 1)
- [ ] Initialize Vite + React 19 + TypeScript
- [ ] Configure Tailwind CSS 4.x
- [ ] Set up ESLint + Prettier
- [ ] Install and configure shadcn/ui
- [ ] Set up TanStack Router
- [ ] Set up TanStack Query
- [ ] Create Axios client with interceptors
- [ ] Create Zustand auth store
- [ ] Create base layout components

### Phase 2: Authentication Module (Week 2)
- [ ] Create auth types and services
- [ ] Implement OIDC flow hooks
- [ ] Build login page with provider selection
- [ ] Build callback handler
- [ ] Implement token refresh
- [ ] Add route protection

### Phase 3: User Management Module (Week 3)
- [ ] Create user types and services
- [ ] Implement user CRUD hooks
- [ ] Build user list with DataTable
- [ ] Build create/edit user forms
- [ ] Build user detail page
- [ ] Add user search and filters

### Phase 4: Roles & Permissions (Week 4)
- [ ] Create authorization types
- [ ] Implement role/permission services
- [ ] Build role CRUD pages
- [ ] Build permission management
- [ ] Implement role permission assignment

### Phase 5: Assignments & Policies (Week 5)
- [ ] Implement assignment service
- [ ] Build role assignment management
- [ ] Implement bulk assignment wizard
- [ ] Build policy CRUD pages
- [ ] Build policy simulator

### Phase 6: OIDC Configuration (Week 6)
- [ ] Create OIDC types and services
- [ ] Build provider configuration wizard
- [ ] Implement attribute mapping editor
- [ ] Implement role mapping editor
- [ ] Build provider test functionality

### Phase 7: Audit & Governance (Week 7)
- [ ] Create audit types and services
- [ ] Build audit log viewer
- [ ] Implement advanced search
- [ ] Build compliance report generator
- [ ] Build governance dashboard

### Phase 8: Testing & Polish (Week 8)
- [ ] Write unit tests
- [ ] Write integration tests
- [ ] Write E2E tests
- [ ] Performance optimization
- [ ] Accessibility audit
- [ ] Documentation

---

## 11. Project Structure

```
auth-react-app/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── tailwind.config.ts
├── .eslintrc.cjs
├── .prettier.rc
├── .env.example
├── index.html
│
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── index.css
│   │
│   ├── core/
│   │   ├── api/
│   │   │   ├── client.ts
│   │   │   ├── interceptors.ts
│   │   │   └── types.ts
│   │   ├── auth/
│   │   │   ├── store.ts
│   │   │   ├── provider.tsx
│   │   │   └── types.ts
│   │   ├── config/
│   │   │   └── env.ts
│   │   ├── hooks/
│   │   │   └── useDebounce.ts
│   │   └── utils/
│   │       ├── cn.ts
│   │       └── date.ts
│   │
│   ├── components/
│   │   ├── ui/           # shadcn components
│   │   ├── layout/
│   │   │   ├── AppShell.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   ├── Header.tsx
│   │   │   └── PageHeader.tsx
│   │   ├── feedback/
│   │   │   ├── LoadingSpinner.tsx
│   │   │   ├── EmptyState.tsx
│   │   │   └── ErrorBoundary.tsx
│   │   └── data/
│   │       ├── DataTable.tsx
│   │       └── Pagination.tsx
│   │
│   ├── features/
│   │   ├── auth/
│   │   │   ├── components/
│   │   │   ├── hooks/
│   │   │   ├── services/
│   │   │   ├── types/
│   │   │   └── pages/
│   │   ├── users/
│   │   │   └── ...
│   │   ├── authorization/
│   │   │   ├── roles/
│   │   │   ├── permissions/
│   │   │   ├── assignments/
│   │   │   ├── policies/
│   │   │   └── types/
│   │   ├── oidc/
│   │   │   └── ...
│   │   └── governance/
│   │       ├── audit/
│   │       ├── compliance/
│   │       ├── certifications/
│   │       └── dashboard/
│   │
│   └── routes/
│       ├── __root.tsx
│       ├── index.tsx
│       ├── login.tsx
│       ├── auth/
│       ├── users/
│       ├── roles/
│       ├── permissions/
│       ├── assignments/
│       ├── policies/
│       ├── oidc/
│       └── audit/
│
├── tests/
│   ├── setup.ts
│   ├── mocks/
│   ├── unit/
│   ├── integration/
│   └── e2e/
│
└── docs/
    ├── ARCHITECTURE.md
    └── API_INTEGRATION.md
```

---

## 12. Getting Started

### Prerequisites
- Node.js 22.x LTS
- npm or pnpm

### Quick Start
```bash
# Navigate to project directory
cd auth-react-app

# Install dependencies
npm install

# Copy environment file
cp .env.example .env

# Start development server
npm run dev
```

### Environment Variables
```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_OIDC_REDIRECT_URI=http://localhost:5173/auth/callback
```

### Available Scripts
| Script | Description |
|--------|-------------|
| `npm run dev` | Start development server |
| `npm run build` | Build for production |
| `npm run preview` | Preview production build |
| `npm run test` | Run unit tests |
| `npm run test:e2e` | Run E2E tests |
| `npm run lint` | Lint codebase |
| `npm run format` | Format with Prettier |

---

## Summary

This implementation plan provides a complete roadmap for building a **production-ready React 19 application** that serves as the administrative console for the Auth Service. 

**Key Highlights**:
- Modern stack: React 19, Node 22, Vite 6, TypeScript 5.6+
- Modular architecture with feature-based organization
- Full TypeScript coverage with Zod validation
- TanStack Query for efficient server state
- Premium UI with shadcn/ui + Tailwind CSS
- Comprehensive security with OIDC integration
- 8-week implementation timeline

**Estimated Duration**: 8 weeks for full implementation including testing and polish.
