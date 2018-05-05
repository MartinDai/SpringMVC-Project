<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<title>SpringMVC Project</title>
<body>
<div>
    <h2>This is the SpringMVC Project index page.</h2>

    <form action="/index/upload" method="post" enctype="multipart/form-data">
        <input type="file" name="template"/>
        <input type="submit" name="提交上传"/>
    </form>
</div>
</body>
</html>
