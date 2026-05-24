# Render Deployment Guide

Hospital Management System Spring Boot API deployment on Render.

## ⚠️ Important: Database Change
**MySQL is NOT supported on Render free tier.** This guide uses **PostgreSQL instead**.
- Database schema has been converted from MySQL to PostgreSQL
- Application is now configured for PostgreSQL
- All environment variables below use PostgreSQL

## Prerequisites
- Render account (free at https://render.com)
- GitHub repository with this code pushed
- Git installed locally
- The latest code with PostgreSQL configuration (already done!)

## Step-by-Step Deployment

### 1. Prepare Your Local Environment (First Time Only)

Before pushing to GitHub, test locally with PostgreSQL:

```bash
# On Windows: Install PostgreSQL from https://www.postgresql.org/download/windows/
# Or use Docker: docker run --name hms-db -e POSTGRES_PASSWORD=password -p 5432:5432 postgres

# Connect to PostgreSQL and create database:
psql -U postgres
CREATE DATABASE hms_db;
\c hms_db
# Copy-paste contents of src/main/resources/schema.sql and run it
```

Then test your Spring Boot app locally:
```bash
mvn spring-boot:run
```

### 2. Push Code to GitHub

```bash
# From SpringbootCRT/demo directory
git init
git add .
git commit -m "HMS Spring Boot with PostgreSQL"
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
git branch -M main
git push -u origin main
```

### 3. Create Render PostgreSQL Database

1. Go to https://render.com and sign in
2. Click **"New +"** → **"PostgreSQL"**
3. Configure:
   - **Name:** `hms-database`
   - **Database:** `hms_db`
   - **User:** `hms_admin` (or any username)
   - **Region:** Choose closest to you
   - **PostgreSQL Version:** Latest stable (e.g., 15+)
4. Click **"Create Database"**
5. ⚠️ **Copy the Internal Database URL** (looks like: `postgres://user:password@host:5432/hms_db`)
6. Also copy the **Hostname, Port, Database, Username, Password** separately

### 4. Create Web Service on Render

1. Click **"New +"** → **"Web Service"**
2. Click **"Deploy an existing repository from GitHub"**
3. Authorize Render to access your GitHub
4. Select your repository
5. Configure:
   - **Name:** `hospital-management-api`
   - **Environment:** `Docker`
   - **Region:** Same as database
   - **Branch:** `main`
   - **Build Command:** (Leave empty)
   - **Start Command:** (Leave empty)
   - **Plan:** `Free` tier

### 5. Add Environment Variables to Web Service

In Render dashboard for your Web Service, click **"Environment"** and add:

```
DATABASE_URL=postgres://USER:PASSWORD@HOST:PORT/DATABASE
DB_USER=USER
DB_PASSWORD=PASSWORD
PORT=8080
```

**Replace with your PostgreSQL database credentials:**
- `USER` → from Render PostgreSQL
- `PASSWORD` → from Render PostgreSQL
- `HOST` → Render database hostname
- `PORT` → Usually 5432
- `DATABASE` → `hms_db`

### 6. Configure Private Network (Important!)

1. In your Render PostgreSQL details, note the **Internal Database URL** (private)
2. Your Web Service will connect via internal network - no extra cost!
3. This is automatically enabled if both services are in the same region

### 7. Deploy

1. Click **"Create Web Service"** button
2. Render will automatically:
   - Build Docker image (using Dockerfile)
   - Deploy the image
   - Start your application
3. Monitor build progress in **"Logs"** tab
4. Once deployed (green status), you get a live URL like:
   - `https://hospital-management-api.onrender.com`

### 8. Initialize Database

Your database schema (tables, indexes) will be created automatically from `src/main/resources/schema.sql` on first startup.

### 9. Test Your Deployment

Once live, test your API endpoints:

```bash
# Check application status
curl https://hospital-management-api.onrender.com

# For your HMS endpoints
curl https://hospital-management-api.onrender.com/api/hms/patients
```

## Troubleshooting

### "Connection refused" error
- ✅ Check DATABASE_URL environment variable is set correctly
- ✅ Verify both services are in same region
- ✅ Check PostgreSQL service is running (green status)

### "Password authentication failed"
- ✅ Double-check DB_USER and DB_PASSWORD match exactly
- ✅ Verify no extra spaces in credentials

### Application won't start
- ✅ Check logs: Click "Logs" in Render dashboard
- ✅ Verify pom.xml has `spring-boot-starter-data-jpa` dependency
- ✅ Check application.properties has PostgreSQL config

### Logs show "table does not exist"
- ✅ Schema will be created on first startup
- ✅ Wait a few seconds and refresh
- ✅ Or manually run schema.sql in Render PostgreSQL console

## Important Files Updated

- ✅ `application.properties` - Now uses PostgreSQL config
- ✅ `pom.xml` - Added Spring Data JPA dependency
- ✅ `src/main/resources/schema.sql` - PostgreSQL schema (auto-created)
- ✅ `Dockerfile` - Already correct for Render deployment

## Cost

**Free tier includes:**
- ✅ Web Service with 0.5 GB RAM
- ✅ PostgreSQL database with 1 GB storage
- ✅ Both services on same region = free internal networking

Charges only if you exceed free limits or upgrade.

## Next Steps

1. Test locally first (see Step 1)
2. Push to GitHub
3. Create PostgreSQL on Render
4. Create Web Service with environment variables
5. Deploy and monitor logs
6. Test live endpoints
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
