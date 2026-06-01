package com.sms.util;

public class Html {

    // ── Zajel Logo inline SVG (compact for nav) ──
    private static String logoImg(String ctx) {
        return "<img src='" + ctx + "/logo.svg' alt='Zajel' "
                + "style='height:36px;width:36px;object-fit:contain;flex-shrink:0'/>"
                + "<span style='margin-left:8px;font-size:20px;font-weight:800;"
                + "color:#0a1628;letter-spacing:-.5px'>Zajel</span>";
    }

    // ── Pass contextPath dynamically ─────────────
    public static String top(String title, String ctx) {
        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1.0'>"
                + "<title>" + title + " — Zajel</title>"
                + "<link rel='stylesheet' href='" + ctx + "/style.css'>"
                + "</head><body>";
    }

    public static String bottom() {
        return "</body></html>";
    }

    public static String adminNav(String name, String ctx) {
        return "<nav class='navbar' style='border-bottom:2px solid #7c4dff'>"
                + "<div class='nav-brand'>" + logoImg(ctx) // ← changed
                + "<span style='font-size:10px;font-weight:800;background:#7c4dff;color:#fff;"
                + "padding:2px 8px;border-radius:6px;margin-left:8px;letter-spacing:.5px'>ADMIN</span></div>"
                + "<div class='nav-links'>"
                + "<a href='" + ctx + "/admin/home'>Dashboard</a>"
                + "<a href='" + ctx + "/admin/customers'>Customers</a>"
                + "<a href='" + ctx + "/admin/stats'>Statistics</a>"
                + "<a href='" + ctx + "/logout' style='color:#7c4dff'>Logout (" + esc(name) + ")</a>"
                + "</div></nav>";
    }

    public static String custNav(String name, String ctx) {
        return "<nav class='navbar' style='border-bottom:2px solid #1a73e8'>"
                + "<div class='nav-brand'>" + logoImg(ctx) // ← changed
                + "</div>"
                + "<div class='nav-links'>"
                + "<a href='" + ctx + "/customer/home'>Home</a>"
                + "<a href='" + ctx + "/customer/send'>Send SMS</a>"
                + "<a href='" + ctx + "/customer/history'>History</a>"
                + "<a href='" + ctx + "/customer/search'>Search</a>"
                + "<a href='" + ctx + "/customer/profile'>Profile</a>"
                + "<a href='" + ctx + "/logout' style='color:#1a73e8'>Logout (" + esc(name) + ")</a>"
                + "</div></nav>";
    }

    public static String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static String alert(String type, String msg) {
        if (msg == null || msg.isEmpty()) {
            return "";
        }
        String icon = "err".equals(type) ? "⚠️" : "✅";
        return "<div class='alert alert-" + type + "'>" + icon + " " + esc(msg) + "</div>";
    }

    public static boolean top(String verify) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static boolean custNav(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static boolean adminNav(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
