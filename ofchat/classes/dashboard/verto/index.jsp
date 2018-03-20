<%@ page import="org.jivesoftware.util.*, org.jivesoftware.openfire.user.*, org.jivesoftware.openfire.*, org.dom4j.*, java.net.*,
                 org.jivesoftware.openfire.vcard.VCardManager, java.util.*, java.net.URLEncoder, javax.xml.bind.DatatypeConverter"%>
<%
    String hostname = XMPPServer.getInstance().getServerInfo().getHostname();    
    String ourIpAddress = hostname;

    try {
        ourIpAddress = InetAddress.getByName(hostname).getHostAddress();
    } catch (Exception e) {  }
        
    String domain = XMPPServer.getInstance().getServerInfo().getXMPPDomain();
    
    boolean isSwitchAvailable = JiveGlobals.getBooleanProperty("freeswitch.enabled", false);
    String sipDomain = JiveGlobals.getProperty("freeswitch.sip.hostname", ourIpAddress);
     
%>
<!DOCTYPE html>
<html ng-app="vertoApp" ng-controller="MainController" lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">

    <meta name="description" content="Verto (VER-to) RTC is a FreeSWITCH endpoint that implements a subset of a JSON-RPC connection designed for use over secure websockets.">
    <meta name="author" content="FreeSWITCH">
    <link rel="icon" href="favicon.ico">

    <title ng-bind="'[' + title + '] ' + 'FreeSWITCH Verto&trade; Video Transcoding'"></title>

    <link rel="stylesheet" href="css/vendor.db6bd80e.css">

    <!-- CSS -->
    <link rel="stylesheet" href="css/verto.e85092b6.css">


    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <script>
      window.fsSipDomain = "<%= sipDomain %>";   
      
      if (location.search) {
        var tmp = location.search;
        location.search = '';
        location.href = getPathFromUrl(location.href) + (location.hash ? location.hash : '#/') + tmp;
      }

      function getPathFromUrl(url) {
        return url.split(/[?#]/)[0];
      }
    </script>
  </head>
  <body>

    <div ng-include="'partials/menu.html'"></div>
    <div ng-include="'partials/settings.html'"></div>

    <div id="wrapper" class="toggled">
      <!-- Sidebar -->
      <div id="sidebar-wrapper">
        <div ng-include="'partials/chat.html'"></div>
      </div>
      <!-- /#sidebar-wrapper -->

      <!-- Page Content -->
      <div id="page-content-wrapper">
        <div class="container-fluid">
          <div class="row" ng-view>
          </div>
        </div>
      </div>
    </div>

    <video class="hide" id="webcam" autoplay="autoplay" style="width:100%; height:100%; object-fit:inherit;"></video>


    <!--<script type="text/javascript" src="js/jquery/jquery.mobile.min.js"></script>-->

    <script src="scripts/vendor.54239bbc.js"></script>

    <script src="scripts/scripts.eb8d8230.js"></script>


  </body>
</html>