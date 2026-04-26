# ⚡ Quick Deploy Guide (5 Minutes)

## 🎯 Fastest Way to Deploy

### Option 1: One-Click Deploy (Recommended)

#### Deploy Backend to Railway
1. **Go to**: https://railway.app
2. **Click**: "Start a New Project"
3. **Select**: "Deploy from GitHub repo"
4. **Choose**: Your repository
5. **Add Database**: Click "New" → "PostgreSQL"
6. **Set Variables**:
   ```
   SPRING_PROFILES_ACTIVE=prod
   JWT_SECRET=change-this-to-random-string
   ```
7. **Copy URL**: `https://your-app.railway.app`

#### Deploy Frontend to Vercel
1. **Go to**: https://vercel.com
2. **Click**: "New Project"
3. **Import**: Your repository
4. **Configure**:
   - Root Directory: `frontend`
   - Framework: Vite
5. **Add Variable**:
   ```
   VITE_API_BASE_URL=https://your-app.railway.app
   ```
6. **Deploy**: Click "Deploy"

**Done! Your app is live! 🎉**

---

## 🚀 Using CLI (For Advanced Users)

### Deploy Frontend (Vercel CLI)
```bash
cd "Online Food Delivery/frontend"
npm install -g vercel
vercel login
vercel --prod
```

### Deploy Backend (Railway CLI)
```bash
cd "Online Food Delivery/backend"
npm install -g @railway/cli
railway login
railway init
railway up
```

---

## 📝 After Deployment

### Update CORS
1. Go to Railway dashboard
2. Add environment variable:
   ```
   CORS_ORIGINS=https://your-app.vercel.app
   ```
3. Redeploy

### Test Your App
1. Visit: `https://your-app.vercel.app`
2. Register a new account
3. Browse restaurants
4. Place an order
5. ✅ Everything works!

---

## 🔗 Important URLs

After deployment, you'll have:
- **Frontend**: `https://your-app.vercel.app`
- **Backend**: `https://your-app.railway.app`
- **Database**: Auto-configured by Railway

---

## 💡 Pro Tips

1. **Custom Domain**: Add in Vercel/Railway settings
2. **Environment Variables**: Never commit secrets
3. **Monitoring**: Check logs in dashboards
4. **Auto-Deploy**: Push to GitHub = auto-deploy

---

## ⚠️ Common Issues

### Issue: API calls fail
**Fix**: Update `VITE_API_BASE_URL` in Vercel with correct backend URL

### Issue: CORS error
**Fix**: Add frontend URL to `CORS_ORIGINS` in Railway

### Issue: Database connection fails
**Fix**: Railway auto-configures this, just wait 2-3 minutes

---

## 🎉 Success!

Your food delivery app is now:
- ✅ Publicly accessible
- ✅ Running on free tier
- ✅ Auto-deploying on push
- ✅ HTTPS enabled
- ✅ Production-ready

**Share your app with the world! 🌍**

---

## 📞 Need Help?

- **Vercel Support**: https://vercel.com/support
- **Railway Support**: https://railway.app/help
- **Documentation**: See DEPLOYMENT_GUIDE.md

---

**Total Time**: 5-10 minutes
**Total Cost**: FREE
**Difficulty**: Easy ⭐

Let's deploy! 🚀
