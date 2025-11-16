#!/bin/bash

# Phase 3 - Task 1: OIDC Provider Configuration APIs Implementation Script
# This script creates the necessary directory structure and generates source files

set -e

# Base directories
DOMAIN_APP_BASE="auth-service/authservice-domain/domain-application/src/main/java/me/namila/service/auth/domain/application"
DOMAIN_APP_TEST_BASE="auth-service/authservice-domain/domain-application/src/test/java/me/namila/service/auth/domain/application"
CONTROLLER_BASE="auth-service/authservice-application/src/main/java/me/namila/service/auth/application/controller"

echo "=== Phase 3 - Task 1: Creating OIDC Provider Configuration APIs ==="
echo ""

# Create directory structure for configuration context
echo "Creating directory structure..."

# Main source directories
mkdir -p "${DOMAIN_APP_BASE}/configuration/dto/request"
mkdir -p "${DOMAIN_APP_BASE}/configuration/dto/response"
mkdir -p "${DOMAIN_APP_BASE}/configuration/service"
mkdir -p "${DOMAIN_APP_BASE}/configuration/mapper"

# Test directories
mkdir -p "${DOMAIN_APP_TEST_BASE}/configuration/service"
mkdir -p "${DOMAIN_APP_TEST_BASE}/configuration/mapper"

# Controller directory
mkdir -p "${CONTROLLER_BASE}"

echo "✓ Directory structure created"
echo ""

echo "=== Next Steps: ==="
echo "1. Run this script to create directories: ./phase3-task1-setup.sh"
echo "2. Create DTO classes in: ${DOMAIN_APP_BASE}/configuration/dto/"
echo "3. Create mapper interface"
echo "4. Create application service"
echo "5. Create REST controller"
echo "6. Create unit tests"
echo "7. Run tests: ./gradlew test"
echo ""
echo "Refer to PHASE3-IMPLEMENTATION-GUIDE.md for complete code examples"
