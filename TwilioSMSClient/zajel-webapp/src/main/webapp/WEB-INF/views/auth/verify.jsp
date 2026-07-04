<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.util.Html" %>
<%
    String error = request.getParameter("error");
    String info = request.getParameter("info");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>Verify — Zajel</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    body { display:flex; height:100vh; overflow:hidden; background:var(--navy); }

    /* ── Left Panel ───────────────────────────── */
    .auth-left {
      flex:1; position:relative; overflow:hidden;
      display:flex; align-items:center; justify-content:center;
      background:radial-gradient(ellipse at 35% 50%, #0d2137 0%, #060e1a 65%);
    }

    /* Pulsing rings */
    .ring {
      position:absolute; border-radius:50%;
      border:1px solid rgba(26,115,232,.08);
      top:50%; left:50%; transform:translate(-50%,-50%);
      animation:rpulse 3.5s ease-out infinite;
    }
    .ring:nth-child(2) { animation-delay:1.15s; }
    .ring:nth-child(3) { animation-delay:2.3s; }
    @keyframes rpulse {
      0%   { width:80px; height:80px; opacity:.7; }
      100% { width:600px; height:600px; opacity:0; }
    }

    /* Brand block */
    .brand { text-align:center; z-index:1; padding:0 48px; }
    .brand-icon {
      width:96px; height:96px; margin:0 auto 28px;
      background:var(--blue, #1a73e8); border-radius:26px;
      display:flex; align-items:center; justify-content:center;
      box-shadow:0 12px 40px rgba(26,115,232,.45);
      animation:fadeUp .5s .1s ease both;
      font-size:48px;
    }
    .brand h1 {
      font-size:28px; font-weight:800; color:#fff;
      margin-bottom:14px; letter-spacing:-.5px;
      animation:fadeUp .5s .2s ease both;
    }
    .brand p {
      font-size:15px; color:#718096; max-width:320px;
      margin:0 auto; line-height:1.8;
      animation:fadeUp .5s .3s ease both;
    }

    /* Floating code bubbles */
    .bubble-layer { position:absolute; inset:0; pointer-events:none; }
    .bubble {
      position:absolute; bottom:-80px; padding:9px 16px;
      font-size:12px; font-weight:600; white-space:nowrap;
      animation:floatUp linear infinite; opacity:0;
      background:rgba(26,115,232,.18); border:1px solid rgba(26,115,232,.35);
      color:#93c5fd; border-radius:16px 16px 3px 16px;
      font-family:var(--mono);
    }
    @keyframes floatUp {
      0%   { transform:translateY(0); opacity:0; }
      8%   { opacity:.85; }
      85%  { opacity:.85; }
      100% { transform:translateY(-110vh); opacity:0; }
    }

    /* ── Right Panel ──────────────────────────── */
    .auth-right {
      width:580px; flex-shrink:0; background:var(--white);
      display:flex; align-items:center; justify-content:center;
      padding:60px 68px; overflow-y:auto;
    }

    .verify-form { width:100%; max-width:400px; animation:slideIn .45s .1s ease both; }
    .verify-form h2 { font-size:28px; font-weight:800; margin-bottom:8px; }
    .verify-form .sub { color:var(--muted); font-size:15px; margin-bottom:32px; line-height:1.6; }

    /* Code input */
    .code-input {
      width:100%; padding:18px 16px; font-size:32px;
      text-align:center; letter-spacing:12px;
      border:2px solid var(--border); border-radius:14px;
      background:#f7fafc; color:var(--text);
      font-family:var(--mono);
      outline:none; transition:border .2s, box-shadow .2s;
      margin-bottom:24px;
    }
    .code-input:focus {
      border-color:var(--blue);
      box-shadow:0 0 0 4px rgba(26,115,232,.12);
      background:#fff;
    }
    .code-input::placeholder {
      letter-spacing:8px; color:#cbd5e0; font-size:28px;
    }

    /* Buttons */
    .btn-verify {
      width:100%; padding:16px; font-size:15px; font-weight:700;
      background:var(--blue); color:#fff; border:none;
      border-radius:12px; cursor:pointer;
      box-shadow:0 4px 14px rgba(26,115,232,.35);
      transition:background .2s, transform .08s;
    }
    .btn-verify:hover { background:var(--blue-dk); }
    .btn-verify:active { transform:scale(.97); }

    .btn-resend {
      width:100%; padding:14px; font-size:14px; font-weight:700;
      background:transparent; color:var(--blue);
      border:1.5px solid var(--blue); border-radius:12px;
      cursor:pointer; transition:background .2s, transform .08s;
      margin-top:12px;
    }
    .btn-resend:hover { background:rgba(26,115,232,.06); }
    .btn-resend:active { transform:scale(.97); }

    .divider-or {
      text-align:center; color:var(--muted); font-size:13px;
      position:relative; margin:24px 0;
    }
    .divider-or::before, .divider-or::after {
      content:''; position:absolute; top:50%;
      width:36%; height:1px; background:var(--border);
    }
    .divider-or::before { left:0; }
    .divider-or::after  { right:0; }

    .back-link {
      display:block; text-align:center; margin-top:28px;
      color:var(--muted); font-size:13.5px; text-decoration:none;
      font-weight:600; transition:color .2s;
    }
    .back-link:hover { color:var(--blue); }

    /* Animations */
    @keyframes fadeUp  { from { opacity:0; transform:translateY(16px); } to { opacity:1; transform:none; } }
    @keyframes slideIn { from { opacity:0; transform:translateX(20px); } to { opacity:1; transform:none; } }

    /* Responsive */
    @media (max-width:980px) {
      .auth-left { display:none; }
      .auth-right { width:100%; padding:48px 36px; }
    }
  </style>
</head>
<body>

<!-- ── Left Panel ──────────────────────────── -->
<div class='auth-left'>
  <div class='ring'></div><div class='ring'></div><div class='ring'></div>
  <div class='bubble-layer' id='bubbleLayer'></div>
  <div class='brand'>
    <div class='brand-icon'>🔐</div>
    <h1>Almost there!</h1>
    <p>We sent a 6-digit verification code to your phone. Enter it to activate your account.</p>
  </div>
</div>

<!-- ── Right Panel ─────────────────────────── -->
<div class='auth-right'>
  <div class='verify-form'>
    <h2>Verify your account 🛡️</h2>
    <p class='sub'>Enter the 6-digit code we sent to your phone number</p>

    <% if (error != null && !error.isEmpty()) { %>
      <div class='alert alert-err'>⚠️ <%= Html.esc(error) %></div>
    <% } %>
    <% if (info != null && !info.isEmpty()) { %>
      <div class='alert alert-ok'>✅ <%= Html.esc(info) %></div>
    <% } %>

    <form method='POST' action='<%= ctx %>/verify'>
      <input type='text' name='code' class='code-input' required
             maxlength='6' pattern='[0-9]{6}' inputmode='numeric'
             placeholder='••••••' autocomplete='one-time-code'/>
      <button type='submit' class='btn-verify'>Verify Code</button>
    </form>

    <div class='divider-or'>Didn't receive the code?</div>

    <form method='POST' action='<%= ctx %>/verify?action=resend'>
      <button type='submit' class='btn-resend'>📩 Resend Verification Code</button>
    </form>

    <a href='<%= ctx %>/login' class='back-link'>← Back to Sign In</a>
  </div>
</div>

<!-- ── Floating Code Bubbles ───────────────── -->
<script>
var CODES=['🔐 4 8 2 9 1 7','🔑 9 3 7 0 2 6','🛡️ 1 5 8 3 4 9',
  '✅ 6 2 0 7 1 8','📱 3 9 4 6 5 2','🔒 7 1 8 0 3 5',
  '📨 5 4 2 9 8 1','🔓 8 6 3 1 7 0'];
function spawn(){
  var el=document.createElement('div');
  el.className='bubble';
  el.textContent=CODES[Math.floor(Math.random()*CODES.length)];
  var dur=6+Math.random()*5, delay=Math.random()*1.5;
  el.style.cssText='left:'+(4+Math.random()*88)+'%;animation-duration:'+dur+'s;animation-delay:'+delay+'s';
  document.getElementById('bubbleLayer').appendChild(el);
  setTimeout(function(){el.remove()},(dur+delay+.5)*1000);
}
setInterval(spawn,1400);
for(var i=0;i<4;i++) setTimeout(spawn,i*350);
</script>

</body>
</html>
