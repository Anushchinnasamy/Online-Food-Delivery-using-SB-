import { useDispatch, useSelector } from 'react-redux';
import { addToCart } from '../store/slices/cartSlice';
import { formatCurrency, getImageUrl } from '../utils/helpers';
import { DEFAULT_FOOD_IMAGES } from '../utils/constants';
import { toast } from 'react-hot-toast';

const MenuItemCard = ({ menuItem, restaurant }) => {
  const dispatch = useDispatch();
  const { isAuthenticated, user } = useSelector((state) => state.auth);

  const handleAddToCart = () => {
    if (!isAuthenticated) {
      toast.error('Please login to add items to cart');
      return;
    }

    if (user?.role !== 'CUSTOMER') {
      toast.error('Only customers can add items to cart');
      return;
    }

    dispatch(addToCart({ menuItem, restaurant }));
    toast.success('Added to cart!');
  };

  const randomImage = DEFAULT_FOOD_IMAGES[Math.floor(Math.random() * DEFAULT_FOOD_IMAGES.length)];

  return (
    <div className="card overflow-hidden">
      <div className="flex">
        {/* Image */}
        <div className="w-32 h-32 flex-shrink-0">
          <img
            src={getImageUrl(menuItem.imageUrl, randomImage)}
            alt={menuItem.name}
            className="w-full h-full object-cover"
            onError={(e) => {
              e.target.src = randomImage;
            }}
          />
        </div>

        {/* Content */}
        <div className="flex-1 p-4">
          <div className="flex justify-between items-start mb-2">
            <div className="flex-1">
              <h4 className="text-lg font-bold text-gray-900 mb-1">
                {menuItem.name}
              </h4>
              <p className="text-gray-600 text-sm line-clamp-2">
                {menuItem.description}
              </p>
            </div>
          </div>

          <div className="flex items-center space-x-2 mb-2">
            {menuItem.isVegetarian && (
              <span className="badge bg-green-100 text-green-800 text-xs">
                🌱 Veg
              </span>
            )}
            {menuItem.isVegan && (
              <span className="badge bg-green-100 text-green-800 text-xs">
                🥬 Vegan
              </span>
            )}
            {menuItem.isSpicy && (
              <span className="badge bg-red-100 text-red-800 text-xs">
                🌶️ Spicy
              </span>
            )}
          </div>

          <div className="flex items-center justify-between">
            <span className="text-xl font-bold text-primary-600">
              {formatCurrency(menuItem.price)}
            </span>
            
            {menuItem.isAvailable ? (
              <button
                onClick={handleAddToCart}
                className="btn-primary text-sm py-1 px-4"
              >
                Add to Cart
              </button>
            ) : (
              <span className="text-red-600 font-medium text-sm">
                Not Available
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default MenuItemCard;
