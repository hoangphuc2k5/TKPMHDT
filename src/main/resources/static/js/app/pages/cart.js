import { mountLayout } from "../mount.js";
import { ui } from "../ui.js";
import { api } from "../api.js";
import { API } from "../config.js";
import { auth } from "../auth.js";

let currentSubtotal = 0;

function normalizeCart(cart) {
  // Backend hiện tại trả: { items: [...], tongTien: ... }
  // Nếu API mới: tuỳ biến sau, nhưng UI vẫn chạy.
  return {
    items: Array.isArray(cart?.items) ? cart.items : [],
    tongTien: Number(cart?.tongTien || 0),
  };
}

function renderEmpty() {
  const container = document.getElementById("cart-items");
  if (!container) return;
  container.innerHTML =
    '<div class="text-center py-5"><h4 class="fw-bold mb-2">Giỏ hàng đang trống</h4><a href="/product-list.html" class="btn btn-primary mt-3">Mua sắm ngay</a></div>';
  ui.setText("#subtotal", ui.formatCurrency(0));
  ui.setText("#total", ui.formatCurrency(0));
  localStorage.removeItem("checkout_coupon");
}

function cartTable(items) {
  return `
    <table class="table align-middle">
      <thead>
        <tr>
          <th>Sản phẩm</th>
          <th>Giá</th>
          <th style="width: 170px;">Số lượng</th>
          <th>Tổng</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        ${items
          .map(
            (item) => `
          <tr>
            <td>
              <div class="fw-bold">${item.tenSanPham || "Sản phẩm"}</div>
              ${item.tuyChinh ? `<small class="text-primary d-block">✨ ${item.tuyChinh}</small>` : ""}
            </td>
            <td>${ui.formatCurrency(item.gia)}</td>
            <td>
              <div class="input-group input-group-sm">
                <button class="btn btn-outline-secondary" data-action="qty" data-id="${item.id}" data-qty="${
              Number(item.soLuong || 1) - 1
            }">-</button>
                <input type="text" class="form-control text-center" value="${item.soLuong || 1}" readonly>
                <button class="btn btn-outline-secondary" data-action="qty" data-id="${item.id}" data-qty="${
              Number(item.soLuong || 1) + 1
            }">+</button>
              </div>
            </td>
            <td class="fw-bold">${ui.formatCurrency(item.thanhTien || (Number(item.gia || 0) * Number(item.soLuong || 1)))}</td>
            <td>
              <button class="btn btn-link text-danger p-0" data-action="remove" data-id="${item.id}" aria-label="remove">🗑️</button>
            </td>
          </tr>
        `
          )
          .join("")}
      </tbody>
    </table>
  `;
}

async function loadCart() {
  const container = document.getElementById("cart-items");
  if (container) {
    container.innerHTML = `<div class="text-center py-5"><div class="spinner-border text-primary"></div></div>`;
  }

  try {
    const cart = await api.getWithFallback(API.cart.get, API.cart.fallbackGet);
    const { items, tongTien } = normalizeCart(cart);

    if (items.length === 0) {
      renderEmpty();
      return;
    }

    container.innerHTML = cartTable(items);
    currentSubtotal = tongTien;
    ui.setText("#subtotal", ui.formatCurrency(tongTien));
    ui.setText("#total", ui.formatCurrency(tongTien));
  } catch (e) {
    renderEmpty();
    ui.toast(e.message || "Không tải được giỏ hàng", "danger");
  }
}

async function updateQty(id, qty) {
  if (qty < 1) return;
  try {
    await api.putWithFallback(
      API.cart.update(id),
      `${API.cart.fallbackUpdate(id)}?soLuong=${encodeURIComponent(qty)}`,
      { soLuong: qty }
    );
    await loadCart();
  } catch (e) {
    ui.toast(e.message || "Cập nhật số lượng thất bại", "danger");
  }
}

async function removeItem(id) {
  if (!confirm("Xóa sản phẩm khỏi giỏ hàng?")) return;
  try {
    await api.deleteWithFallback(API.cart.remove(id), API.cart.fallbackRemove(id));
    ui.toast("Đã xóa sản phẩm");
    await loadCart();
  } catch (e) {
    ui.toast(e.message || "Xóa thất bại", "danger");
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  mountLayout({ activeNav: "products" });
  if (!auth.requireAuth("KHACH_HANG")) return;

  await loadCart();

  document.addEventListener("click", (e) => {
    const btn = e.target.closest("[data-action]");
    if (!btn) return;
    const action = btn.getAttribute("data-action");
    const id = btn.getAttribute("data-id");
    if (!id) return;

    if (action === "qty") updateQty(id, Number(btn.getAttribute("data-qty")));
    if (action === "remove") removeItem(id);
  });

  document.getElementById("apply-coupon")?.addEventListener("click", () => {
    applyCoupon();
  });
});

async function applyCoupon() {
  const code = (document.getElementById("coupon-code")?.value || "").trim().toUpperCase();
  if (!code) {
    ui.toast("Vui lòng nhập mã giảm giá", "warning");
    return;
  }
  try {
    const res = await fetch(`/api/v1/coupons/${encodeURIComponent(code)}/validate?amount=${encodeURIComponent(currentSubtotal)}`, {
      headers: { Authorization: `Bearer ${auth.getToken()}` },
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || "Mã giảm giá không hợp lệ");
    ui.setText("#total", ui.formatCurrency(data.thanhTienSauGiam || currentSubtotal));
    localStorage.setItem("checkout_coupon", code);
    ui.toast(`Áp dụng thành công mã ${code}`);
  } catch (e) {
    localStorage.removeItem("checkout_coupon");
    ui.toast(e.message || "Không áp dụng được mã giảm giá", "danger");
  }
}
