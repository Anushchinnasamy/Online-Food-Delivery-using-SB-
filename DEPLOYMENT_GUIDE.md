# 🚀 Complete Deployment Guide

## Overview

Your application has two parts:
1. **Frontend (React)** → Deploy to **Vercel** ✅
2. **Backend (Spring Boot)** → Deploy to **Railway** ✅

---

## 🎯 Quick Deployment Steps

### Part 1: Deploy Backend to Railway (5 minutes)

#### Step 1: Create Railway Account
1. Go to https://railway.app
2. Sign up with GitHub
3. Click "New Project"

#### Step 2: Deploy Backend
1. Click "Deploy from GitHub repo"
2. Select your repository
3. Railway will auto-detect Spring Boot
4. Add PostgreSQL database:
   - Click "New" → "Database" → "PostgreSQL"
   - Railway will auto-configure connection

#### Step 3: Set Environment Variables
In Railway dashboard, add these variables:
```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
CORS_ORIGINS=https://your-frontend-url.vercel.app
```

#### Step 4: Get Backend URL
- Railway will provide a URL like: `https://your-app.railway.app`
- Copy this URL for frontend configuration

---

### Part 2: Deploy Frontend to Vercel (3 minutes)

#### Step 1: Prepare Frontend
Update `.env.production`:
```bash
VITE_API_BASE_URL=https://your-backend-url.railway.app
```

#### Step 2: Deploy to Vercel
1. Go to https://vercel.com
2. Sign up with GitHub
3. Click "New Project"
4. Import your repository
5. Configure:
   - **Framework Preset**: Vite
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`

#### Step 3: Add Environment Variable
In Vercel dashboard:
- Go to Settings → Environment Variables
- Add: `VITE_API_BASE_URL` = `https://your-backend-url.railway.app`

#### Step 4: Deploy
- Click "Deploy"
- Wait 2-3 minutes
- Your app will be live at: `https://your-app.vercel.app`

---

## 🔧 Alternative: Deploy Backend to Render

### Step 1: Create Render Account
1. Go to https://render.com
2. Sign up with GitHub

### Step 2: Create PostgreSQL Database
1. Click "New" → "PostgreSQL"
2. Name: `fooddelivery-db`
3. Copy the "Internal Database URL"

### Step 3: Deploy Backend
1. Click "New" → "Web Service"
2. Connect your repository
3. Configure:
   - **Name**: `fooddelivery-backend`
   - **Root Directory**: `backend`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`
   - **Environment**: Java

### Step 4: Add Environment Variables
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=<your-postgres-url>
JWT_SECRET=your-secret-key
CORS_ORIGINS=https://your-frontend.vercel.app
PORT=8080
```

---

## 📝 Pre-Deployment Checklist

### Backend Checklist
- [x] `system.properties` created (Java 17)
- [x] `Procfile` created
- [x] `application-prod.yml` created
- [ ] Update CORS origins with frontend URL
- [ ] Set strong JWT secret
- [ ] Database configured
- [ ] Test locally with production profile

### Frontend Checklist
- [x] `vercel.json` created
- [ ] Update `.env.production` with backend URL
- [ ] Test build locally: `npm run build`
- [ ] Verify API calls work
- [ ] Check all routes work

---

## 🧪 Test Deployment Locally

### Test Backend with Production Profile
```bash
cd backend
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://localhost:5432/fooddelivery
export JWT_SECRET=test-secret
mvn spring-boot:run
```

### Test Frontend Build
```bash
cd frontend
npm run build
npm run preview
```

---

## 🌐 Update CORS After Deployment

### Step 1: Get Your Vercel URL
After deploying frontend, you'll get: `https://your-app.vercel.app`

### Step 2: Update Backend CORS
In Railway/Render, update environment variable:
```
CORS_ORIGINS=https://your-app.vercel.app,https://your-app-*.vercel.app
```

### Step 3: Redeploy Backend
Railway/Render will auto-redeploy with new CORS settings

---

## 📱 Custom Domain (Optional)

### For Frontend (Vercel)
1. Go to Vercel dashboard
2. Settings → Domains
3. Add your domain
4. Update DNS records as instructed

### For Backend (Railway)
1. Go to Railway dashboard
2. Settings → Domains
3. Add custom domain
4. Update DNS records

---

## 🔒 Security Checklist

- [ ] Change JWT secret to strong random string
- [ ] Use environment variables for all secrets
- [ ] Enable HTTPS (automatic on Vercel/Railway)
- [ ] Set proper CORS origins
- [ ] Use production database
- [ ] Disable debug logging
- [ ] Set secure session cookies

---

## 💰 Cost Estimate

### Free Tier Limits

**Vercel (Frontend)**
- ✅ Unlimited deployments
- ✅ 100GB bandwidth/month
- ✅ Custom domain
- ✅ Automatic HTTPS
- **Cost**: FREE

**Railway (Backend)**
- ✅ $5 free credit/month
- ✅ PostgreSQL included
- ✅ Automatic deployments
- ✅ Custom domain
- **Cost**: FREE (with $5 credit)

**Render (Alternative)**
- ✅ Free tier available
- ✅ PostgreSQL included
- ⚠️ Spins down after inactivity
- **Cost**: FREE

---

## 🚀 Quick Deploy Commands

### Deploy Frontend to Vercel (CLI)
```bash
cd frontend
npm install -g vercel
vercel login
vercel --prod
```

### Deploy Backend to Railway (CLI)
```bash
cd backend
npm install -g @railway/cli
railway login
railway init
railway up
```

---

## 📊 Monitoring & Logs

### Vercel
- Dashboard → Your Project → Deployments
- View build logs and runtime logs
- Monitor performance

### Railway
- Dashboard → Your Project → Deployments
- View logs in real-time
- Monitor resource usage

---

## 🐛 Troubleshooting

### Frontend Issues

**Issue**: API calls failing
**Solution**: 
- Check VITE_API_BASE_URL is set correctly
- Verify backend is running
- Check CORS configuration

**Issue**: Build fails
**Solution**:
- Run `npm run build` locally
- Check for TypeScript errors
- Verify all dependencies installed

### Backend Issues

**Issue**: Database connection fails
**Solution**:
- Verify DATABASE_URL is correct
- Check PostgreSQL is running
- Verify credentials

**Issue**: CORS errors
**Solution**:
- Add frontend URL to CORS_ORIGINS
- Include both main and preview URLs
- Redeploy backend

---

## 📞 Support Resources

- **Vercel Docs**: https://vercel.com/docs
- **Railway Docs**: https://docs.railway.app
- **Render Docs**: https://render.com/docs
- **Spring Boot Deployment**: https://spring.io/guides/gs/spring-boot/

---

## ✅ Post-Deployment Checklist

After deployment, test:
- [ ] Homepage loads
- [ ] User registration works
- [ ] User login works
- [ ] Browse restaurants
- [ ] Add items to cart
- [ ] Place order
- [ ] View orders
- [ ] Admin features work
- [ ] All images load
- [ ] Mobile responsive
- [ ] HTTPS enabled

---

## 🎉 You're Live!

Once deployed, share your app:
- **Frontend**: `https://your-app.vercel.app`
- **Backend**: `https://your-app.railway.app`

**Total deployment time**: ~10 minutes
**Total cost**: FREE (with free tiers)

---

## 📝 Environment Variables Summary

### Backend (Railway/Render)
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=<auto-configured>
DATABASE_USERNAME=<auto-configured>
DATABASE_PASSWORD=<auto-configured>
JWT_SECRET=<your-secret-key>
CORS_ORIGINS=https://your-frontend.vercel.app
PORT=8080
```

### Frontend (Vercel)
```
VITE_API_BASE_URL=https://your-backend.railway.app
```

---

**Ready to deploy? Follow the steps above and your app will be live in minutes! 🚀**

