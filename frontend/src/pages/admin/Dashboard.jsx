import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Package, ShoppingBag, TrendingUp, Users } from 'lucide-react';
import { toast } from 'react-toastify';
import { adminAPI } from '../../utils/api';

const Dashboard = () => {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    setLoading(true);
    try {
      const response = await adminAPI.getAnalytics();
      setAnalytics(response.data);
    } catch (error) {
      toast.error('Failed to load analytics');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
    }).format(price);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold mb-8">Admin Dashboard</h1>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div className="card p-6">
          <div className="flex items-center justify-between mb-4">
            <div className="p-3 bg-primary-100 rounded-lg">
              <TrendingUp className="text-primary-600" size={24} />
            </div>
          </div>
          <h3 className="text-gray-600 text-sm font-medium mb-1">Revenue (30 days)</h3>
          <p className="text-2xl font-bold text-gray-900">
            {analytics ? formatPrice(analytics.revenue()) : '₹0'}
          </p>
        </div>

        <div className="card p-6">
          <div className="flex items-center justify-between mb-4">
            <div className="p-3 bg-green-100 rounded-lg">
              <ShoppingBag className="text-green-600" size={24} />
            </div>
          </div>
          <h3 className="text-gray-600 text-sm font-medium mb-1">Orders (30 days)</h3>
          <p className="text-2xl font-bold text-gray-900">
            {analytics ? analytics.orderCount : 0}
          </p>
        </div>

        <div className="card p-6">
          <div className="flex items-center justify-between mb-4">
            <div className="p-3 bg-purple-100 rounded-lg">
              <Package className="text-purple-600" size={24} />
            </div>
          </div>
          <h3 className="text-gray-600 text-sm font-medium mb-1">Avg Order Value</h3>
          <p className="text-2xl font-bold text-gray-900">
            {analytics && analytics.orderCount > 0
              ? formatPrice(analytics.revenue() / analytics.orderCount)
              : '₹0'}
          </p>
        </div>

        <div className="card p-6">
          <div className="flex items-center justify-between mb-4">
            <div className="p-3 bg-orange-100 rounded-lg">
              <Users className="text-orange-600" size={24} />
            </div>
          </div>
          <h3 className="text-gray-600 text-sm font-medium mb-1">Active</h3>
          <p className="text-2xl font-bold text-gray-900">Dashboard</p>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Link to="/admin/products" className="card p-6 hover:shadow-lg transition-shadow">
          <div className="flex items-center gap-4">
            <div className="p-4 bg-primary-100 rounded-lg">
              <Package className="text-primary-600" size={32} />
            </div>
            <div>
              <h3 className="text-xl font-bold mb-1">Manage Products</h3>
              <p className="text-gray-600">Add, edit, or remove products</p>
            </div>
          </div>
        </Link>

        <Link to="/admin/orders" className="card p-6 hover:shadow-lg transition-shadow">
          <div className="flex items-center gap-4">
            <div className="p-4 bg-green-100 rounded-lg">
              <ShoppingBag className="text-green-600" size={32} />
            </div>
            <div>
              <h3 className="text-xl font-bold mb-1">Manage Orders</h3>
              <p className="text-gray-600">View and update order status</p>
            </div>
          </div>
        </Link>
      </div>
    </div>
  );
};

export default Dashboard;
