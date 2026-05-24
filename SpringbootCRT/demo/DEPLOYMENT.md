# Railway Deployment Guide

## Prerequisites
- Railway account (free at https://railway.app)
- Git installed locally
- Your code pushed to GitHub/GitLab

## Step-by-Step Deployment

### 1. Create a Railway Account & Project
- Go to [https://railway.app](https://railway.app)
- Sign up with GitHub (recommended for easy integration)
- Create a new project

### 2. Connect Your Repository
- In Railway dashboard, click "New Project"
- Select "Deploy from GitHub"
- Authorize Railway to access your GitHub account
- Select your repository containing this code

### 3. Configure Environment Variables
In your Railway project settings, add these environment variables:

**For MySQL Database (if using Railway's MySQL):**
```
HMS_DB_URL=jdbc:mysql://<railway-mysql-host>:3306/hms_db
HMS_DB_USER=<mysql-username>
HMS_DB_PASSWORD=<mysql-password>
```

**Or if using Railway's MySQL add-on:**
- Railway will automatically provide `DATABASE_URL` variable
- Our app will read it and configure accordingly

### 4. Add MySQL Database (Optional)
- In your project, click "Add" → "Add a Service"
- Select "MySQL"
- Railway will automatically set up environment variables
- Database credentials will be available as `DATABASE_URL`

### 5. Deploy
- Railway automatically deploys when you push to your main branch
- Monitor the deployment in the Railway dashboard
- Once deployed, you'll get a live URL for your API

### 6. Test Your Deployment
Once deployed, test your endpoints:
```bash
# Check system status
curl https://<your-railway-url>/api/hms/status

# Add a patient
curl -X POST https://<your-railway-url>/api/hms/patients \
  -H "Content-Type: application/json" \
  -d '{"patientID":"P001","name":"John Doe","patientType":"Visitor","rateOrFee":500}'
```

## Files Created for Deployment

- **Procfile** - Tells Railway how to run the application
- **Dockerfile** - Multi-stage build for efficient containerization
- **railway.json** - Railway-specific configuration
- **.railwayignore** - Excludes unnecessary files from deployment

## Environment Variables Reference

| Variable | Purpose | Default |
|----------|---------|---------|
| `PORT` | Server port | 8080 |
| `HMS_DB_URL` | MySQL connection URL | localhost:3306/hms_db |
| `HMS_DB_USER` | MySQL username | root |
| `HMS_DB_PASSWORD` | MySQL password | (required for production) |

## Troubleshooting

### Build Fails
- Check that `pom.xml` has correct dependencies
- Ensure Java 21 is specified (already configured)

### Database Connection Fails
- Make sure environment variables are set correctly in Railway
- Check MySQL service is running (if using Railway's MySQL add-on)
- App will fall back to in-memory mode if DB unavailable

### Port Issues
- Railway automatically assigns a PORT environment variable
- Your app already reads this: `server.port=${PORT:8080}`

## Next Steps
1. Push code to GitHub
2. Go to Railway dashboard
3. Connect your GitHub repository
4. Set environment variables
5. Watch the deployment logs
6. Test your live API endpoints!

For more info: https://docs.railway.app/
