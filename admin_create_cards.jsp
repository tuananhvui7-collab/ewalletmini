<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo thẻ cào - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <script src="assets/js/main.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <script> checkAdminAuth(); </script>
</head>
<body class="bg-light">

    <jsp:include page="admin_navbar.jsp">
        <jsp:param name="activePage" value="cards" />
    </jsp:include>

    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-7">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h4 class="card-title">Tạo Thẻ Cào Hàng Loạt</h4>
                        <form id="createCardForm">
                            <div class="mb-3">
                                <label for="quantity" class="form-label">Số lượng</label>
                                <input type="number" class="form-control" id="quantity" required>
                            </div>
                            <div class="mb-3">
                                <label for="denomination" class="form-label">Mệnh giá (VND)</label>
                                <input type="number" class="form-control" id="denomination" required>
                            </div>
                            <div class="mb-3">
                                <label for="expiryDate" class="form-label">Ngày hết hạn</label>
                                <input type="date" class="form-control" id="expiryDate" required>
                            </div>
                            <div id="formMessage" class="alert d-none" role="alert"></div>
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary" id="createBtn">Tạo thẻ</button>
                            </div>
                        </form>
                    </div>
                </div>
                
                <div id="resultArea" class="mt-3 d-none">
                    <h5>Kết quả tạo thẻ:</h5>
                    <textarea class="form-control" id="resultText" rows="10" readonly></textarea>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById("createCardForm").addEventListener("submit", async (event) => {
            event.preventDefault();
            
            const button = document.getElementById("createBtn");
            const messageDiv = document.getElementById("formMessage");
            const resultArea = document.getElementById("resultArea");
            const resultText = document.getElementById("resultText");

            button.disabled = true;
            button.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang tạo...';
            messageDiv.className = "alert d-none"; 
            resultArea.classList.add("d-none");

            try {
                //
                const result = await apiFetch("/api/admin/scratch-cards/create", {
                    method: "POST",
                    body: JSON.stringify({
                        quantity: document.getElementById("quantity").value,
                        denomination: document.getElementById("denomination").value,
                        expiryDate: document.getElementById("expiryDate").value
                    })
                });

                if (result.success) {
                    messageDiv.className = "alert alert-success";
                    messageDiv.textContent = result.message;
                    
                    // Hiển thị danh sách thẻ đã tạo
                    let cardListText = "Serial | Code | Denomination\n";
                    cardListText += "--------------------------------------\n";
                    result.data.forEach(card => {
                        cardListText += `${card.serialNumber} | ${card.cardCode} | ${card.denomination}\n`;
                    });
                    
                    resultText.value = cardListText;
                    resultArea.classList.remove("d-none");
                    document.getElementById("createCardForm").reset();
                } else {
                    messageDiv.className = "alert alert-danger";
                    messageDiv.textContent = result.message;
                }
            } catch (err) {
                messageDiv.className = "alert alert-danger";
                messageDiv.textContent = "Lỗi kết nối.";
            }
            button.disabled = false;
            button.innerHTML = 'Tạo thẻ';
        });
    </script>
</body>
</html>