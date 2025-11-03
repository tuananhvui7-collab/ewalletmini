<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login - Mini E-Wallet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/css/main.css" rel="stylesheet">
    <script src="assets/js/main.js"></script>
</head>
<body class="bg-dark">

    <div class="auth-wrapper">
        <div class="card auth-card shadow-sm">
            <div class="card-body p-4">
                <h3 class="card-title text-center mb-4">Admin Login</h3>
                
                <form id="adminLoginForm">
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control" id="username" required>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" required>
                    </div>
                    
                    <div id="errorMessage" class="alert alert-danger d-none" role="alert"></div>
                    
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary" id="loginButton">Đăng nhập</button>
                    </div>
                </form>
                
                <div class="text-center mt-3">
                    <a href="login.jsp">Quay lại trang User</a>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.getElementById("adminLoginForm").addEventListener("submit", async function(event) {
            event.preventDefault(); 
            
            const username = document.getElementById("username").value;
            const pass = document.getElementById("password").value;
            const errorDiv = document.getElementById("errorMessage");
            const loginButton = document.getElementById("loginButton");

            loginButton.disabled = true;
            loginButton.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang xử lý...';
            errorDiv.classList.add("d-none");

            try {
                // Gọi đến Servlet MỚI của Admin
                const result = await apiFetch("/api/admin/login", { 
                    method: "POST",
                    body: JSON.stringify({
                        username: username,
                        password: pass
                    })
                });

                if (result.success) {
                    // ĐĂNG NHẬP THÀNH CÔNG
                    saveToken(result.data.token);
                    saveUser({
                        fullName: result.data.fullName,
                        role: result.data.role
                    });
                    
                    // Đây là "cái để dẫn đến admin dashboard"
                    window.location.href = "admin_dashboard.jsp"; 
                } else {
                    errorDiv.textContent = result.message; 
                    errorDiv.classList.remove("d-none");
                    loginButton.disabled = false;
                    loginButton.innerHTML = 'Đăng nhập';
                }
            } catch (err) {
                console.error("Lỗi:", err);
                errorDiv.textContent = "Có lỗi xảy ra, vui lòng thử lại.";
                errorDiv.classList.remove("d-none");
                loginButton.disabled = false;
                loginButton.innerHTML = 'Đăng nhập';
            }
        });
    </script>
</body>
</html>