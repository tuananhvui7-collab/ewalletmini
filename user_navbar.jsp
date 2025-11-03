<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String activePage = request.getParameter("activePage"); %>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container">
        <a class="navbar-brand" href="dashboard.jsp">
            <i class="bi bi-wallet2"></i> Mini E-Wallet
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link <%= "dashboard".equals(activePage) ? "active" : "" %>" href="dashboard.jsp">Tổng quan</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= "transfer".equals(activePage) ? "active" : "" %>" href="transfer.jsp">Chuyển tiền</a>
                </li>
                 <li class="nav-item">
                    <a class="nav-link <%= "payment".equals(activePage) ? "active" : "" %>" href="payment_qr.jsp">Thanh toán QR</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= "topup".equals(activePage) ? "active" : "" %>" href="topup.jsp">Nạp tiền</a>
                </li>
            </ul>
            <ul class="navbar-nav ms-auto">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                        Chào, <span id="navUserName">...</span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="change_password.jsp">Đổi mật khẩu</a></li>
                        <li><a class="dropdown-item" href="change_pin.jsp">Đổi mã PIN</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#" id="logoutButton">Đăng xuất</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>

<script>
// Script này cần nằm trong file navbar vì nó được dùng trên mọi trang
document.addEventListener("DOMContentLoaded", () => {
    const user = getUser();
    if (user) {
        document.getElementById("navUserName").textContent = user.fullName;
    }
    
    // Gắn sự kiện logout vào nút
    const logoutBtn = document.getElementById("logoutButton");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    }
});
</script>