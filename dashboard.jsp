<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tổng quan - Mini E-Wallet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="assets/css/main.css">
    <script src="assets/js/main.js"></script>
    <script>checkAuth();</script>
    <script src="https://cdn.jsdelivr.net/npm/qrcodejs@1.0.0/qrcode.min.js"></script>
</head>
<body class="bg-light">

    <jsp:include page="user_navbar.jsp">
        <jsp:param name="activePage" value="dashboard" />
    </jsp:include>

    <div class="container mt-4">
        
        <div class="card shadow-sm border-0 mb-4" style="background: linear-gradient(135deg, #0d6efd, #0a58ca);">
            <div class="card-body p-4 text-white">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h5 class="mb-0">Số dư khả dụng</h5>
                        <h1 class="display-4 fw-bold" id="balanceDisplay">
                            <span class="spinner-border spinner-border-sm"></span>
                        </h1>
                        <p class="mb-0 text-white-50">SĐT: <span id="phoneDisplay">...</span></p>
                    </div>
                    <div id="merchantQrButton" class="text-center d-none">
                        <button class="btn btn-light" data-bs-toggle="modal" data-bs-target="#qrModal">
                            <i class="bi bi-qr-code fs-1"></i>
                            <span class="d-block">Xem QR</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <div class="card shadow-sm border-0">
            <div class="card-body">
                <h5 class="card-title">Lịch sử giao dịch</h5>
                <div id="historyLoading" class="text-center my-3">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <tbody id="historyTableBody">
                            
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    
    <div class="modal fade" id="qrModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Mã QR Nhận Tiền</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body text-center">
                    <p>Đưa mã này cho người thanh toán</p>
                    <div id="qrcode" class="d-flex justify-content-center"></div>
                    <p class="mt-3 fw-bold" id="qrText"></p>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const user = getUser();
            if (user) {
                document.getElementById("navUserName").textContent = user.fullName;
            }

            fetchProfile();
            fetchHistory();
        });

        async function fetchProfile() {
            try {
                const result = await apiFetch("/api/user/profile"); 
                
                if (result && result.success) {
                    const data = result.data;
                    document.getElementById("navUserName").textContent = data.fullName;
                    document.getElementById("balanceDisplay").textContent = formatCurrency(data.balance);
                    document.getElementById("phoneDisplay").textContent = data.phoneNumber;
                    saveUser(data);

                    if (data.role === 'MERCHANT') {
                        document.getElementById("merchantQrButton").classList.remove("d-none");
                        const qrValue = data.phoneNumber; 
                        document.getElementById("qrText").textContent = qrValue;
                        new QRCode(document.getElementById("qrcode"), {
                            text: qrValue,
                            width: 256,
                            height: 256
                        });
                    }
                } else {
                    console.error("Profile error:", result ? result.message : "No response");
                    document.getElementById("balanceDisplay").textContent = "Lỗi";
                }
            } catch (err) {
                console.error("Profile exception:", err);
                document.getElementById("balanceDisplay").textContent = "Lỗi";
            }
        }

        async function fetchHistory() {
            const tableBody = document.getElementById("historyTableBody");
            const loadingDiv = document.getElementById("historyLoading");
            
            try {
                const result = await apiFetch("/api/transaction/history?limit=10"); 
                loadingDiv.classList.add("d-none");

                if (result && result.success && result.data && Array.isArray(result.data) && result.data.length > 0) {
                    tableBody.innerHTML = "";
                    
                    result.data.forEach(txn => {
                        const isOut = txn.type === 'P2P' || txn.type === 'PAYMENT';
                        const amountClass = isOut ? 'text-danger' : 'text-success';
                        const amountSign = isOut ? '-' : '+';

                        const formattedDate = new Date(txn.createdAt).toLocaleString('vi-VN');

                        const row = `
                            <tr>
                                <td>
                                    <strong class="d-block">${txn.type}</strong>
                                    <small class="text-muted">${formattedDate}</small>
                                </td>
                                <td class="text-end">
                                    <strong class="${amountClass}">${amountSign}${formatCurrency(txn.amount)}</strong>
                                    <br>
                                    <small class="text-muted">${txn.description || '...'}</small>
                                </td>
                            </tr>
                        `;
                        tableBody.innerHTML += row;
                    });
                } else {
                    tableBody.innerHTML = '<tr><td colspan="2" class="text-center text-muted">Chưa có giao dịch nào.</td></tr>';
                }
            } catch (err) {
                console.error("History exception:", err);
                loadingDiv.classList.add("d-none");
                tableBody.innerHTML = '<tr><td colspan="2" class="text-center text-danger">Lỗi tải dữ liệu.</td></tr>';
            }
        }
    </script>
</body>
</html>