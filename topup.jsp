<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nạp tiền - Mini E-Wallet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <script src="assets/js/main.js"></script>
    <script> checkAuth(); </script>
        <link rel="stylesheet" href="assets/css/main.css">

</head>
<body class="bg-light">

    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
        <div class="container">
            <a class="navbar-brand" href="dashboard.jsp"><i class="bi bi-wallet2"></i> Mini E-Wallet</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="mainNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item"><a class="nav-link" href="dashboard.jsp">Tổng quan</a></li>
                    <li class="nav-item"><a class="nav-link" href="transfer.jsp">Chuyển tiền</a></li>
                    <li class="nav-item"><a class="nav-link active" href="topup.jsp">Nạp tiền</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow-sm border-0">
                    <div class="card-body p-4">
                        <h3 class="card-title text-center mb-4">Nạp tiền bằng thẻ cào</h3>
                        
                        <form id="topupForm">
                            <div class="mb-3">
                                <label for="serial" class="form-label">Số Serial</label>
                                <input type="text" class="form-control" id="serial" required>
                            </div>
                            <div class="mb-3">
                                <label for="code" class="form-label">Mã thẻ cào</label>
                                <input type="text" class="form-control" id="code" required>
                            </div>
                            
                            <div id="formMessage" class="alert d-none" role="alert"></div>
                            
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary" id="topupButton">Xác nhận nạp tiền</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById("topupForm").addEventListener("submit", async (event) => {
            event.preventDefault();
            
            const button = document.getElementById("topupButton");
            const messageDiv = document.getElementById("formMessage");
            
            button.disabled = true;
            button.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang xử lý...';
            messageDiv.className = "alert d-none"; // Reset thông báo

            try {
                const result = await apiFetch("/api/transaction/topup", { //
                    method: "POST",
                    body: JSON.stringify({
                        serialNumber: document.getElementById("serial").value,
                        cardCode: document.getElementById("code").value
                    })
                });

                if (result.success) {
                    messageDiv.className = "alert alert-success";
                    messageDiv.textContent = `Nạp thành công \${formatCurrency(result.data.amount)}! Đang chuyển về trang chủ...`;
                    // Chuyển về dashboard sau 2s
                    setTimeout(() => {
                        window.location.href = contextPath + "/dashboard.jsp";
                    }, 2000);
                } else {
                    messageDiv.className = "alert alert-danger";
                    messageDiv.textContent = result.message;
                    button.disabled = false;
                    button.innerHTML = 'Xác nhận nạp tiền';
                }
            } catch (err) {
                messageDiv.className = "alert alert-danger";
                messageDiv.textContent = "Lỗi kết nối. Vui lòng thử lại.";
                button.disabled = false;
                button.innerHTML = 'Xác nhận nạp tiền';
            }
        });
    </script>
</body>
</html>