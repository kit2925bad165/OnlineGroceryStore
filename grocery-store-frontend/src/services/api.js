const API_BASE_URL = 'http://localhost:8080/api';

async function request(path, options = {}) {
  let response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers: {
        ...(options.body ? { 'Content-Type': 'application/json' } : {}),
        ...options.headers,
      },
    });
  } catch {
    throw new Error('Unable to reach the store. Please make sure the backend is running.');
  }

  const text = await response.text();
  let data;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }

  if (!response.ok) {
    const message = typeof data === 'string' ? data : data?.message;
    throw new Error(message || 'Something went wrong. Please try again.');
  }
  return data;
}

export const getProducts = () => request('/products');
export const getProduct = (id) => request(`/products/${id}`);
export const getCustomer = (id) => request(`/customers/${id}`);
export const getCart = (customerId) => request(`/cart/${customerId}`);
export const addToCart = (customerId, productId, quantity) => request(`/cart/${customerId}/items`, {
  method: 'POST',
  body: JSON.stringify({ productId, quantity }),
});
export const updateCartItem = (customerId, cartItemId, quantity) => request(`/cart/${customerId}/items/${cartItemId}`, {
  method: 'PUT',
  body: JSON.stringify({ quantity }),
});
export const removeCartItem = (customerId, cartItemId) => request(`/cart/${customerId}/items/${cartItemId}`, {
  method: 'DELETE',
});
export const placeOrder = (customerId) => request(`/orders/${customerId}`, { method: 'POST' });
export const getOrders = (customerId) => request(`/orders/${customerId}`);
export const getBill = (orderId) => request(`/bills/${orderId}`);
