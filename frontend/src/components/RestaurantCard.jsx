import { Link } from 'react-router-dom';
import { formatCurrency, getImageUrl } from '../utils/helpers';
import { DEFAULT_RESTAURANT_IMAGE } from '../utils/constants';

const RestaurantCard = ({ restaurant }) => {
  return (
    <Link to={`/restaurant/${restaurant.id}`}>
      <div className="card overflow-hidden cursor-pointer hover-lift group">
        {/* Image with overlay effect */}
        <div className="relative h-56 overflow-hidden">
          <img
            src={getImageUrl(restaurant.imageUrl, DEFAULT_RESTAURANT_IMAGE)}
            alt={restaurant.name}
            className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
            onError={(e) => {
              e.target.src = DEFAULT_RESTAURANT_IMAGE;
            }}
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          {!restaurant.isOpen && (
            <div className="absolute inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center">
              <span className="text-white font-bold text-2xl px-6 py-3 bg-red-500/80 rounded-full">CLOSED</span>
            </div>
          )}
          {/* Floating badge */}
          <div className="absolute top-4 right-4">
            <span className="badge bg-gradient-to-r from-yellow-400 to-orange-500 text-white shadow-lg">
              ⭐ {restaurant.rating || '4.0'}
            </span>
          </div>
        </div>

        {/* Content with glassmorphism */}
        <div className="p-6 bg-gradient-to-br from-white/95 to-white/90">
          <h3 className="text-2xl font-bold text-gray-900 mb-2 truncate group-hover:text-purple-600 transition-colors">
            {restaurant.name}
          </h3>
          
          <p className="text-gray-600 text-sm mb-4 line-clamp-2 leading-relaxed">
            {restaurant.description}
          </p>

          <div className="flex items-center space-x-2 mb-4">
            <span className="badge bg-gradient-to-r from-green-400 to-emerald-500 text-white shadow-md">
              ⭐ {restaurant.rating || '4.0'}
            </span>
            <span className="text-gray-500 text-sm font-medium">
              ({restaurant.totalReviews || 0} reviews)
            </span>
          </div>

          <div className="flex items-center justify-between text-sm text-gray-600 mb-4">
            <span className="flex items-center bg-purple-100 px-3 py-1 rounded-full">
              <svg className="w-4 h-4 mr-1 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span className="font-semibold text-purple-700">{restaurant.deliveryTimeMinutes || 30} mins</span>
            </span>
            <span className="bg-blue-100 px-3 py-1 rounded-full font-semibold text-blue-700">
              {formatCurrency(restaurant.minimumOrderAmount || 0)} min
            </span>
          </div>

          <div className="flex items-center justify-between">
            <span className="badge bg-gradient-to-r from-orange-400 to-pink-500 text-white shadow-md">
              {restaurant.cuisineType}
            </span>
            <span className="text-sm text-gray-600 flex items-center">
              <span className="mr-1">📍</span>
              <span className="font-medium">{restaurant.city}</span>
            </span>
          </div>
        </div>
      </div>
    </Link>
  );
};

export default RestaurantCard;
