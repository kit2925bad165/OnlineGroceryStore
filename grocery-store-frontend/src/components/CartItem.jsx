const currency = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' });

export default function CartItem({ item, onQuantityChange, onRemove, busy }) {
  return (
    <article className="cart-line">
      <div className="cart-product-icon" aria-hidden="true">{(item.productName || 'G').slice(0, 1).toUpperCase()}</div>
      <div className="cart-line-main">
        <h2>{item.productName}</h2>
        <p>{currency.format(Number(item.price) || 0)} <span>per item</span></p>
        <div className="quantity-control" aria-label={`Quantity for ${item.productName}`}>
          <button type="button" aria-label="Decrease quantity" disabled={busy || Number(item.quantity) <= 1} onClick={() => onQuantityChange(item, Number(item.quantity) - 1)}>−</button>
          <span>{item.quantity}</span>
          <button type="button" aria-label="Increase quantity" disabled={busy} onClick={() => onQuantityChange(item, Number(item.quantity) + 1)}>＋</button>
        </div>
      </div>
      <div className="cart-line-end">
        <strong>{currency.format(Number(item.subtotal) || (Number(item.price) || 0) * Number(item.quantity))}</strong>
        <button className="text-button remove-button" type="button" disabled={busy} onClick={() => onRemove(item)}>Remove</button>
      </div>
    </article>
  );
}
