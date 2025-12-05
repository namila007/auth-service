# Auth Service React App - Technical Setup & Architecture

This document serves as the comprehensive technical guide for the `auth-react-app`. It covers the technology stack, project architecture, design system implementation, and setup instructions.

## 1. Technology Stack

### Core Framework
-   **React 19**: Modern UI library for building the application interface.
-   **Vite**: Next-generation frontend tooling for fast development and building.
-   **TypeScript**: Statically typed JavaScript for improved developer experience and code safety.

### UI & Styling
-   **Material UI (MUI) v6**: Primary component library, providing a robust set of accessible and customizable components.
-   **Emotion**: CSS-in-JS styling engine used by MUI.
-   **Tailwind CSS 4**: Used mainly for utility classes in conjunction with MUI where convenient (configured via `@tailwindcss/vite`).

### Routing & Data Fetching
-   **TanStack Router**: Type-safe, file-based routing system. Handles client-side navigation, nested layouts, and URL state management.
-   **TanStack Query (React Query)**: Powerful asynchronous state management for server data, handling caching, synchronization, and updates.
-   **Axios**: HTTP client for API requests, configured with interceptors for token handling.

### State Management
-   **Zustand**: Lightweight, global client-side state management (used primarily for Authentication state).

### Forms & Validation
-   **React Hook Form**: Performant, flexible forms with easy validation.
-   **Zod**: Schema declaration and validation library, integrated with React Hook Form.

## 2. Project Architecture

The project follows a feature-based folder structure to ensure scalability and maintainability.

```
src/
├── components/          # Shared, generic UI components
│   ├── layout/          # Layout components (AppShell, Header, Sidebar)
│   └── ui/              # Reusable atoms (if any custom ones beyond MUI)
├── core/                # Core application logic
│   ├── api/             # API client (Axios instance) & interceptors
│   ├── auth/            # Auth logic (Store, Hooks, Services)
│   └── config/          # Environment & global config constants
├── features/            # Feature modules (Business Logic)
│   ├── auth/            # Login, Logout, Callback pages & logic
│   ├── dashboard/       # Dashboard widgets and stats
│   └── users/           # User management (List, Create, Edit, Details)
├── routes/              # TanStack Router route definitions
│   ├── _protected/      # Layout route for authenticated pages (Sidebar/Header)
│   │   ├── index.tsx    # Dashboard Route
│   │   └── users/       # Nested User Routes
│   ├── __root.tsx       # Root layout (Providers, DevTools)
│   └── login.tsx        # Public Login Route
├── theme/               # Material UI Theme Configuration
└── main.tsx             # Application Entry Point
```

## 3. Routing Architecture

Routing is handled by **TanStack Router** using a file-based routing convention.

-   **File Layout**: Routes are defined in `src/routes/`.
-   **Root Route (`__root.tsx`)**: The top-level wrapper. It usually contains global providers or dev tools.
-   **Protected Layout (`_protected.tsx`)**: This file serves as a layout route for all authenticated pages. It renders the `AppShell` (Sidebar + Header) and wraps the child routes (`<Outlet />`).
    -   *Logic*: It checks the `useAuthStore` state. If the user is unauthenticated, it redirects to `/login` (currently bypassed for dev).
-   **Public Routes**: Routes like `/login` or `/auth/callback` exist outside the `_protected` directory and do not render the sidebar.

## 4. Design System & Theming

The application uses a custom **Material UI Theme** configured in `src/theme/index.ts`.

### Theme: "Mira"
-   **Primary Color**: `#2563eb` (Blue 600) - Used for primary buttons, active states, and highlights.
-   **Background**: `#f8fafc` (Slate 50) - Light gray background for the main content area to reduce eye strain.
-   **Sidebar**: `#111827` (Slate 900) - deeply dark background for the navigation menu.
-   **Typography**: Uses the `Inter` font family (falling back to Roboto/System fonts).
-   **Component Overrides**:
    -   **MuiButton**: Rounded corners (8px), no uppercase text.
    -   **MuiCard**: Subtle border and shadow for a clean "flat-layered" look.
    -   **MuiDrawer**: Styled to match the dark sidebar theme.

## 5. Authentication Flow

Authentication is managed via OIDC (OpenID Connect) Authorization Code Flow.

1.  **Login**: User clicks "Login", redirected to Auth Service (Keycloak/Identity Server).
2.  **Callback**: Browser returns with `code`. `src/features/auth/pages/CallbackPage.tsx` exchanges code for tokens.
3.  **Storage**: Tokens (Access/Refresh) are stored in `localstorage` via Zustand persistence.
4.  **Interceptors**: Axios interceptors in `src/core/api/client.ts` automatically attach the Bearer token and handle 401 errors by attempting a token refresh.

> **Note**: Currently, a login bypass is implemented for development ease. The `useAuthStore` initializes with a mock user.

## 6. Setup Instructions

### Prerequisites
-   Node.js v18 or higher.
-   A running instance of the backend Auth Service (for real API calls).

### Installation
1.  Clone the repository.
2.  Install dependencies:
    ```bash
    npm install
    ```

### Configuration
Create a `.env` file in the root directory:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_AUTH_URL=http://localhost:8080/oauth2/authorize
VITE_CLIENT_ID=auth-admin-client
VITE_REDIRECT_URI=http://localhost:5173/auth/callback
```

### Development
Start the development server:
```bash
npm run dev
```

### Production Build
Build the application for production:
```bash
npm run build
```
This runs the TypeScript compiler (`tsc`) and then the Vite build process. Output is located in `dist/`.

## 7. Common Development Tasks

-   **Adding a New Page**: Create a new file in `src/routes/_protected/` (e.g., `roles.tsx`). It will automatically utilize the AppShell layout.
-   **Adding a Feature**: Create a folder in `src/features/` with `components/`, `hooks/`, `services/`, and `types/`.
-   **Modifying Theme**: Edit `src/theme/index.ts` to adjust colors, typography, or component defaults.
