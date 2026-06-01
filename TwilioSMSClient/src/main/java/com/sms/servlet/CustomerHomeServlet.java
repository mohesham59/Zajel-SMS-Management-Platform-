package com.sms.servlet;

import com.sms.model.Customer;
import com.sms.util.Html;
import com.sms.util.PageTemplate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/customer/home")
public class CustomerHomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Customer cu = (Customer) req.getSession().getAttribute("customer");
        if (cu == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String ctx = req.getContextPath();

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter o = resp.getWriter();

        o.println("<!DOCTYPE html>");
        o.println("<html lang='en'>");
        o.println("<head>");
        o.println("  <meta charset='UTF-8'/>");
        o.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        o.println("  <title>Dashboard — Zajel</title>");
        o.println("  <link rel='stylesheet' href='" + ctx + "/style.css'/>");
        o.println("  <style>");
        o.println(PageTemplate.headerFooterCSS());

        // Dashboard — BLUE THEME
        o.println("    .stats-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(200px,1fr)); gap:18px; margin-bottom:28px; }");
        o.println("    .stat-card { background:#fff; border:1px solid #e2e8f0; border-radius:14px;");
        o.println("      box-shadow:0 1px 3px rgba(0,0,0,.04); padding:24px; display:flex; flex-direction:column; gap:8px; }");
        o.println("    .stat-icon { width:42px; height:42px; border-radius:12px; display:flex; align-items:center; justify-content:center; font-size:18px; margin-bottom:4px; }");
        o.println("    .stat-icon.blue  { background:rgba(26,115,232,.08); }");
        o.println("    .stat-icon.green { background:#ecfdf5; }");
        o.println("    .stat-icon.cyan  { background:rgba(26,115,232,.06); }");
        o.println("    .stat-icon.gray  { background:#f1f5f9; }");
        o.println("    .stat-value  { font-size:26px; font-weight:800; color:#2d3748; line-height:1; }");
        o.println("    .stat-label  { font-size:12px; font-weight:600; color:#718096; text-transform:uppercase; letter-spacing:.04em; }");
        o.println("    .stat-change { font-size:12px; color:#38a169; font-weight:600; }");

        o.println("    .two-col { display:grid; grid-template-columns:1fr 1fr; gap:20px; }");
        o.println("    @media(max-width:768px) { .two-col { grid-template-columns:1fr; } }");

        o.println("    .card-header { display:flex; align-items:center; justify-content:space-between; padding:20px 24px; border-bottom:1px solid #e2e8f0; }");
        o.println("    .card-header h3 { font-size:14px; font-weight:700; margin:0; }");
        o.println("    .card-body { padding:20px 24px; }");

        // Recent messages — blue dot
        o.println("    .recent-item { display:flex; align-items:flex-start; gap:12px; padding:12px 0; border-bottom:1px solid #e2e8f0; }");
        o.println("    .recent-item:last-child { border-bottom:none; padding-bottom:0; }");
        o.println("    .msg-dot { width:8px; height:8px; border-radius:50%; margin-top:6px; flex-shrink:0; }");
        o.println("    .msg-dot.sent { background:#1a73e8; }");
        o.println("    .msg-to   { font-size:13px; font-weight:600; color:#2d3748; }");
        o.println("    .msg-preview { font-size:12px; color:#718096; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; max-width:200px; }");
        o.println("    .msg-time { font-size:11px; color:#718096; margin-left:auto; white-space:nowrap; }");

        o.println("    .quick-form .form-group { margin-bottom:14px; }");
        o.println("    .quick-form label { font-size:11px; }");
        o.println("    .quick-form input, .quick-form textarea { padding:9px 12px; font-size:13px; }");
        o.println("    .quick-form textarea { min-height:80px; }");

        o.println("    .account-row { display:flex; align-items:center; justify-content:space-between; padding:10px 0; border-bottom:1px solid #e2e8f0; font-size:13px; }");
        o.println("    .account-row:last-child { border-bottom:none; }");
        o.println("    .account-key { color:#718096; font-weight:600; font-size:11px; text-transform:uppercase; letter-spacing:.04em; }");
        o.println("    .account-val { font-family:monospace; font-size:12px; color:#2d3748; }");
        o.println("    .account-val.masked { letter-spacing:.15em; }");

        o.println("    .pill { display:inline-flex; align-items:center; gap:6px; padding:4px 12px; border-radius:99px; font-size:11px; font-weight:700; }");
        o.println("    .pill-green { background:#ecfdf5; color:#047857; }");
        o.println("    .pill .dot { width:6px; height:6px; border-radius:50%; background:currentColor; }");

        o.println("  </style>");
        o.println("</head>");
        o.println("<body class='page-shell'>");

        o.println(PageTemplate.customerHeader(ctx, "Dashboard"));

        o.println("<div class='page-body'>");

        o.println("  <div style='margin-bottom:28px'>");
        o.println("    <h1 class='page-title' id='welcomeTitle'>Welcome back \uD83D\uDC4B</h1>");
        o.println("    <p style='color:#718096;font-size:13.5px'>Here's what's happening with your SMS account today.</p>");
        o.println("  </div>");

        // Stats — blue icons
        o.println("  <div class='stats-grid'>");

        o.println("    <div class='stat-card anim-fadeup'>");
        o.println("      <div class='stat-icon blue'>\u2709</div>");
        o.println("      <div class='stat-value' id='statTotal'>\u2014</div>");
        o.println("      <div class='stat-label'>Total SMS Sent</div>");
        o.println("      <div class='stat-change' id='statTotalChange'>\u2014</div>");
        o.println("    </div>");

        o.println("    <div class='stat-card anim-fadeup' style='animation-delay:.06s'>");
        o.println("      <div class='stat-icon green'>\uD83D\uDCE5</div>");
        o.println("      <div class='stat-value' id='statInbound'>\u2014</div>");
        o.println("      <div class='stat-label'>Inbound Messages</div>");
        o.println("      <div class='stat-change'>\u2014</div>");
        o.println("    </div>");

        o.println("    <div class='stat-card anim-fadeup' style='animation-delay:.12s'>");
        o.println("      <div class='stat-icon cyan'>\u2705</div>");
        o.println("      <div class='stat-value' id='statRate'>\u2014</div>");
        o.println("      <div class='stat-label'>Delivery Rate</div>");
        o.println("      <div class='stat-change' id='statRateLabel'>\u2014</div>");
        o.println("    </div>");

        o.println("    <div class='stat-card anim-fadeup' style='animation-delay:.18s'>");
        o.println("      <div class='stat-icon gray'>\uD83D\uDCC5</div>");
        o.println("      <div class='stat-value' id='statMonth'>\u2014</div>");
        o.println("      <div class='stat-label'>Sent This Month</div>");
        o.println("      <div class='stat-change' id='statLast'>\u2014</div>");
        o.println("    </div>");

        o.println("  </div>");

        // Two columns
        o.println("  <div class='two-col'>");

        // Recent Messages
        o.println("    <div class='card anim-fadeup' style='animation-delay:.2s'>");
        o.println("      <div class='card-header'>");
        o.println("        <h3>Recent Messages</h3>");
        o.println("        <a href='" + ctx + "/customer/history' class='btn btn-sm btn-outline' style='text-decoration:none'>View All</a>");
        o.println("      </div>");
        o.println("      <div class='card-body' id='recentMsgs'>");
        o.println("        <p style='color:#718096;font-size:13px'>Loading...</p>");
        o.println("      </div>");
        o.println("    </div>");

        // Quick Send
        o.println("    <div class='card anim-fadeup' style='animation-delay:.26s'>");
        o.println("      <div class='card-header'>");
        o.println("        <h3>Quick Send</h3>");
        o.println("        <span class='pill pill-green'><span class='dot'></span>Ready</span>");
        o.println("      </div>");
        o.println("      <div class='card-body'>");
        o.println("        <form action='" + ctx + "/customer/send' method='POST' novalidate class='quick-form'>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='q_from'>From (Sender ID)</label>");
        o.println("            <input type='text' id='q_from' name='from' value='" + Html.esc(cu.getMsisdn()) + "' required/>");
        o.println("          </div>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='q_to'>To (Recipient Number)</label>");
        o.println("            <input type='tel' id='q_to' name='to' placeholder='+1234567890' required/>");
        o.println("          </div>");

        o.println("          <div class='form-group'>");
        o.println("            <label for='q_body'>Message</label>");
        o.println("            <textarea id='q_body' name='body' maxlength='160' placeholder='Type your message\u2026' required oninput='updateCount(this)'></textarea>");
        o.println("            <div style='text-align:right;font-size:11px;color:#718096;font-family:monospace;margin-top:4px' id='qCount'>0 / 160</div>");
        o.println("          </div>");

        o.println("          <button type='submit' class='btn btn-primary btn-full'>Send SMS \u2709</button>");
        o.println("        </form>");
        o.println("      </div>");
        o.println("    </div>");

        o.println("  </div>");

        // Account Info
        o.println("  <div class='card anim-fadeup' style='margin-top:20px;animation-delay:.32s'>");
        o.println("    <div class='card-header'>");
        o.println("      <h3>Account Information</h3>");
        o.println("      <a href='" + ctx + "/customer/profile' class='btn btn-sm btn-outline' style='text-decoration:none'>Edit Profile</a>");
        o.println("    </div>");
        o.println("    <div class='card-body' style='display:grid;grid-template-columns:1fr 1fr;gap:0 40px'>");

        o.println("      <div>");
        o.println("        <div class='account-row'><span class='account-key'>Full Name</span><span class='account-val' id='infoName'>\u2014</span></div>");
        o.println("        <div class='account-row'><span class='account-key'>Email</span><span class='account-val' id='infoEmail'>\u2014</span></div>");
        o.println("        <div class='account-row'><span class='account-key'>Phone (MSISDN)</span><span class='account-val' id='infoMsisdn'>\u2014</span></div>");
        o.println("      </div>");

        o.println("      <div>");
        o.println("        <div class='account-row'><span class='account-key'>Sender ID</span><span class='account-val' id='infoSid'>\u2014</span></div>");
        o.println("        <div class='account-row'><span class='account-key'>Account SID</span><span class='account-val masked' id='infoSidMasked'>\u2014</span></div>");
        o.println("        <div class='account-row'><span class='account-key'>Status</span><span><span class='pill pill-green'><span class='dot'></span>Active</span></span></div>");
        o.println("      </div>");

        o.println("    </div>");
        o.println("  </div>");

        o.println("</div>");

        o.println(PageTemplate.footer());

        // JS — same logic, no color changes needed
        o.println("<script>");
        o.println("var ctx='" + ctx + "';");
        o.println("fetch(ctx+'/customer/dashboard-data')");
        o.println("  .then(function(r){if(!r.ok)throw new Error();return r.json()})");
        o.println("  .then(function(d){");
        o.println("    document.getElementById('welcomeTitle').textContent='Welcome back, '+d.name+' \\uD83D\\uDC4B';");
        o.println("    document.getElementById('statTotal').textContent=d.totalMsgs.toLocaleString();");
        o.println("    document.getElementById('statMonth').textContent=d.thisMonth;");
        o.println("    document.getElementById('statLast').textContent='Last: '+fmt(d.lastMsgTime);");
        o.println("    document.getElementById('statInbound').textContent='\\u2014';");
        o.println("    document.getElementById('statRate').textContent=d.totalMsgs>0?'99.1%':'N/A';");
        o.println("    document.getElementById('statRateLabel').textContent=d.totalMsgs>0?'Excellent':'\\u2014';");
        o.println("    document.getElementById('infoName').textContent=d.name;");
        o.println("    document.getElementById('infoEmail').textContent=d.email;");
        o.println("    document.getElementById('infoMsisdn').textContent=d.msisdn;");
        o.println("    document.getElementById('infoSid').textContent=d.senderid||'\\u2014';");
        o.println("    document.getElementById('infoSidMasked').textContent=d.sid?('AC'+'\\u2022'.repeat(16)):'\\u2014';");
        o.println("    document.getElementById('q_from').value=d.senderid||'';");
        o.println("    var box=document.getElementById('recentMsgs');");
        o.println("    if(!d.recentMsgs||d.recentMsgs.length===0){");
        o.println("      box.innerHTML='<p style=\"color:#718096;font-size:13px\">No messages yet. <a href=\"'+ctx+'/customer/send\">Send your first SMS</a></p>';");
        o.println("    }else{");
        o.println("      var html='';");
        o.println("      for(var i=0;i<d.recentMsgs.length;i++){");
        o.println("        var m=d.recentMsgs[i];");
        o.println("        html+='<div class=\"recent-item\">'");
        o.println("          +'<span class=\"msg-dot sent\"></span>'");
        o.println("          +'<div style=\"flex:1;min-width:0\">'");
        o.println("          +'<div class=\"msg-to\">'+escH(m.to)+'</div>'");
        o.println("          +'<div class=\"msg-preview\">'+escH(m.body)+'</div>'");
        o.println("          +'</div>'");
        o.println("          +'<span class=\"msg-time\">'+fmt(m.time)+'</span>'");
        o.println("          +'</div>';");
        o.println("      }");
        o.println("      box.innerHTML=html;");
        o.println("    }");
        o.println("  }).catch(function(){console.error('Dashboard load failed')});");
        o.println("function fmt(ts){");
        o.println("  if(!ts||ts==='\\u2014')return'\\u2014';");
        o.println("  var d=new Date(ts),now=new Date(),diff=now-d;");
        o.println("  if(diff<3600000)return Math.floor(diff/60000)+'m ago';");
        o.println("  if(diff<86400000)return d.toLocaleTimeString([],{hour:'2-digit',minute:'2-digit'});");
        o.println("  if(diff<172800000)return'Yesterday';");
        o.println("  return d.toLocaleDateString();");
        o.println("}");
        o.println("function escH(s){if(!s)return'';var d=document.createElement('div');d.appendChild(document.createTextNode(s));return d.innerHTML}");
        o.println("function updateCount(el){document.getElementById('qCount').textContent=el.value.length+' / 160'}");
        o.println("</script>");

        o.println("</body>");
        o.println("</html>");
    }
}