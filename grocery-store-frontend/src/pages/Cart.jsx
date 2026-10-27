import { useCallback, useContext, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { CartContext, CUSTOMER_ID } from '../App.jsx';
import CartItem from '../components/CartItem.jsx';
import Loading from '../components/Loading.jsx';
import { getCart, removeCartItem, updateCartItem } from '../services/api.js';

const currency = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' });

export default function Cart() {
  const { refreshCart } = useContext(CartContext);
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [busyId, setBusyId] = useState(null);

  const loadCart = useCallback(async () => {
    setError('');
    try { setCart(await getCart(CUSTOMER_ID)); }
    catch (failure) { setError(failure.message || 'Unable to load your cart. Please make sure the backend is running.'); }
    finally { setLoading(false); }
  }, []);

  useEffect(() => { loadCart(); }, [loadCart]);

  async function changeQuantity(item, quantity) {
    setBusyId(item.id);
    setError('');
    try {
      await updateCartItem(CUSTOMER_ID, item.id, quantity);
      await Promise.all([loadCart(), refreshCart()]);
    } catch (failure) { setError(failure.message || 'Could not update this item.'); }
    finally { setBusyId(null); }
  }

  async function removeItem(item) {
    setBusyId(item.id);
    setError('');
    try {
      await removeCartItem(CUSTOMER_ID, item.id);
      await Promise.all([loadCart(), refreshCart()]);
    } catch (failure) { setError(failure.message || 'Could not remove this item.'); }
    finally { setBusyId(null); }
  }

  const items = cart?.items || [];
  const total = Number(cart?.totalPrice ?? items.reduce((sum, item) => sum + Number(item.subtotal || 0), 0));

  if (loading) return <Loading label="Loading your cart…" />;
  return (
    <section className="content-page">
      <div className="page-heading"><div><p className="section-kicker">YOUR BASKET</p><h1>Your cart<span className="heading-period">.</span></h1><p className="page-subtitle">A good selection, ready when you are.</p></div><Link className="back-link" to="/">← Continue shopping</Link></div>
      {error && <div className="inline-error" role="alert">{error}</div>}
      {items.length === 0 ? <div className="state-panel empty-cart"><span className="empty-basket" aria-hidden="true">⌑</span><h2>Your cart is empty.</h2><p>Find something lovely for the table.</p><Link className="button button-primary" to="/">Explore the market</Link></div> : <div className="cart-layout">
        <div className="cart-items-list">{items.map((item) => <CartItem key={item.id} item={item} busy={busyId === item.id} onQuantityChange={changeQuantity} onRemove={removeItem} />)}</div>
        <aside className="summary-panel"><p className="section-kicker">ORDER TOTAL</p><div className="summary-row"><span>Items ({items.length})</span><span>{currency.format(total)}</span></div><div className="summary-row muted"><span>Delivery</span><span>Calculated at checkout</span></div><div className="summary-total"><span>Total</span><strong>{currency.format(total)}</strong></div><Link className="button button-primary checkout-link" to="/checkout">Continue to checkout <span>→</span></Link><p className="secure-note"><span>✳</span> Final total confirmed before placing your order</p></aside>
      </div>}
    </section>
  );
}
