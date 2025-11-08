# setup-docker-env.ps1 - Setup script for Windows PowerShell
# Auth Service Docker Environment Setup

# Set error action preference
$ErrorActionPreference = "Stop"

# Function to print colored output
function Write-Success {
    param([string]$Message)
    Write-Host "✓ $Message" -ForegroundColor Green
}

function Write-Error-Custom {
    param([string]$Message)
    Write-Host "✗ $Message" -ForegroundColor Red
}

function Write-Info {
    param([string]$Message)
    Write-Host "ℹ $Message" -ForegroundColor Cyan
}

function Write-Warning-Custom {
    param([string]$Message)
    Write-Host "⚠ $Message" -ForegroundColor Yellow
}

function Write-Header {
    param([string]$Message)
    Write-Host ""
    Write-Host "================================================" -ForegroundColor Blue
    Write-Host $Message -ForegroundColor Blue
    Write-Host "================================================" -ForegroundColor Blue
    Write-Host ""
}

# Check if Docker is installed
function Test-Docker {
    try {
        $null = docker --version 2>&1
        Write-Success "Docker is installed"
        return $true
    }
    catch {
        Write-Error-Custom "Docker is not installed. Please install Docker Desktop first."
        return $false
    }
}

# Check if Docker Compose is installed
function Test-DockerCompose {
    try {
        $null = docker-compose --version 2>&1
        Write-Success "Docker Compose is installed"
        return $true
    }
    catch {
        try {
            $null = docker compose version 2>&1
            Write-Success "Docker Compose (v2) is installed"
            return $true
        }
        catch {
            Write-Error-Custom "Docker Compose is not installed. Please install Docker Desktop with Compose."
            return $false
        }
    }
}

Write-Header "Auth Service Docker Environment Setup"

# Check prerequisites
Write-Info "Checking prerequisites..."
if (-not (Test-Docker)) {
    exit 1
}
if (-not (Test-DockerCompose)) {
    exit 1
}

# Create directory structure
Write-Info "Creating directory structure..."
$directories = @("init-scripts", "keycloak\realms", "pgadmin", "logs", "config", "data\postgres", "data\redis", "data\keycloak", "data\pgadmin")
foreach ($dir in $directories) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
}
Write-Success "Directory structure created"

# Create PostgreSQL initialization script for both databases
Write-Info "Creating PostgreSQL initialization script..."
$pgInitScript = @'
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
'@
$pgInitScript | Out-File -FilePath "init-scripts\01-init-databases.sql" -Encoding UTF8
Write-Success "PostgreSQL initialization script created"

# Create Keycloak realm configuration
Write-Info "Creating Keycloak realm configuration..."
$keycloakRealm = @'
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
'@
$keycloakRealm | Out-File -FilePath "keycloak\realms\auth-service-realm.json" -Encoding UTF8
Write-Success "Keycloak realm configuration created"

# Create pgAdmin server configuration
Write-Info "Creating pgAdmin configuration..."
$pgAdminConfig = @'
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
'@
$pgAdminConfig | Out-File -FilePath "pgadmin\servers.json" -Encoding UTF8
Write-Success "pgAdmin configuration created"

# Create .env file
Write-Info "Creating .env file..."
$envContent = @'
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
'@
$envContent | Out-File -FilePath ".env" -Encoding UTF8
Write-Success ".env file created"

# Create .gitignore
Write-Info "Creating .gitignore..."
$gitignoreContent = @'
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
'@
$gitignoreContent | Out-File -FilePath ".gitignore" -Encoding UTF8
Write-Success ".gitignore created"

Write-Header "Setup Complete!"

Write-Host "Directory structure:" -ForegroundColor White
Write-Success "init-scripts\ (PostgreSQL initialization)"
Write-Success "keycloak\realms\ (Keycloak realm configuration)"
Write-Success "pgadmin\ (pgAdmin server configuration)"
Write-Success "logs\ (Application logs)"
Write-Success "config\ (Configuration files)"
Write-Success ".env (Environment variables)"
Write-Success ".gitignore (Git ignore file)"

Write-Host ""
Write-Info "Next steps:"
Write-Host "  1. Review .env file and update passwords if needed" -ForegroundColor White
Write-Host "  2. Run: docker-compose up -d" -ForegroundColor White
Write-Host "  3. Wait for services to start (30-60 seconds)" -ForegroundColor White
Write-Host "  4. Check status: docker-compose ps" -ForegroundColor White
Write-Host ""

Write-Info "Service URLs:"
Write-Host "  • Keycloak Admin: http://localhost:8080" -ForegroundColor White
Write-Host "  • pgAdmin: http://localhost:5050" -ForegroundColor White
Write-Host "  • Redis Commander: http://localhost:8081" -ForegroundColor White
Write-Host ""

Write-Info "Default Credentials:"
Write-Host "  • Keycloak: admin / admin123" -ForegroundColor White
Write-Host "  • pgAdmin: admin@authservice.com / admin123" -ForegroundColor White
Write-Host "  • PostgreSQL: authuser / authpass123" -ForegroundColor White
Write-Host "  • Redis: redis123" -ForegroundColor White
Write-Host ""

Write-Warning-Custom "⚠️  Remember to change default passwords in production!"
Write-Host ""

Write-Host "Press any key to continue..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")