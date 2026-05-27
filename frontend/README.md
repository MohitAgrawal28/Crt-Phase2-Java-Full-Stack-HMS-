# HMS Frontend

Vercel-ready frontend for the Hospital Management System API.

## Local development

```powershell
npm install
npm run dev
```

By default, local development calls:

```text
http://localhost:8080
```

To point at a deployed Railway backend, set:

```text
VITE_API_BASE_URL=https://your-railway-backend.up.railway.app
```

## Vercel settings

Set the Vercel project root directory to:

```text
frontend
```

Add this environment variable:

```text
VITE_API_BASE_URL=https://your-railway-backend.up.railway.app
```
