import { createContext, useCallback, useEffect, useState } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import Navbar from './components/Navbar.jsx';
import { getCart } from './services/api.js';
import Bill from './pages/Bill.jsx';
import Cart from './pages/Cart.jsx';
import Checkout from './pages/Checkout.jsx';
import Orders from './pages/Orders.jsx';
import Products from './pages/Products.jsx';

export const CartContext = createContext({ refreshCart: () => {}, cartCount: 0 });
export const CUSTOMER_ID = 1;

export default function App() {
  const [cartCount, setCartCount] = useState(0);
  const refreshCart = useCallback(async () => {
    try {
      const cart = await getCart(CUSTOMER_ID);
      setCartCount((cart?.items || []).reduce((sum, item) => sum + Number(item.quantity || 0), 0));
    } catch {
      setCartCount(0);
    }
  }, []);

  useEffect(() => { refreshCart(); }, [refreshCart]);

  return (
    <CartContext.Provider value={{ refreshCart, cartCount }}>
      <div className="app-shell">
        <Navbar cartCount={cartCount} />
        <main className="page-shell">
          <Routes>
            <Route path="/" element={<Products />} />
            <Route path="/products" element={<Products />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/checkout" element={<Checkout />} />
            <Route path="/bill/:orderId" element={<Bill />} />
            <Route path="/orders" element={<Orders />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
        <footer className="site-footer"><span>sprig<span className="brand-period">.</span></span><span>Good food, gathered simply.</span></footer>
      </div>
    </CartContext.Provider>
  );
}
