import { ui } from "../ui.js";
import { auth } from "../auth.js";
import { API } from "../config.js";

async function login(tenDangNhap, matKhau) {
  // Auth endpoint theo backend hiện tại: /api/v1/auth/login
  const res = await fetch(`${API.fallbackBase}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ tenDangNhap, matKhau }),
  });

  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.error || data.message || "Đăng nhập thất bại");
  return data;
}

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("login-form");
  form?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const tenDangNhap = document.getElementById("username")?.value || "";
    const matKhau = document.getElementById("password")?.value || "";

    try {
      const data = await login(tenDangNhap, matKhau);
      auth.saveSession({ token: data.token, role: data.vaiTro, username: data.tenDangNhap });
      ui.toast("Đăng nhập thành công!");

      setTimeout(() => {
        if (data.vaiTro === "ADMIN") window.location.href = "/admin/index.html";
        else if (data.vaiTro === "NHAN_VIEN_BAN_HANG") window.location.href = "/staff/index.html";
        else if (data.vaiTro === "QUAN_LY_KHO") window.location.href = "/warehouse/index.html";
        else window.location.href = "/";
      }, 600);
    } catch (err) {
      ui.toast(err.message || "Lỗi hệ thống", "danger");
    }
  });
});

