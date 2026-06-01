package com.sms.servlet;

import com.sms.dao.MsgDAO;
import com.sms.model.*;
import com.sms.util.Html;
import com.sms.util.PageTemplate;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/customer/search")
public class SearchSmsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String ctx = req.getContextPath();
        String receiver = req.getParameter("receiver");
        String dateFrom = req.getParameter("date_from");
        String dateTo   = req.getParameter("date_to");
        boolean searched = (receiver != null || dateFrom != null || dateTo != null);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        o.println("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'/>");
        o.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        o.println("<title>Search SMS — Zajel</title>");
        o.println("<link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("<style>" + PageTemplate.headerFooterCSS());
        o.println("  .search-grid { display:grid; grid-template-columns:1fr 1fr 1fr; gap:14px; align-items:end; }");
        o.println("  @media(max-width:600px) { .search-grid { grid-template-columns:1fr; } }");
        o.println("  .mono { font-family:monospace; font-size:12px; }");
        o.println("  .type-pill { padding:3px 10px; border-radius:99px; font-size:11px; font-weight:700; display:inline-block; }");
        o.println("  .type-sent { background:#e3f2fd; color:#1a73e8; border:1px solid #bbdefb; }");
        o.println("  .type-error { background:#fef2f2; color:#dc2626; border:1px solid #fecaca; }");
        o.println("</style></head>");
        o.println("<body class='page-shell'>");

        o.println(PageTemplate.customerHeader(ctx, "Search"));

        o.println("<div class='page-body'>");
        o.println("  <h1 class='page-title'>\uD83D\uDD0D Search SMS</h1>");
        o.println("  <p class='page-sub'>Find messages by recipient number or date range.</p>");

        // Search form
        o.println("  <div class='card anim-fadeup' style='padding:24px;margin-bottom:20px'>");
        o.println("    <form method='GET'>");
        o.println("      <div class='search-grid'>");
        o.println("        <div class='form-group' style='margin:0'><label>Receiver MSISDN</label><input type='text' name='receiver' value='" + Html.esc(receiver) + "' placeholder='+20...'/></div>");
        o.println("        <div class='form-group' style='margin:0'><label>Date From</label><input type='date' name='date_from' value='" + Html.esc(dateFrom) + "'/></div>");
        o.println("        <div class='form-group' style='margin:0'><label>Date To</label><input type='date' name='date_to' value='" + Html.esc(dateTo) + "'/></div>");
        o.println("      </div>");
        o.println("      <button class='btn btn-primary' style='margin-top:16px'>Search</button>");
        o.println("    </form>");
        o.println("  </div>");

        if (searched) {
            List<Msg> results = new MsgDAO().search(cu.getCustomerId(), receiver, dateFrom, dateTo);
            o.println("  <div class='card anim-fadeup' style='animation-delay:.06s'>");
            o.println("    <div style='padding:16px 24px;border-bottom:1px solid #e2e8f0;font-size:13px;font-weight:600;color:#718096'>" + results.size() + " result" + (results.size() != 1 ? "s" : "") + "</div>");

            if (results.isEmpty()) {
                o.println("    <div style='padding:48px;text-align:center;color:#718096'><div style='font-size:48px;margin-bottom:12px'>\uD83D\uDCED</div><h3>No messages found</h3></div>");
            } else {
                o.println("    <div style='overflow-x:auto'><table class='tbl'>");
                o.println("      <thead><tr><th>#</th><th>From</th><th>To</th><th>Message</th><th>Date</th><th>Status</th></tr></thead><tbody>");
                int i = 1;
                for (Msg m : results) {
                    String pill = "SUCCESS".equals(m.getStatus())
                        ? "<span class='type-pill type-sent'>SUCCESS</span>"
                        : "<span class='type-pill type-error'>" + Html.esc(m.getErrorMsg() != null ? m.getErrorMsg() : "FAILED") + "</span>";

                    o.println("      <tr>");
                    o.println("        <td>" + (i++) + "</td>");
                    o.println("        <td class='mono'>" + Html.esc(cu.getMsisdn()) + "</td>");
                    o.println("        <td class='mono'>" + Html.esc(m.getReceiverMsisdn()) + "</td>");
                    o.println("        <td>" + Html.esc(m.getMsgBody()) + "</td>");
                    o.println("        <td style='white-space:nowrap;font-size:12px;color:#718096'>" + m.getMsgStamp() + "</td>");
                    o.println("        <td>" + pill + "</td>");
                    o.println("      </tr>");
                }
                o.println("    </tbody></table></div>");
            }
            o.println("  </div>");
        }

        o.println("</div>");
        o.println(PageTemplate.footer());
        o.println("</body></html>");
    }
}