import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import orderService from '../services/orderService';
import LoadingSpinner from '../components/LoadingSpinner';
import { formatCurrency, formatDate, getErrorMessage } from '../utils/helpers';
import { ORDER_STATUS_DISPLAY, ORDER_STATUS_COLORS } from '../utils/constants';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await orderService.getMyOrders();
      // Sort by created date descending
      const sortedOrders = data.sort((a, b) => 
        new Date(b.createdAt) - new Date(a.createdAt)
      );
      setOrders(sortedOrders);
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
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 mb-4">{error}</p>
          <button onClick={fetchOrders} className="btn-primary">
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">My Orders</h1>

        {orders.length === 0 ? (
          <div className="text-center py-12">
            <div className="text-6xl mb-4">📦</div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">No orders yet</h2>
            <p className="text-gray-600 mb-6">Start ordering delicious food!</p>
            <Link to="/">
              <button className="btn-primary">Browse Restaurants</button>
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {orders.map((order) => (
              <Link key={order.id} to={`/orders/${order.id}`}>
                <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition cursor-pointer">
                  <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-4">
                    <div>
                      <h3 className="text-lg font-bold text-gray-900 mb-1">
                        {order.restaurantName}
                      </h3>
                      <p className="text-sm text-gray-600">
                        Order #{order.orderNumber}
                      </p>
                      <p className="text-sm text-gray-500">
                        {formatDate(order.createdAt)}
                      </p>
                    </div>
                    <div className="mt-4 md:mt-0 flex flex-col items-start md:items-end">
                      <span className={`badge ${ORDER_STATUS_COLORS[order.status]} mb-2`}>
                        {ORDER_STATUS_DISPLAY[order.status]}
                      </span>
                      <p className="text-xl font-bold text-primary-600">
                        {formatCurrency(order.totalAmount)}
                      </p>
                    </div>
                  </div>

                  <div className="border-t pt-4">
                    <div className="flex flex-wrap gap-2">
                      {order.orderItems?.slice(0, 3).map((item, index) => (
                        <span key={index} className="text-sm text-gray-600">
                          {item.menuItemName} x {item.quantity}
                          {index < Math.min(order.orderItems.length - 1, 2) && ','}
                        </span>
                      ))}
                      {order.orderItems?.length > 3 && (
                        <span className="text-sm text-gray-600">
                          +{order.orderItems.length - 3} more
                        </span>
                      )}
                    </div>
                  </div>

                  <div className="mt-4 flex items-center justify-between">
                    <div className="text-sm text-gray-600">
                      📍 {order.deliveryAddress}
                    </div>
                    <div className="text-primary-600 font-medium">
                      View Details →
                    </div>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Orders;
