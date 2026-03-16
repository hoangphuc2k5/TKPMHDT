export const APP = {
  brand: "DRINKSTORE",
};

// Ưu tiên API theo yêu cầu đề bài: /api/sanpham
// Fallback để chạy được ngay với backend hiện tại: /api/v1/products
export const API = {
  primaryBase: "/api",
  fallbackBase: "/api/v1",

  // Các endpoint chính (không kèm base)
  products: {
    list: "/sanpham",
    detail: (id) => `/sanpham/${encodeURIComponent(id)}`,
    // fallback cho backend hiện tại
    fallbackList: "/products/search",
    fallbackDetail: (id) => `/products/${encodeURIComponent(id)}`,
  },

  formulas: {
    list: "/congthuc",
    fallbackList: "/formulas",
  },

  cart: {
    get: "/giohang",
    add: "/giohang",
    update: (id) => `/giohang/${encodeURIComponent(id)}`,
    remove: (id) => `/giohang/${encodeURIComponent(id)}`,
    // fallback
    fallbackGet: "/cart",
    fallbackAdd: "/cart/add",
    fallbackUpdate: (id) => `/cart/update/${encodeURIComponent(id)}`,
    fallbackRemove: (id) => `/cart/remove/${encodeURIComponent(id)}`,
  },

  orders: {
    checkout: "/donhang",
    history: "/donhang",
    cancel: (id) => `/donhang/${encodeURIComponent(id)}/huy`,
    // fallback
    fallbackCheckout: "/orders/checkout",
    fallbackHistory: "/orders/history",
    fallbackCancel: (id) => `/orders/${encodeURIComponent(id)}/cancel`,
  },
};

export const STORAGE_KEYS = {
  token: "token",
  role: "role",
  username: "username",
};

