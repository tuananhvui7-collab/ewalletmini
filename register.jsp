<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - Mini E-Wallet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="assets/js/main.js"></script>
        <link rel="stylesheet" href="assets/css/main.css">

</head>
<body class="bg-light">

    <div class="container">
        <div class="row justify-content-center align-items-center" style="min-height: 100vh;">
            <div class="col-md-6 col-lg-5">
                <div class="card shadow-sm">
                    <div class="card-body p-4">
                        <h3 class="card-title text-center mb-4">Tạo Tài Khoản Mới</h3>
                        
                        <form id="registerForm">
                            <div id="step1">
                                <div class="mb-3">
                                    <label for="regPhone" class="form-label">Số điện thoại</label>
                                    <input type="tel" class="form-control" id="regPhone" required>
                                </div>
                                <div class="d-grid">
                                    <button type="button" class="btn btn-secondary" id="requestOtpButton">Gửi mã OTP</button>
                                </div>
                                <div id="otpError" class="alert alert-danger d-none mt-3" role="alert"></div>
                            </div>

                            <div id="step2" class="d-none">
                                <p>Mã OTP đã được gửi đến <strong id="phoneDisplay"></strong>.</p>
                                <div class="mb-3">
                                    <label for="regOtp" class="form-label">Mã OTP (6 số)</label>
                                    <input type="text" class="form-control" id="regOtp" required>
                                </div>
                                <div class="mb-3">
                                    <label for="regEmail" class="form-label">Email</label>
                                    <input type="email" class="form-control" id="regEmail" required>
                                </div>
                                <div class="mb-3">
                                    <label for="regName" class="form-label">Họ và tên</label>
                                    <input type="text" class="form-control" id="regName" required>
                                </div>
                                <div class="mb-3">
                                    <label for="regPass" class="form-label">Mật khẩu (ít nhất 6 ký tự)</label>
                                    <input type="password" class="form-control" id="regPass" required>
                                </div>
                                <div class="mb-3">
                                    <label for="regPin" class="form-label">Mã PIN (6 chữ số)</label>
                                    <input type="password" class="form-control" id="regPin" required maxlength="6">
                                </div>
                                
                                <div id="registerError" class="alert alert-danger d-none" role="alert"></div>
                                
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary" id="registerButton">Đăng Ký</button>
                                </div>
                            </div>
                        </form>
                        
                        <div class="text-center mt-3">
                            <a href="login.jsp">Đã có tài khoản? Đăng nhập</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        const step1 = document.getElementById("step1");
        const step2 = document.getElementById("step2");
        const otpButton = document.getElementById("requestOtpButton");
        const otpError = document.getElementById("otpError");
        const regError = document.getElementById("registerError");
        const regButton = document.getElementById("registerButton");
        const regForm = document.getElementById("registerForm");
        const regPhone = document.getElementById("regPhone");

        // Xử lý Bước 1: Yêu cầu OTP
        otpButton.addEventListener("click", async () => {
            otpButton.disabled = true;
            otpButton.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang gửi...';
            otpError.classList.add("d-none");
            
            try {
                const result = await apiFetch("/api/auth/register?action=request-otp", { //
                    method: "POST",
                    body: JSON.stringify({
                        phoneNumber: regPhone.value,
                        contactType: "phone" 
                    })
                });

                if (result.success) {
                    // Thành công, chuyển sang Bước 2
                    step1.classList.add("d-none");
                    step2.classList.remove("d-none");
                    document.getElementById("phoneDisplay").textContent = regPhone.value;
                    // Bạn có thể hiển thị mã OTP debug nếu muốn: 
                    // alert("DEBUG OTP: " + result.data.debugOtp);
                } else {
                    otpError.textContent = result.message;
                    otpError.classList.remove("d-none");
                    otpButton.disabled = false;
                    otpButton.innerHTML = 'Gửi mã OTP';
                }
            } catch (err) {
                otpError.textContent = "Lỗi kết nối. Vui lòng thử lại.";
                otpError.classList.remove("d-none");
                otpButton.disabled = false;
                otpButton.innerHTML = 'Gửi mã OTP';
            }
        });

        // Xử lý Bước 2: Đăng ký
        regForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            if (!step2.classList.contains("d-none")) { // Chỉ chạy khi ở bước 2
                
                regButton.disabled = true;
                regButton.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang đăng ký...';
                regError.classList.add("d-none");

                try {
                    const result = await apiFetch("/api/auth/register?action=verify-register", { //
                        method: "POST",
                        body: JSON.stringify({
                            phoneNumber: regPhone.value,
                            email: document.getElementById("regEmail").value,
                            fullName: document.getElementById("regName").value,
                            password: document.getElementById("regPass").value,
                            transactionPin: document.getElementById("regPin").value,
                            otp: document.getElementById("regOtp").value
                        })
                    });

                    if (result.success) {
                        // Đăng ký thành công -> Đăng nhập luôn
                        saveToken(result.data.token);
                        localStorage.setItem("eWalletUser", result.data.fullName);
                        window.location.href = contextPath + "/dashboard.jsp";
                    } else {
                        regError.textContent = result.message;
                        regError.classList.remove("d-none");
                        regButton.disabled = false;
                        regButton.innerHTML = 'Đăng Ký';
                    }
                } catch (err) {
                    regError.textContent = "Lỗi kết nối. Vui lòng thử lại.";
                    regError.classList.remove("d-none");
                    regButton.disabled = false;
                    regButton.innerHTML = 'Đăng Ký';
                }
            }
        });
    </script>

</body>
</html>