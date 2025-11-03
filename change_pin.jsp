<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đổi mã PIN - Mini E-Wallet</title>
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
                        <h3 class="card-title text-center mb-4">Đổi Mã PIN Giao Dịch</h3>
                        
                        <form id="changePinForm">
                            <div class="mb-3">
                                <label for="password" class="form-label">Mật khẩu (để xác thực)</label>
                                <input type="password" class="form-control" id="password" required>
                            </div>
                            <div class="mb-3">
                                <label for="oldPin" class="form-label">Mã PIN cũ (6 số)</label>
                                <input type="password" class="form-control" id="oldPin" required maxlength="6">
                            </div>
                            <div class="mb-3">
                                <label for="newPin" class="form-label">Mã PIN mới (6 số)</label>
                                <input type="password" class="form-control" id="newPin" required maxlength="6">
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
        document.getElementById("changePinForm").addEventListener("submit", async (event) => {
            event.preventDefault();
            
            const button = document.getElementById("submitBtn");
            const messageDiv = document.getElementById("formMessage");
            
            button.disabled = true;
            button.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang lưu...';
            messageDiv.className = "alert d-none"; 

            try {
                //
                const result = await apiFetch("/api/user/change-pin", {
                    method: "POST",
                    body: JSON.stringify({
                        password: document.getElementById("password").value,
                        oldPin: document.getElementById("oldPin").value,
                        newPin: document.getElementById("newPin").value
                    })
                });

                if (result.success) {
                    messageDiv.className = "alert alert-success";
                    messageDiv.textContent = "Đổi PIN thành công!";
                    document.getElementById("changePinForm").reset();
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