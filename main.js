/**
 * File JavaScript chung cho Mini E-Wallet
 */

// Lấy context path (ví dụ: /ViDienTu)
// Cách tốt hơn: Tìm từ <base> tag hoặc meta tag trong HTML
let contextPath = "";
const baseTag = document.querySelector('base');
if (baseTag) {
    contextPath = baseTag.href.replace(window.location.origin, '').replace(/\/$/, '');
} else {
    // Fallback: Tính từ pathname
    const pathArray = window.location.pathname.split('/').filter(x => x);
    if (pathArray.length > 0 && !pathArray[0].includes('.')) {
        contextPath = '/' + pathArray[0];
    }
}
console.log("Context Path:", contextPath);

// --- Quản lý Token & Session ---
function getToken() {
    return localStorage.getItem("eWalletToken");
}

function saveToken(token) {
    localStorage.setItem("eWalletToken", token);
}

/**
 * Lưu thông tin user (gồm role) vào localStorage
 */
function saveUser(user) {
    localStorage.setItem("eWalletUser", JSON.stringify(user));
}

/**
 * Lấy thông tin user (gồm role)
 */
function getUser() {
    try {
        return JSON.parse(localStorage.getItem("eWalletUser"));
    } catch (e) {
        return null;
    }
}

/**
 * Xóa session và chuyển về trang đăng nhập
 */
function logout() {
    localStorage.removeItem("eWalletToken");
    localStorage.removeItem("eWalletUser");
    window.location.href = contextPath + "/login.jsp";
}

// --- Bảo vệ trang (Guards) ---
/**
 * "Guard" - Kiểm tra xem người dùng đã đăng nhập chưa
 */
function checkAuth() {
    if (!getToken()) {
        console.log("No token found, logging out");
        logout();
    }
}

/**
 * "Guard" - Kiểm tra xem có phải ADMIN đã đăng nhập không
 */
function checkAdminAuth() {
    const user = getUser();
    if (!getToken() || !user || (user.role !== 'SUPER_ADMIN' && user.role !== 'OPERATOR')) { 
        logout(); 
    }
}

// --- Hàm gọi API chung ---
/**
 * Hàm chung để gọi API (Fetch API wrapper)
 */
async function apiFetch(endpoint, options = {}) {
    const token = getToken();
    const headers = {
        "Content-Type": "application/json",
        "Accept": "application/json",
        ...options.headers,
    };
    if (token) {
        headers["Authorization"] = "Bearer " + token;
    }
    
    const fullUrl = contextPath + endpoint;
    console.log(`[apiFetch] ${options.method || 'GET'} ${fullUrl}`);
    console.log(`[apiFetch] Token: ${token ? 'YES' : 'NO'}`);
    
    const response = await fetch(fullUrl, {
        ...options,
        headers: headers,
    });
    
    console.log(`[apiFetch] Status: ${response.status}`);
    
    if (response.status === 401) {
        console.log("[apiFetch] 401 Unauthorized, logging out");
        logout();
        return;
    }
    
    if (!response.ok && response.status !== 400) {
        throw new Error("Lỗi máy chủ: " + response.statusText);
    }
    
    const data = await response.json();
    console.log(`[apiFetch] Response:`, data);
    return data;
}

// --- Tiện ích ---
/**
 * Định dạng tiền tệ VNĐ
 */
function formatCurrency(amount) {
    if (typeof amount !== 'number') {
        amount = parseFloat(amount) || 0;
    }
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

/**
 * Format ngày giờ theo locale Việt Nam
 */
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN');
}