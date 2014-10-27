<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:layout>

    <jsp:attribute name="pageTitle">Publication: <c:out value="${publication.title}" escapeXml="true" /></jsp:attribute>

    <jsp:attribute name="modals">
    
    </jsp:attribute>
    
    <jsp:attribute name="specific_css">
      <link href="${pageContext.request.contextPath}/resources/acavis-libs/yearly-histogram/yearlyHistogram.css" rel="stylesheet">
    </jsp:attribute>
    
    <jsp:attribute name="specific_js">
      <script src="${pageContext.request.contextPath}/resources/js/search.js"></script>
      <script src="${pageContext.request.contextPath}/resources/libs/d3tip/d3tip.min.js"></script>
      <script src="${pageContext.request.contextPath}/resources/acavis-libs/yearly-histogram/yearlyHistogram.js"></script>
    </jsp:attribute>
    
    <jsp:body>

        <h1><small>Publication</small> <c:out value="${publication.title}" escapeXml="true" /></h1>
        
        <p>Author, domain, subdomain, keywords</p>
        
        <div class="row">
        
          <div class="col-sm-6">
            <h3>References (${fn:length(publication.references)})</h3>
            <div class="publication-reference-wrapper">
              <ul>
                <li></li>
                <li></li>
                <li></li>
                <li></li>
                <li></li>
              </ul>
            </div>
          </div>
          
          <div class="col-sm-6">
            <h3>Citations (${fn:length(publication.citations)})</h3>
            <div class="publication-reference-wrapper">
              <ul>
                <li></li>
                <li></li>
                <li></li>
                <li></li>
                <li></li>
              </ul>
            </div>
          </div>
          
        </div>

    </jsp:body>

</t:layout>