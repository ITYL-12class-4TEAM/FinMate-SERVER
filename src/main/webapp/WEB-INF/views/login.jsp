<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>로그인</title>
</head>
<body>
<form method="post" action="${pageContext.request.contextPath}/login">
    <input type="text" name="email" placeholder="이메일" />
    <input type="password" name="password" placeholder="비밀번호" />
    <button type="submit">로그인</button>
</form>
</body>
</html>
