#!/bin/bash
# setup-docker-env.sh - Setup script for Linux/Mac
# Auth Service Docker Environment Setup

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_header() {
    echo ""
    echo "================================================"
    echo "$1"
    echo "================================================"
    echo ""
}

# Check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    print_success "Docker is installed"
}

# Check if Docker Compose is installed
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    print_success "Docker Compose is installed"
}

print_header "Auth Service Docker Environment Setup"

# Check prerequisites
print_info "Checking prerequisites..."
check_docker
check_docker_compose

# Create directory structure
print_info "Creating directory structure..."
mkdir -p init-scripts
mkdir -p keycloak/realms
mkdir -p pgadmin
mkdir -p logs
mkdir -p config
mkdir -p data/postgres
mkdir -p data/redis
mkdir -p data/keycloak
mkdir -p data/pgadmin
print_success "Directory structure created"

# Create PostgreSQL initialization script for both databases
print_info "Creating PostgreSQL initialization script..."
cat > init-scripts/01-init-databases.sql << 'EOF'
-- Create Keycloak database and user
\c postgres;
CREATE DATABASE keycloakdb;
CREATE USER keycloakuser WITH ENCRYPTED PASSWORD 'keycloakpass123';
GRANT ALL PRIVILEGES ON DATABASE keycloakdb TO keycloakuser;

-- Connect to keycloakdb and grant schema permissions
\c keycloakdb;
GRANT ALL ON SCHEMA public TO keycloakuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO keycloakuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO keycloakuser;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO keycloakuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO keycloakuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO keycloakuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO keycloakuser;
ALTER SCHEMA public OWNER TO keycloakuser;

-- Create Auth Service database and user
\c postgres;
CREATE DATABASE authdb;
CREATE USER authuser WITH ENCRYPTED PASSWORD 'authpass123';
GRANT ALL PRIVILEGES ON DATABASE authdb TO authuser;

-- Connect to authdb and create extensions
\c authdb;
GRANT ALL ON SCHEMA public TO authuser;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create schemas
CREATE SCHEMA IF NOT EXISTS identity;
CREATE SCHEMA IF NOT EXISTS authorization;
CREATE SCHEMA IF NOT EXISTS configuration;
CREATE SCHEMA IF NOT EXISTS governance;

GRANT ALL ON SCHEMA identity TO authuser;
GRANT ALL ON SCHEMA authorization TO authuser;
GRANT ALL ON SCHEMA configuration TO authuser;
GRANT ALL ON SCHEMA governance TO authuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA identity TO authuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA authorization TO authuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA configuration TO authuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA governance TO authuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA identity TO authuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA authorization TO authuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA configuration TO authuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA governance TO authuser;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO authuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO authuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA identity GRANT ALL ON TABLES TO authuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA identity GRANT ALL ON SEQUENCES TO authuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA authorization GRANT ALL ON TABLES TO authuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA authorization GRANT ALL ON SEQUENCES TO authuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA configuration GRANT ALL ON TABLES TO authuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA configuration GRANT ALL ON SEQUENCES TO authuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA governance GRANT ALL ON TABLES TO authuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA governance GRANT ALL ON SEQUENCES TO authuser;

-- Set search path
ALTER DATABASE authdb SET search_path TO public, identity, authorization, configuration, governance;
EOF
print_success "PostgreSQL initialization script created"

# Copy Keycloak realm configuration
print_info "Creating Keycloak realm configuration..."
cat > keycloak/realms/auth-service-realm.json << 'EOF'
{
  "realm": "auth-service",
  "enabled": true,
  "displayName": "Auth Service Realm",
  "displayNameHtml": "<b>Auth Service</b>",
  "sslRequired": "none",
  "registrationAllowed": true,
  "registrationEmailAsUsername": false,
  "rememberMe": true,
  "verifyEmail": false,
  "loginWithEmailAllowed": true,
  "duplicateEmailsAllowed": false,
  "resetPasswordAllowed": true,
  "editUsernameAllowed": false,
  "bruteForceProtected": true,
  "permanentLockout": false,
  "maxFailureWaitSeconds": 900,
  "minimumQuickLoginWaitSeconds": 60,
  "waitIncrementSeconds": 60,
  "quickLoginCheckMilliSeconds": 1000,
  "maxDeltaTimeSeconds": 43200,
  "failureFactor": 5,
  "defaultSignatureAlgorithm": "RS256",
  "offlineSessionIdleTimeout": 2592000,
  "accessTokenLifespan": 300,
  "accessTokenLifespanForImplicitFlow": 900,
  "ssoSessionIdleTimeout": 1800,
  "ssoSessionMaxLifespan": 36000,
  "clients": [
    {
      "clientId": "auth-service-client",
      "name": "Auth Service Client",
      "description": "Client for Auth Service integration",
      "enabled": true,
      "clientAuthenticatorType": "client-secret",
      "secret": "auth-service-secret-key-change-in-production",
      "redirectUris": [
        "http://localhost:8090/api/v1/auth/oidc/callback",
        "http://localhost:3000/auth/callback",
        "http://localhost:8090/*"
      ],
      "webOrigins": [
        "http://localhost:8090",
        "http://localhost:3000"
      ],
      "protocol": "openid-connect",
      "publicClient": false,
      "standardFlowEnabled": true,
      "implicitFlowEnabled": false,
      "directAccessGrantsEnabled": true,
      "serviceAccountsEnabled": false,
      "authorizationServicesEnabled": false,
      "fullScopeAllowed": true,
      "defaultClientScopes": [
        "profile",
        "email",
        "roles",
        "web-origins"
      ],
      "optionalClientScopes": [
        "address",
        "phone",
        "offline_access",
        "microprofile-jwt"
      ],
      "attributes": {
        "pkce.code.challenge.method": "S256"
      }
    }
  ],
  "users": [
    {
      "username": "admin",
      "enabled": true,
      "emailVerified": true,
      "email": "admin@authservice.com",
      "firstName": "Admin",
      "lastName": "User",
      "credentials": [
        {
          "type": "password",
          "value": "admin123",
          "temporary": false
        }
      ],
      "realmRoles": [
        "user",
        "admin"
      ],
      "attributes": {
        "department": ["IT"],
        "employeeId": ["EMP001"]
      }
    },
    {
      "username": "john.doe",
      "enabled": true,
      "emailVerified": true,
      "email": "john.doe@company.com",
      "firstName": "John",
      "lastName": "Doe",
      "credentials": [
        {
          "type": "password",
          "value": "password123",
          "temporary": false
        }
      ],
      "realmRoles": [
        "user"
      ],
      "attributes": {
        "department": ["Engineering"],
        "employeeId": ["EMP002"]
      }
    },
    {
      "username": "jane.smith",
      "enabled": true,
      "emailVerified": true,
      "email": "jane.smith@company.com",
      "firstName": "Jane",
      "lastName": "Smith",
      "credentials": [
        {
          "type": "password",
          "value": "password123",
          "temporary": false
        }
      ],
      "realmRoles": [
        "user",
        "manager"
      ],
      "attributes": {
        "department": ["Finance"],
        "employeeId": ["EMP003"]
      }
    }
  ],
  "roles": {
    "realm": [
      {
        "name": "user",
        "description": "Standard user role"
      },
      {
        "name": "admin",
        "description": "Administrator role"
      },
      {
        "name": "manager",
        "description": "Manager role"
      },
      {
        "name": "developer",
        "description": "Developer role"
      }
    ]
  },
  "groups": [
    {
      "name": "Admins",
      "path": "/Admins",
      "attributes": {
        "description": ["Administrator group"]
      },
      "realmRoles": ["admin"]
    },
    {
      "name": "Engineering",
      "path": "/Engineering",
      "attributes": {
        "description": ["Engineering department"]
      },
      "realmRoles": ["developer", "user"]
    },
    {
      "name": "Finance",
      "path": "/Finance",
      "attributes": {
        "description": ["Finance department"]
      },
      "realmRoles": ["user"]
    }
  ],
  "clientScopes": [
    {
      "name": "groups",
      "description": "Group membership scope",
      "protocol": "openid-connect",
      "attributes": {
        "include.in.token.scope": "true",
        "display.on.consent.screen": "true"
      },
      "protocolMappers": [
        {
          "name": "groups",
          "protocol": "openid-connect",
          "protocolMapper": "oidc-group-membership-mapper",
          "consentRequired": false,
          "config": {
            "full.path": "false",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "groups",
            "userinfo.token.claim": "true"
          }
        }
      ]
    }
  ]
}
EOF
print_success "Keycloak realm configuration created"

# Copy pgAdmin server configuration
print_info "Creating pgAdmin configuration..."
cat > pgadmin/servers.json << 'EOF'
{
  "Servers": {
    "1": {
      "Name": "Auth Service DB",
      "Group": "Servers",
      "Host": "postgres",
      "Port": 5432,
      "MaintenanceDB": "authdb",
      "Username": "authuser",
      "Password": "authpass123",
      "SSLMode": "prefer",
      "Favorite": true
    }
  }
}
EOF
print_success "pgAdmin configuration created"

# Create .env file
print_info "Creating .env file..."
cat > .env << 'EOF'
# PostgreSQL Configuration
POSTGRES_DB=authdb
POSTGRES_USER=authuser
POSTGRES_PASSWORD=authpass123

# Redis Configuration
REDIS_PASSWORD=redis123

# Keycloak Configuration
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin123
KEYCLOAK_DB=keycloakdb
KEYCLOAK_DB_USER=keycloakuser
KEYCLOAK_DB_PASSWORD=keycloakpass123

# pgAdmin Configuration
PGADMIN_EMAIL=admin@authservice.com
PGADMIN_PASSWORD=admin123

# Application Configuration
AUTH_SERVICE_PORT=8090
AUTH_SERVICE_DB_URL=jdbc:postgresql://postgres:5432/authdb
AUTH_SERVICE_REDIS_URL=redis://:redis123@redis:6379

# Keycloak OIDC Configuration
OIDC_ISSUER_URI=http://localhost:8080/realms/auth-service
OIDC_CLIENT_ID=auth-service-client
OIDC_CLIENT_SECRET=auth-service-secret-key-change-in-production
EOF
print_success ".env file created"

# Create .gitignore
print_info "Creating .gitignore..."
cat > .gitignore << 'EOF'
# Environment files
.env
.env.local
.env.*.local

# Logs
logs/
*.log

# Docker volumes data (if mounted locally)
data/
volumes/

# IDE
.idea/
.vscode/
*.swp
*.swo
*~

# OS
.DS_Store
Thumbs.db

# Backup files
*.bak
*.backup
EOF
print_success ".gitignore created"

print_header "Setup Complete!"

echo "Directory structure:"
print_success "init-scripts/ (PostgreSQL initialization)"
print_success "keycloak/realms/ (Keycloak realm configuration)"
print_success "pgadmin/ (pgAdmin server configuration)"
print_success "logs/ (Application logs)"
print_success "config/ (Configuration files)"
print_success ".env (Environment variables)"
print_success ".gitignore (Git ignore file)"

echo ""
print_info "Next steps:"
echo "  1. Review .env file and update passwords if needed"
echo "  2. Run: docker-compose up -d"
echo "  3. Wait for services to start (30-60 seconds)"
echo "  4. Check status: docker-compose ps"
echo ""

print_info "Service URLs:"
echo "  • Keycloak Admin: http://localhost:8080"
echo "  • pgAdmin: http://localhost:5050"
echo "  • Redis Commander: http://localhost:8081"
echo ""

print_info "Default Credentials:"
echo "  • Keycloak: admin / admin123"
echo "  • pgAdmin: admin@authservice.com / admin123"
echo "  • PostgreSQL: authuser / authpass123"
echo "  • Redis: redis123"
echo ""

print_warning "⚠️  Remember to change default passwords in production!"
echo ""