<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Admin - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <script src="assets/js/main.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">

    <script> checkAdminAuth(); </script>
</head>
<body class="bg-light">

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
        <div class="container">
            <a class="navbar-brand" href="admin_dashboard.jsp"><i class="bi bi-shield-lock"></i> Admin Panel</a>
            <div class="collapse navbar-collapse">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item"><a class="nav-link" href="admin_dashboard.jsp">Báo cáo</a></li>
                    <li class="nav-item"><a class="nav-link" href="admin_merchants.jsp">Quản lý Merchant</a></li>
                    <li class="nav-item"><a class="nav-link active" href="admin_management.jsp">Quản lý Admin</a></li>
                </ul>
                <ul class="navbar-nav ms-auto"><li class="nav-item"><a class="nav-link" href="#" id="logoutButton">Đăng xuất</a></li></ul>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h4 class="card-title">Thêm Tài Khoản Admin Mới</h4>
                        <form id="createAdminForm">
                            <div class="mb-3">
                                <label for="adminUser" class="form-label">Username</label>
                                <input type="text" class="form-control" id="adminUser" required>
                            </div>
                            <div class="mb-3">
                                <label for="adminPass" class="form-label">Mật khẩu (ít nhất 6 ký tự)</label>
                                <input type="password" class="form-control" id="adminPass" required>
                            </div>
                            <div class="mb-3">
                                <label for="adminName" class="form-label">Họ và tên</label>
                                <input type="text" class="form-control" id="adminName" required>
                            </div>
                            <div id="createMsg" class="alert d-none" role="alert"></div>
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary" id="createBtn">Tạo Admin</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const createForm = document.getElementById("createAdminForm");
        const createBtn = document.getElementById("createBtn");
        const createMsg = document.getElementById("createMsg");

        createForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            createBtn.disabled = true;
            createMsg.className = "alert d-none";

            try {
                const result = await apiFetch("/api/admin/management/create", { //
                    method: "POST",
                    body: JSON.stringify({
                        username: document.getElementById("adminUser").value,
                        password: document.getElementById("adminPass").value,
                        fullName: document.getElementById("adminName").value
                    })
                });

                if (result.success) {
                    createMsg.className = "alert alert-success";
                    createMsg.textContent = `Tạo admin '${result.data.username}' thành công!`;
                    createForm.reset();
                } else {
                    createMsg.className = "alert alert-danger";
                    createMsg.textContent = result.message;
                }
            } catch (err) {
                createMsg.className = "alert alert-danger";
                createMsg.textContent = "Lỗi kết nối.";
            }
            createBtn.disabled = false;
        });

        document.getElementById("logoutButton").addEventListener("click", logout);
    </script>
</body>
</html>