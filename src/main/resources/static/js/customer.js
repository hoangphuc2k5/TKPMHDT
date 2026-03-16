document.addEventListener('DOMContentLoaded', () => {
    if (!checkAccess('KHACH_HANG')) return;

    const productList = document.getElementById('productList');
    const filterBtn = document.getElementById('filterBtn');
    const searchName = document.getElementById('searchName');
    const maxPrice = document.getElementById('maxPrice');
    const cartBtn = document.getElementById('cartBtn');

    if (cartBtn) {
        cartBtn.addEventListener('click', (e) => {
            e.preventDefault();
            window.location.href = '/cart.html';
        });
    }

    async function fetchProducts() {
        const query = (searchName?.value || '').trim();
        const max = maxPrice?.value || '';

        try {
            const products = await apiFetch(`/products/search?query=${encodeURIComponent(query)}&giaMax=${encodeURIComponent(max)}`);
            renderProducts(Array.isArray(products) ? products : []);
        } catch (error) {
            ui.showToast(error.message || 'Không tải được sản phẩm', 'danger');
        }
    }

    function renderProducts(products) {
        if (!productList) return;

        productList.innerHTML = '';
        products.forEach((p) => {
            const card = `
                <div class="col-md-4 mb-4">
                    <div class="card h-100 shadow-sm">
                        <img src="${p.hinhAnh || 'https://via.placeholder.com/400x300'}" class="card-img-top" alt="${p.ten}">
                        <div class="card-body">
                            <h5 class="card-title">${p.ten}</h5>
                            <p class="card-text text-danger fw-bold">${ui.formatCurrency(p.gia || 0)}</p>
                            <button class="btn btn-outline-primary btn-sm" data-action="detail" data-id="${p.id}">Chi tiết</button>
                            <button class="btn btn-primary btn-sm" data-action="add" data-id="${p.id}">Mua ngay</button>
                        </div>
                    </div>
                </div>
            `;
            productList.insertAdjacentHTML('beforeend', card);
        });
    }

    async function showDetail(id) {
        try {
            const p = await apiFetch(`/products/${id}`);

            document.getElementById('productTitle').innerText = p.ten;
            document.getElementById('productDetail').innerHTML = `
                <p><strong>Mô tả:</strong> ${p.moTa || 'Đang cập nhật'}</p>
                <p><strong>Giá:</strong> ${ui.formatCurrency(p.gia || 0)}</p>
                ${p.loaiSanPham === 'NUOC_UONG_SAN' ? `<p><strong>Loại:</strong> ${p.loaiNuoc || ''} ${p.dungTich ? `(${p.dungTich})` : ''}</p>` : ''}
            `;

            const modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('productModal'));
            modal.show();
        } catch (error) {
            ui.showToast(error.message || 'Không tải được chi tiết sản phẩm', 'danger');
        }
    }

    async function addToCart(id) {
        try {
            await apiFetch('/cart/add', {
                method: 'POST',
                body: JSON.stringify({ sanPhamId: id, soLuong: 1 })
            });

            ui.showToast('Đã thêm vào giỏ hàng!');
            if (window.updateGlobalCartCount) window.updateGlobalCartCount();
        } catch (error) {
            ui.showToast(error.message || 'Thêm giỏ hàng thất bại', 'danger');
        }
    }

    productList?.addEventListener('click', (e) => {
        const btn = e.target.closest('button[data-action]');
        if (!btn) return;

        const action = btn.getAttribute('data-action');
        const id = btn.getAttribute('data-id');
        if (!id) return;

        if (action === 'detail') showDetail(id);
        if (action === 'add') addToCart(id);
    });

    filterBtn?.addEventListener('click', fetchProducts);
    fetchProducts();
});
