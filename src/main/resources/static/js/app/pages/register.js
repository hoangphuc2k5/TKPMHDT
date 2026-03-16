import { ui } from "../ui.js";
import { auth } from "../auth.js";
import { API } from "../config.js";

async function register(payload) {
  const res = await fetch(`${API.fallbackBase}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.error || data.message || "Đăng ký thất bại");
  return data;
}

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("register-form");
  form?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const payload = {
      tenDangNhap: document.getElementById("username")?.value || "",
      matKhau: document.getElementById("password")?.value || "",
      hoTen: document.getElementById("fullname")?.value || "",
      email: document.getElementById("email")?.value || "",
      soDienThoai: document.getElementById("phone")?.value || "",
      diaChi: document.getElementById("address")?.value || "",
    };

    try {
      const data = await register(payload);
      auth.saveSession({ token: data.token, role: data.vaiTro, username: data.tenDangNhap });
      ui.toast("Đăng ký thành công!");
      setTimeout(() => (window.location.href = "/"), 700);
    } catch (err) {
      ui.toast(err.message || "Lỗi hệ thống", "danger");
    }
  });
});

