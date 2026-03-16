export const ui = {
  formatCurrency: (amount) =>
    new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
      Number(amount || 0)
    ),

  ensureToastHost: () => {
    let host = document.getElementById("toast-host");
    if (host) return host;
    host = document.createElement("div");
    host.id = "toast-host";
    host.className = "toast-container";
    document.body.appendChild(host);
    return host;
  },

  toast: (message, type = "success") => {
    const host = ui.ensureToastHost();
    const el = document.createElement("div");
    el.className = `alert alert-${type} alert-dismissible fade show shadow-sm`;
    el.role = "alert";
    el.innerHTML = `
      ${message}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    host.appendChild(el);
    setTimeout(() => {
      el.classList.remove("show");
      setTimeout(() => el.remove(), 250);
    }, 3000);
  },

  setText: (selector, text) => {
    const el = document.querySelector(selector);
    if (el) el.textContent = text;
  },
};

