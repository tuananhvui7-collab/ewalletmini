<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Test API</title>
    <script src="assets/js/main.js"></script>
</head>
<body>
    <h1>Test API</h1>
    <div id="result">Loading...</div>
    
    <script>
        console.log("contextPath:", contextPath);
        console.log("token:", getToken());
        
        // Gọi API test
        fetch(contextPath + "/api/test")
            .then(r => r.json())
            .then(data => {
                document.getElementById("result").innerHTML = "<pre>" + JSON.stringify(data, null, 2) + "</pre>";
            })
            .catch(err => {
                document.getElementById("result").innerHTML = "<p style='color:red'>Error: " + err.message + "</p>";
            });
    </script>
</body>
</html>