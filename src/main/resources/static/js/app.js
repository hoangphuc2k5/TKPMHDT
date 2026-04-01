async function api(method, url, body) {
    const options = {
        method: method,
        headers: { "Content-Type": "application/json" }
    };
    if (body !== undefined && body !== null) {
        options.body = JSON.stringify(body);
    }
    const response = await fetch(url, options);
    const text = await response.text();
    let data = text;
    try {
        data = text ? JSON.parse(text) : {};
    } catch (e) {
        // keep raw text
    }
    if (!response.ok) {
        throw new Error(typeof data === "string" ? data : JSON.stringify(data));
    }
    return data;
}

async function apiUpload(url, formData) {
    const response = await fetch(url, {
        method: "POST",
        body: formData
    });
    const text = await response.text();
    let data = text;
    try {
        data = text ? JSON.parse(text) : {};
    } catch (e) {
        // keep raw text
    }
    if (!response.ok) {
        throw new Error(typeof data === "string" ? data : JSON.stringify(data));
    }
    return data;
}

function isUUID(str) {
    const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
    return uuidRegex.test(str);
}

function showResult(elementId, data) {
    const el = document.getElementById(elementId);
    el.textContent = typeof data === "string" ? data : JSON.stringify(data, null, 2);
}

// Utility functions
function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value || 0);
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

function formatDateTime(dateString) {
    return new Date(dateString).toLocaleString('vi-VN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function showMessage(message, type = 'success', duration = 3000) {
    const messageEl = document.createElement('div');
    messageEl.textContent = message;
    messageEl.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        padding: 1rem 1.5rem;
        border-radius: 8px;
        color: white;
        background: ${type === 'success' ? '#16a34a' : '#e74c3c'};
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 9999;
        animation: slideIn 0.3s ease-in-out;
    `;
    document.body.appendChild(messageEl);
    setTimeout(() => {
        messageEl.style.animation = 'slideOut 0.3s ease-in-out';
        setTimeout(() => messageEl.remove(), 300);
    }, duration);
}

// Add animation styles
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

