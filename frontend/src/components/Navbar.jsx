import { Link, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../store/slices/authSlice';
import { selectCartItemCount } from '../store/slices/cartSlice';
import { USER_ROLES } from '../utils/constants';

const Navbar = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { isAuthenticated, user } = useSelector((state) => state.auth);
  const cartItemCount = useSelector(selectCartItemCount);

  const handleLogout = () => {
    dispatch(logout());
    navigate('/');
  };

  return (
    <nav className="glass-panel sticky top-0 z-50 backdrop-blur-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-20">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2 hover-lift">
            <div className="text-3xl font-bold gradient-text">
              🍔 FoodExpress
            </div>
          </Link>

          {/* Navigation Links */}
          <div className="hidden md:flex items-center space-x-8">
            <Link to="/" className="text-white hover:text-purple-200 font-semibold transition-all duration-300 hover:scale-110">
              Home
            </Link>
            {isAuthenticated && user?.role === USER_ROLES.CUSTOMER && (
              <Link to="/orders" className="text-white hover:text-purple-200 font-semibold transition-all duration-300 hover:scale-110">
                My Orders
              </Link>
            )}
            {isAuthenticated && user?.role === USER_ROLES.RESTAURANT_ADMIN && (
              <>
                <Link to="/restaurant/dashboard" className="text-white hover:text-purple-200 font-semibold transition-all duration-300 hover:scale-110">
                  Dashboard
                </Link>
                <Link to="/restaurant/orders" className="text-white hover:text-purple-200 font-semibold transition-all duration-300 hover:scale-110">
                  Orders
                </Link>
                <Link to="/restaurant/menu" className="text-white hover:text-purple-200 font-semibold transition-all duration-300 hover:scale-110">
                  Menu
                </Link>
              </>
            )}
            {isAuthenticated && user?.role === USER_ROLES.PLATFORM_ADMIN && (
              <Link to="/admin/dashboard" className="text-white hover:text-purple-200 font-semibold transition-all duration-300 hover:scale-110">
                Admin Dashboard
              </Link>
            )}
          </div>

          {/* Right side buttons */}
          <div className="flex items-center space-x-4">
            {/* Cart */}
            {isAuthenticated && user?.role === USER_ROLES.CUSTOMER && (
              <Link to="/cart" className="relative hover-lift">
                <button className="p-3 text-white hover:text-purple-200 transition-all duration-300 bg-white/10 rounded-xl backdrop-blur-md border border-white/20">
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                  {cartItemCount > 0 && (
                    <span className="absolute -top-2 -right-2 bg-gradient-to-r from-pink-500 to-purple-500 text-white text-xs rounded-full w-6 h-6 flex items-center justify-center font-bold animate-pulse glow">
                      {cartItemCount}
                    </span>
                  )}
                </button>
              </Link>
            )}

            {/* Auth buttons */}
            {isAuthenticated ? (
              <div className="flex items-center space-x-4">
                <span className="text-white font-semibold hidden md:block bg-white/10 px-4 py-2 rounded-xl backdrop-blur-md border border-white/20">
                  {user?.name}
                </span>
                <button
                  onClick={handleLogout}
                  className="btn-outline"
                >
                  Logout
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-3">
                <Link to="/login">
                  <button className="btn-outline">Login</button>
                </Link>
                <Link to="/register">
                  <button className="btn-primary">Sign Up</button>
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
