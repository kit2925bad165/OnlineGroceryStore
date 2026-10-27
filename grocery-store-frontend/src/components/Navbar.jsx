import { NavLink } from 'react-router-dom';

export default function Navbar({ cartCount }) {
  return (
    <header className="site-header">
      <div className="nav-wrap">
        <NavLink className="brand" to="/" aria-label="Sprig home">
          <span className="brand-mark" aria-hidden="true"><span /></span>
          <span>sprig<span className="brand-period">.</span></span>
        </NavLink>
        <nav className="main-nav" aria-label="Main navigation">
          <NavLink to="/" end>Shop</NavLink>
          <NavLink to="/cart" className="cart-nav">Cart <span className="nav-count">{cartCount}</span></NavLink>
          <NavLink to="/orders">Orders</NavLink>
        </nav>
        <div className="delivery-note"><span className="delivery-dot" /> Fresh picks, every day</div>
      </div>
    </header>
  );
}
