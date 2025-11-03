<%-- Trang này s? t? ??ng ?i?u h??ng ng??i dùng --%>
<!DOCTYPE html>
<html lang = 'vi'>
<head>
    <link rel="stylesheet" href="assets/css/main.css">

    <title> Dang tai...</title>
    <script>
        const contextPath = "/" + window.location.pathname.split('/')[1];
        if (localStorage.getItem("eWalletToken")) {
            window.location.href = contextPath + "/dashboard.jsp";
        } else {
            window.location.href = contextPath + "/login.jsp";
        }
    </script>
</head>
<body>
    <p>dang chuyen huong...</p>
</body>
</html>