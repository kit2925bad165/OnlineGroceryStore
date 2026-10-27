import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Loading from '../components/Loading.jsx';
import { getBill } from '../services/api.js';
import { formatBackendDateTime } from '../utils/dateTime.js';

const currency = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' });

export default function Bill() {
  const { orderId } = useParams();
  const [bill, setBill] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getBill(orderId).then(setBill).catch((failure) => setError(failure.message || 'Unable to load this bill. Please make sure the backend is running.')).finally(() => setLoading(false));
  }, [orderId]);

  if (loading) return <Loading label="Loading your bill…" />;
  if (error) return <div className="state-panel error-panel"><span className="state-icon">!</span><h2>Bill unavailable</h2><p>{error}</p><Link className="button button-secondary" to="/orders">View orders</Link></div>;
  return (
    <section className="content-page bill-page">
      <div className="bill-actions no-print"><Link className="back-link" to="/orders">← Orders</Link><button className="button button-secondary print-button" type="button" onClick={() => window.print()}><span aria-hidden="true">▤</span> Print bill</button></div>
      <article className="invoice">
        <div className="invoice-head"><div className="invoice-brand"><span className="brand-mark" aria-hidden="true"><span /></span><span>sprig<span className="brand-period">.</span></span></div><div className="invoice-title"><p className="section-kicker">ORDER COMPLETE</p><h1>Invoice</h1></div></div>
        <div className="invoice-meta"><div><span>Bill ID</span><strong>#{bill.billId}</strong></div><div><span>Order ID</span><strong>#{bill.orderId}</strong></div><div><span>Date issued</span><strong>{formatBackendDateTime(bill.billDate)}</strong></div></div>
        <div className="invoice-lines"><div className="invoice-line"><span>Subtotal</span><strong>{currency.format(Number(bill.subtotal) || 0)}</strong></div><div className="invoice-line"><span>Tax</span><strong>{currency.format(Number(bill.tax) || 0)}</strong></div><div className="invoice-line"><span>Delivery charge</span><strong>{currency.format(Number(bill.deliveryCharge) || 0)}</strong></div></div>
        <div className="invoice-total"><span>Total paid</span><strong>{currency.format(Number(bill.totalAmount) || 0)}</strong></div>
        <p className="invoice-thanks">Thank you for choosing fresh.</p>
      </article>
      <div className="bill-bottom no-print"><Link className="button button-primary" to="/">Continue shopping <span>→</span></Link><Link className="button button-secondary" to="/orders">View orders</Link></div>
    </section>
  );
}
