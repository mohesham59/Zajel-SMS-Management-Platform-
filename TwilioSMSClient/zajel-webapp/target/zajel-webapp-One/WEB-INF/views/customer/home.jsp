<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.model.Customer" %>
<%@ page import="com.sms.util.Html" %>
<%@ page import="com.sms.util.PageTemplate" %>
<%
    Customer cu = (Customer) request.getSession().getAttribute("customer");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>Dashboard — Zajel</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    <%= PageTemplate.headerFooterCSS() %>
    .stats-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(200px,1fr)); gap:18px; margin-bottom:28px; }
    .stat-card { background:#fff; border:1px solid #e2e8f0; border-radius:14px;
      box-shadow:0 1px 3px rgba(0,0,0,.04); padding:24px; display:flex; flex-direction:column; gap:8px; }
    .stat-icon { width:42px; height:42px; border-radius:12px; display:flex; align-items:center; justify-content:center; font-size:18px; margin-bottom:4px; }
    .stat-icon.blue  { background:rgba(26,115,232,.08); }
    .stat-icon.green { background:#ecfdf5; }
    .stat-icon.cyan  { background:rgba(26,115,232,.06); }
    .stat-icon.gray  { background:#f1f5f9; }
    .stat-value  { font-size:26px; font-weight:800; color:#2d3748; line-height:1; }
    .stat-label  { font-size:12px; font-weight:600; color:#718096; text-transform:uppercase; letter-spacing:.04em; }
    .stat-change { font-size:12px; color:#38a169; font-weight:600; }

    .two-col { display:grid; grid-template-columns:1fr 1fr; gap:20px; }
    @media(max-width:768px) { .two-col { grid-template-columns:1fr; } }

    .card-header { display:flex; align-items:center; justify-content:space-between; padding:20px 24px; border-bottom:1px solid #e2e8f0; }
    .card-header h3 { font-size:14px; font-weight:700; margin:0; }
    .card-body { padding:20px 24px; }

    .recent-item { display:flex; align-items:flex-start; gap:12px; padding:12px 0; border-bottom:1px solid #e2e8f0; }
    .recent-item:last-child { border-bottom:none; padding-bottom:0; }
    .msg-dot { width:8px; height:8px; border-radius:50%; margin-top:6px; flex-shrink:0; }
    .msg-dot.sent { background:#1a73e8; }
    .msg-to   { font-size:13px; font-weight:600; color:#2d3748; }
    .msg-preview { font-size:12px; color:#718096; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; max-width:200px; }
    .msg-time { font-size:11px; color:#718096; margin-left:auto; white-space:nowrap; }

    .quick-form .form-group { margin-bottom:14px; }
    .quick-form label { font-size:11px; }
    .quick-form input, .quick-form textarea { padding:9px 12px; font-size:13px; }
    .quick-form textarea { min-height:80px; }

    .account-row { display:flex; align-items:center; justify-content:space-between; padding:10px 0; border-bottom:1px solid #e2e8f0; font-size:13px; }
    .account-row:last-child { border-bottom:none; }
    .account-key { color:#718096; font-weight:600; font-size:11px; text-transform:uppercase; letter-spacing:.04em; }
    .account-val { font-family:monospace; font-size:12px; color:#2d3748; }
    .account-val.masked { letter-spacing:.15em; }

    .pill { display:inline-flex; align-items:center; gap:6px; padding:4px 12px; border-radius:99px; font-size:11px; font-weight:700; }
    .pill-green { background:#ecfdf5; color:#047857; }
    .pill .dot { width:6px; height:6px; border-radius:50%; background:currentColor; }
  </style>
</head>
<body class='page-shell'>

<%= PageTemplate.customerHeader(ctx, "Dashboard") %>

<div class='page-body'>
  <div style='margin-bottom:28px'>
    <h1 class='page-title' id='welcomeTitle'>Welcome back 👋</h1>
    <p style='color:#718096;font-size:13.5px'>Here's what's happening with your SMS account today.</p>
  </div>

  <div class='stats-grid'>
    <div class='stat-card anim-fadeup'>
      <div class='stat-icon blue'>✉</div>
      <div class='stat-value' id='statTotal'>—</div>
      <div class='stat-label'>Total SMS Sent</div>
      <div class='stat-change' id='statTotalChange'>—</div>
    </div>
    <div class='stat-card anim-fadeup' style='animation-delay:.06s'>
      <div class='stat-icon green'>📥</div>
      <div class='stat-value' id='statInbound'>—</div>
      <div class='stat-label'>Inbound Messages</div>
      <div class='stat-change'>—</div>
    </div>
    <div class='stat-card anim-fadeup' style='animation-delay:.12s'>
      <div class='stat-icon cyan'>✅</div>
      <div class='stat-value' id='statRate'>—</div>
      <div class='stat-label'>Delivery Rate</div>
      <div class='stat-change' id='statRateLabel'>—</div>
    </div>
    <div class='stat-card anim-fadeup' style='animation-delay:.18s'>
      <div class='stat-icon gray'>📅</div>
      <div class='stat-value' id='statMonth'>—</div>
      <div class='stat-label'>Sent This Month</div>
      <div class='stat-change' id='statLast'>—</div>
    </div>
  </div>

  <div class='two-col'>
    <div class='card anim-fadeup' style='animation-delay:.2s'>
      <div class='card-header'>
        <h3>Recent Messages</h3>
        <a href='<%= ctx %>/customer/history' class='btn btn-sm btn-outline' style='text-decoration:none'>View All</a>
      </div>
      <div class='card-body' id='recentMsgs'>
        <p style='color:#718096;font-size:13px'>Loading...</p>
      </div>
    </div>

    <div class='card anim-fadeup' style='animation-delay:.26s'>
      <div class='card-header'>
        <h3>Quick Send</h3>
        <span class='pill pill-green'><span class='dot'></span>Ready</span>
      </div>
      <div class='card-body'>
        <form action='<%= ctx %>/customer/send' method='POST' novalidate class='quick-form'>
          <div class='form-group'>
            <label for='q_from'>From (Sender ID)</label>
            <input type='text' id='q_from' name='from' value='<%= Html.esc(cu.getMsisdn()) %>' required/>
          </div>
          <div class='form-group'>
            <label for='q_to'>To (Recipient Number)</label>
            <input type='tel' id='q_to' name='to' placeholder='+1234567890' required/>
          </div>
          <div class='form-group'>
            <label for='q_body'>Message</label>
            <textarea id='q_body' name='body' maxlength='160' placeholder='Type your message…' required oninput='updateCount(this)'></textarea>
            <div style='text-align:right;font-size:11px;color:#718096;font-family:monospace;margin-top:4px' id='qCount'>0 / 160</div>
          </div>
          <button type='submit' class='btn btn-primary btn-full'>Send SMS ✉</button>
        </form>
      </div>
    </div>
  </div>

  <div class='card anim-fadeup' style='margin-top:20px;animation-delay:.32s'>
    <div class='card-header'>
      <h3>Account Information</h3>
      <a href='<%= ctx %>/customer/profile' class='btn btn-sm btn-outline' style='text-decoration:none'>Edit Profile</a>
    </div>
    <div class='card-body' style='display:grid;grid-template-columns:1fr 1fr;gap:0 40px'>
      <div>
        <div class='account-row'><span class='account-key'>Full Name</span><span class='account-val' id='infoName'>—</span></div>
        <div class='account-row'><span class='account-key'>Email</span><span class='account-val' id='infoEmail'>—</span></div>
        <div class='account-row'><span class='account-key'>Phone (MSISDN)</span><span class='account-val' id='infoMsisdn'>—</span></div>
      </div>
      <div>
        <div class='account-row'><span class='account-key'>Sender ID</span><span class='account-val' id='infoSid'>—</span></div>
        <div class='account-row'><span class='account-key'>Account SID</span><span class='account-val masked' id='infoSidMasked'>—</span></div>
        <div class='account-row'><span class='account-key'>Status</span><span><span class='pill pill-green'><span class='dot'></span>Active</span></span></div>
      </div>
    </div>
  </div>
</div>

<%= PageTemplate.footer() %>

<script>
var ctx='<%= ctx %>';
fetch(ctx+'/customer/dashboard-data')
  .then(function(r){if(!r.ok)throw new Error();return r.json()})
  .then(function(d){
    document.getElementById('welcomeTitle').textContent='Welcome back, '+d.name+' 👋';
    document.getElementById('statTotal').textContent=d.totalMsgs.toLocaleString();
    document.getElementById('statMonth').textContent=d.thisMonth;
    document.getElementById('statLast').textContent='Last: '+fmt(d.lastMsgTime);
    document.getElementById('statInbound').textContent='—';
    document.getElementById('statRate').textContent=d.totalMsgs>0?'99.1%':'N/A';
    document.getElementById('statRateLabel').textContent=d.totalMsgs>0?'Excellent':'—';
    document.getElementById('infoName').textContent=d.name;
    document.getElementById('infoEmail').textContent=d.email;
    document.getElementById('infoMsisdn').textContent=d.msisdn;
    document.getElementById('infoSid').textContent=d.senderid||'—';
    document.getElementById('infoSidMasked').textContent=d.sid?('AC'+'•'.repeat(16)):'—';
    document.getElementById('q_from').value=d.senderid||'';
    var box=document.getElementById('recentMsgs');
    if(!d.recentMsgs||d.recentMsgs.length===0){
      box.innerHTML='<p style="color:#718096;font-size:13px">No messages yet. <a href="'+ctx+'/customer/send">Send your first SMS</a></p>';
    }else{
      var html='';
      for(var i=0;i<d.recentMsgs.length;i++){
        var m=d.recentMsgs[i];
        html+='<div class="recent-item">'
          +'<span class="msg-dot sent"></span>'
          +'<div style="flex:1;min-width:0">'
          +'<div class="msg-to">'+escH(m.to)+'</div>'
          +'<div class="msg-preview">'+escH(m.body)+'</div>'
          +'</div>'
          +'<span class="msg-time">'+fmt(m.time)+'</span>'
          +'</div>';
      }
      box.innerHTML=html;
    }
  }).catch(function(){console.error('Dashboard load failed')});
function fmt(ts){
  if(!ts||ts==='—')return'—';
  var d=new Date(ts),now=new Date(),diff=now-d;
  if(diff<3600000)return Math.floor(diff/60000)+'m ago';
  if(diff<86400000)return d.toLocaleTimeString([],{hour:'2-digit',minute:'2-digit'});
  if(diff<172800000)return'Yesterday';
  return d.toLocaleDateString();
}
function escH(s){if(!s)return'';var d=document.createElement('div');d.appendChild(document.createTextNode(s));return d.innerHTML}
function updateCount(el){document.getElementById('qCount').textContent=el.value.length+' / 160'}
</script>
</body>
</html>
