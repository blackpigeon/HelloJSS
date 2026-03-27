<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Hello JSS</title>
    <style type="text/css">
        body {
            font-family: Tahoma, Arial, sans-serif;
            background-color: #f3f5f7;
            color: #1f2d3d;
            margin: 0;
            padding: 40px;
        }
        .panel {
            max-width: 600px;
            margin: 40px auto;
            background-color: #ffffff;
            border: 1px solid #d9e1ea;
            padding: 24px;
        }
        .hello {
            font-size: 28px;
            font-weight: bold;
            color: #4775D1;
            margin: 0 0 12px 0;
        }
        .jss-green {
            color: #2e7d32;
        }
        .time {
            font-size: 16px;
            margin: 0;
        }
    </style>
</head>
<body>
<div class="panel">
    <p class="hello"><span class="jss-green"></span>Hello JSS</span></p>
    <p class="time">Server time: <%= new java.util.Date() %></p>
</div>
</body>
</html>
