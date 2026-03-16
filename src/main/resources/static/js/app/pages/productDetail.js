import { mountLayout } from "../mount.js";
import { ui } from "../ui.js";
import { api } from "../api.js";
import { API } from "../config.js";
import { auth } from "../auth.js";

const state = {
  product: null,
  formulas: [],
  selectedFormula: null,
  ingredientAmounts: new Map(), // key: name -> number (0..2x)
};

function getProductId() {
  const params = new URLSearchParams(window.location.search);
  return params.get("id");
}

function renderProduct(p) {
  const host = document.getElementById("product-detail-container");
  if (!host) return;

  host.innerHTML = `
    <div class="col-md-6 mb-4">
      <img src="${p.hinhAnh || "https://via.placeholder.com/800x600"}" class="img-fluid rounded-4 shadow-medium" alt="${p.ten}">
    </div>
    <div class="col-md-6">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
          <li class="breadcrumb-item"><a href="/product-list.html">Sản phẩm</a></li>
          <li class="breadcrumb-item active">${p.loaiSanPham || "Sản phẩm"}</li>
        </ol>
      </nav>
      <h1 class="fw-bold mb-2">${p.ten}</h1>
      <p class="fs-4 text-orange fw-bold mb-4">${ui.formatCurrency(p.gia)}</p>

      <div class="card border-0 bg-light p-4 mb-4" style="border-radius: 20px;">
        <h6 class="fw-bold mb-2">Mô tả</h6>
        <p class="text-muted mb-0">${p.moTa || "Đang cập nhật"}</p>
      </div>

      <div class="d-flex gap-3">
        <button class="btn btn-primary btn-lg flex-grow-1" data-action="add-cart">Thêm vào giỏ hàng</button>
        <button class="btn btn-outline-primary btn-lg" data-action="open-builder">Tùy chỉnh ✨</button>
      </div>
      <div class="text-muted small mt-3">
        Tip: Bạn có thể tùy chỉnh công thức và lượng nguyên liệu trong “Tùy chỉnh”.
      </div>
    </div>
  `;
}

async function loadProduct(productId) {
  const host = document.getElementById("product-detail-container");
  if (host) {
    host.innerHTML = `
      <div class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>
    `;
  }

  const p = await api.getWithFallback(API.products.detail(productId), API.products.fallbackDetail(productId));
  state.product = p;
  renderProduct(p);
}

async function loadFormulas() {
  try {
    const list = await api.getWithFallback(API.formulas.list, API.formulas.fallbackList);
    state.formulas = Array.isArray(list) ? list : [];
  } catch (e) {
    state.formulas = [];
  }
}

function calcRealtimePrice() {
  const base = Number(state.product?.gia || 0);

  // Tính đơn giản: base + phụ thu theo nguyên liệu tăng thêm (0..2x soLuong)
  // Nếu backend cung cấp đơn giá nguyên liệu thì có thể thay ngay; hiện UI vẫn chạy.
  let extra = 0;
  state.ingredientAmounts.forEach((multiplier) => {
    // multiplier 0..2, mặc định 1 là giữ nguyên => phụ thu theo phần tăng thêm
    extra += Math.max(0, multiplier - 1) * 2000; // 2.000đ mỗi "bậc" tăng (placeholder)
  });

  return base + extra;
}

function renderBuilder(formula) {
  const list = document.getElementById("formula-list");
  const priceEl = document.getElementById("realtime-price");
  const summaryEl = document.getElementById("custom-summary");
  if (!list || !priceEl || !summaryEl) return;

  const ingredients = formula?.danhSachNguyenLieu || formula?.nguyenLieu || [];

  list.innerHTML = `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div>
        <div class="fw-bold">Công thức</div>
        <div class="text-muted small">${formula?.ten || "Tùy chỉnh"}</div>
      </div>
      <select id="formula-select" class="form-select form-select-sm" style="width: 220px;">
        ${state.formulas
          .map((f) => `<option value="${f.id}" ${f.id === formula?.id ? "selected" : ""}>${f.ten}</option>`)
          .join("")}
      </select>
    </div>
  `;

  if (!Array.isArray(ingredients) || ingredients.length === 0) {
    list.innerHTML += `<p class="text-muted mb-0">Công thức chưa có nguyên liệu.</p>`;
  } else {
    list.innerHTML += ingredients
      .map((it, idx) => {
        const name = it.tenNguyenLieu || it.ten || `Nguyên liệu ${idx + 1}`;
        const unit = it.donViTinh || it.donVi || "";
        const baseQty = Number(it.soLuong || it.luong || 1);

        const key = name;
        if (!state.ingredientAmounts.has(key)) state.ingredientAmounts.set(key, 1);

        const current = state.ingredientAmounts.get(key);

        return `
          <div class="mb-3">
            <div class="d-flex justify-content-between">
              <div class="fw-semibold">${name}</div>
              <div class="text-muted small">
                <span data-qty-label="${encodeURIComponent(key)}">${(baseQty * current).toFixed(1)} ${unit}</span>
              </div>
            </div>
            <input
              class="form-range ingredient-slider"
              type="range"
              min="0"
              max="2"
              step="0.25"
              value="${current}"
              data-ingredient="${encodeURIComponent(key)}"
              data-baseqty="${baseQty}"
              data-unit="${unit}"
            />
          </div>
        `;
      })
      .join("");
  }

  const price = calcRealtimePrice();
  priceEl.textContent = ui.formatCurrency(price);
  summaryEl.textContent = `Base: ${ui.formatCurrency(state.product?.gia || 0)} • Extra: ${ui.formatCurrency(
    Math.max(0, price - Number(state.product?.gia || 0))
  )}`;

  document.getElementById("formula-select")?.addEventListener("change", (e) => {
    const id = e.target.value;
    const f = state.formulas.find((x) => String(x.id) === String(id));
    if (f) {
      state.selectedFormula = f;
      state.ingredientAmounts.clear();
      renderBuilder(f);
    }
  });
}

async function openBuilder() {
  if (!state.product) return;
  if (!auth.isAuthenticated()) {
    ui.toast("Vui lòng đăng nhập để tùy chỉnh", "warning");
    setTimeout(() => (window.location.href = "/login.html"), 700);
    return;
  }

  if (state.formulas.length === 0) {
    await loadFormulas();
  }

  const formula = state.formulas[0] || { ten: "Tùy chỉnh", danhSachNguyenLieu: [] };
  state.selectedFormula = formula;
  state.ingredientAmounts.clear();
  renderBuilder(formula);

  const modalEl = document.getElementById("customDrinkModal");
  const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
  modal.show();
}

async function addToCart({ tuyChinh = null } = {}) {
  if (!state.product) return;
  if (!auth.isAuthenticated()) {
    ui.toast("Vui lòng đăng nhập để mua hàng", "warning");
    setTimeout(() => (window.location.href = "/login.html"), 700);
    return;
  }

  try {
    await api.postWithFallback(API.cart.add, API.cart.fallbackAdd, {
      sanPhamId: state.product.id,
      soLuong: 1,
      tuyChinh,
    });
    ui.toast("Đã thêm vào giỏ hàng!");
  } catch (e) {
    ui.toast(e.message || "Thêm giỏ hàng thất bại", "danger");
  }
}

function bindBuilderEvents() {
  document.addEventListener("input", (e) => {
    const slider = e.target.closest("input[type=range][data-ingredient]");
    if (!slider) return;

    const key = decodeURIComponent(slider.getAttribute("data-ingredient"));
    const baseQty = Number(slider.getAttribute("data-baseqty") || 1);
    const unit = slider.getAttribute("data-unit") || "";
    const val = Number(slider.value);

    state.ingredientAmounts.set(key, val);

    const label = document.querySelector(`[data-qty-label="${encodeURIComponent(key)}"]`);
    if (label) label.textContent = `${(baseQty * val).toFixed(1)} ${unit}`.trim();

    const priceEl = document.getElementById("realtime-price");
    const summaryEl = document.getElementById("custom-summary");
    if (priceEl) priceEl.textContent = ui.formatCurrency(calcRealtimePrice());
    if (summaryEl) {
      const price = calcRealtimePrice();
      summaryEl.textContent = `Base: ${ui.formatCurrency(state.product?.gia || 0)} • Extra: ${ui.formatCurrency(
        Math.max(0, price - Number(state.product?.gia || 0))
      )}`;
    }
  });

  document.getElementById("confirm-custom-add")?.addEventListener("click", async () => {
    const note = document.getElementById("custom-note")?.value || "";

    // gửi theo DTO backend đang có: TuyChinhRequest { congThucId, tuyChinh }
    await addToCart({
      tuyChinh: {
        congThucId: state.selectedFormula?.id || null,
        tuyChinh: note,
      },
    });

    const modalEl = document.getElementById("customDrinkModal");
    bootstrap.Modal.getOrCreateInstance(modalEl).hide();
  });
}

document.addEventListener("DOMContentLoaded", async () => {
  mountLayout({ activeNav: "products" });

  const productId = getProductId();
  if (!productId) {
    window.location.href = "/product-list.html";
    return;
  }

  await loadProduct(productId);
  await loadFormulas();
  bindBuilderEvents();

  document.addEventListener("click", (e) => {
    const btn = e.target.closest("[data-action]");
    if (!btn) return;
    const action = btn.getAttribute("data-action");
    if (action === "add-cart") addToCart();
    if (action === "open-builder") openBuilder();
  });
});

