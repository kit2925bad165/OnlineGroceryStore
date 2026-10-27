import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { CUSTOMER_ID } from '../App.jsx';
import Loading from '../components/Loading.jsx';
import { getOrders } from '../services/api.js';
import { formatBackendDateTime } from '../utils/dateTime.js';

const currency = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' });

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getOrders(CUSTOMER_ID).then((data) => setOrders(Array.isArray(data) ? data : [])).catch((failure) => setError(failure.message || 'Unable to load orders. Please make sure the backend is running.')).finally(() => setLoading(false));
  }, []);

  if (loading) return <Loading label="Loading your orders…" />;
  return (
    <section className="content-page">
      <div className="page-heading"><div><p className="section-kicker">YOUR PURCHASES</p><h1>Order history<span className="heading-period">.</span></h1><p className="page-subtitle">A record of the good things you brought home.</p></div><Link className="back-link" to="/">Shop groceries →</Link></div>
      {error ? <div className="state-panel error-panel"><span className="state-icon">!</span><h2>Orders unavailable</h2><p>{error}</p></div> : orders.length === 0 ? <div className="state-panel"><span className="state-icon">↗</span><h2>No orders found.</h2><p>Your next market trip is just around the corner.</p><Link className="button button-primary" to="/">Explore the market</Link></div> : <div className="orders-list">{orders.map((order) => <article className="order-card" key={order.id}><div className="order-card-head"><div><p className="section-kicker">ORDER</p><h2>#{order.id}</h2></div><span className="order-status">{order.status || 'PLACED'}</span></div><div className="order-date">{formatBackendDateTime(order.orderDate)}</div><div className="order-detail-list">{(order.items || []).map((item) => <div className="order-detail" key={item.id}><span>{item.productName} <small>× {item.quantity}</small></span><strong>{currency.format(Number(item.subtotal) || 0)}</strong></div>)}</div><div className="order-card-foot"><div><span>Total</span><strong>{currency.format(Number(order.totalAmount) || 0)}</strong></div><Link className="button button-secondary" to={`/bill/${order.id}`}>View bill <span>→</span></Link></div></article>)}</div>}
    </section>
  );
}
