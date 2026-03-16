import { API } from "./config.js";
import { auth } from "./auth.js";

async function requestJson(url, options = {}) {
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };

  const token = auth.getToken();
  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(url, {
    ...options,
    headers,
  });

  if (res.status === 204) return null;
  const contentType = res.headers.get("content-type") || "";
  const isJson = contentType.includes("application/json");
  const data = isJson ? await res.json().catch(() => null) : await res.text().catch(() => null);

  if (!res.ok) {
    const message =
      (data && (data.message || data.error)) ||
      (typeof data === "string" && data) ||
      `HTTP ${res.status}`;
    const err = new Error(message);
    err.status = res.status;
    err.data = data;
    throw err;
  }

  return data;
}

export const api = {
  // gọi base chính trước, nếu 404 thì fallback
  getWithFallback: async (primaryPath, fallbackPath) => {
    try {
      return await requestJson(`${API.primaryBase}${primaryPath}`);
    } catch (e) {
      if (e && e.status === 404) {
        return await requestJson(`${API.fallbackBase}${fallbackPath}`);
      }
      throw e;
    }
  },

  postWithFallback: async (primaryPath, fallbackPath, body) => {
    try {
      return await requestJson(`${API.primaryBase}${primaryPath}`, {
        method: "POST",
        body: JSON.stringify(body ?? {}),
      });
    } catch (e) {
      if (e && e.status === 404) {
        return await requestJson(`${API.fallbackBase}${fallbackPath}`, {
          method: "POST",
          body: JSON.stringify(body ?? {}),
        });
      }
      throw e;
    }
  },

  putWithFallback: async (primaryPath, fallbackPath, body) => {
    try {
      return await requestJson(`${API.primaryBase}${primaryPath}`, {
        method: "PUT",
        body: JSON.stringify(body ?? {}),
      });
    } catch (e) {
      if (e && e.status === 404) {
        return await requestJson(`${API.fallbackBase}${fallbackPath}`, {
          method: "PUT",
          body: JSON.stringify(body ?? {}),
        });
      }
      throw e;
    }
  },

  deleteWithFallback: async (primaryPath, fallbackPath) => {
    try {
      return await requestJson(`${API.primaryBase}${primaryPath}`, {
        method: "DELETE",
      });
    } catch (e) {
      if (e && e.status === 404) {
        return await requestJson(`${API.fallbackBase}${fallbackPath}`, {
          method: "DELETE",
        });
      }
      throw e;
    }
  },
};
