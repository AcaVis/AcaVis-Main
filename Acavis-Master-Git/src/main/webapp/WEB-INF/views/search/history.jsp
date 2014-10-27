<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="searchtags" tagdir="/WEB-INF/tags/search"%>

<t:layout>

    <jsp:attribute name="pageTitle">Search history</jsp:attribute>
    
    <jsp:attribute name="specific_js">
      <script src="${pageContext.request.contextPath}/resources/js/search.js"></script>
    </jsp:attribute>
    
    <jsp:body>
    
        <h1>
          Search history
          <small>
          ${fn:length(searches)} 
          <c:choose>
            <c:when test="${fn:length(searches) == 1}">search</c:when>
            <c:otherwise>searches</c:otherwise>
          </c:choose>
          </small>
        </h1>

        <ul>
          <c:forEach items="${searches}" var="search">
          <li>
            <h2>
              <a href="${pageContext.request.contextPath}/search/results/${search.id}">&ldquo;<c:out value="${search.query}" escapeXml="true" />&rdquo;</a>
            </h2>
            <h4>
              This search delivered ${search.statResults} publications
              <c:if test="${search.statResults == search.maxResults}">
              <span class="help-tip" title="There are possibly more results available">
                This search was limited to ${search.maxResults} results and contains this amount of results. There are possibly more results than shown.<br />
                Perform a new search with a higher limit to get more results.
              </span>
              </c:if>
            </h4>
            <p>
              Executed: <fmt:formatDate type="both" dateStyle="short" timeStyle="short" value="${search.created}"/>
            </p>
          </li>
          </c:forEach>
        </ul>

    </jsp:body>

</t:layout>