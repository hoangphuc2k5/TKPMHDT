import { mountLayout } from "../mount.js";
import { ui } from "../ui.js";
import { api } from "../api.js";
import { API } from "../config.js";

function buildQuery(params) {
  const sp = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v === undefined || v === null) return;
    const s = String(v).trim();
    if (!s) return;
    sp.set(k, s);
  });
  const qs = sp.toString();
  return qs ? `?${qs}` : "";
}

function productGridCard(p) {
  return `
    <div class="col-md-6 col-xl-4 mb-4">
      <div class="product-card card">
        <div class="position-relative">
          <img src="${p.hinhAnh || "https://via.placeholder.com/400x300"}" class="card-img-top" alt="${p.ten}">
          ${
            p.loaiSanPham
              ? `<span class="badge bg-primary position-absolute top-0 end-0 m-3">${p.loaiSanPham}</span>`
              : ""
          }
        </div>
        <div class="card-body">
          <h5 class="card-title fw-bold">${p.ten}</h5>
          <p class="text-muted small text-truncate mb-3">${p.moTa || "Nước uống tươi mát"}</p>
          <div class="d-flex justify-content-between align-items-center">
            <span class="product-price">${ui.formatCurrency(p.gia)}</span>
            <a href="/product-detail.html?id=${p.id}" class="btn btn-primary btn-sm">Chi tiết</a>
          </div>
        </div>
      </div>
    </div>
  `;
}

async function loadProducts() {
  const grid = document.getElementById("product-grid");
  const results = document.getElementById("results-count");
  if (!grid) return;

  const query = document.getElementById("search-input")?.value || "";
  const loai = document.getElementById("type-filter")?.value || "";
  const giaMax = document.getElementById("price-filter")?.value || "";

  grid.innerHTML = `<div class="text-center py-5"><div class="spinner-border text-primary"></div></div>`;
  if (results) results.textContent = "Đang tải...";

  const qsPrimary = buildQuery({ q: query, loai, giaMax });
  const qsFallback = buildQuery({ query, loai, giaMax });

  try {
    const products = await api.getWithFallback(
      `${API.products.list}${qsPrimary}`,
      `${API.products.fallbackList}${qsFallback}`
    );
    const arr = Array.isArray(products) ? products : [];

    if (results) results.textContent = `${arr.length} sản phẩm`;
    grid.innerHTML =
      arr.length === 0
        ? `<div class="col-12 text-center py-5"><h4 class="fw-bold mb-2">Không tìm thấy sản phẩm</h4><p class="text-muted mb-0">Hãy thử bộ lọc khác.</p></div>`
        : arr.map(productGridCard).join("");
  } catch (e) {
    if (results) results.textContent = "Lỗi tải";
    grid.innerHTML = `<div class="col-12 text-center py-5"><p class="text-muted mb-0">Không tải được sản phẩm.</p></div>`;
    ui.toast(e.message || "Lỗi tải dữ liệu", "danger");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  mountLayout({ activeNav: "products" });

  const priceFilter = document.getElementById("price-filter");
  const priceValue = document.getElementById("price-value");
  priceFilter?.addEventListener("input", (e) => {
    if (priceValue) priceValue.textContent = ui.formatCurrency(e.target.value);
  });

  document.getElementById("apply-filters")?.addEventListener("click", loadProducts);
  loadProducts();
});

