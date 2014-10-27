<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="searchtags" tagdir="/WEB-INF/tags/search"%>

<t:layout>

    <jsp:attribute name="pageTitle">Search</jsp:attribute>

    <jsp:attribute name="modals">
        
      <%-- Search Options Modal --%>
      <searchtags:options-modal />
      <%-- /Search Options Modal --%>

      <!-- Statistics Modal -->
      <div class="modal fade" id="modalSearchStatistics" tabindex="-1" role="dialog" aria-labelledby="modalSearchStatisticsLabel" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content"></div>
        </div>
      </div>
      <!-- /Statistics Modal -->
    </jsp:attribute>
    
    <jsp:attribute name="specific_js">
      <script src="${pageContext.request.contextPath}/resources/acavis-libs/formtools/formtools.js"></script>
      <script src="${pageContext.request.contextPath}/resources/js/search.js"></script>
    </jsp:attribute>
    
    <jsp:body>

        <!-- Search input form -->
        <form action="${pageContext.request.contextPath}/search" method="post" class="form searchform-landing well well-sm clearfix" role="form">

          <div class="searchinput-group">
            <label class="sr-only" for="searchQuery">Search for</label>
            <input type="text" class="form-control input-lg searchinput" name="query" value="${query}" id="searchQuery" placeholder="Search for ...">
            <span class="searchinput-btn">
              <button type="submit" name="send" value="true" class="btn btn-lg btn-primary"><span class="glyphicon glyphicon-search"></span> Search</button>
              <button type="button" class="btn btn-lg btn-default" data-toggle="modal" data-target="#modalSearchOptions"><span class="glyphicon glyphicon-cog"></span><span class="hidden-xs hidden-sm"> Search Options</span></button>
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

    </jsp:body>
    
</t:layout>