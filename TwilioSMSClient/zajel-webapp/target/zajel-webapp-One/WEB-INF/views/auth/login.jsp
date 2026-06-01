<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.util.Html" %>
<%
    String error = request.getParameter("error");
    String success = request.getParameter("success");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>Login — Zajel</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    body { display:flex; height:100vh; overflow:hidden; background:var(--navy); }
    .auth-left {
      flex:1; position:relative; overflow:hidden;
      display:flex; align-items:center; justify-content:center;
      background:radial-gradient(ellipse at 35% 50%, #0d2137 0%, #060e1a 65%);
    }
    .bubble-layer { position:absolute; inset:0; pointer-events:none; }
    .bubble {
      position:absolute; bottom:-80px; padding:9px 16px;
      font-size:12px; font-weight:600; white-space:nowrap;
      animation:floatUp linear infinite; opacity:0;
    }
    .bubble.out { background:rgba(26,115,232,.18); border:1px solid rgba(26,115,232,.35); color:#93c5fd; border-radius:16px 16px 3px 16px; }
    .bubble.in  { background:rgba(255,255,255,.06); border:1px solid rgba(255,255,255,.12); color:#a0aec0; border-radius:16px 16px 16px 3px; }
    @keyframes floatUp {
      0%   { transform:translateY(0); opacity:0; }
      8%   { opacity:.85; }
      85%  { opacity:.85; }
      100% { transform:translateY(-110vh); opacity:0; }
    }
    .ring {
      position:absolute; border-radius:50%;
      border:1px solid rgba(26,115,232,.08);
      top:50%; left:50%; transform:translate(-50%,-50%);
      animation:rpulse 3.5s ease-out infinite;
    }
    .ring:nth-child(2) { animation-delay:1.15s; }
    .ring:nth-child(3) { animation-delay:2.3s; }
    @keyframes rpulse {
      0%   { width:80px;  height:80px;  opacity:.7; }
      100% { width:600px; height:600px; opacity:0;  }
    }
    .brand { text-align:center; z-index:1; padding:0 48px; }
    .brand-icon {
      width:72px; height:72px; margin:0 auto 24px;
      background:var(--blue, #1a73e8); border-radius:22px;
      display:flex; align-items:center; justify-content:center;
      box-shadow:0 12px 40px rgba(26,115,232,.45);
      animation:fadeUp .5s .1s ease both;
    }
    .brand h1 { font-size:32px; font-weight:800; color:#fff; margin-bottom:14px; letter-spacing:-.5px; animation:fadeUp .5s .2s ease both; }
    .brand p  { font-size:15px; color:#718096; max-width:300px; margin:0 auto 36px; line-height:1.8; animation:fadeUp .5s .3s ease both; }
    .brand-stats { display:flex; gap:40px; justify-content:center; animation:fadeUp .5s .4s ease both; }
    .bstat-val { font-size:22px; font-weight:800; color:#1a73e8; }
    .bstat-lbl { font-size:11px; color:#4a5568; margin-top:2px; }
    .auth-right {
      width:580px; flex-shrink:0; background:var(--white);
      display:flex; align-items:center; justify-content:center;
      padding:60px 68px; overflow-y:auto;
    }
    .login-form { width:100%; animation:slideIn .45s .1s ease both; }
    .login-form h2  { font-size:30px; font-weight:800; margin-bottom:8px; }
    .login-form .sub { color:var(--muted); font-size:15px; margin-bottom:38px; }
    .login-form .form-group { margin-bottom:22px; }
    .login-form .form-group label { font-size:12.5px; margin-bottom:9px; }
    .login-form .form-group input,
    .login-form .form-group select { padding:14px 16px; font-size:15px; }
    .login-form .form-group input:focus,
    .login-form .form-group select:focus {
      border-color:#1a73e8;
      box-shadow:0 0 0 3px rgba(26,115,232,.12);
    }
    .login-form .btn { padding:15px; font-size:15px; }
    .divider-or {
      text-align:center; color:var(--muted); font-size:13.5px;
      position:relative; margin:26px 0;
    }
    .divider-or::before, .divider-or::after {
      content:''; position:absolute; top:50%;
      width:36%; height:1px; background:var(--border);
    }
    .divider-or::before { left:0; }
    .divider-or::after  { right:0; }
    .login-btn-primary {
      background:#1a73e8 !important; color:#fff;
      box-shadow:0 4px 14px rgba(26,115,232,.35);
    }
    .login-btn-primary:hover { background:#0d47a1 !important; }
    .login-btn-outline {
      background:transparent; color:#1a73e8;
      border:1.5px solid #1a73e8;
    }
    .login-btn-outline:hover { background:rgba(26,115,232,.06); }
    @keyframes fadeUp  { from { opacity:0; transform:translateY(16px); } to { opacity:1; transform:none; } }
    @keyframes slideIn { from { opacity:0; transform:translateX(20px); } to { opacity:1; transform:none; } }
    @media (max-width:980px) { .auth-left { display:none; } .auth-right { width:100%; padding:48px 36px; } }
  </style>
</head>
<body>
<div class='auth-left'>
  <div class='ring'></div><div class='ring'></div><div class='ring'></div>
  <div class='bubble-layer' id='bubbleLayer'></div>
  <div class='brand'>
    <div class='brand-icon'><img src='<%= ctx %>/logo.svg' alt='Zajel' style='width:72px;height:72px;object-fit:contain;'/></div>
    <h1>Zajel</h1>
    <p>Send, track, and manage SMS messages reliably and at scale.</p>
    <div class='brand-stats'>
      <div><div class='bstat-val'>99.9%</div><div class='bstat-lbl'>Uptime</div></div>
      <div><div class='bstat-val'>&lt;2s</div><div class='bstat-lbl'>Delivery</div></div>
      <div><div class='bstat-val'>Global</div><div class='bstat-lbl'>Reach</div></div>
    </div>
  </div>
</div>
<div class='auth-right'>
  <div class='login-form'>
    <h2>Welcome back 👋</h2>
    <p class='sub'>Sign in to your SMS account to continue</p>
    <% if (error != null && !error.isEmpty()) { %>
        <div class='alert alert-err'>⚠️ <%= Html.esc(error) %></div>
    <% } %>
    <% if (success != null && !success.isEmpty()) { %>
        <div class='alert alert-ok'>✅ <%= Html.esc(success) %></div>
    <% } %>
    <form action='<%= ctx %>/login' method='POST'>
      <div class='form-group'>
        <label for='email'>Email Address</label>
        <input type='email' id='email' name='email' placeholder='name@company.com' required autocomplete='email'/>
      </div>
      <div class='form-group'>
        <label for='password'>Password</label>
        <input type='password' id='password' name='password' placeholder='••••••••' required autocomplete='current-password'/>
      </div>
      <div class='form-group' style='margin-bottom:32px'>
        <label for='role'>Sign in as</label>
        <select id='role' name='role'>
          <option value='customer'>Customer</option>
          <option value='admin'>Administrator</option>
        </select>
      </div>
      <button type='submit' class='btn login-btn-primary btn-full'>Sign In</button>
    </form>
    <div class='divider-or'> Don't have an account? </div>
    <a href='<%= ctx %>/register' class='btn login-btn-outline btn-full' style='text-decoration:none'>Create an account</a>
  </div>
</div>
<script>
var MSGS=['📦 Order shipped!','🔔 OTP: 748291','✅ Payment confirmed',
  '📱 Meeting at 3 PM','🚀 Welcome aboard!','💬 New message',
  '🔐 Login alert','📨 Code: 391027','👋 Hello there!'];
function spawn(){
  var el=document.createElement('div');
  el.className='bubble '+(Math.random()>.5?'out':'in');
  el.textContent=MSGS[Math.floor(Math.random()*MSGS.length)];
  var dur=6+Math.random()*5, delay=Math.random()*1.5;
  el.style.cssText='left:'+(4+Math.random()*88)+'%;animation-duration:'+dur+'s;animation-delay:'+delay+'s';
  document.getElementById('bubbleLayer').appendChild(el);
  setTimeout(function(){el.remove()},(dur+delay+.5)*1000);
}
setInterval(spawn,1100);
for(var i=0;i<5;i++) setTimeout(spawn,i*280);
</script>
</body>
</html>
