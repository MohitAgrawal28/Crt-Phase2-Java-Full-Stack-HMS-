# Render Deployment Guide

Hospital Management System Spring Boot API deployment on Render.

## Prerequisites
- Render account (free at https://render.com)
- GitHub repository with this code pushed
- Git installed locally

## Step-by-Step Deployment

### 1. Push Code to GitHub

```bash
# Initialize Git (if not already done)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit: HMS Spring Boot API"

# Add remote and push
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
git branch -M main
git push -u origin main
```

### 2. Create Render Account & Connect GitHub

1. Go to [https://render.com](https://render.com)
2. Sign up (GitHub recommended)
3. Click "New +" → "Web Service"
4. Select "Deploy an existing repository from GitHub"
5. Authorize Render to access your GitHub account
6. Select your repository

### 3. Configure Web Service on Render

**Basic Settings:**
- **Name:** `hospital-management-api` (or your choice)
- **Environment:** `Docker`
- **Build Command:** (Leave empty - Dockerfile handles it)
- **Start Command:** (Leave empty - Dockerfile handles it)
- **Instance Type:** `Free` (to start) or `Standard` for production

**Advanced Settings:**
- Click "Advanced"
- Click "Add Secret File" or "Add Environment Variable"

### 4. Add Environment Variables

In Render dashboard, add these environment variables:

```
DATABASE_URL=postgres://user:password@host:5432/hms_db
DB_USER=hms_admin
DB_PASSWORD=your_secure_password
PORT=8080
```

Use Render PostgreSQL. `DATABASE_URL` can be either Render's default
`postgres://user:password@host:5432/database` value or a JDBC URL such as
`jdbc:postgresql://host:5432/database`.

### 5. Add PostgreSQL Database

1. In your Render project, click "New +"
2. Select "PostgreSQL"
3. Configuration:
   - **Name:** `hms-database`
   - **Database Name:** `hms_db`
   - **Database User:** `hms_admin`
   - **PostgreSQL Version:** Choose latest

4. Render will create environment variable `DATABASE_URL` automatically
5. Update Render web service to connect to this database

### 6. Deploy

1. Click "Deploy" button on Render dashboard
2. Monitor logs in real-time:
   - Build logs show Maven compilation
   - Runtime logs show Spring Boot startup

3. Once deployed (green checkmark), you'll get a live URL like:
   - `https://hospital-management-api.onrender.com`

### 7. Test Your Deployment

Once live, test endpoints:

```bash
# Check system status
curl https://your-app.onrender.com/api/hms/health

# Add a patient
curl -X POST https://your-app.onrender.com/api/hms/patients \
  -H "Content-Type: application/json" \
  -d '{
    "patientID":"P001",
    "name":"John Doe",
    "patientType":"Visitor",
    "rateOrFee":500
  }'

# Add a doctor
curl -X POST https://your-app.onrender.com/api/hms/doctors \
  -H "Content-Type: application/json" \
  -d '{
    "docID":"D001",
    "name":"Dr. Smith",
    "specialization":"Cardiology"
  }'

# List doctors
curl https://your-app.onrender.com/api/hms/doctors

# Schedule appointment
curl -X POST https://your-app.onrender.com/api/hms/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "patientID":"P001",
    "docID":"D001",
    "appointmentDate":"2026-06-01"
  }'
```

## Files Used for Deployment

- **Dockerfile** - Multi-stage build for efficient containerization
- **pom.xml** - Maven configuration with Java 21 and Spring Boot 4.0.6
- **.env.example** - Template for environment variables
- **application.properties** - Spring Boot configuration (reads env vars)

## Environment Variables Reference

| Variable | Purpose | Required | Example |
|----------|---------|----------|---------|
| `DATABASE_URL` | PostgreSQL connection string | Yes | `postgres://user:pass@host:5432/hms_db` |
| `DB_USER` | PostgreSQL username | Optional if present in `DATABASE_URL` | `hms_admin` |
| `DB_PASSWORD` | PostgreSQL password | Optional if present in `DATABASE_URL` | Secure password |
| `PORT` | Server port | No | `8080` |

## Troubleshooting

### Build Fails - "Maven not found"
- Check Dockerfile is in root directory
- Ensure pom.xml exists
- View full logs in Render dashboard

### Build Succeeds, App Crashes
- Check environment variables in Render dashboard
- Verify DATABASE_URL format is correct
- Check PostgreSQL connection is accessible from Render
- View runtime logs for Spring Boot errors

### Database Connection Fails
- Verify `DATABASE_URL` format matches: `postgres://user:password@host:port/db`
- Test PostgreSQL is running and accessible
- Check credentials are correct
- For Render PostgreSQL, wait 2-3 minutes after creation before using

### High Memory Usage (Free Tier)
- Free tier has 512MB RAM
- Spring Boot 4.0.6 uses ~300-400MB
- If crashes occur, upgrade to Standard tier

### Port Issues
- Render sets `PORT` environment variable automatically
- App already configured to use it: `server.port=${PORT:8080}`
- No manual port configuration needed

## Performance Tips

1. **Caching:** Add Redis for session/query caching
2. **Database:** Use Render PostgreSQL in the same region for lower latency
3. **Monitoring:** Enable Render's metrics tab to monitor CPU, memory, network
4. **Auto-Deploy:** Render auto-deploys on push to main branch

## Next Steps

1. ✅ Remove hardcoded credentials (DONE)
2. ✅ Push code to GitHub
3. ✅ Create Render account
4. ✅ Connect GitHub repository
5. ✅ Add MySQL database
6. ✅ Deploy and test
7. Monitor logs and performance

## Support

- Render Docs: https://render.com/docs
- Spring Boot Docs: https://spring.io/projects/spring-boot
- Docker Docs: https://docs.docker.com

## Additional Commands (Local Testing)

Build Docker image locally:
```bash
docker build -t hms-api:latest .
```

Run locally with Docker:
```bash
docker run -e DATABASE_URL=postgres://user:pass@host:5432/hms_db \
           -e DB_USER=postgres \
           -e DB_PASSWORD=password \
           -p 8080:8080 \
           hms-api:latest
```

---

**Deployment Status:** ✅ Ready for Render deployment
