const BASE_URL = '/api/v1';

// JWT & Auth Utilities
const auth = {
    saveToken: (token, role) => {
        localStorage.setItem('token', token);
        localStorage.setItem('role', role);
    },
    getToken: () => localStorage.getItem('token'),
    getRole: () => localStorage.getItem('role'),
    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        window.location.href = '/login.html';
    },
    isAuthenticated: () => !!localStorage.getItem('token'),
    getHeaders: () => {
        const token = localStorage.getItem('token');
        return {
            'Content-Type': 'application/json',
            'Authorization': token ? `Bearer ${token}` : ''
        };
    }
};

// Generic Fetch API wrapper
async function apiFetch(endpoint, options = {}) {
    const url = `${BASE_URL}${endpoint}`;
    const headers = auth.getHeaders();
    
    const config = {
        ...options,
        headers: {
            ...headers,
            ...options.headers
        }
    };

    try {
        const response = await fetch(url, config);
        
        if (response.status === 401) {
            auth.logout();
            return;
        }

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Có lỗi xảy ra!');
        }

        // Return null for 204 No Content or empty responses
        if (response.status === 204 || response.headers.get('content-length') === '0') {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// UI Utilities
const ui = {
    showToast: (message, type = 'success') => {
        const toastContainer = document.getElementById('toast-container');
        if (!toastContainer) {
            const container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container';
            document.body.appendChild(container);
        }
        
        const toast = document.createElement('div');
        toast.className = `alert alert-${type} alert-dismissible fade show shadow-sm`;
        toast.role = 'alert';
        toast.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.getElementById('toast-container').appendChild(toast);
        
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 500);
        }, 3000);
    },
    formatCurrency: (amount) => {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
    }
};

// Global Auth Check for protected pages
function checkAccess(requiredRole = null) {
    if (!auth.isAuthenticated()) {
        window.location.href = '/login.html';
        return false;
    }
    
    if (requiredRole && auth.getRole() !== requiredRole) {
        window.location.href = '/index.html';
        return false;
    }
    return true;
}
