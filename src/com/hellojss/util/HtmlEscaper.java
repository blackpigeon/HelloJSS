package com.hellojss.util;

public final class HtmlEscaper {
    private HtmlEscaper() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value;
        escaped = escaped.replace("&", "&amp;");
        escaped = escaped.replace("<", "&lt;");
        escaped = escaped.replace(">", "&gt;");
        escaped = escaped.replace("\"", "&quot;");
        escaped = escaped.replace("'", "&#39;");
        return escaped;
    }

    public static String escapeWithBreaks(String value) {
        return escape(value).replace("\r\n", "\n").replace("\n", "<br/>");
    }
}