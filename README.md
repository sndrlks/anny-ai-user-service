# anny-ai-user-service

## Changelog

### 2025-04-21
- Secured POST /api/v1/auth/logout with USER and ADMIN roles.
- Updated SecurityConfig to allow public access to /api/v1/auth/login and /api/v1/auth/refresh, and to protect logout.
- Added @PreAuthorize("hasAnyRole('USER','ADMIN')") on AuthController.logout.
- Created `.codex/plan_2025-04-21.md` for today's planning.