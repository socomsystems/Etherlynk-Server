<%@ page import="org.jivesoftware.util.*, org.jivesoftware.openfire.*, java.util.*, java.net.URLEncoder" %>
<%
    String hostname = XMPPServer.getInstance().getServerInfo().getHostname();
    String domain = XMPPServer.getInstance().getServerInfo().getXMPPDomain(); 
%>
<!DOCTYPE html>
<html><head><title>User Enrollment</title>
<script>
    window.addEventListener("load", function()
    {
        var authorization = atob("<%= request.getHeader("authorization").substring(6) %>").split(":");
        var url = location.protocol + "//" + location.host + "/rest/api/restapi/v1/chat/" + authorization[0] + "/enroll";
        var options = {method: "POST", headers: {"authorization":"<%= request.getHeader("authorization") %>"}, body: authorization[1]};    

        console.log("fetch", url, options);    

        fetch(url, options).then(function(response){ return response.text()}).then(function(qrUrl) 
        {           
            console.log('connection ok', qrUrl);
            var qrcode = document.getElementById("qrcode");
            qrcode.innerHTML = "<img src='" + qrUrl + "'>";

        }).catch(function (err) {
            console.error('connection error', err);
        });
    });
    
</script>
</head>
<body>
    <div id="qrcode">
    </div>
</body>
</html>
