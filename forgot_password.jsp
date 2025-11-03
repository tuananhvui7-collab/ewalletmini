<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE futuristic "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd" >
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu - Mini E-Wallet</title>
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
                        <h3 class="card-title text-center mb-4">Khôi Phục Mật Khẩu</h3>
                        
                        <form id="forgotPassForm">
                            <div id="step1_fp">
                                <div class="mb-3">
                                    <label for="fpPhone" class="form-label">Số điện thoại</label>
                                    <input type="tel" class="form-control" id="fpPhone" required>
                                </div>
                                <div id="step1Error_fp" class="alert alert-danger d-none" role="alert"></div>
                                <div class="d-grid">
                                    <button type="button" class="btn btn-secondary" id="requestOtpButton_fp">Gửi mã OTP</button>
                                </div>
                            </div>

                            <div id="step2_fp" class="d-none">
                                <p>Mã OTP đã được gửi đến <strong id="phoneDisplay_fp"></strong>.</p>
                                <div class="mb-3">
                                    <label for="fpOtp" class="form-label">Mã OTP</label>
                                    <input type="text" class="form-control" id="fpOtp" required>
                                </div>
                                <div class="mb-3">
                                    <label for="fpNewPass" class="form-label">Mật khẩu mới</label>
                                    <input type="password" class="form-control" id="fpNewPass" required>
                                </div>
                                <div class="mb-3">
                                    <label for="fpConfirmPass" class="form-label">Xác nhận mật khẩu mới</label>
                                    <input type="password" class="form-control" id="fpConfirmPass" required>
                                </div>
                                
                                <div id="step2Error_fp" class="alert alert-danger d-none" role="alert"></div>
                                <div id="step2Success_fp" class="alert alert-success d-none" role="alert"></div>

                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary" id="resetButton_fp">Đặt lại mật khẩu</button>
                                </div>
                            </div>
                        </form>
                        
                        <div class="text-center mt-3">
                            <a href="login.jsp">Quay lại Đăng nhập</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        const step1 = document.getElementById("step1_fp");
        const step2 = document.getElementById("step2_fp");
        const otpButton = document.getElementById("requestOtpButton_fp");
        const resetButton = document.getElementById("resetButton_fp");
        const forgotForm = document.getElementById("forgotPassForm");
        const phoneInput = document.getElementById("fpPhone");
        const phoneDisplay = document.getElementById("phoneDisplay_fp");
        const step1Error = document.getElementById("step1Error_fp");
        const step2Error = document.getElementById("step2Error_fp");
        const step2Success = document.getElementById("step2Success_fp");

        // Xử lý Bước 1: Yêu cầu OTP
        otpButton.addEventListener("click", async () => {
            otpButton.disabled = true;
            otpButton.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang gửi...';
            step1Error.classList.add("d-none");
            
            try {
                //
                const result = await apiFetch("/api/auth/forgot-password?action=request-otp", {
                    method: "POST",
                    body: JSON.stringify({ phoneNumber: phoneInput.value })
                });

                if (result.success) {
                    step1.classList.add("d-none");
                    step2.classList.remove("d-none");
                    phoneDisplay.textContent = phoneInput.value;
                } else {
                    step1Error.textContent = result.message;
                    step1Error.classList.remove("d-none");
                    otpButton.disabled = false;
                    otpButton.innerHTML = 'Gửi mã OTP';
                }
            } catch (err) {
                step1Error.textContent = "Lỗi kết nối.";
                step1Error.classList.remove("d-none");
                otpButton.disabled = false;
                otpButton.innerHTML = 'Gửi mã OTP';
            }
        });

        // Xử lý Bước 2: Đặt lại
        forgotForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            if (step2.classList.contains("d-none")) return; // Chỉ chạy khi ở bước 2

            resetButton.disabled = true;
            resetButton.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang xử lý...';
            step2Error.classList.add("d-none");
            step2Success.classList.add("d-none");

            try {
                //
                const result = await apiFetch("/api/auth/forgot-password?action=verify-reset", {
                    method: "POST",
                    body: JSON.stringify({
                        phoneNumber: phoneInput.value,
                        otp: document.getElementById("fpOtp").value,
                        newPassword: document.getElementById("fpNewPass").value,
                        confirmPassword: document.getElementById("fpConfirmPass").value
                    })
                });

                if (result.success) {
                    step2Success.textContent = "Đặt lại mật khẩu thành công! Đang chuyển đến trang đăng nhập...";
                    step2Success.classList.remove("d-none");
                    setTimeout(() => {
                        window.location.href = contextPath + "/login.jsp";
                    }, 2000);
                } else {
                    step2Error.textContent = result.message;
                    step2Error.classList.remove("d-none");
                    resetButton.disabled = false;
                    resetButton.innerHTML = 'Đặt lại mật khẩu';
                }
            } catch (err) {
                step2Error.textContent = "Lỗi kết nối.";
                step2Error.classList.remove("d-none");
                resetButton.disabled = false;
                resetButton.innerHTML = 'Đặt lại mật khẩu';
            }
        });
    </script>
</body>
</html>