import { mountLayout } from "../mount.js";
import { ui } from "../ui.js";
import { api } from "../api.js";
import { API } from "../config.js";

function productCard(p) {
  return `
    <div class="col-lg-4 col-md-6 mb-4">
      <div class="product-card card">
        <img src="${p.hinhAnh || "https://images.unsplash.com/photo-1544145945-f904253d0c7b?auto=format&fit=crop&w=800&q=80"}" class="card-img-top" alt="${p.ten}">
        <div class="card-body text-center">
          <h5 class="card-title fw-bold">${p.ten}</h5>
          <p class="text-muted small mb-3">${p.moTa || "Nước uống tươi mát từ thiên nhiên"}</p>
          <div class="d-flex justify-content-between align-items-center">
            <span class="product-price">${ui.formatCurrency(p.gia)}</span>
            <a href="/product-detail.html?id=${p.id}" class="btn btn-primary btn-sm">Xem</a>
          </div>
        </div>
      </div>
    </div>
  `;
}

async function loadFeatured() {
  const host = document.getElementById("featured-products");
  if (!host) return;

  host.innerHTML = `
    <div class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
  `;

  try {
    // list: /api/sanpham?limit=...
    // fallback: /api/v1/products/search?limit=...
    const products = await api.getWithFallback(
      `${API.products.list}?limit=3`,
      `${API.products.fallbackList}?limit=3`
    );

    const arr = Array.isArray(products) ? products : [];
    host.innerHTML = arr.slice(0, 3).map(productCard).join("");
  } catch (e) {
    host.innerHTML = `<div class="text-center py-5"><p class="text-muted mb-0">Không tải được sản phẩm nổi bật.</p></div>`;
    ui.toast(e.message || "Lỗi tải dữ liệu", "danger");
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  mountLayout({ activeNav: "home" });
  await loadFeatured();
});

