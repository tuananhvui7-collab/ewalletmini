<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String activePage = request.getParameter("activePage"); %>
    

<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand" href="admin_dashboard.jsp">
            <i class="bi bi-shield-lock"></i> Admin Panel
        </a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link <%= "dashboard".equals(activePage) ? "active" : "" %>" href="admin_dashboard.jsp">Báo cáo</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= "merchants".equals(activePage) ? "active" : "" %>" href="admin_merchants.jsp">Quản lý Merchant</a>
                </li>
                 <li class="nav-item">
                    <a class="nav-link <%= "cards".equals(activePage) ? "active" : "" %>" href="admin_create_cards.jsp">Tạo thẻ cào</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= "admins".equals(activePage) ? "active" : "" %>" href="admin_management.jsp">Quản lý Admin</a>
                </li>
            </ul>
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link" href="#" id="adminLogoutButton">Đăng xuất</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<script>
// Gắn sự kiện logout cho admin
document.addEventListener("DOMContentLoaded", () => {
    const logoutBtn = document.getElementById("adminLogoutButton");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    }
});
</script>