<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.util.Html" %>
<%
    String error = request.getParameter("error");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>Register — Zajel</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    body { padding: 40px 20px 60px; }
    .register-wrap { max-width: 800px; margin: 0 auto; animation: fadeUp .45s ease both; }
    @keyframes fadeUp { from { opacity:0; transform:translateY(16px); } to { opacity:1; transform:none; } }
    .reg-header { text-align: center; margin-bottom: 32px; }
    .reg-logo {
      display: inline-flex; align-items: center; justify-content: center;
      width: 56px; height: 56px; background: #1a73e8; border-radius: 18px;
      margin-bottom: 14px;
      box-shadow: 0 8px 24px rgba(26,115,232,.3);
    }
    .reg-header h1 { font-size: 22px; font-weight: 800; margin-bottom: 4px; }
    .reg-header p  { color: var(--muted); font-size: 13.5px; }
    .steps { display: flex; align-items: center; justify-content: center; gap: 0; margin-bottom: 30px; }
    .step-dot {
      width: 36px; height: 36px; border-radius: 50%; border: 2px solid var(--border);
      display: flex; align-items: center; justify-content: center;
      font-size: 13px; font-weight: 700; color: var(--muted); background: var(--white);
      transition: all .3s; position: relative; z-index: 1;
    }
    .step-dot.active  { background: #1a73e8; border-color: #1a73e8; color: #fff; box-shadow: 0 4px 14px rgba(26,115,232,.4); }
    .step-dot.done    { background: #38a169; border-color: #38a169; color: #fff; }
    .step-label { font-size: 11px; font-weight: 600; color: var(--muted); margin-top: 6px; text-align: center; white-space: nowrap; }
    .step-label.active { color: #1a73e8; }
    .step-line { width: 80px; height: 2px; background: var(--border); border-radius: 99px; transition: background .3s; }
    .step-line.done { background: #38a169; }
    .step-wrap { display: flex; flex-direction: column; align-items: center; }
    .reg-card { background: var(--white); border: 1px solid var(--border); border-radius: 14px; box-shadow: 0 1px 3px rgba(0,0,0,.04); padding: 40px; }
    .step-panel { display: none; animation: fadeUp .35s ease both; }
    .step-panel.active { display: block; }
    .twilio-note {
      background: #eff6ff; border: 1px solid #bfdbfe;
      border-radius: 10px; padding: 14px 18px;
      font-size: 13px; color: #1e40af; margin-bottom: 22px; line-height: 1.7;
    }
    .twilio-note a { color: #1d4ed8; }
    .form-footer { text-align: center; margin-top: 22px; font-size: 13px; color: var(--muted); }
    .form-footer a { color: #1a73e8; text-decoration: none; font-weight: 600; }
    .step-actions { display: flex; gap: 12px; margin-top: 28px; }
    .step-actions .btn { flex: 1; padding: 13px; font-size: 14.5px; }
    .panel-title { font-size: 16px; font-weight: 800; margin-bottom: 4px; color: var(--text); }
    .panel-sub   { font-size: 13px; color: var(--muted); margin-bottom: 24px; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 18px; }
    .form-grid .form-group { margin-bottom: 18px; }
    .form-grid .span-2 { grid-column: span 2; }
    @media (max-width: 600px) { .form-grid { grid-template-columns: 1fr; } .form-grid .span-2 { grid-column: span 1; } }
    .reg-card .form-group input:focus,
    .reg-card .form-group select:focus {
      border-color: #1a73e8;
      box-shadow: 0 0 0 3px rgba(26,115,232,.12);
    }
  </style>
</head>
<body>
<div class='register-wrap'>
  <div class='reg-header'>
    <div class='reg-logo'><img src='<%= ctx %>/logo.svg' alt='Zajel' style='width:72px;height:72px;object-fit:contain;'/></div>
    <h1>Create your account</h1>
    <p>Two quick steps and you're ready to go</p>
  </div>
  <div class='steps'>
    <div class='step-wrap'>
      <div class='step-dot active' id='dot1'>1</div>
      <div class='step-label active' id='lbl1'>Your Info</div>
    </div>
    <div class='step-line' id='line1'></div>
    <div class='step-wrap'>
      <div class='step-dot' id='dot2'>2</div>
      <div class='step-label' id='lbl2'>Twilio Setup</div>
    </div>
  </div>
  <div class='reg-card'>
    <% if (error != null && !error.isEmpty()) { %>
        <div class='alert alert-err'>⚠️ <%= Html.esc(error) %></div>
    <% } %>
    <form action='<%= ctx %>/register' method='POST' novalidate id='regForm'>
      <div class='step-panel active' id='panel1'>
        <p class='panel-title'>Personal Information</p>
        <p class='panel-sub'>Tell us a bit about yourself to get started.</p>
        <div class='form-grid'>
          <div class='form-group'>
            <label for='name'>Full Name *</label>
            <input type='text' id='name' name='name' placeholder='John Doe' required/>
          </div>
          <div class='form-group'>
            <label for='email'>Email Address *</label>
            <input type='email' id='email' name='email' placeholder='john@example.com' required/>
          </div>
          <div class='form-group'>
            <label for='birthday'>Birthday *</label>
            <input type='date' id='birthday' name='birthday' required/>
          </div>
          <div class='form-group'>
            <label for='job'>Job Title</label>
            <input type='text' id='job' name='job' placeholder='Software Engineer'/>
          </div>
          <div class='form-group span-2'>
            <label for='address'>Address</label>
            <input type='text' id='address' name='address' placeholder='123 Main St, City'/>
          </div>
          <div class='form-group'>
            <label for='password'>Password *</label>
            <input type='password' id='password' name='password' placeholder='Min. 8 characters' required autocomplete='new-password'/>
          </div>
          <div class='form-group'>
            <label for='confirm'>Confirm Password *</label>
            <input type='password' id='confirm' name='confirm' placeholder='••••••••' required autocomplete='new-password'/>
          </div>
        </div>
        <div class='step-actions'>
          <button type='button' class='btn btn-primary' onclick='goStep2()'>
            Next — Twilio Setup →
          </button>
        </div>
      </div>
      <div class='step-panel' id='panel2'>
        <p class='panel-title'>Twilio Configuration</p>
        <p class='panel-sub'>Connect your Twilio account to start sending SMS.</p>
        <div class='twilio-note'>
          🔒 Your credentials are stored securely and only used for sending SMS on your behalf.
          Find them in the <a href='https://console.twilio.com' target='_blank' rel='noopener'>Twilio Console</a>.
        </div>
        <div class='form-grid'>
          <div class='form-group'>
            <label for='msisdn'>Phone Number (MSISDN) *</label>
            <input type='tel' id='msisdn' name='msisdn' placeholder='+1234567890' required/>
          </div>
          <div class='form-group'>
            <label for='senderid'>Sender ID *</label>
            <input type='text' id='senderid' name='senderid' placeholder='MyBrand' required/>
          </div>
          <div class='form-group span-2'>
            <label for='sid'>Account SID *</label>
            <input type='text' id='sid' name='sid' placeholder='ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx' required style='font-family:monospace;font-size:13px'/>
          </div>
          <div class='form-group span-2'>
            <label for='token'>Auth Token *</label>
            <input type='password' id='token' name='token' placeholder='••••••••••••••••••••••••••••••••' required style='font-family:monospace'/>
          </div>
        </div>
        <div class='step-actions'>
          <button type='button' class='btn btn-outline' onclick='goStep1()' style='flex:.4'>
            ← Back
          </button>
          <button type='submit' class='btn btn-primary'>
            Register &amp; Send Verification ✓
          </button>
        </div>
      </div>
    </form>
    <div class='form-footer'>
      Already have an account? <a href='<%= ctx %>/login'>Sign in</a>
    </div>
  </div>
</div>
<script>
function goStep2() {
  var name     = document.getElementById('name');
  var email    = document.getElementById('email');
  var password = document.getElementById('password');
  var confirm  = document.getElementById('confirm');
  var birthday = document.getElementById('birthday');

  if (!name.value.trim() || !email.value.trim() || !password.value || !birthday.value) {
    alert('Please fill in all required fields.'); return;
  }
  if (password.value !== confirm.value) {
    alert('Passwords do not match.'); return;
  }
  if (password.value.length < 8) {
    alert('Password must be at least 8 characters.'); return;
  }

  document.getElementById('panel1').classList.remove('active');
  document.getElementById('panel2').classList.add('active');

  document.getElementById('dot1').classList.remove('active');
  document.getElementById('dot1').classList.add('done');
  document.getElementById('dot1').textContent = '✓';
  document.getElementById('lbl1').classList.remove('active');

  document.getElementById('line1').classList.add('done');

  document.getElementById('dot2').classList.add('active');
  document.getElementById('lbl2').classList.add('active');
}

function goStep1() {
  document.getElementById('panel2').classList.remove('active');
  document.getElementById('panel1').classList.add('active');

  document.getElementById('dot1').classList.add('active');
  document.getElementById('dot1').classList.remove('done');
  document.getElementById('dot1').textContent = '1';
  document.getElementById('lbl1').classList.add('active');

  document.getElementById('line1').classList.remove('done');

  document.getElementById('dot2').classList.remove('active');
  document.getElementById('lbl2').classList.remove('active');
}
</script>
</body>
</html>
