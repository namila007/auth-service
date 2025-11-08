# Auth Service Docker Environment

Complete Docker setup for Authentication and Authorization Service infrastructure with PostgreSQL, Redis, Keycloak, and management UIs.

## 📋 Table of Contents

- [Services Overview](#services-overview)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
  - [Linux/Mac](#linuxmac)
  - [Windows](#windows)
- [Service Access](#service-access)
- [Configuration Files](#configuration-files)
- [Usage Guide](#usage-guide)
- [Troubleshooting](#troubleshooting)
- [Production Notes](#production-notes)

## 🚀 Services Overview

| Service | Description | Port | Container Name |
|---------|-------------|------|----------------|
| **PostgreSQL** | Main database (authdb + keycloakdb) | 5432 | auth-postgres |
| **Redis** | Cache and session storage | 6379 | auth-redis |
| **Keycloak** | OIDC/SAML Identity Provider | 8080 | auth-keycloak |
| **pgAdmin** | PostgreSQL management UI | 5050 | auth-pgadmin |
| **Redis Commander** | Redis management UI | 8081 | auth-redis-commander |

## ✅ Prerequisites

### All Platforms
- **Docker Desktop** 4.0+ or Docker Engine 20.10+
- **Docker Compose** 2.0+ (included with Docker Desktop)
- Minimum 4GB RAM allocated to Docker
- 10GB free disk space

### Platform-Specific

**Linux/Mac:**
- Bash shell
- `curl` command (for testing)

**Windows:**
- PowerShell 5.1+ or PowerShell Core 7+
- Windows 10/11 or Windows Server 2019+

## 🎯 Quick Start

### Linux/Mac

```bash
# 1. Make the setup script executable
chmod +x setup-docker-env.sh

# 2. Run the setup script
./setup-docker-env.sh

# 3. Start all services
docker-compose up -d

# 4. Check service status
docker-compose ps

# 5. View logs (optional)
docker-compose logs -f
```

### Windows

```powershell
# 1. Run PowerShell as Administrator (recommended)

# 2. Allow script execution (if needed)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# 3. Run the setup script
.\setup-docker-env.ps1

# 4. Start all services
docker-compose up -d

# 5. Check service status
docker-compose ps

# 6. View logs (optional)
docker-compose logs -f
```

## 🔐 Service Access

### Keycloak Admin Console
- **URL:** http://localhost:8080
- **Username:** `admin`
- **Password:** `admin123`
- **Realm:** `auth-service`

**Pre-configured Test Users:**
| Username | Password | Roles |
|----------|----------|-------|
| admin | admin123 | admin, user |
| john.doe | password123 | user |
| jane.smith | password123 | manager, user |

### pgAdmin
- **URL:** http://localhost:5050
- **Email:** `admin@authservice.com`
- **Password:** `admin123`

Pre-configured servers will appear automatically:
- Auth Service DB (authdb)
- Keycloak DB (keycloakdb)

### Redis Commander
- **URL:** http://localhost:8081
- **No authentication required** (development only)

### Direct Database Access

**PostgreSQL (Auth Service DB):**
```bash
# Linux/Mac
docker-compose exec postgres psql -U authuser -d authdb

# Windows PowerShell
docker-compose exec postgres psql -U authuser -d authdb
```

**PostgreSQL (Keycloak DB):**
```bash
# Linux/Mac
docker-compose exec postgres psql -U keycloakuser -d keycloakdb

# Windows PowerShell
docker-compose exec postgres psql -U keycloakuser -d keycloakdb
```

**Redis CLI:**
```bash
# Linux/Mac
docker-compose exec redis redis-cli -a redis123

# Windows PowerShell
docker-compose exec redis redis-cli -a redis123
```

## 📁 Configuration Files

After running the setup script, the following structure is created:

```
.
├── docker-compose.yml              # Main Docker Compose configuration
├── .env                            # Environment variables (DO NOT COMMIT)
├── .gitignore                      # Git ignore rules
├── setup-docker-env.sh             # Linux/Mac setup script
├── setup-docker-env.ps1            # Windows PowerShell setup script
├── init-scripts/
│   └── 01-init-databases.sql       # PostgreSQL initialization
├── keycloak/
│   └── realms/
│       └── auth-service-realm.json # Keycloak realm configuration
├── pgadmin/
│   └── servers.json                # pgAdmin server configuration
└── logs/                           # Application logs directory
```

### Environment Variables (.env)

The `.env` file contains all configuration:

```properties
# PostgreSQL
POSTGRES_DB=authdb
POSTGRES_USER=authuser
POSTGRES_PASSWORD=authpass123

# Redis
REDIS_PASSWORD=redis123

# Keycloak
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin123

# pgAdmin
PGADMIN_EMAIL=admin@authservice.com
PGADMIN_PASSWORD=admin123

# OIDC Configuration
OIDC_ISSUER_URI=http://localhost:8080/realms/auth-service
OIDC_CLIENT_ID=auth-service-client
OIDC_CLIENT_SECRET=auth-service-secret-key-change-in-production
```

## 📖 Usage Guide

### Starting Services

```bash
# Start all services in background
docker-compose up -d

# Start specific service
docker-compose up -d keycloak

# Start with logs visible
docker-compose up
```

### Stopping Services

```bash
# Stop all services (keeps data)
docker-compose stop

# Stop and remove containers (keeps volumes/data)
docker-compose down

# Stop and remove everything including volumes (CLEAN SLATE)
docker-compose down -v
```

### Viewing Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f keycloak
docker-compose logs -f postgres
docker-compose logs -f redis

# Last 100 lines
docker-compose logs --tail=100 keycloak
```

### Restarting Services

```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart keycloak
```

### Checking Service Health

```bash
# View status of all containers
docker-compose ps

# Check specific service health
docker-compose ps keycloak

# Execute health check commands
docker-compose exec postgres pg_isready -U authuser -d authdb
docker-compose exec redis redis-cli -a redis123 ping
```

### Database Operations

**Create a backup:**
```bash
# Linux/Mac
docker-compose exec postgres pg_dump -U authuser authdb > backup_$(date +%Y%m%d).sql

# Windows PowerShell
docker-compose exec postgres pg_dump -U authuser authdb > "backup_$(Get-Date -Format 'yyyyMMdd').sql"
```

**Restore from backup:**
```bash
# Linux/Mac
docker-compose exec -T postgres psql -U authuser authdb < backup.sql

# Windows PowerShell
Get-Content backup.sql | docker-compose exec -T postgres psql -U authuser authdb
```

**Access database schemas:**
```bash
docker-compose exec postgres psql -U authuser -d authdb -c "\dn"
```

## 🧪 Testing OIDC Integration

### 1. Verify Keycloak Configuration

Visit the discovery endpoint:
```
http://localhost:8080/realms/auth-service/.well-known/openid-configuration
```

### 2. Test Authentication Flow

**Get Authorization URL:**
```bash
# Linux/Mac
open "http://localhost:8080/realms/auth-service/protocol/openid-connect/auth?client_id=auth-service-client&redirect_uri=http://localhost:8090/api/v1/auth/oidc/callback&response_type=code&scope=openid%20profile%20email%20groups&state=random-state-123"

# Windows PowerShell
Start-Process "http://localhost:8080/realms/auth-service/protocol/openid-connect/auth?client_id=auth-service-client&redirect_uri=http://localhost:8090/api/v1/auth/oidc/callback&response_type=code&scope=openid%20profile%20email%20groups&state=random-state-123"
```

**Exchange Code for Token:**
```bash
curl -X POST http://localhost:8080/realms/auth-service/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code" \
  -d "client_id=auth-service-client" \
  -d "client_secret=auth-service-secret-key-change-in-production" \
  -d "code=YOUR_CODE_HERE" \
  -d "redirect_uri=http://localhost:8090/api/v1/auth/oidc/callback"
```

**Get User Info:**
```bash
curl -X GET http://localhost:8080/realms/auth-service/protocol/openid-connect/userinfo \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 3. OIDC Endpoints Reference

| Endpoint | URL |
|----------|-----|
| Discovery | http://localhost:8080/realms/auth-service/.well-known/openid-configuration |
| Authorization | http://localhost:8080/realms/auth-service/protocol/openid-connect/auth |
| Token | http://localhost:8080/realms/auth-service/protocol/openid-connect/token |
| UserInfo | http://localhost:8080/realms/auth-service/protocol/openid-connect/userinfo |
| JWKS | http://localhost:8080/realms/auth-service/protocol/openid-connect/certs |
| Logout | http://localhost:8080/realms/auth-service/protocol/openid-connect/logout |

## 🔧 Troubleshooting

### Services Won't Start

**Check Docker is running:**
```bash
docker ps
```

**Check for port conflicts:**
```bash
# Linux/Mac
lsof -i :8080
lsof -i :5432
lsof -i :6379

# Windows PowerShell
netstat -ano | findstr :8080
netstat -ano | findstr :5432
netstat -ano | findstr :6379
```

**Free up ports if needed:**
- Change ports in `docker-compose.yml`
- Or stop conflicting services

### Keycloak Fails to Start

**Check logs:**
```bash
docker-compose logs keycloak
```

**Common issues:**
- PostgreSQL not ready → Wait 30 seconds and retry
- Port 8080 in use → Change port mapping
- Memory issues → Increase Docker memory allocation

**Restart Keycloak:**
```bash
docker-compose restart keycloak
```

### Database Connection Errors

**Verify PostgreSQL is running:**
```bash
docker-compose ps postgres
docker-compose logs postgres
```

**Test connection:**
```bash
docker-compose exec postgres psql -U authuser -d authdb -c "SELECT 1"
```

**Reset database (WARNING: Deletes all data):**
```bash
docker-compose down -v
docker-compose up -d
```

### Redis Connection Issues

**Check Redis status:**
```bash
docker-compose exec redis redis-cli -a redis123 ping
# Should return: PONG
```

**Check Redis info:**
```bash
docker-compose exec redis redis-cli -a redis123 INFO
```

### pgAdmin Can't Connect

**Verify server configuration:**
1. Open http://localhost:5050
2. Right-click on server → Properties
3. Ensure hostname is `postgres` (not localhost)
4. Port should be `5432`

### Complete Reset

If everything is broken, start fresh:

```bash
# Stop and remove everything
docker-compose down -v

# Remove all auth-related containers
docker container prune -f

# Remove networks
docker network prune -f

# Re-run setup
./setup-docker-env.sh  # Linux/Mac
.\setup-docker-env.ps1  # Windows

# Start services
docker-compose up -d
```

## ⚠️ Production Notes

**This setup is for DEVELOPMENT ONLY!**

### Security Checklist for Production

- [ ] Change ALL default passwords
- [ ] Enable SSL/TLS for all services
- [ ] Use secrets management (Vault, AWS Secrets Manager)
- [ ] Remove management UIs or restrict access
- [ ] Enable authentication for Redis
- [ ] Configure proper firewall rules
- [ ] Use production-grade PostgreSQL configuration
- [ ] Enable Keycloak production mode
- [ ] Implement backup strategy
- [ ] Set up monitoring and alerting
- [ ] Use persistent volumes for production data
- [ ] Configure log rotation
- [ ] Implement rate limiting
- [ ] Enable audit logging
- [ ] Use strong JWT signing keys
- [ ] Disable debug/development features

### Production Configuration Changes

**Keycloak:**
```yaml
command:
  - start  # Remove '-dev' flag
  - --optimized
environment:
  KC_HTTP_ENABLED: false
  KC_HOSTNAME_STRICT_HTTPS: true
```

**PostgreSQL:**
- Use managed database service (RDS, Cloud SQL)
- Enable automated backups
- Configure replication
- Set appropriate connection limits

**Redis:**
- Use managed Redis service (ElastiCache, MemoryStore)
- Enable persistence
- Configure eviction policies
- Enable clustering for high availability

## 📝 Additional Resources

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Redis Documentation](https://redis.io/documentation)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

## 🆘 Support

For issues or questions:
1. Check the troubleshooting section above
2. Review Docker Compose logs
3. Check service-specific documentation
4. Verify Docker resources (memory, disk)

## 📄 License

This configuration is provided as-is for development purposes.