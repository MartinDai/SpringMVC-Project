<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>服务器线程信息</title>
</head>
<body>
<%
    for (Map.Entry<Thread, StackTraceElement[]> threadEntry : Thread.getAllStackTraces().entrySet()) {
        Thread thread = threadEntry.getKey();
        if (thread.equals(Thread.currentThread())) {
            continue;
        }

        StackTraceElement[] stack = threadEntry.getValue();
        out.print("\n线程：" + thread.getName() + "\n");
        for (StackTraceElement stackTraceElement : stack) {
            out.print("\t" + stackTraceElement + "\n");
        }
    }
%>
</body>
</html>
