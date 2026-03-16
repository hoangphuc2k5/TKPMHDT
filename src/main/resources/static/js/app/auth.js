import { STORAGE_KEYS } from "./config.js";

export const auth = {
  saveSession: ({ token, role, username }) => {
    if (token) localStorage.setItem(STORAGE_KEYS.token, token);
    if (role) localStorage.setItem(STORAGE_KEYS.role, role);
    if (username) localStorage.setItem(STORAGE_KEYS.username, username);
  },

  getToken: () => localStorage.getItem(STORAGE_KEYS.token),
  getRole: () => localStorage.getItem(STORAGE_KEYS.role),
  getUsername: () => localStorage.getItem(STORAGE_KEYS.username),

  isAuthenticated: () => Boolean(localStorage.getItem(STORAGE_KEYS.token)),

  logout: () => {
    localStorage.removeItem(STORAGE_KEYS.token);
    localStorage.removeItem(STORAGE_KEYS.role);
    localStorage.removeItem(STORAGE_KEYS.username);
    window.location.href = "/login.html";
  },

  requireAuth: (requiredRole = null) => {
    if (!auth.isAuthenticated()) {
      window.location.href = "/login.html";
      return false;
    }
    if (requiredRole && auth.getRole() !== requiredRole) {
      window.location.href = "/";
      return false;
    }
    return true;
  },
};

