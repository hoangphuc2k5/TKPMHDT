export function renderFooter() {
  return `
  <footer class="py-5 bg-white border-top mt-5">
    <div class="container">
      <div class="row">
        <div class="col-lg-4 mb-4">
          <h4 class="fw-bold mb-3">DRINK<span style="color: var(--primary-blue);">STORE</span></h4>
          <p class="text-muted mb-0">Fresh • Clean • Modern. Đồ uống tươi mát cho mỗi ngày.</p>
        </div>
        <div class="col-lg-2 col-md-4 mb-4">
          <h6 class="fw-bold mb-3">Liên kết</h6>
          <ul class="list-unstyled">
            <li><a href="/" class="text-muted text-decoration-none">Trang chủ</a></li>
            <li><a href="/product-list.html" class="text-muted text-decoration-none">Sản phẩm</a></li>
            <li><a href="/product-list.html#promo" class="text-muted text-decoration-none">Khuyến mãi</a></li>
          </ul>
        </div>
        <div class="col-lg-3 col-md-4 mb-4">
          <h6 class="fw-bold mb-3">Liên hệ</h6>
          <ul class="list-unstyled text-muted mb-0">
            <li>📍 123 Đường ABC, Quận 1, TP.HCM</li>
            <li>📞 1900 1234</li>
            <li>✉️ contact@drinkstore.com</li>
          </ul>
        </div>
        <div class="col-lg-3 col-md-4 mb-4">
          <h6 class="fw-bold mb-3">Theo dõi</h6>
          <div class="d-flex gap-3">
            <a href="#" class="fs-5 text-primary text-decoration-none">FB</a>
            <a href="#" class="fs-5 text-danger text-decoration-none">IG</a>
            <a href="#" class="fs-5 text-info text-decoration-none">TW</a>
          </div>
        </div>
      </div>
      <hr>
      <div class="text-center text-muted small">
        © 2026 Drink Store
      </div>
    </div>
  </footer>
  `;
}

