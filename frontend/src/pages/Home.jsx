import { Link } from 'react-router-dom';
import { ArrowRight, ShoppingBag, Truck, Shield, Star } from 'lucide-react';

const Home = () => {
  return (
    <div>
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-primary-600 to-primary-800 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
          <div className="text-center">
            <h1 className="text-5xl md:text-6xl font-bold mb-6">
              Premium Branded Shirts
            </h1>
            <p className="text-xl md:text-2xl mb-8 text-primary-100">
              Elevate your style with our curated collection of finest shirts
            </p>
            <Link to="/products" className="inline-flex items-center space-x-2 bg-white text-primary-600 px-8 py-4 rounded-lg font-semibold hover:bg-gray-100 transition-colors">
              <span>Shop Now</span>
              <ArrowRight size={20} />
            </Link>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div className="text-center">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 text-primary-600 rounded-full mb-4">
                <ShoppingBag size={32} />
              </div>
              <h3 className="text-lg font-semibold mb-2">Premium Quality</h3>
              <p className="text-gray-600">Finest fabrics and craftsmanship</p>
            </div>
            
            <div className="text-center">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 text-primary-600 rounded-full mb-4">
                <Truck size={32} />
              </div>
              <h3 className="text-lg font-semibold mb-2">Free Shipping</h3>
              <p className="text-gray-600">On orders above ₹2,000</p>
            </div>
            
            <div className="text-center">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 text-primary-600 rounded-full mb-4">
                <Shield size={32} />
              </div>
              <h3 className="text-lg font-semibold mb-2">Secure Payment</h3>
              <p className="text-gray-600">100% secure transactions</p>
            </div>
            
            <div className="text-center">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 text-primary-600 rounded-full mb-4">
                <Star size={32} />
              </div>
              <h3 className="text-lg font-semibold mb-2">Best Service</h3>
              <p className="text-gray-600">Customer satisfaction guaranteed</p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="bg-primary-600 rounded-2xl p-12 text-center text-white">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              New Collection Available
            </h2>
            <p className="text-xl mb-8 text-primary-100">
              Discover our latest arrivals and trending styles
            </p>
            <Link to="/products" className="inline-block bg-white text-primary-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 transition-colors">
              Explore Collection
            </Link>
          </div>
        </div>
      </section>

      {/* Categories Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 className="text-3xl font-bold text-center mb-12">Shop by Category</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <Link to="/products?colorFilter=White" className="group">
              <div className="card overflow-hidden">
                <div className="h-64 bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center group-hover:scale-105 transition-transform duration-300">
                  <span className="text-4xl font-bold text-gray-700">Formal</span>
                </div>
                <div className="p-4 text-center">
                  <h3 className="text-xl font-semibold">Formal Shirts</h3>
                </div>
              </div>
            </Link>
            
            <Link to="/products?colorFilter=Blue" className="group">
              <div className="card overflow-hidden">
                <div className="h-64 bg-gradient-to-br from-blue-100 to-blue-200 flex items-center justify-center group-hover:scale-105 transition-transform duration-300">
                  <span className="text-4xl font-bold text-blue-700">Casual</span>
                </div>
                <div className="p-4 text-center">
                  <h3 className="text-xl font-semibold">Casual Shirts</h3>
                </div>
              </div>
            </Link>
            
            <Link to="/products?sort=price-desc" className="group">
              <div className="card overflow-hidden">
                <div className="h-64 bg-gradient-to-br from-purple-100 to-purple-200 flex items-center justify-center group-hover:scale-105 transition-transform duration-300">
                  <span className="text-4xl font-bold text-purple-700">Premium</span>
                </div>
                <div className="p-4 text-center">
                  <h3 className="text-xl font-semibold">Premium Collection</h3>
                </div>
              </div>
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;
