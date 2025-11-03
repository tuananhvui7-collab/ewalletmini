<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - Mini E-Wallet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="assets/js/main.js"></script>
</head>
<body class="bg-light">

    <div class="container">
        <div class="row justify-content-center align-items-center" style="min-height: 100vh;">
            <div class="col-md-6 col-lg-4">
                <div class="card shadow-sm">
                    <div class="card-body p-4">
                        <h3 class="card-title text-center mb-4">Đăng Nhập E-Wallet</h3>
                        
                        <form id="loginForm">
                            <div class="mb-3">
                                <label for="phoneNumber" class="form-label">Số điện thoại</label>
                                <input type="tel" class="form-control" id="phoneNumber" required>
                            </div>
                            <div class="mb-3">
                                <label for="password" class="form-label">Mật khẩu</label>
                                <input type="password" class="form-control" id="password" required>
                            </div>
                            
                            <div id="errorMessage" class="alert alert-danger d-none" role="alert"></div>
                            
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary" id="loginButton">Đăng nhập</button>
                            </div>
                        </form>
                        
                        <div class="text-center mt-3">
                            <a href="register.jsp">Tạo tài khoản mới</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.getElementById("loginForm").addEventListener("submit", async function(event) {
            event.preventDefault(); 
            
            const phone = document.getElementById("phoneNumber").value;
            const pass = document.getElementById("password").value;
            const errorDiv = document.getElementById("errorMessage");
            const loginButton = document.getElementById("loginButton");

            // Vô hiệu hóa nút bấm, hiển thị loading
            loginButton.disabled = true;
            loginButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Đang xử lý...';
            errorDiv.classList.add("d-none"); // Ẩn lỗi cũ

            try {
                const result = await apiFetch("/api/auth/login", { //
                    method: "POST",
                    body: JSON.stringify({
                        phoneNumber: phone,
                        password: pass
                    })
                });

                if (result.success) {
                    // ĐĂNG NHẬP THÀNH CÔNG
                    saveToken(result.data.token);
                    // LƯU CẢ ROLE VÀ FULLNAME
                    saveUser({
                        fullName: result.data.fullName,
                        role: result.data.role
                    }); 
                    
                    // PHÂN LUỒNG ĐIỀU HƯỚNG
                    if (result.data.role === 'ADMIN') {
                        window.location.href = contextPath + "/admin_dashboard.jsp";
                    } else {
                        window.location.href = contextPath + "/dashboard.jsp";
                    }
                } else {
                    // ĐĂNG NHẬP THẤT BẠI
                    errorDiv.textContent = result.message; // Hiển thị lỗi từ server
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