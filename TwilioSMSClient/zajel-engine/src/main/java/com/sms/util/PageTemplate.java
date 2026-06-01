package com.sms.util;

public class PageTemplate {

// ═══════════════════════════════════════════════
// ZAJEL LOGO — Your actual brand logo (SVG)
// ═══════════════════════════════════════════════
    private static String zajelLogoImg(String ctx) {
        return "<img src='" + ctx + "/logo.svg' alt='Zajel' "
                + "style='height:90px;width:90px;object-fit:contain;flex-shrink:0'/>";
    }

    private static String zajelLogoImgWhite(String ctx) {
        return "<img src='" + ctx + "/logo.svg' alt='Zajel' "
                + "style='height:90px;width:90px;object-fit:contain;flex-shrink:0'/>";
    }

    // ═══════════════════════════════════════════════
    // HEADER + FOOTER CSS
    // ═══════════════════════════════════════════════
    public static String headerFooterCSS() {
        return ""
                // Page shell
                + ".page-shell { display:flex; flex-direction:column; min-height:100vh; }\n"
                + ".page-body { flex:1; max-width:1100px; margin:0 auto; padding:28px 24px; width:100%; }\n"
                + ".page-title { font-size:22px; font-weight:800; margin-bottom:4px; }\n"
                + ".page-sub { color:#718096; font-size:13.5px; margin-bottom:24px; }\n"
                + "\n"
                // ── Site Header (Customer — Blue) ────
                + ".site-header {\n"
                + "  display:flex; align-items:center; justify-content:space-between;\n"
                + "  padding:0 32px; height:60px;\n"
                + "  background:#fff;\n"
                + "  border-bottom:2px solid #1a73e8;\n"
                + "  position:sticky; top:0; z-index:100;\n"
                + "  box-shadow:0 1px 8px rgba(0,0,0,.06);\n"
                + "}\n"
                + ".header-logo {\n"
                + "  display:flex; align-items:center; gap:10px;\n"
                + "  font-size:20px; font-weight:800; color:#2d3748;\n"
                + "  text-decoration:none; letter-spacing:-.5px;\n"
                + "}\n"
                + ".header-logo svg {\n"
                + "  height:36px; width:auto; flex-shrink:0;\n"
                + "}\n"
                + ".header-nav {\n"
                + "  display:flex; align-items:center; gap:4px;\n"
                + "}\n"
                + ".header-nav a {\n"
                + "  padding:8px 16px; font-size:13.5px; font-weight:600;\n"
                + "  color:#718096; text-decoration:none;\n"
                + "  border-radius:8px; transition:background .15s, color .15s;\n"
                + "}\n"
                + ".header-nav a:hover {\n"
                + "  background:rgba(26,115,232,.06); color:#1a73e8;\n"
                + "}\n"
                + ".header-nav a.active {\n"
                + "  background:rgba(26,115,232,.08); color:#1a73e8;\n"
                + "}\n"
                + ".nav-logout {\n"
                + "  color:#1a73e8 !important; font-weight:700 !important;\n"
                + "}\n"
                + "\n"
                // ── Admin Header (Purple) ────────────
                + ".admin-header {\n"
                + "  border-bottom-color:#7c4dff;\n"
                + "}\n"
                + ".admin-header .header-nav a:hover,\n"
                + ".admin-header .header-nav a.active {\n"
                + "  background:rgba(124,77,255,.08); color:#7c4dff;\n"
                + "}\n"
                + ".admin-tag {\n"
                + "  font-size:10px; font-weight:800; background:#7c4dff;\n"
                + "  color:#fff; padding:2px 8px; border-radius:6px;\n"
                + "  margin-left:8px; letter-spacing:.5px; vertical-align:middle;\n"
                + "}\n"
                + ".admin-logout { color:#7c4dff !important; }\n"
                + "\n"
                // ── Site Footer ──────────────────────
                + ".site-footer {\n"
                + "  text-align:center; padding:24px;\n"
                + "  border-top:1px solid #e2e8f0;\n"
                + "  font-size:12px; color:#718096;\n"
                + "  background:#fff; margin-top:auto;\n"
                + "}\n"
                + ".site-footer a {\n"
                + "  color:#718096; text-decoration:none; margin:0 10px;\n"
                + "}\n"
                + ".site-footer a:hover { color:#2d3748; }\n"
                + "\n"
                // ── Animations ───────────────────────
                + ".anim-fadeup { animation:fadeUp .4s ease both; }\n"
                + "@keyframes fadeUp { from{opacity:0;transform:translateY(12px)} to{opacity:1;transform:none} }\n";
    }

    // ═══════════════════════════════════════════════
    // CUSTOMER HEADER (Blue)
    // ═══════════════════════════════════════════════
    public static String customerHeader(String ctx, String activePage) {
        return "<header class='site-header'>\n"
                + "  <a href='" + ctx + "/customer/home' class='header-logo'>"
                + zajelLogoImg(ctx) + "</a>\n" // ← changed
                + "  <nav class='header-nav'>\n"
                + navLink(ctx + "/customer/home", "Dashboard", activePage)
                + navLink(ctx + "/customer/send", "Send SMS", activePage)
                + navLink(ctx + "/customer/history", "History", activePage)
                + navLink(ctx + "/customer/search", "Search", activePage)
                + navLink(ctx + "/customer/profile", "My Profile", activePage)
                + "    <a href='" + ctx + "/logout' class='nav-logout'>Logout</a>\n"
                + "  </nav>\n"
                + "</header>\n";
    }

    // ═══════════════════════════════════════════════
    // ADMIN HEADER (Purple)
    // ═══════════════════════════════════════════════
    public static String adminHeader(String ctx, String activePage) {
        return "<header class='site-header admin-header'>\n"
                + "  <div class='header-logo'>"
                + zajelLogoImg(ctx) // ← changed
                + "<span class='admin-tag'>ADMIN</span></div>\n"
                + "  <nav class='header-nav'>\n"
                + navLink(ctx + "/admin/home", "Dashboard", activePage)
                + navLink(ctx + "/admin/stats", "Statistics", activePage)
                + navLink(ctx + "/admin/customers", "Manage Customers", activePage)
                + "    <a href='" + ctx + "/logout' class='nav-logout admin-logout'>Sign Out</a>\n"
                + "  </nav>\n"
                + "</header>\n";
    }

    // ═══════════════════════════════════════════════
    // FOOTER
    // ═══════════════════════════════════════════════
    public static String footer() {
        return "<footer class='site-footer'>\n"
                + "  <div style='margin-bottom:8px'>\n"
                + "    <a href='#'>Support</a>\n"
                + "    <a href='#'>Twilio API Docs</a>\n"
                + "    <a href='#'>Privacy Policy</a>\n"
                + "  </div>\n"
                + "  <p>&copy; 2026 Zajel. All rights reserved.</p>\n"
                + "</footer>\n";
    }

    // ═══════════════════════════════════════════════
    // WHITE LOGO GETTER (for login/register pages)
    // ═══════════════════════════════════════════════
    public static String getWhiteLogo(String ctx) {
        return zajelLogoImgWhite(ctx);
    }

    public static String getDarkLogo(String ctx) {
        return zajelLogoImg(ctx);
    }

    // ═══════════════════════════════════════════════
    // NAV LINK HELPER
    // ═══════════════════════════════════════════════
    private static String navLink(String href, String label, String active) {
        String cls = label.equals(active) ? " class='active'" : "";
        return "    <a href='" + href + "'" + cls + ">" + label + "</a>\n";
    }
}
