import { mountLayout } from "../mount.js";
import { ui } from "../ui.js";
import { api } from "../api.js";
import { API } from "../config.js";
import { auth } from "../auth.js";

function normalizeCart(cart) {
  return {
    items: Array.isArray(cart?.items) ? cart.items : [],
    tongTien: Number(cart?.tongTien || 0),
  };
}

async function loadSummary() {
  const container = document.getElementById("order-items-summary");
  if (container) container.innerHTML = "";

  const cart = await api.getWithFallback(API.cart.get, API.cart.fallbackGet);
  const { items, tongTien } = normalizeCart(cart);

  if (items.length === 0) {
    window.location.href = "/cart.html";
    return;
  }

  items.forEach((item) => {
    container.insertAdjacentHTML(
      "beforeend",
      `
      <div class="d-flex justify-content-between align-items-center mb-2">
        <span class="text-muted small">${item.soLuong}x ${item.tenSanPham}</span>
        <span class="small fw-bold">${ui.formatCurrency(item.thanhTien)}</span>
      </div>
    `
    );
  });

  ui.setText("#subtotal", ui.formatCurrency(tongTien));
  ui.setText("#total", ui.formatCurrency(tongTien));
  const coupon = localStorage.getItem("checkout_coupon");
  if (coupon) {
    try {
      const res = await fetch(`/api/v1/coupons/${encodeURIComponent(coupon)}/validate?amount=${encodeURIComponent(tongTien)}`, {
        headers: { Authorization: `Bearer ${auth.getToken()}` },
      });
      const data = await res.json();
      if (res.ok) {
        ui.setText("#total", ui.formatCurrency(data.thanhTienSauGiam || tongTien));
      }
    } catch (_) {}
  }
}

async function checkout(payload) {
  // API chuẩn: POST /api/donhang
  // fallback: POST /api/v1/orders/checkout
  return await api.postWithFallback(API.orders.checkout, API.orders.fallbackCheckout, payload);
}

document.addEventListener("DOMContentLoaded", async () => {
  mountLayout({ activeNav: "products" });
  if (!auth.requireAuth("KHACH_HANG")) return;

  try {
    await loadSummary();
  } catch (e) {
    ui.toast(e.message || "Không tải được giỏ hàng", "danger");
  }

  document.getElementById("checkout-form")?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const payload = {
      phuongThucThanhToan: document.querySelector('input[name="payment-method"]:checked')?.value,
      maGiamGia: (localStorage.getItem("checkout_coupon") || "").trim() || null,
      diaChiGiaoHang: document.getElementById("address")?.value || "",
      soDienThoaiGiaoHang: document.getElementById("phone")?.value || "",
      ghiChu: document.getElementById("note")?.value || "",
    };

    try {
      const order = await checkout(payload);
      localStorage.removeItem("checkout_coupon");
      ui.toast("Đặt hàng thành công! Đang chuyển hướng...");
      setTimeout(() => (window.location.href = `/customer/orders.html?id=${order?.id || ""}`), 900);
    } catch (err) {
      ui.toast(err.message || "Đặt hàng thất bại", "danger");
    }
  });
});
