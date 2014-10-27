<%@tag description="Default AcaVis Layout" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@ attribute name="pageTitle" fragment="true" %>
<%@ attribute name="specific_css" fragment="true" %>
<%@ attribute name="specific_js" fragment="true" %>
<%@ attribute name="modals" fragment="true" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title><jsp:invoke fragment="pageTitle" /> - AcaVis</title>

    <!-- CSS Libraries -->
    <!-- <link href="${pageContext.request.contextPath}/resources/libs/bootstrap/css/bootstrap.min.css" rel="stylesheet">-->
    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css" rel="stylesheet">
    <!-- <link href="${pageContext.request.contextPath}/resources/libs/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">-->
    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/libs/bootstrap-touchspin/jquery.bootstrap-touchspin.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/libs/bootstrap-select/bootstrap-select.min.css" rel="stylesheet">

    <!-- AcaVis CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/search.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/layout.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/collection.css" rel="stylesheet">
    <jsp:invoke fragment="specific_css" />
    
    <!--
    <link href="visualizations/publicationNetwork.css" rel="stylesheet">
    <link href="visualizations/communityNetwork.css" rel="stylesheet">-->

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    <style type="text/css">
      .pageloader-loader{background-image:url(${pageContext.request.contextPath}/resources/images/pageloader-two.gif);}
    </style>
    
    <script>
      var contextPath = "${pageContext.request.contextPath}/";
    </script>
  </head>

  <body>
  
    <div class="pageloader-overlay">
      <div class="pageloader-loader"></div>
    </div>
  
    <jsp:invoke fragment="modals" />

    <div class="container">
    
      <!-- The navigation -->
      <t:navigation />
      <!-- /The navigation -->

      <div class="container-fluid pagecontainer">
        <jsp:doBody />
      </div>
    
    </div>

    <!-- Push JavaScript to bottom to improve load-times -->
    <!-- JS Libraries -->
    <!-- <script src="${pageContext.request.contextPath}/resources/libs/jquery/jquery-1.11.1.min.js"></script> -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <!-- <script src="${pageContext.request.contextPath}/resources/libs/bootstrap/js/bootstrap.min.js"></script> -->
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/libs/bootstrap-select/bootstrap-select.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/libs/bootstrap-touchspin/jquery.bootstrap-touchspin.min.js"></script>
    <!-- <script src="${pageContext.request.contextPath}/resources/libs/d3/d3.min.js"></script> -->
    <script src="//cdnjs.cloudflare.com/ajax/libs/d3/3.4.11/d3.min.js"></script>
    
    <script src="${pageContext.request.contextPath}/resources/js/page.js"></script>
    
    <!-- Page-specific JS -->
    <jsp:invoke fragment="specific_js" />
    
  </body>
</html>