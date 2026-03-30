<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.Collections,java.util.List,com.hellojss.model.GuestMessage,com.hellojss.migration.SchemaMigrationResult,com.hellojss.service.GuestbookService,com.hellojss.util.HtmlEscaper" %>
<%
request.setCharacterEncoding("UTF-8");

GuestbookService guestbookService = new GuestbookService();
List<GuestMessage> messages = Collections.emptyList();
String errorMessage = null;
String successMessage = null;
String senderName = request.getParameter("name") == null ? "" : request.getParameter("name");
String messageText = request.getParameter("message") == null ? "" : request.getParameter("message");
boolean databaseReady = false;

try {
    SchemaMigrationResult migrationResult = guestbookService.ensureReady();
    databaseReady = true;
    if (migrationResult.isMigratedNow()) {
        successMessage = "Database schema was initialized automatically for this environment.";
    }

    if ("POST".equalsIgnoreCase(request.getMethod())) {
        guestbookService.addMessage(senderName, messageText);
        response.sendRedirect(request.getContextPath() + "/index.jsp?saved=1");
        return;
    }

    if ("1".equals(request.getParameter("saved"))) {
        successMessage = "Your message was saved.";
    }
} catch (IllegalArgumentException e) {
    errorMessage = e.getMessage();
} catch (Exception e) {
    errorMessage = "The application could not connect to the database or complete schema setup. " + e.getMessage();
}

if (databaseReady) {
    try {
        messages = guestbookService.getMessages();
    } catch (Exception e) {
        if (errorMessage == null) {
            errorMessage = "The application could not load the saved messages. " + e.getMessage();
        }
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="crossorigin" />
    <link href="https://fonts.googleapis.com/css2?family=Prompt:wght@300;400;500;600;700&display=swap" rel="stylesheet" />
    <title>Hello JSS</title>
    <style type="text/css">
        body {
            font-family: "Prompt", "Trebuchet MS", Arial, sans-serif;
            background: #ffffff;
            color: #1f3a2a;
            margin: 0;
            padding: 32px 18px 40px 18px;
        }
        .shell {
            max-width: 1080px;
            margin: 0 auto;
        }
        .hero {
            margin: 0 auto 20px auto;
            padding: 22px 24px;
            background-color: #1f7a45;
            color: #f6fff9;
            border: 1px solid #185f36;
            box-shadow: 0 16px 40px rgba(31, 122, 69, 0.2);
        }
        .hero h1 {
            margin: 0 0 8px 0;
            font-size: 34px;
            letter-spacing: 1px;
            text-transform: uppercase;
        }
        .hero p {
            margin: 0;
            font-size: 15px;
            line-height: 1.6;
            color: #d7f5e1;
        }
        .layout {
            display: flex;
            align-items: flex-start;
            gap: 2%;
        }
        .panel {
            background-color: #ffffff;
            border: 1px solid #d6e9dc;
            box-shadow: 0 10px 30px rgba(27, 94, 44, 0.08);
            padding: 22px;
            min-height: 460px;
        }
        .composer {
            width: 34%;
            box-sizing: border-box;
        }
        .feed {
            width: 64%;
            box-sizing: border-box;
        }
        .section-title {
            margin: 0 0 16px 0;
            font-size: 22px;
            color: #1f7a45;
        }
        .label {
            display: block;
            font-size: 13px;
            font-weight: bold;
            text-transform: uppercase;
            letter-spacing: 1px;
            color: #2f6b45;
            margin: 0 0 8px 0;
        }
        .text-input,
        .text-area {
            width: 100%;
            border: 1px solid #b9d9c6;
            background-color: #ffffff;
            color: #1f3a2a;
            font-size: 15px;
            padding: 10px 12px;
            box-sizing: border-box;
            margin: 0 0 16px 0;
        }
        .text-area {
            min-height: 180px;
            resize: vertical;
            line-height: 1.6;
        }
        .submit {
            border: 0;
            background-color: #1f7a45;
            color: #ffffff;
            padding: 12px 18px;
            font-size: 15px;
            font-weight: bold;
            cursor: pointer;
        }
        .hint {
            margin: 14px 0 0 0;
            color: #666666;
            font-size: 13px;
            line-height: 1.6;
        }
        .notice,
        .error {
            padding: 12px 14px;
            margin: 0 0 16px 0;
            font-size: 14px;
            line-height: 1.5;
        }
        .notice {
            background-color: #ebf5e8;
            border: 1px solid #a8c8a0;
            color: #2d5d34;
        }
        .error {
            background-color: #fff0ec;
            border: 1px solid #e0a490;
            color: #9a3d1f;
        }
        .feed-header {
            margin: 0 0 18px 0;
            overflow: hidden;
        }
        .feed-title {
            float: left;
            margin: 0;
            font-size: 22px;
            color: #1f7a45;
        }
        .feed-meta {
            float: right;
            margin: 4px 0 0 0;
            color: #6c7782;
            font-size: 13px;
        }
        .empty {
            padding: 24px;
            border: 1px dashed #98c6ab;
            background-color: #f7fffa;
            color: #2f6b45;
            font-size: 15px;
        }
        .message-card {
            border-top: 1px solid #d6e9dc;
            padding: 18px 0;
        }
        .message-card.first {
            border-top: 0;
            padding-top: 0;
        }
        .message-head {
            overflow: hidden;
            margin: 0 0 8px 0;
        }
        .message-name {
            float: left;
            font-size: 18px;
            color: #1d6f3f;
            margin: 0;
        }
        .message-time {
            float: right;
            color: #6c7782;
            font-size: 13px;
            margin: 3px 0 0 0;
        }
        .message-text {
            font-size: 15px;
            line-height: 1.7;
            color: #244033;
        }
        .footer-note {
            clear: both;
            padding-top: 18px;
            color: #66727d;
            font-size: 12px;
        }
        @media screen and (max-width: 900px) {
            .layout {
                display: block;
            }
            .composer,
            .feed {
                width: auto;
                margin: 0 0 18px 0;
            }
            .feed-meta,
            .message-time,
            .message-name,
            .feed-title {
                float: none;
                display: block;
            }
            .message-time,
            .feed-meta {
                margin-top: 6px;
            }
        }
    </style>
</head>
<body>
<div class="shell">
    <div class="hero">
        <h1>Hello JSS</h1>
    </div>

    <div class="layout">
        <div class="panel composer">
            <h2 class="section-title">Leave a message</h2>
            <% if (successMessage != null) { %>
                <div class="notice"><%= HtmlEscaper.escape(successMessage) %></div>
            <% } %>
            <% if (errorMessage != null) { %>
                <div class="error"><%= HtmlEscaper.escape(errorMessage) %></div>
            <% } %>

            <form method="post" action="index.jsp">
                <label class="label" for="name">Your name</label>
                <input class="text-input" type="text" id="name" name="name" maxlength="100" value="<%= HtmlEscaper.escape(senderName) %>" />

                <label class="label" for="message">Message</label>
                <textarea class="text-area" id="message" name="message" maxlength="2000"><%= HtmlEscaper.escape(messageText) %></textarea>

                <input class="submit" type="submit" value="Save Message" />
            </form>

        </div>

        <div class="panel feed">
            <div class="feed-header">
                <h2 class="feed-title">Recent messages</h2>
                <p class="feed-meta">Server time: <%= new java.util.Date() %></p>
            </div>

            <% if (messages.isEmpty()) { %>
                <div class="empty">No messages yet. Be the first person to write something.</div>
            <% } else { %>
                <% for (int index = 0; index < messages.size(); index++) {
                       GuestMessage message = messages.get(index);
                %>
                    <div class="message-card<%= index == 0 ? " first" : "" %>">
                        <div class="message-head">
                            <p class="message-name"><%= HtmlEscaper.escape(message.getSenderName()) %></p>
                            <p class="message-time"><%= message.getCreatedAt() %></p>
                        </div>
                        <div class="message-text"><%= HtmlEscaper.escapeWithBreaks(message.getMessageText()) %></div>
                    </div>
                <% } %>
            <% } %>

            <div class="footer-note">Messages are ordered from newest to oldest based on database timestamp.</div>
        </div>
    </div>
</div>
</body>
</html>
