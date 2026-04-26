import { useState, useEffect } from 'react';
import { useDispatch } from 'react-redux';
import restaurantService from '../services/restaurantService';
import RestaurantCard from '../components/RestaurantCard';
import LoadingSpinner from '../components/LoadingSpinner';
import { setRestaurants, setLoading } from '../store/slices/restaurantSlice';
import { getErrorMessage } from '../utils/helpers';

const Home = () => {
  const dispatch = useDispatch();
  const [restaurants, setRestaurantsState] = useState([]);
  const [loading, setLoadingState] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterCity, setFilterCity] = useState('');
  const [filterCuisine, setFilterCuisine] = useState('');

  useEffect(() => {
    fetchRestaurants();
  }, []);

  const fetchRestaurants = async () => {
    try {
      setLoadingState(true);
      setError(null);
      const data = await restaurantService.getAllRestaurants();
      setRestaurantsState(data);
      dispatch(setRestaurants(data));
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoadingState(false);
    }
  };

  const handleSearch = async () => {
    try {
      setLoadingState(true);
      setError(null);
      const params = {};
      if (searchQuery) params.name = searchQuery;
      if (filterCity) params.city = filterCity;
      if (filterCuisine) params.cuisine = filterCuisine;

      const data = await restaurantService.searchRestaurants(params);
      setRestaurantsState(data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoadingState(false);
    }
  };

  const handleClearFilters = () => {
    setSearchQuery('');
    setFilterCity('');
    setFilterCuisine('');
    fetchRestaurants();
  };

  // Get unique cities and cuisines
  const cities = [...new Set(restaurants.map(r => r.city))].filter(Boolean);
  const cuisines = [...new Set(restaurants.map(r => r.cuisineType))].filter(Boolean);

  return (
    <div className="min-h-screen">
      {/* Hero Section with 3D effect */}
      <div className="relative overflow-hidden py-20">
        <div className="absolute inset-0 bg-gradient-to-br from-purple-600/20 to-pink-600/20 backdrop-blur-3xl"></div>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="text-center fade-in">
            <h1 className="text-5xl md:text-7xl font-bold mb-6 gradient-text floating">
              Order Food Online
            </h1>
            <p className="text-2xl mb-12 text-white font-light slide-in-left">
              Delicious food delivered to your doorstep ✨
            </p>

            {/* Search Bar with glassmorphism */}
            <div className="max-w-4xl mx-auto glass-panel p-6 scale-in">
              <div className="flex flex-col md:flex-row gap-4">
                <input
                  type="text"
                  placeholder="Search restaurants..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="input-field flex-1"
                />
                <select
                  value={filterCity}
                  onChange={(e) => setFilterCity(e.target.value)}
                  className="input-field md:w-48"
                >
                  <option value="">All Cities</option>
                  {cities.map(city => (
                    <option key={city} value={city}>{city}</option>
                  ))}
                </select>
                <select
                  value={filterCuisine}
                  onChange={(e) => setFilterCuisine(e.target.value)}
                  className="input-field md:w-48"
                >
                  <option value="">All Cuisines</option>
                  {cuisines.map(cuisine => (
                    <option key={cuisine} value={cuisine}>{cuisine}</option>
                  ))}
                </select>
                <button
                  onClick={handleSearch}
                  className="btn-primary px-8"
                >
                  🔍 Search
                </button>
              </div>
              {(searchQuery || filterCity || filterCuisine) && (
                <button
                  onClick={handleClearFilters}
                  className="mt-4 text-white hover:text-purple-200 text-sm font-semibold transition-all duration-300"
                >
                  ✕ Clear Filters
                </button>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Restaurants Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <h2 className="text-4xl font-bold text-white mb-12 text-center gradient-text">
          🍽️ Restaurants Near You
        </h2>

        {loading ? (
          <LoadingSpinner size="lg" />
        ) : error ? (
          <div className="text-center py-12 glass-panel p-8">
            <p className="text-red-300 mb-6 text-lg">{error}</p>
            <button onClick={fetchRestaurants} className="btn-primary">
              🔄 Try Again
            </button>
          </div>
        ) : restaurants.length === 0 ? (
          <div className="text-center py-12 glass-panel p-8">
            <p className="text-white text-xl">No restaurants found</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {restaurants.map((restaurant, index) => (
              <div key={restaurant.id} className="fade-in" style={{ animationDelay: `${index * 0.1}s` }}>
                <RestaurantCard restaurant={restaurant} />
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Home;
