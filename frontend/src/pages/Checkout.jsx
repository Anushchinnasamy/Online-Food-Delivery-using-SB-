import { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { clearCart, selectCartTotal } from '../store/slices/cartSlice';
import orderService from '../services/orderService';
import LoadingSpinner from '../components/LoadingSpinner';
import { formatCurrency, getErrorMessage } from '../utils/helpers';
import { PAYMENT_METHODS, PAYMENT_METHOD_DISPLAY } from '../utils/constants';

const Checkout = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { items, restaurant } = useSelector((state) => state.cart);
  const { user } = useSelector((state) => state.auth);
  const subtotal = useSelector(selectCartTotal);

  const [formData, setFormData] = useState({
    deliveryAddress: user?.address || '',
    customerPhone: user?.phone || '',
    specialInstructions: '',
    paymentMethod: 'CASH_ON_DELIVERY',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const deliveryFee = restaurant?.deliveryFee || 0;
  const taxAmount = subtotal * 0.05;
  const total = subtotal + parseFloat(deliveryFee) + taxAmount;

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.deliveryAddress || !formData.customerPhone) {
      setError('Please fill in all required fields');
      return;
    }

    if (items.length === 0) {
      setError('Your cart is empty');
      return;
    }

    try {
      setLoading(true);
      setError('');

      const orderData = {
        restaurantId: restaurant.id,
        items: items.map(item => ({
          menuItemId: item.id,
          quantity: item.quantity,
          specialInstructions: '',
        })),
        deliveryAddress: formData.deliveryAddress,
        customerPhone: formData.customerPhone,
        specialInstructions: formData.specialInstructions,
        paymentMethod: formData.paymentMethod,
      };

      const order = await orderService.createOrder(orderData);
      dispatch(clearCart());
      navigate(`/orders/${order.id}`);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  if (items.length === 0) {
    navigate('/cart');
    return null;
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">Checkout</h1>

        {error && (
          <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Delivery Details */}
            <div className="lg:col-span-2 space-y-6">
              {/* Restaurant Info */}
              <div className="bg-white rounded-lg shadow-md p-6">
                <h2 className="text-xl font-bold text-gray-900 mb-4">Delivering From</h2>
                <div>
                  <h3 className="font-bold text-gray-900">{restaurant.name}</h3>
                  <p className="text-gray-600 text-sm">{restaurant.address}</p>
                </div>
              </div>

              {/* Delivery Address */}
              <div className="bg-white rounded-lg shadow-md p-6">
                <h2 className="text-xl font-bold text-gray-900 mb-4">Delivery Details</h2>
                
                <div className="space-y-4">
                  <div>
                    <label htmlFor="deliveryAddress" className="block text-sm font-medium text-gray-700 mb-2">
                      Delivery Address *
                    </label>
                    <textarea
                      id="deliveryAddress"
                      name="deliveryAddress"
                      required
                      rows={3}
                      value={formData.deliveryAddress}
                      onChange={handleChange}
                      className="input-field"
                      placeholder="Enter complete delivery address"
                    />
                  </div>

                  <div>
                    <label htmlFor="customerPhone" className="block text-sm font-medium text-gray-700 mb-2">
                      Phone Number *
                    </label>
                    <input
                      id="customerPhone"
                      name="customerPhone"
                      type="tel"
                      required
                      value={formData.customerPhone}
                      onChange={handleChange}
                      className="input-field"
                      placeholder="10-digit phone number"
                    />
                  </div>

                  <div>
                    <label htmlFor="specialInstructions" className="block text-sm font-medium text-gray-700 mb-2">
                      Special Instructions (Optional)
                    </label>
                    <textarea
                      id="specialInstructions"
                      name="specialInstructions"
                      rows={2}
                      value={formData.specialInstructions}
                      onChange={handleChange}
                      className="input-field"
                      placeholder="Any special requests?"
                    />
                  </div>
                </div>
              </div>

              {/* Payment Method */}
              <div className="bg-white rounded-lg shadow-md p-6">
                <h2 className="text-xl font-bold text-gray-900 mb-4">Payment Method</h2>
                
                <div className="space-y-3">
                  {Object.entries(PAYMENT_METHODS).map(([key, value]) => (
                    <label
                      key={key}
                      className={`flex items-center p-4 border-2 rounded-lg cursor-pointer transition ${
                        formData.paymentMethod === value
                          ? 'border-primary-600 bg-primary-50'
                          : 'border-gray-200 hover:border-gray-300'
                      }`}
                    >
                      <input
                        type="radio"
                        name="paymentMethod"
                        value={value}
                        checked={formData.paymentMethod === value}
                        onChange={handleChange}
                        className="mr-3"
                      />
                      <span className="font-medium text-gray-900">
                        {PAYMENT_METHOD_DISPLAY[key]}
                      </span>
                    </label>
                  ))}
                </div>
              </div>
            </div>

            {/* Order Summary */}
            <div className="lg:col-span-1">
              <div className="bg-white rounded-lg shadow-md p-6 sticky top-20">
                <h3 className="text-xl font-bold text-gray-900 mb-4">Order Summary</h3>

                <div className="space-y-2 mb-4">
                  {items.map((item) => (
                    <div key={item.id} className="flex justify-between text-sm">
                      <span className="text-gray-700">
                        {item.name} x {item.quantity}
                      </span>
                      <span className="text-gray-900 font-medium">
                        {formatCurrency(parseFloat(item.price) * item.quantity)}
                      </span>
                    </div>
                  ))}
                </div>

                <div className="border-t pt-3 space-y-2 mb-4">
                  <div className="flex justify-between text-gray-700">
                    <span>Subtotal</span>
                    <span>{formatCurrency(subtotal)}</span>
                  </div>
                  <div className="flex justify-between text-gray-700">
                    <span>Delivery Fee</span>
                    <span>{formatCurrency(deliveryFee)}</span>
                  </div>
                  <div className="flex justify-between text-gray-700">
                    <span>Tax (5%)</span>
                    <span>{formatCurrency(taxAmount)}</span>
                  </div>
                </div>

                <div className="border-t pt-3 mb-6">
                  <div className="flex justify-between text-lg font-bold text-gray-900">
                    <span>Total</span>
                    <span>{formatCurrency(total)}</span>
                  </div>
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full btn-primary flex items-center justify-center"
                >
                  {loading ? <LoadingSpinner size="sm" /> : 'Place Order'}
                </button>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Checkout;
