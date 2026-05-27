# Railway Deployment Guide

## Prerequisites
- Railway account
- GitHub repository connected to Railway
- Backend root directory set to `SpringbootCRT/demo`

## Backend Deployment

1. Create a Railway project.
2. Choose `Deploy from GitHub repo`.
3. Select this repository.
4. Set the backend service root directory to:

```text
SpringbootCRT/demo
```

5. Add a PostgreSQL service in the same Railway project.
6. Add this variable to the Spring Boot service:

```text
DATABASE_URL=${{Postgres.DATABASE_URL}}
```

The app reads Railway's `postgres://...` URL directly and extracts the username/password from it. You can also override with `HMS_DB_URL`, `DB_USER`, and `DB_PASSWORD` when needed.

## Verify Backend

After Railway deploys, generate a public domain and check:

```bash
curl https://<your-railway-url>/api/hms/health
```

Expected:

```json
{
  "message": "Hospital Management System is running",
  "memoryMode": false
}
```

If `memoryMode` is `true`, the backend is running but PostgreSQL is not connected.

## Frontend Deployment

Deploy the repository to Vercel with this root directory:

```text
frontend
```

Add this Vercel environment variable:

```text
VITE_API_BASE_URL=https://<your-railway-url>
```

The backend currently allows local frontend origins and Vercel domains through CORS. To lock CORS to one domain, set this Railway variable after the Vercel domain is final:

```text
ALLOWED_ORIGIN_PATTERNS=https://<your-vercel-domain>
```

## Environment Variables

| Variable | Purpose | Default |
|----------|---------|---------|
| `PORT` | Server port | `8080` |
| `DATABASE_URL` | Railway PostgreSQL URL | local MySQL fallback |
| `HMS_DB_URL` | Optional JDBC URL override | local MySQL fallback |
| `DB_USER` / `HMS_DB_USER` | Optional DB username override | URL user or local fallback |
| `DB_PASSWORD` / `HMS_DB_PASSWORD` | Optional DB password override | URL password or local fallback |
| `ALLOWED_ORIGIN_PATTERNS` | Backend CORS origin patterns | local dev and `https://*.vercel.app` |

## Troubleshooting

- If Railway health checks fail, confirm `/api/hms/health` is reachable.
- If the API returns `memoryMode: true`, check the PostgreSQL service and `DATABASE_URL`.
- If Vercel cannot call the API, check `VITE_API_BASE_URL` and Railway CORS settings.
