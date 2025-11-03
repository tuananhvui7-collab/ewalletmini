<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán QR - Mini E-Wallet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <script src="assets/js/main.js"></script>
    <script> checkAuth(); </script>
        <link rel="stylesheet" href="assets/css/main.css">

</head>
<body class="bg-light">

    <jsp:include page="user_navbar.jsp">
        <jsp:param name="activePage" value="payment" />
    </jsp:include>

    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow-sm border-0">
                    <div class="card-body p-4">
                        <h3 class="card-title text-center mb-4">Thanh toán QR</h3>
                        
                        <form id="paymentForm">
                            <div class="mb-3">
                                <label for="qrCode" class="form-label">Mã QR Merchant (Nhập tay)</label>
                                <input type="text" class="form-control" id="qrCode" required>
                            </div>
                            <div class="mb-3">
                                <label for="amount" class="form-label">Số tiền (VND)</label>
                                <input type="number" class="form-control" id="amount" required>
                            </div>
                            
                            <div id="formMessage" class="alert d-none" role="alert"></div>
                            
                            <div class="d-grid">
                                <button type="button" class="btn btn-primary" id="payButton" data-bs-toggle="modal" data-bs-target="#pinModal">
                                    Tiếp tục
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <%@ include file="pin_modal.jsp" %>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const payButton = document.getElementById("payButton");
        const messageDiv = document.getElementById("formMessage");

        // 1. Lắng nghe sự kiện 'pin-verified' từ modal
        document.addEventListener('pin-verified', (e) => {
            if (e.detail.verified) {
                // Nếu PIN đúng, tiến hành gọi API thanh toán
                executePayment();
            }
        });

        // 2. Hàm thực thi thanh toán
        async function executePayment() {
            payButton.disabled = true;
            payButton.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang xử lý...';
            messageDiv.className = "alert d-none"; 

            try {
                // Gọi đến PaymentServlet
                // KHÔNG CẦN GỬI PIN nữa
                const result = await apiFetch("/api/transaction/payment-qr", {
                    method: "POST",
                    body: JSON.stringify({
                        qrCode: document.getElementById("qrCode").value,
                        amount: document.getElementById("amount").value
                        // transactionPin đã được AuthTransactionServlet xác thực
                    })
                });

                if (result.success) {
                    messageDiv.className = "alert alert-success";
                    messageDiv.textContent = `Thanh toán thành công ${formatCurrency(result.data.amount)}! Đang chuyển về trang chủ...`;
                    setTimeout(() => {
                        window.location.href = contextPath + "/dashboard.jsp";
                    }, 2000);
                } else {
                    messageDiv.className = "alert alert-danger";
                    messageDiv.textContent = result.message;
                    payButton.disabled = false;
                    payButton.innerHTML = 'Tiếp tục';
                }
            } catch (err) {
                messageDiv.className = "alert alert-danger";
                messageDiv.textContent = "Lỗi kết nối. Vui lòng thử lại.";
                payButton.disabled = false;
                payButton.innerHTML = 'Tiếp tục';
            }
        };
    </script>
</body>
</html>