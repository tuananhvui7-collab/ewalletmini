<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal fade" id="pinModal" tabindex="-1" data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Xác thực giao dịch</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p>Vui lòng nhập mã PIN (6 số) để tiếp tục.</p>
                <form id="pinForm">
                    <div class="mb-3">
                        <label for="transactionPinInput" class="form-label">Mã PIN</label>
                        <input type="password" class="form-control" id="transactionPinInput" required maxlength="6" autocomplete="off">
                    </div>
                    <div id="pinErrorMessage" class="alert alert-danger d-none" role="alert"></div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary" id="confirmPinButton">Xác nhận</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    const pinModalEl = document.getElementById('pinModal');
    const pinForm = document.getElementById('pinForm');
    const pinInput = document.getElementById('transactionPinInput');
    const pinError = document.getElementById('pinErrorMessage');
    const pinButton = document.getElementById('confirmPinButton');

    // Khởi tạo Modal của Bootstrap
    const pinModal = new bootstrap.Modal(pinModalEl);

    // Xử lý khi form PIN được submit
    pinForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        pinButton.disabled = true;
        pinButton.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang kiểm tra...';
        pinError.classList.add('d-none');

        try {
            // Gọi đến AuthTransactionServlet
            const result = await apiFetch('/api/auth/verify-transaction', {
                method: 'POST',
                body: JSON.stringify({
                    transactionPin: pinInput.value
                })
            });

            if (result.success) {
                // PIN ĐÚNG!
                // Gửi một sự kiện tùy chỉnh (custom event) để trang cha biết
                const verifiedEvent = new CustomEvent('pin-verified', { 
                    detail: { verified: true } 
                });
                document.dispatchEvent(verifiedEvent);
                
                // Đóng modal và reset form
                pinModal.hide();
                pinForm.reset();
            } else {
                // PIN SAI
                pinError.textContent = result.message;
                pinError.classList.remove('d-none');
            }

        } catch (err) {
            pinError.textContent = 'Lỗi kết nối. Vui lòng thử lại.';
            pinError.classList.remove('d-none');
        }

        pinButton.disabled = false;
        pinButton.innerHTML = 'Xác nhận';
    });
</script>