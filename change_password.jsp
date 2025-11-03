<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đổi mật khẩu - Mini E-Wallet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <script src="assets/js/main.js"></script>
        <link rel="stylesheet" href="assets/css/main.css">

    <script> checkAuth(); </script>
</head>
<body class="bg-light">

    <jsp:include page="user_navbar.jsp">
        <jsp:param name="activePage" value="profile" />
    </jsp:include>

    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow-sm border-0">
                    <div class="card-body p-4">
                        <h3 class="card-title text-center mb-4">Đổi Mật Khẩu</h3>
                        
                        <form id="changePassForm">
                            <div class="mb-3">
                                <label for="oldPassword" class="form-label">Mật khẩu cũ</label>
                                <input type="password" class="form-control" id="oldPassword" required>
                            </div>
                            <div class="mb-3">
                                <label for="newPassword" class="form-label">Mật khẩu mới</label>
                                <input type="password" class="form-control" id="newPassword" required>
                            </div>
                            <div class="mb-3">
                                <label for="confirmNewPassword" class="form-label">Xác nhận mật khẩu mới</label>
                                <input type="password" class="form-control" id="confirmNewPassword" required>
                            </div>
                            
                            <div id="formMessage" class="alert d-none" role="alert"></div>
                            
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary" id="submitBtn">Lưu thay đổi</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById("changePassForm").addEventListener("submit", async (event) => {
            event.preventDefault();
            
            const button = document.getElementById("submitBtn");
            const messageDiv = document.getElementById("formMessage");
            
            const newPass = document.getElementById("newPassword").value;
            const confirmPass = document.getElementById("confirmNewPassword").value;

            if (newPass !== confirmPass) {
                messageDiv.className = "alert alert-danger";
                messageDiv.textContent = "Mật khẩu mới không khớp.";
                return;
            }

            button.disabled = true;
            button.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang lưu...';
            messageDiv.className = "alert d-none"; 

            try {
                //
                const result = await apiFetch("/api/user/change-password", {
                    method: "POST",
                    body: JSON.stringify({
                        oldPassword: document.getElementById("oldPassword").value,
                        newPassword: newPass
                    })
                });

                if (result.success) {
                    messageDiv.className = "alert alert-success";
                    messageDiv.textContent = "Đổi mật khẩu thành công!";
                    document.getElementById("changePassForm").reset();
                } else {
                    messageDiv.className = "alert alert-danger";
                    messageDiv.textContent = result.message;
                }
            } catch (err) {
                messageDiv.className = "alert alert-danger";
                messageDiv.textContent = "Lỗi kết nối.";
            }
            button.disabled = false;
            button.innerHTML = 'Lưu thay đổi';
        });
    </script>
</body>
</html>