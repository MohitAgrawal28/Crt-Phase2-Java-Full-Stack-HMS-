# Deployment Guide

## Backend On Render

The repository includes a root `render.yaml` Blueprint. Render reads it from GitHub and creates:

- `hms-backend` web service
- `hms-postgres` PostgreSQL database
- `DATABASE_URL` wired from the database to the backend
- Health check at `/api/hms/health`

### Deploy

1. Open Render.
2. Click `New +`.
3. Choose `Blueprint`.
4. Select this GitHub repository.
5. Confirm the Blueprint plan.
6. Render will create and deploy the backend automatically.

### Verify Backend

After Render deploys, check:

```bash
curl https://<your-render-url>/api/hms/health
```

Expected:

```json
{
  "message": "Hospital Management System is running",
  "memoryMode": false
}
```

If `memoryMode` is `true`, the backend is running but PostgreSQL is not connected.

## Frontend On Vercel

Deploy the repository to Vercel with this root directory:

```text
frontend
```

Add this Vercel environment variable:

```text
VITE_API_BASE_URL=https://<your-render-url>
```

The backend currently allows local frontend origins and Vercel domains through CORS. To lock CORS to one domain, set this Render environment variable after the Vercel domain is final:

```text
ALLOWED_ORIGIN_PATTERNS=https://<your-vercel-domain>
```

## Environment Variables

| Variable | Purpose | Default |
|----------|---------|---------|
| `PORT` | Server port | `8080` |
| `DATABASE_URL` | Render PostgreSQL URL | local MySQL fallback |
| `HMS_DB_URL` | Optional JDBC URL override | local MySQL fallback |
| `DB_USER` / `HMS_DB_USER` | Optional DB username override | URL user or local fallback |
| `DB_PASSWORD` / `HMS_DB_PASSWORD` | Optional DB password override | URL password or local fallback |
| `ALLOWED_ORIGIN_PATTERNS` | Backend CORS origin patterns | local dev and `https://*.vercel.app` |

## Troubleshooting

- If Render health checks fail, confirm `/api/hms/health` is reachable.
- If the API returns `memoryMode: true`, check the PostgreSQL service and `DATABASE_URL`.
- If Vercel cannot call the API, check `VITE_API_BASE_URL` and Render CORS settings.
