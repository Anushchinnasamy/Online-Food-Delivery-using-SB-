# Backend Deployment Guide

## Files Created for Deployment

1. **system.properties** - Specifies Java version
2. **Procfile** - Tells platform how to run the app
3. **application-prod.yml** - Production configuration

## Deploy to Railway

### Step 1: Push to GitHub
```bash
git add .
git commit -m "Prepare for deployment"
git push origin main
```

### Step 2: Deploy on Railway
1. Go to https://railway.app
2. Click "New Project"
3. Select "Deploy from GitHub repo"
4. Choose your repository
5. Railway auto-detects Spring Boot

### Step 3: Add PostgreSQL
1. Click "New" → "Database" → "PostgreSQL"
2. Railway auto-configures connection

### Step 4: Set Environment Variables
```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=your-super-secret-key-min-32-chars
CORS_ORIGINS=https://your-frontend.vercel.app
```

### Step 5: Deploy
- Railway automatically builds and deploys
- Get your URL: `https://your-app.railway.app`

## Deploy to Render

### Step 1: Create Account
1. Go to https://render.com
2. Sign up with GitHub

### Step 2: Create PostgreSQL
1. New → PostgreSQL
2. Copy "Internal Database URL"

### Step 3: Create Web Service
1. New → Web Service
2. Connect repository
3. Configure:
   - **Root Directory**: `backend`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`

### Step 4: Environment Variables
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=<your-postgres-url>
JWT_SECRET=your-secret-key
CORS_ORIGINS=https://your-frontend.vercel.app
PORT=8080
```

## Test Locally with Production Profile

```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://localhost:5432/fooddelivery
export JWT_SECRET=test-secret
mvn spring-boot:run
```

## Environment Variables Explained

- **SPRING_PROFILES_ACTIVE**: Activates production configuration
- **DATABASE_URL**: PostgreSQL connection string
- **JWT_SECRET**: Secret key for JWT tokens (min 32 characters)
- **CORS_ORIGINS**: Allowed frontend URLs
- **PORT**: Server port (auto-set by platform)

## Verify Deployment

Test these endpoints:
- `GET /restaurants` - Should return restaurants
- `POST /auth/login` - Should accept login
- `POST /auth/register` - Should create user

## Troubleshooting

### Build Fails
- Check Java version in `system.properties`
- Verify `pom.xml` is correct
- Check build logs

### Database Connection Fails
- Verify DATABASE_URL is set
- Check PostgreSQL is running
- Verify credentials

### CORS Errors
- Add frontend URL to CORS_ORIGINS
- Include preview URLs: `https://*.vercel.app`
- Redeploy after changes

## Monitoring

### Railway
- View logs in dashboard
- Monitor resource usage
- Check deployment status

### Render
- View logs in dashboard
- Monitor performance
- Check health status

## Cost

### Railway
- $5 free credit/month
- ~$5-10/month after free tier
- PostgreSQL included

### Render
- Free tier available
- Spins down after inactivity
- PostgreSQL included

## Security Checklist

- [ ] Strong JWT secret set
- [ ] CORS properly configured
- [ ] Database credentials secure
- [ ] HTTPS enabled (automatic)
- [ ] Debug logging disabled
- [ ] Production profile active

## Next Steps

1. Deploy backend
2. Get backend URL
3. Update frontend .env.production
4. Deploy frontend
5. Update CORS with frontend URL
6. Test everything works

Your backend is ready to deploy! 🚀
