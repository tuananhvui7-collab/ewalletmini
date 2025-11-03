<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Mini E-Wallet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <script src="assets/js/main.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">

    <script>
        checkAdminAuth(); // Bảo vệ trang này
    </script>
</head>
<body class="bg-light">

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
        <div class="container">
            <a class="navbar-brand" href="admin_dashboard.jsp">
                <i class="bi bi-shield-lock"></i> Admin Panel
            </a>
            <div class="collapse navbar-collapse">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item"><a class="nav-link active" href="admin_dashboard.jsp">Báo cáo</a></li>
                    <li class="nav-item"><a class="nav-link" href="admin_merchants.jsp">Quản lý Merchant</a></li>
                    <li class="nav-item"><a class="nav-link" href="admin_management.jsp">Quản lý Admin</a></li>
                </ul>
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="#" id="logoutButton">Đăng xuất</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <h3 class="mb-3">Báo cáo nhanh</h3>
        
        <div class="row">
            <div class="col-md-4">
                <div class="card text-white bg-primary mb-3">
                    <div class="card-body">
                        <h5 class="card-title">Tổng giao dịch</h5>
                        <p class="card-text fs-3" id="totalTxn">...</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-white bg-success mb-3">
                    <div class="card-body">
                        <h5 class="card-title">Tổng số tiền (VND)</h5>
                        <p class="card-text fs-3" id="totalAmount">...</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-white bg-info mb-3">
                    <div class="card-body">
                        <h5 class="card-title">Tổng người dùng</h5>
                        <p class="card-text fs-3" id="totalUsers">...</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener("DOMContentLoaded", async () => {
            // Lấy báo cáo
            try {
                const result = await apiFetch("/api/admin/reports?type=dashboard"); //
                if(result.success) {
                    document.getElementById("totalTxn").textContent = result.data.totalTransactions;
                    document.getElementById("totalAmount").textContent = formatCurrency(result.data.totalAmount);
                    document.getElementById("totalUsers").textContent = result.data.totalUsers;
                }
            } catch (e) {
                console.error("Lỗi tải báo cáo", e);
            }
        });
        
        document.getElementById("logoutButton").addEventListener("click", logout);
    </script>
</body>
</html>