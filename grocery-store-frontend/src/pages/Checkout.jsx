import { useContext, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { CartContext, CUSTOMER_ID } from '../App.jsx';
import Loading from '../components/Loading.jsx';
import { getCart, getCustomer, placeOrder } from '../services/api.js';

const currency = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' });

export default function Checkout() {
  const { refreshCart } = useContext(CartContext);
  const navigate = useNavigate();
  const [customer, setCustomer] = useState(null);
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [placing, setPlacing] = useState(false);

  useEffect(() => {
    Promise.all([getCustomer(CUSTOMER_ID), getCart(CUSTOMER_ID)])
      .then(([customerData, cartData]) => { setCustomer(customerData); setCart(cartData); })
      .catch((failure) => setError(failure.message || 'Unable to load checkout details. Please make sure the backend is running.'))
      .finally(() => setLoading(false));
  }, []);

  async function handlePlaceOrder() {
    if (placing || !(cart?.items || []).length) return;
    setPlacing(true);
    setError('');
    try {
      const order = await placeOrder(CUSTOMER_ID);
      await refreshCart();
      navigate(`/bill/${order.id}`);
    } catch (failure) {
      setError(failure.message || 'Order could not be placed. Please try again.');
      setPlacing(false);
    }
  }

  if (loading) return <Loading label="Preparing checkout…" />;
  const items = cart?.items || [];
  const total = Number(cart?.totalPrice ?? items.reduce((sum, item) => sum + Number(item.subtotal || 0), 0));
  return (
    <section className="content-page checkout-page">
      <div className="page-heading"><div><p className="section-kicker">ONE LAST LOOK</p><h1>Checkout<span className="heading-period">.</span></h1><p className="page-subtitle">Confirm your details and basket before placing your order.</p></div><Link className="back-link" to="/cart">← Back to cart</Link></div>
      {error && <div className="inline-error" role="alert">{error}</div>}
      {items.length === 0 ? <div className="state-panel empty-cart"><span className="empty-basket" aria-hidden="true">⌑</span><h2>Your cart is empty.</h2><p>Add groceries before heading to checkout.</p><Link className="button button-primary" to="/">Explore the market</Link></div> : <div className="checkout-grid">
        <section className="checkout-section"><div className="subheading"><span className="step-number">01</span><div><p className="section-kicker">DELIVERY TO</p><h2>Customer information</h2></div></div><div className="customer-details"><div className="customer-avatar">{customer?.name?.slice(0, 1) || 'C'}</div><div><h3>{customer?.name || 'Customer'}</h3><p>{customer?.email || 'Email unavailable'}</p><p>{customer?.phone || 'Phone unavailable'}</p><p>{customer?.address || 'Address unavailable'}</p></div></div>
          <div className="subheading order-subheading"><span className="step-number">02</span><div><p className="section-kicker">IN YOUR BASKET</p><h2>Order summary</h2></div></div><div className="checkout-items">{items.map((item) => <div className="checkout-item" key={item.id}><span><strong>{item.productName}</strong><small> × {item.quantity}</small></span><span>{currency.format(Number(item.subtotal) || Number(item.price) * Number(item.quantity))}</span></div>)}</div>
        </section>
        <aside className="summary-panel checkout-total"><p className="section-kicker">PAYMENT SUMMARY</p><div className="summary-row"><span>Subtotal</span><span>{currency.format(total)}</span></div><div className="summary-row"><span>Tax</span><span>{currency.format(0)}</span></div><div className="summary-row"><span>Delivery</span><span>{currency.format(0)}</span></div><div className="summary-total"><span>Total due</span><strong>{currency.format(total)}</strong></div><button className="button button-primary checkout-link" type="button" disabled={placing} onClick={handlePlaceOrder}>{placing ? 'Placing order…' : 'Place order'} <span>→</span></button><p className="secure-note">Your order will be confirmed by the store.</p></aside>
      </div>}
    </section>
  );
}
