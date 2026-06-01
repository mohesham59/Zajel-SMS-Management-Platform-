<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sms.model.Customer" %>
<%@ page import="com.sms.util.Html" %>
<%@ page import="com.sms.util.PageTemplate" %>
<%
    Customer cu = (Customer) request.getSession().getAttribute("customer");
    String ctx = request.getContextPath();
    String error = request.getParameter("error");
    String success = request.getParameter("success");
    String senderid = cu.getMsisdn();
%>
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8'/>
  <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
  <title>Send SMS — Zajel</title>
  <link rel='stylesheet' href='<%= ctx %>/style.css'/>
  <style>
    <%= PageTemplate.headerFooterCSS() %>
    .send-layout { display:flex; gap:56px; align-items:flex-start; max-width:1500px; margin:0 auto; }
    .send-card { flex:1.2; min-width:0; }

    .phone-wrap { width:340px; flex-shrink:0; display:flex; flex-direction:column; align-items:center; gap:16px; position:sticky; top:90px; }
    .phone-label { font-size:13px; font-weight:700; text-transform:uppercase; letter-spacing:.07em; color:var(--muted); }
    .phone { width:340px; background:#1a1a2e; border-radius:48px; padding:22px 18px 28px;
      box-shadow:0 20px 60px rgba(0,0,0,.25),0 0 0 1px rgba(255,255,255,.06); position:relative; }

    .phone-notch { width:96px; height:26px; background:#111; border-radius:0 0 18px 18px;
      margin:0 auto 16px; display:flex; align-items:center; justify-content:center; gap:8px; }
    .phone-notch .cam { width:10px; height:10px; border-radius:50%; background:#2a2a3e; border:1px solid #333; }

    .phone-screen { background:#f0f0f5; border-radius:30px; overflow:hidden; min-height:440px; display:flex; flex-direction:column; }
    .phone-screen-header { background:#fff; padding:14px 20px 12px; border-bottom:1px solid #e8e8ec; text-align:center; }
    .phone-screen-header .to-num { font-size:15px; font-weight:700; color:#1a1a2e; }
    .phone-screen-header .sub-txt { font-size:12px; color:#999; margin-top:2px; }

    .phone-messages { flex:1; padding:16px 14px; display:flex; flex-direction:column; gap:10px; overflow:hidden; }
    .phone-bubble { max-width:80%; padding:10px 14px; border-radius:18px; font-size:13px; line-height:1.45; color:#1a1a2e; word-break:break-word; }
    .phone-bubble.out { background:#1a73e8; color:#fff; align-self:flex-end; border-bottom-right-radius:4px; }
    .phone-bubble.placeholder { background:#e2e2ea; align-self:flex-end; border-bottom-right-radius:4px; color:#999; font-style:italic; }
    .phone-bubble.in { background:#fff; align-self:flex-start; border-bottom-left-radius:4px; box-shadow:0 1px 3px rgba(0,0,0,.08); }
    .bubble-time { font-size:10px; color:rgba(255,255,255,.6); margin-top:3px; text-align:right; }
    .bubble-time.dark { color:#bbb; }

    .phone-input-bar { background:#fff; border-top:1px solid #e8e8ec; padding:12px 14px; display:flex; align-items:center; gap:8px; }
    .phone-input-bar .fake-input { flex:1; background:#f0f0f5; border-radius:99px; padding:8px 14px; font-size:12px; color:#bbb; }
    .phone-send-btn { width:36px; height:36px; border-radius:50%; background:#1a73e8; display:flex; align-items:center; justify-content:center; font-size:15px; flex-shrink:0; color:#fff; }
    .phone-home-bar { width:80px; height:5px; background:rgba(255,255,255,.2); border-radius:99px; margin:14px auto 0; }

    @media (max-width:700px) { .send-layout { flex-direction:column; } .phone-wrap { display:none; } }
  </style>
</head>
<body class='page-shell'>

<%= PageTemplate.customerHeader(ctx, "Send SMS") %>

<div class='page-body'>
  <h1 class='page-title'>Send Message</h1>
  <p class='page-sub'>Compose and send an SMS using your Twilio credentials.</p>

  <% if (error != null && !error.isEmpty()) { %>
  <div class='alert alert-err'>⚠️ <%= Html.esc(error) %></div>
  <% } %>
  <% if (success != null && !success.isEmpty()) { %>
  <div class='alert alert-ok'>✅ <%= Html.esc(success) %></div>
  <% } %>

  <div class='send-layout'>
    <div class='send-card'>
      <article class='card' style='padding:36px'>
        <form action='<%= ctx %>/customer/send' method='POST' novalidate>
          <div class='form-group'>
            <label for='from'>From (Sender ID)</label>
            <input type='text' id='from' name='from' placeholder='Your allowed Sender ID' required
                   value='<%= Html.esc(senderid) %>' oninput='updatePreview()'/>
          </div>
          <div class='form-group'>
            <label for='to'>To (Recipient Number)</label>
            <input type='tel' id='to' name='to' placeholder='+1234567890' required oninput='updatePreview()'/>
          </div>
          <div class='form-group'>
            <label for='body'>Message Body</label>
            <textarea id='body' name='body' maxlength='160'
                      placeholder='Type your message here…' required
                      oninput='updateCount(this); updatePreview()'></textarea>
            <div style='display:flex;justify-content:space-between;margin-top:5px'>
              <span style='font-size:11.5px;color:var(--muted)'>Max 160 characters</span>
              <span id='charCount' style='font-size:11.5px;color:var(--muted);font-family:monospace'>0 / 160</span>
            </div>
          </div>
          <button type='submit' class='btn btn-primary btn-full'>Send SMS ✉</button>
        </form>
        <p style='font-size:12px;color:var(--muted);text-align:center;margin-top:18px;line-height:1.6'>
          This will securely use your pre-configured Account SID and Token.
        </p>
      </article>
    </div>

    <div class='phone-wrap'>
      <span class='phone-label'>Preview</span>
      <div class='phone'>
        <div class='phone-notch'><div class='cam'></div></div>
        <div class='phone-screen'>
          <div class='phone-screen-header'>
            <div class='to-num' id='prev-to'>+1234567890</div>
            <div class='sub-txt'>via <%= Html.esc(senderid) %></div>
          </div>
          <div class='phone-messages' id='prev-messages'>
            <div class='phone-bubble in'>
              Hey, do you have any updates?
              <div class='bubble-time dark'>10:14 AM</div>
            </div>
            <div class='phone-bubble placeholder' id='prev-bubble'>
              Your message will appear here…
            </div>
          </div>
          <div class='phone-input-bar'>
            <div class='fake-input'>Message</div>
            <div class='phone-send-btn'>↑</div>
          </div>
        </div>
        <div class='phone-home-bar'></div>
      </div>
    </div>
  </div>
</div>

<%= PageTemplate.footer() %>

<script>
function updateCount(ta){document.getElementById('charCount').textContent=ta.value.length+' / 160'}
function updatePreview(){
  var toVal=document.getElementById('to').value.trim()||'+1234567890';
  var bodyVal=document.getElementById('body').value.trim();
  document.getElementById('prev-to').textContent=toVal;
  var bubble=document.getElementById('prev-bubble');
  if(bodyVal){bubble.className='phone-bubble out';bubble.innerHTML=escH(bodyVal)+'<div class=\"bubble-time\">Now</div>'}
  else{bubble.className='phone-bubble placeholder';bubble.innerHTML='Your message will appear here…'}
}
function escH(s){var d=document.createElement('div');d.appendChild(document.createTextNode(s));return d.innerHTML}
</script>

</body>
</html>
