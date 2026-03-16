import { APP } from "../config.js";
import { auth } from "../auth.js";

export function renderHeader(active = "home") {
  const isAuthed = auth.isAuthenticated();

  return `
  <nav class="navbar navbar-expand-lg sticky-top">
    <div class="container">
      <a class="navbar-brand" href="/">${APP.brand}</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav mx-auto">
          <li class="nav-item"><a class="nav-link ${active === "home" ? "active" : ""}" href="/">Trang chủ</a></li>
          <li class="nav-item"><a class="nav-link ${active === "products" ? "active" : ""}" href="/product-list.html">Sản phẩm</a></li>
          <li class="nav-item"><a class="nav-link" href="/product-list.html#promo">Khuyến mãi</a></li>
        </ul>
        <div class="d-flex align-items-center gap-2">
          <a href="/cart.html" class="nav-link cart-icon">
            🛒 <span id="cart-count" class="cart-count">0</span>
          </a>
          ${
            isAuthed
              ? `
              <div class="dropdown ms-2">
                <button class="btn btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                  <span id="username-display">${auth.getUsername() || "Tài khoản"}</span>
                </button>
                <ul class="dropdown-menu dropdown-menu-end">
                  <li><a class="dropdown-item" href="/customer/orders.html">Đơn hàng của tôi</a></li>
                  <li><hr class="dropdown-divider"></li>
                  <li><a class="dropdown-item text-danger" href="#" data-action="logout">Đăng xuất</a></li>
                </ul>
              </div>
            `
              : `
              <a href="/login.html" class="btn btn-outline-primary">Đăng nhập</a>
              <a href="/register.html" class="btn btn-primary">Đăng ký</a>
            `
          }
        </div>
      </div>
    </div>
  </nav>
  `;
}

export function bindHeaderEvents(root = document) {
  root.addEventListener("click", (e) => {
    const btn = e.target.closest("[data-action]");
    if (!btn) return;
    const action = btn.getAttribute("data-action");
    if (action === "logout") {
      e.preventDefault();
      auth.logout();
    }
  });
}

