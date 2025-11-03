<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Merchant - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <script src="assets/js/main.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">



    <script> checkAdminAuth(); </script>


</head>
<body class="bg-light">

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
        <div class="container">
            <a class="navbar-brand" href="admin_dashboard.jsp"><i class="bi bi-shield-lock"></i> Admin Panel</a>
            <div class="collapse navbar-collapse">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item"><a class="nav-link" href="admin_dashboard.jsp">Báo cáo</a></li>
                    <li class="nav-item"><a class="nav-link active" href="admin_merchants.jsp">Quản lý Merchant</a></li>
                    <li class="nav-item"><a class="nav-link" href="admin_management.jsp">Quản lý Admin</a></li>
                </ul>
                <ul class="navbar-nav ms-auto"><li class="nav-item"><a class="nav-link" href="#" id="logoutButton">Đăng xuất</a></li></ul>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row">
            <div class="col-md-5">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h4 class="card-title">Thêm Merchant Mới</h4>
                        <form id="createMerchantForm">
                            <div class="mb-3">
                                <label for="merPhone" class="form-label">Số điện thoại</label>
                                <input type="tel" class="form-control" id="merPhone" required>
                            </div>
                            <div class="mb-3">
                                <label for="merEmail" class="form-label">Email</label>
                                <input type="email" class="form-control" id="merEmail" required>
                            </div>
                            <div class="mb-3">
                                <label for="merName" class="form-label">Tên đầy đủ (Tên cửa hàng)</label>
                                <input type="text" class="form-control" id="merName" required>
                            </div>
                            <div id="createMsg" class="alert d-none" role="alert"></div>
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary" id="createBtn">Tạo Merchant</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            
            <div class="col-md-7">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h4 class="card-title">Quản lý Merchant</h4>
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" placeholder="Nhập User ID của Merchant..." id="searchUserId">
                            <button class="btn btn-outline-secondary" type="button" id="searchBtn">Tìm</button>
                        </div>
                        
                        <div id="manageMsg" class="alert d-none" role="alert"></div>
                        
                        <div id="merchantDetails" class="d-none">
                            <h5>Thông tin User</h5>
                            <p><strong>ID:</strong> <span id="detailId"></span></p>
                            <p><strong>Tên:</strong> <span id="detailName"></span></p>
                            <p><strong>SĐT:</strong> <span id="detailPhone"></span></p>
                            <p><strong>Trạng thái:</strong> <span id="detailStatus" class="badge"></span></p>
                            <hr>
                            <button class="btn btn-danger" id="lockBtn">Khóa tài khoản</button>
                            <button class="btn btn-success" id="unlockBtn">Mở khóa tài khoản</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const createForm = document.getElementById("createMerchantForm");
        const createBtn = document.getElementById("createBtn");
        const createMsg = document.getElementById("createMsg");

        // Xử lý Thêm Merchant
        createForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            createBtn.disabled = true;
            createMsg.className = "alert d-none";

            try {
                const result = await apiFetch("/api/admin/merchants", { //
                    method: "POST",
                    body: JSON.stringify({
                        phoneNumber: document.getElementById("merPhone").value,
                        email: document.getElementById("merEmail").value,
                        fullName: document.getElementById("merName").value
                    })
                });

                if (result.success) {
                    createMsg.className = "alert alert-success";
                    createMsg.innerHTML = `Tạo thành công! ID: ${result.data.id} <br> QR Code: ${result.data.qrCode}`;
                    createForm.reset();
                } else {
                    createMsg.className = "alert alert-danger";
                    createMsg.textContent = result.message;
                }
            } catch (err) {
                createMsg.className = "alert alert-danger";
                createMsg.textContent = "Lỗi kết nối.";
            }
            createBtn.disabled = false;
        });

        // --- Xử lý Quản lý Merchant ---
        const searchBtn = document.getElementById("searchBtn");
        const searchInput = document.getElementById("searchUserId");
        const manageMsg = document.getElementById("manageMsg");
        const detailsDiv = document.getElementById("merchantDetails");
        let currentUserId = null;

        // Tìm kiếm
        searchBtn.addEventListener("click", async () => {
            currentUserId = searchInput.value;
            if (!currentUserId) return;
            
            manageMsg.className = "alert d-none";
            detailsDiv.classList.add("d-none");

            try {
                const result = await apiFetch(`/api/admin/users/${currentUserId}`); //
                
                if (result.success) {
                    const user = result.data;
                    document.getElementById("detailId").textContent = user.id;
                    document.getElementById("detailName").textContent = user.fullName;
                    document.getElementById("detailPhone").textContent = user.phoneNumber;
                    
                    const statusBadge = document.getElementById("detailStatus");
                    statusBadge.textContent = user.status;
                    statusBadge.className = user.status === 'ACTIVE' ? "badge bg-success" : "badge bg-danger";
                    
                    document.getElementById("lockBtn").disabled = (user.status === 'LOCKED');
                    document.getElementById("unlockBtn").disabled = (user.status === 'ACTIVE');
                    
                    detailsDiv.classList.remove("d-none");
                } else {
                    manageMsg.className = "alert alert-danger";
                    manageMsg.textContent = result.message;
                }
            } catch (err) {
                manageMsg.className = "alert alert-danger";
                manageMsg.textContent = "Lỗi kết nối.";
            }
        });
        
        // Nút Khóa
        document.getElementById("lockBtn").addEventListener("click", async () => {
            await handleLockUnlock("lock");
        });

        // Nút Mở Khóa
        document.getElementById("unlockBtn").addEventListener("click", async () => {
            await handleLockUnlock("unlock");
        });

        async function handleLockUnlock(action) {
            if (!currentUserId) return;
            manageMsg.className = "alert d-none";
            
            try {
                //
                const result = await apiFetch(`/api/admin/users/${currentUserId}?action=${action}`, { 
                    method: "POST" 
                });
                
                if(result.success) {
                    manageMsg.className = "alert alert-success";
                    manageMsg.textContent = result.message;
                    // Tải lại thông tin
                    searchBtn.click();
                } else {
                    manageMsg.className = "alert alert-danger";
                    manageMsg.textContent = result.message;
                }
            } catch (err) {
                 manageMsg.className = "alert alert-danger";
                 manageMsg.textContent = "Lỗi kết nối.";
            }
        }
        
        document.getElementById("logoutButton").addEventListener("click", logout);
    </script>
</body>
</html>