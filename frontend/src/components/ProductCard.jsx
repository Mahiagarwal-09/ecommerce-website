import { Link } from 'react-router-dom';
import { ShoppingCart } from 'lucide-react';

const ProductCard = ({ product, onAddToCart }) => {
  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
    }).format(price);
  };

  return (
    <div className="card group hover:shadow-xl transition-shadow duration-300">
      <Link to={`/products/${product.id}`} className="block">
        <div className="aspect-w-3 aspect-h-4 bg-gray-200 overflow-hidden">
          <img
            src={product.images?.[0] || 'https://via.placeholder.com/600x800'}
            alt={product.name}
            className="w-full h-64 object-cover group-hover:scale-105 transition-transform duration-300"
          />
        </div>
      </Link>
      
      <div className="p-4">
        <Link to={`/products/${product.id}`}>
          <h3 className="text-lg font-semibold text-gray-900 mb-2 hover:text-primary-600 transition-colors">
            {product.name}
          </h3>
        </Link>
        
        <p className="text-gray-600 text-sm mb-3 line-clamp-2">{product.description}</p>
        
        <div className="flex items-center justify-between mb-3">
          <span className="text-xl font-bold text-primary-600">
            {formatPrice(product.price)}
          </span>
          {product.stock > 0 ? (
            <span className="text-sm text-green-600">In Stock</span>
          ) : (
            <span className="text-sm text-red-600">Out of Stock</span>
          )}
        </div>
        
        {product.sizes && product.sizes.length > 0 && (
          <div className="flex gap-1 mb-3">
            {product.sizes.slice(0, 5).map((size) => (
              <span key={size} className="text-xs px-2 py-1 bg-gray-100 rounded">
                {size}
              </span>
            ))}
          </div>
        )}
        
        <button
          onClick={() => onAddToCart(product)}
          disabled={product.stock === 0}
          className="w-full btn-primary flex items-center justify-center space-x-2"
        >
          <ShoppingCart size={18} />
          <span>Add to Cart</span>
        </button>
      </div>
    </div>
  );
};

export default ProductCard;
