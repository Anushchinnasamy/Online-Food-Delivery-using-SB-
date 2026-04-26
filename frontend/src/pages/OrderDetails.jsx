import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import orderService from '../services/orderService';
import LoadingSpinner from '../components/LoadingSpinner';
import { formatCurrency, formatDate, getErrorMessage } from '../utils/helpers';
import { ORDER_STATUS_DISPLAY, ORDER_STATUS_COLORS, PAYMENT_METHOD_DISPLAY, USER_ROLES } from '../utils/constants';

const OrderDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cancelling, setCancelling] = useState(false);

  useEffect(() => {
    fetchOrderDetails();
  }, [id]);

  const fetchOrderDetails = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await orderService.getOrderById(id);
      setOrder(data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const handleCancelOrder = async () => {
    if (!window.confirm('Are you sure you want to cancel this order?')) {
      return;
    }

    try {
      setCancelling(true);
      const updatedOrder = await orderService.cancelOrder(id, 'Customer requested cancellation');
      setOrder(updatedOrder);
    } catch (err) {
      alert(getErrorMessage(err));
    } finally {
      setCancelling(false);
    }
  };

  const canCancelOrder = () => {
    return order && (order.status === 'PLACED' || order.status === 'ACCEPTED');
  };

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 mb-4">{error}</p>
          <button onClick={() => navigate('/orders')} className="btn-primary">
            Back to Orders
          </button>
        </div>
      </div>
    );
  }

  if (!order) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p className="text-gray-600">Order not found</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-6">
          <button
            onClick={() => navigate('/orders')}
            className="text-primary-600 hover:text-primary-700 font-medium mb-4 flex items-center"
          >
            ← Back to Orders
          </button>
          <div className="flex justify-between items-start">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">
                Order #{order.orderNumber}
              </h1>
              <p className="text-gray-600">{formatDate(order.createdAt)}</p>
            </div>
            <span className={`badge ${ORDER_STATUS_COLORS[order.status]} text-lg`}>
              {ORDER_STATUS_DISPLAY[order.status]}
            </span>
          </div>
        </div>

        {/* Order Status Timeline */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Order Status</h2>
          <div className="space-y-3">
            {['PLACED', 'ACCEPTED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED'].map((status, index) => {
              const isCompleted = ['PLACED', 'ACCEPTED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED']
                .indexOf(order.status) >= index;
              const isCurrent = order.status === status;
              
              return (
                <div key={status} className="flex items-center">
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                    isCompleted ? 'bg-green-500' : 'bg-gray-300'
                  }`}>
                    {isCompleted && (
                      <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                      </svg>
                    )}
                  </div>
                  <div className="ml-4">
                    <p className={`font-medium ${isCurrent ? 'text-primary-600' : 'text-gray-900'}`}>
                      {ORDER_STATUS_DISPLAY[status]}
                    </p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Restaurant Info */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Restaurant</h2>
          <h3 className="font-bold text-gray-900">{order.restaurantName}</h3>
        </div>

        {/* Order Items */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Order Items</h2>
          <div className="space-y-3">
            {order.orderItems?.map((item, index) => (
              <div key={index} className="flex justify-between items-center py-2 border-b last:border-b-0">
                <div className="flex-1">
                  <p className="font-medium text-gray-900">{item.menuItemName}</p>
                  <p className="text-sm text-gray-600">Quantity: {item.quantity}</p>
                  {item.specialInstructions && (
                    <p className="text-sm text-gray-500 italic">Note: {item.specialInstructions}</p>
                  )}
                </div>
                <p className="font-semibold text-gray-900">
                  {formatCurrency(item.price * item.quantity)}
                </p>
              </div>
            ))}
          </div>
        </div>

        {/* Delivery Details */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Delivery Details</h2>
          <div className="space-y-2">
            <div>
              <p className="text-sm text-gray-600">Address</p>
              <p className="font-medium text-gray-900">{order.deliveryAddress}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">Phone</p>
              <p className="font-medium text-gray-900">{order.customerPhone}</p>
            </div>
            {order.specialInstructions && (
              <div>
                <p className="text-sm text-gray-600">Special Instructions</p>
                <p className="font-medium text-gray-900">{order.specialInstructions}</p>
              </div>
            )}
          </div>
        </div>

        {/* Payment Summary */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Payment Summary</h2>
          <div className="space-y-2">
            <div className="flex justify-between text-gray-700">
              <span>Subtotal</span>
              <span>{formatCurrency(order.subtotal)}</span>
            </div>
            <div className="flex justify-between text-gray-700">
              <span>Delivery Fee</span>
              <span>{formatCurrency(order.deliveryFee)}</span>
            </div>
            <div className="flex justify-between text-gray-700">
              <span>Tax</span>
              <span>{formatCurrency(order.taxAmount)}</span>
            </div>
            {order.discountAmount > 0 && (
              <div className="flex justify-between text-green-600">
                <span>Discount</span>
                <span>-{formatCurrency(order.discountAmount)}</span>
              </div>
            )}
            <div className="border-t pt-2">
              <div className="flex justify-between text-lg font-bold text-gray-900">
                <span>Total</span>
                <span>{formatCurrency(order.totalAmount)}</span>
              </div>
            </div>
            <div className="mt-4">
              <p className="text-sm text-gray-600">Payment Method</p>
              <p className="font-medium text-gray-900">
                {PAYMENT_METHOD_DISPLAY[order.payment?.method] || 'Cash on Delivery'}
              </p>
            </div>
          </div>
        </div>

        {/* Cancel Order Button */}
        {user?.role === USER_ROLES.CUSTOMER && canCancelOrder() && (
          <button
            onClick={handleCancelOrder}
            disabled={cancelling}
            className="w-full bg-red-600 hover:bg-red-700 text-white font-medium py-3 px-4 rounded-lg transition flex items-center justify-center"
          >
            {cancelling ? <LoadingSpinner size="sm" /> : 'Cancel Order'}
          </button>
        )}
      </div>
    </div>
  );
};

export default OrderDetails;
