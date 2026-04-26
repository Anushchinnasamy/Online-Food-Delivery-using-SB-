import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import restaurantService from '../services/restaurantService';
import menuService from '../services/menuService';
import MenuItemCard from '../components/MenuItemCard';
import LoadingSpinner from '../components/LoadingSpinner';
import { formatCurrency, formatTime, getImageUrl, getErrorMessage } from '../utils/helpers';
import { DEFAULT_RESTAURANT_IMAGE } from '../utils/constants';

const RestaurantDetails = () => {
  const { id } = useParams();
  const [restaurant, setRestaurant] = useState(null);
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState('All');

  useEffect(() => {
    fetchRestaurantDetails();
  }, [id]);

  const fetchRestaurantDetails = async () => {
    try {
      setLoading(true);
      setError(null);
      const [restaurantData, menuData] = await Promise.all([
        restaurantService.getRestaurantById(id),
        menuService.getMenuItemsByRestaurant(id),
      ]);
      setRestaurant(restaurantData);
      setMenuItems(menuData);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 mb-4">{error}</p>
          <button onClick={fetchRestaurantDetails} className="btn-primary">
            Try Again
          </button>
        </div>
      </div>
    );
  }

  if (!restaurant) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-600">Restaurant not found</p>
      </div>
    );
  }

  // Get unique categories
  const categories = ['All', ...new Set(menuItems.map(item => item.category))].filter(Boolean);

  // Filter menu items by category
  const filteredMenuItems = selectedCategory === 'All'
    ? menuItems
    : menuItems.filter(item => item.category === selectedCategory);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Restaurant Header */}
      <div className="bg-white shadow-md">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex flex-col md:flex-row gap-6">
            {/* Restaurant Image */}
            <div className="w-full md:w-64 h-64 rounded-xl overflow-hidden flex-shrink-0">
              <img
                src={getImageUrl(restaurant.imageUrl, DEFAULT_RESTAURANT_IMAGE)}
                alt={restaurant.name}
                className="w-full h-full object-cover"
                onError={(e) => {
                  e.target.src = DEFAULT_RESTAURANT_IMAGE;
                }}
              />
            </div>

            {/* Restaurant Info */}
            <div className="flex-1">
              <h1 className="text-3xl font-bold text-gray-900 mb-2">
                {restaurant.name}
              </h1>
              <p className="text-gray-600 mb-4">{restaurant.description}</p>

              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
                <div>
                  <p className="text-sm text-gray-500">Rating</p>
                  <p className="text-lg font-semibold">⭐ {restaurant.rating || '4.0'}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Delivery Time</p>
                  <p className="text-lg font-semibold">{restaurant.deliveryTimeMinutes || 30} mins</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Min Order</p>
                  <p className="text-lg font-semibold">{formatCurrency(restaurant.minimumOrderAmount || 0)}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Delivery Fee</p>
                  <p className="text-lg font-semibold">{formatCurrency(restaurant.deliveryFee || 0)}</p>
                </div>
              </div>

              <div className="flex flex-wrap gap-2 mb-4">
                <span className="badge bg-orange-100 text-orange-800">
                  {restaurant.cuisineType}
                </span>
                <span className={`badge ${restaurant.isOpen ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                  {restaurant.isOpen ? 'Open' : 'Closed'}
                </span>
                {restaurant.openingTime && restaurant.closingTime && (
                  <span className="badge bg-blue-100 text-blue-800">
                    {formatTime(restaurant.openingTime)} - {formatTime(restaurant.closingTime)}
                  </span>
                )}
              </div>

              <div className="flex items-center text-gray-600">
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                {restaurant.address}, {restaurant.city} - {restaurant.pincode}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Menu Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Menu</h2>

        {/* Category Filter */}
        <div className="flex gap-2 mb-6 overflow-x-auto pb-2">
          {categories.map((category) => (
            <button
              key={category}
              onClick={() => setSelectedCategory(category)}
              className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap transition ${
                selectedCategory === category
                  ? 'bg-primary-600 text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-100'
              }`}
            >
              {category}
            </button>
          ))}
        </div>

        {/* Menu Items */}
        {filteredMenuItems.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-600">No menu items available</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-4">
            {filteredMenuItems.map((menuItem) => (
              <MenuItemCard
                key={menuItem.id}
                menuItem={menuItem}
                restaurant={restaurant}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default RestaurantDetails;
