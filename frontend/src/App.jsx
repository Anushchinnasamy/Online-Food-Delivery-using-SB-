import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Provider } from 'react-redux';
import { Toaster } from 'react-hot-toast';
import store from './store/store';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import { USER_ROLES } from './utils/constants';

// Pages
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import RestaurantDetails from './pages/RestaurantDetails';
import Cart from './pages/Cart';
import Checkout from './pages/Checkout';
import Orders from './pages/Orders';
import OrderDetails from './pages/OrderDetails';

// Restaurant Admin Pages
import RestaurantDashboard from './pages/restaurant/RestaurantDashboard';

function App() {
  return (
    <Provider store={store}>
      <Router>
        <div className="min-h-screen bg-gray-50">
          <Navbar />
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/restaurant/:id" element={<RestaurantDetails />} />

            {/* Customer Routes */}
            <Route
              path="/cart"
              element={
                <ProtectedRoute allowedRoles={[USER_ROLES.CUSTOMER]}>
                  <Cart />
                </ProtectedRoute>
              }
            />
            <Route
              path="/checkout"
              element={
                <ProtectedRoute allowedRoles={[USER_ROLES.CUSTOMER]}>
                  <Checkout />
                </ProtectedRoute>
              }
            />
            <Route
              path="/orders"
              element={
                <ProtectedRoute allowedRoles={[USER_ROLES.CUSTOMER]}>
                  <Orders />
                </ProtectedRoute>
              }
            />
            <Route
              path="/orders/:id"
              element={
                <ProtectedRoute allowedRoles={[USER_ROLES.CUSTOMER, USER_ROLES.RESTAURANT_ADMIN, USER_ROLES.PLATFORM_ADMIN]}>
                  <OrderDetails />
                </ProtectedRoute>
              }
            />

            {/* Restaurant Admin Routes */}
            <Route
              path="/restaurant/dashboard"
              element={
                <ProtectedRoute allowedRoles={[USER_ROLES.RESTAURANT_ADMIN]}>
                  <RestaurantDashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="/restaurant/orders"
              element={
                <ProtectedRoute allowedRoles={[USER_ROLES.RESTAURANT_ADMIN]}>
                  <RestaurantDashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="/restaurant/menu"
              element={
                <ProtectedRoute allowedRoles={[USER_ROLES.RESTAURANT_ADMIN]}>
                  <RestaurantDashboard />
                </ProtectedRoute>
              }
            />

            {/* Platform Admin Routes */}
            <Route
              path="/admin/dashboard"
              element={
                <ProtectedRoute allowedRoles={[USER_ROLES.PLATFORM_ADMIN]}>
                  <div className="min-h-screen flex items-center justify-center">
                    <h1 className="text-3xl font-bold">Platform Admin Dashboard</h1>
                  </div>
                </ProtectedRoute>
              }
            />

            {/* 404 */}
            <Route
              path="*"
              element={
                <div className="min-h-screen flex items-center justify-center">
                  <div className="text-center">
                    <h1 className="text-6xl font-bold text-gray-900 mb-4">404</h1>
                    <p className="text-xl text-gray-600">Page not found</p>
                  </div>
                </div>
              }
            />
          </Routes>
          <Toaster position="top-right" />
        </div>
      </Router>
    </Provider>
  );
}

export default App;
