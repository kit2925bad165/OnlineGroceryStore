const categoryImages = {
  dairy: 'https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=900&q=85',
  fruits: 'https://images.unsplash.com/photo-1610832958506-aa56368176cf?auto=format&fit=crop&w=900&q=85',
  vegetables: 'https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=900&q=85',
  bakery: 'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=900&q=85',
  grains: 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?auto=format&fit=crop&w=900&q=85',
};
import { useState } from 'react';

const defaultImage = 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=900&q=85';
const productImages = {
  'fresh milk': 'https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=900&q=85',
  'basmati rice': 'https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=900&q=85',
  apple: 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&w=900&q=85',
  tomato: 'https://images.unsplash.com/photo-1546094096-0df4bcaaa337?auto=format&fit=crop&w=900&q=85',
  potato: 'https://images.unsplash.com/photo-1518977676601-b53f82aba655?auto=format&fit=crop&w=900&q=85',
  bread: 'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=900&q=85',
  eggs: 'https://images.unsplash.com/photo-1518569656558-1f25e69d93d7?auto=format&fit=crop&w=900&q=85',
  orange: 'https://images.unsplash.com/photo-1547514701-42782101795e?auto=format&fit=crop&w=900&q=85',
  banana: 'https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?auto=format&fit=crop&w=900&q=85',
  grapes: 'https://images.unsplash.com/photo-1537640538966-79f369143f8f?auto=format&fit=crop&w=900&q=85',
  carrot: 'https://images.unsplash.com/photo-1445282768818-728615cc910a?auto=format&fit=crop&w=900&q=85',
  onion: 'https://images.pexels.com/photos/6316541/pexels-photo-6316541.jpeg?auto=compress&cs=tinysrgb&w=900',
  cucumber: 'https://images.unsplash.com/photo-1604977042946-1eecc30f269e?auto=format&fit=crop&w=900&q=85',
  curd: 'https://images.unsplash.com/photo-1488477181946-6428a0291777?auto=format&fit=crop&w=900&q=85',
  paneer: 'https://images.unsplash.com/photo-1631452180519-c014fe946bc7?auto=format&fit=crop&w=900&q=85',
  'wheat flour': 'https://images.unsplash.com/photo-1627485937980-221c88ac04f9?auto=format&fit=crop&w=900&q=85',
  'toor dal': 'https://images.pexels.com/photos/8996219/pexels-photo-8996219.jpeg?auto=compress&cs=tinysrgb&w=900',
  biscuits: 'https://images.unsplash.com/photo-1558961363-fa8fdf82db35?auto=format&fit=crop&w=900&q=85',
  'potato chips': 'https://images.unsplash.com/photo-1566478989037-eec170784d0b?auto=format&fit=crop&w=900&q=85',
  'orange juice': 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?auto=format&fit=crop&w=900&q=85',
  'green tea': 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?auto=format&fit=crop&w=900&q=85',
  chocolate: 'https://images.unsplash.com/photo-1511381939415-e44015466834?auto=format&fit=crop&w=900&q=85',
  'cooking oil': 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=900&q=85',
};
const currency = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' });

export default function ProductCard({ product, onAdd, adding }) {
  const [imageFailed, setImageFailed] = useState(false);
  const providedImage = [product.imageUrl, product.image_url].find((value) => typeof value === 'string' && value.trim());
  const productName = (product.name || '').trim().toLowerCase();
  const image = productImages[productName] || providedImage || defaultImage;
  const unavailable = Number(product.stockQuantity) <= 0;

  return (
    <article className="product-card">
      <div className="product-image">
        <img src={imageFailed ? defaultImage : image} alt={product.name} onError={(event) => {
          if (imageFailed) event.currentTarget.style.visibility = 'hidden';
          else setImageFailed(true);
        }} />
        <span className="category-pill">{product.category || 'Grocery'}</span>
      </div>
      <div className="product-info">
        <div className="product-copy">
          <h2>{product.name}</h2>
          <p>{product.description || 'Freshly selected for your table.'}</p>
        </div>
        <div className="product-meta">
          <strong>{currency.format(Number(product.price) || 0)}</strong>
          <span className={unavailable ? 'stock stock-empty' : 'stock'}>
            {unavailable ? 'Out of stock' : `${product.stockQuantity} available`}
          </span>
        </div>
        <button className="button button-primary add-button" type="button" onClick={() => onAdd(product)} disabled={unavailable || adding}>
          {adding ? 'Adding…' : unavailable ? 'Unavailable' : <><span aria-hidden="true">＋</span> Add to cart</>}
        </button>
      </div>
    </article>
  );
}
