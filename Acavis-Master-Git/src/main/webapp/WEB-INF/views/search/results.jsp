<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="searchtags" tagdir="/WEB-INF/tags/search"%>

<t:layout>

    <jsp:attribute name="pageTitle">Search Results: ${fn:escapeXml(search.query)}</jsp:attribute>

    <jsp:attribute name="modals">

      <!-- Search Options Modal -->
      <searchtags:options-modal />
      <!-- /Search Options Modal -->
      
      <!-- Add to collection Modals -->
      <searchtags:add-single-to-collection-modal />
      <searchtags:add-selected-to-collection-modal />
      <searchtags:add-all-to-collection-modal />
      <!-- /Add to collection Modals -->
    
      <!-- Statistics Modal -->
      <div class="modal fade" id="modalSearchStatistics" tabindex="-1" role="dialog" aria-labelledby="modalSearchStatisticsLabel" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content"></div>
        </div>
      </div>
      <!-- /Statistics Modal -->
    </jsp:attribute>
    
    <jsp:attribute name="specific_css">
      <link href="${pageContext.request.contextPath}/resources/acavis-libs/yearly-histogram/yearlyHistogram.css" rel="stylesheet">
    </jsp:attribute>
    
    <jsp:attribute name="specific_js">
      <script src="${pageContext.request.contextPath}/resources/acavis-libs/formtools/formtools.js"></script>
      <script src="${pageContext.request.contextPath}/resources/js/search.js"></script>
      <script src="${pageContext.request.contextPath}/resources/libs/d3tip/d3tip.min.js"></script>
      <script src="${pageContext.request.contextPath}/resources/acavis-libs/yearly-histogram/yearlyHistogram.js"></script>
    </jsp:attribute>
    
    <jsp:body>

        <!-- Search input form -->
        <form action="${pageContext.request.contextPath}/search" method="post" class="form searchform well well-sm" role="form">
          <div class="searchinput-group">
            <label class="sr-only" for="searchQuery">Search for</label>
            <input type="text" class="form-control searchinput" name="query" value="${fn:escapeXml(search.query)}" id="searchQuery" placeholder="Search for ...">
            <span class="searchinput-btn">
              <button type="submit" name="send" value="true" class="btn btn-primary"><span class="glyphicon glyphicon-search"></span> Search</button>
              <button type="button" class="btn btn-default" data-toggle="modal" data-target="#modalSearchOptions"><span class="glyphicon glyphicon-cog"></span><span class="hidden-xs"> Search Options</span></button>
            </span>
          </div>
          <div class="text-muted"><a href="${pageContext.request.contextPath}/search/history">Search history</a> | It's highly recommended to have a look at the search options first.</div>
        </form>
        <!-- /Search input form -->
        
        
        <%-- The alerts - when something goes wrong --%>
        <c:if test="${alert_level == 'warning'}">
          <searchtags:warning />
        </c:if>
        
        <c:if test="${alert_level == 'error'}">
          <searchtags:error />
        </c:if>
        <%-- /The alerts --%>
        
        
        <c:if test="${search.results != null}">
        
        <h1 class="searchresults-headcolumn">Search results <small>${search.statResults} publications</small></h1>
        
        
        <%-- Sorting, filtering, collection-management --%>
        <searchtags:results-options />
        <%-- /Sorting, filtering, collection-management --%>
        
        <!-- Search results -->
        <ul class="searchresults">
          <c:forEach items="${search.results}" var="result">
          <li>
            <label class="selection"><input type="checkbox" name="publications[]" value="${result.serializedIdentifier}"></label>
            <c:if test="${result.metricName != null}">
            <div class="searchresult-metricwrapper">
              <div class="searchresult-metric">
                <div class="metricvalue">${result.metric}</div>
                <div class="metricname">${result.metricName}</div>
              </div>
            </div>
            </c:if>
            <h3 class="searchresult-title">
              <a href="${pageContext.request.contextPath}/publication?pubid=${result.serializedIdentifier}">${result.title}</a><br />
              <small>
              published: ${result.year gt 0 ? result.year : '<em>unknown</em>'}
              <%--<c:if test="${result.metricName != null}"> / ${result.metricName}: ${result.metric}</c:if>--%>
              </small>
            </h3>
            <h5 class="searchresult-authors">
            <c:choose>
              <c:when test="${fn:length(result.authors) eq 1}">Author: </c:when>
              <c:when test="${fn:length(result.authors) gt 1}">Authors: </c:when>
              <c:otherwise>Author: <span class="text-muted">Unknown</span></c:otherwise>
            </c:choose>
            <c:forEach items="${result.authors}" var="author" varStatus="loop"><!-- <a href="">${author}</a> -->${author}${!loop.last ? ', ' : ''}</c:forEach>
            </h5>
            <c:if test="${result.source != ''}"><p class="searchresult-meta-source"><small>${result.source}</small></p></c:if>
            <div class="searchresult-actionbar">
              <button data-publication-identifier="${result.serializedIdentifier}" class="btn btn-xs btn-default publication-to-collection" type="button">
                <span class="glyphicon glyphicon-share"></span> Add to collection &hellip;
              </button>
            </div>
          </li>
          </c:forEach>
        </ul>
        <!-- /Search results -->
        </c:if>
        

    </jsp:body>

</t:layout>