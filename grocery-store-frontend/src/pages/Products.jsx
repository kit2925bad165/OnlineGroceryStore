import { useContext, useEffect, useMemo, useState } from 'react';
import { CartContext, CUSTOMER_ID } from '../App.jsx';
import ProductCard from '../components/ProductCard.jsx';
import Loading from '../components/Loading.jsx';
import { addToCart, getProducts } from '../services/api.js';

export default function Products() {
  const { refreshCart } = useContext(CartContext);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState('All');
  const [addingId, setAddingId] = useState(null);
  const [notice, setNotice] = useState('');

  useEffect(() => {
    getProducts()
      .then((data) => setProducts(Array.isArray(data) ? data : []))
      .catch((failure) => setError(failure.message || 'Unable to load products. Please make sure the backend is running.'))
      .finally(() => setLoading(false));
  }, []);

  const categories = useMemo(() => ['All', ...new Set(products.map((product) => product.category).filter(Boolean))], [products]);
  const filteredProducts = useMemo(() => products.filter((product) => {
    const matchesQuery = `${product.name || ''} ${product.category || ''}`.toLowerCase().includes(query.trim().toLowerCase());
    return matchesQuery && (category === 'All' || product.category === category);
  }), [products, query, category]);

  async function handleAdd(product) {
    setAddingId(product.id);
    setNotice('');
    try {
      await addToCart(CUSTOMER_ID, product.id, 1);
      await refreshCart();
      setNotice(`${product.name} added to your cart.`);
    } catch (failure) {
      setNotice(failure.message || 'Could not add this item. Please try again.');
    } finally {
      setAddingId(null);
    }
  }

  return (
    <section className="catalog-page">
      <div className="catalog-intro">
        <div>
          <p className="eyebrow"><span className="eyebrow-mark" /> YOUR NEIGHBORHOOD MARKET</p>
          <h1>Fresh finds for<br /><span>everyday living.</span></h1>
          <p className="intro-copy">A little goodness for your basket, picked with care.</p>
        </div>
        <div className="intro-stamp" aria-hidden="true"><span>GOOD<br />THINGS<br /><b>GROW</b></span><i>✳</i></div>
      </div>

      <div className="catalog-toolbar">
        <div><p className="section-kicker">THE MARKET</p><h2>Shop all groceries <span>{products.length}</span></h2></div>
        <label className="search-field"><span aria-hidden="true">⌕</span><input type="search" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search products" aria-label="Search products by name or category" /><kbd>/</kbd></label>
      </div>

      <div className="filter-row" aria-label="Filter by category">
        {categories.map((item) => <button className={`filter-chip${category === item ? ' selected' : ''}`} type="button" key={item} onClick={() => setCategory(item)}>{item}</button>)}
      </div>
      {notice && <div className="notice" role="status">{notice}</div>}
      {loading ? <Loading label="Loading products…" /> : error ? <div className="state-panel error-panel"><span className="state-icon">!</span><h2>Market unavailable</h2><p>{error}</p><button className="button button-secondary" onClick={() => window.location.reload()}>Try again</button></div> : filteredProducts.length === 0 ? <div className="state-panel"><span className="state-icon">⌕</span><h2>{products.length ? 'No matching groceries' : 'The shelves are empty'}</h2><p>{products.length ? 'Try another search or category.' : 'There are no products to show right now.'}</p></div> : <div className="product-grid">{filteredProducts.map((product) => <ProductCard key={product.id} product={product} onAdd={handleAdd} adding={addingId === product.id} />)}</div>}
    </section>
  );
}
