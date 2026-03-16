document.addEventListener('DOMContentLoaded', () => {
    // Global Cart Count Update
    async function updateGlobalCartCount() {
        const cartCountBadge = document.getElementById('cart-count');
        if (cartCountBadge && auth.isAuthenticated()) {
            try {
                const cart = await apiFetch('/cart');
                const count = cart.items ? cart.items.length : 0;
                cartCountBadge.innerText = count;
                cartCountBadge.style.display = count > 0 ? 'inline' : 'none';
            } catch (error) {
                console.error('Error updating cart count:', error);
            }
        }
    }

    if (auth.isAuthenticated()) {
        updateGlobalCartCount();
    }

    // Export for use in other scripts
    window.updateGlobalCartCount = updateGlobalCartCount;
});
