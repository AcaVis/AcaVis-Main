<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page trimDirectiveWhitespaces="true" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="collectiontags" tagdir="/WEB-INF/tags/collections"%>

<t:layout>

    <jsp:attribute name="pageTitle">Collection: ${collection.name}</jsp:attribute>
    
    <jsp:attribute name="modals">
      <!-- Histogram Modal -->
      <div class="modal fade" id="modalCollectionHistogram" tabindex="-1" role="dialog" aria-labelledby="modalCollectionHistogramLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
          <div class="modal-content"></div>
        </div>
      </div>
      <!-- /Histogram Modal -->
      
      <!-- Publication network Modal -->
      <div class="modal fade" id="modalCollectionPubnet" tabindex="-1" role="dialog" aria-labelledby="modalCollectionPubnetLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
          <div class="modal-content"></div>
        </div>
      </div>
      <!-- /Publication network Modal -->
      
      
      <!-- Community network Modal -->
      <div class="modal fade" id="modalCollectionCommnet" tabindex="-1" role="dialog" aria-labelledby="modalCollectionCommnetLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
          <div class="modal-content"></div>
        </div>
      </div>
      <!-- /Community network Modal -->
    </jsp:attribute>

    <jsp:attribute name="specific_css">
      <link href="${pageContext.request.contextPath}/resources/acavis-libs/yearly-histogram/yearlyHistogram.css" rel="stylesheet">
      <link href="${pageContext.request.contextPath}/resources/acavis-libs/publication-network/publicationNetwork.css" rel="stylesheet">
    </jsp:attribute>
    
    <jsp:attribute name="specific_js">
      <script src="${pageContext.request.contextPath}/resources/acavis-libs/formtools/formtools.js"></script>
      <script src="${pageContext.request.contextPath}/resources/acavis-libs/tagcloud/tagcloud.js"></script>
      <script src="${pageContext.request.contextPath}/resources/libs/jquery-mixitup/jquery.mixitup.min.js"></script>
      <script src="${pageContext.request.contextPath}/resources/js/collection.js"></script>
    </jsp:attribute>

    <jsp:body>
        <%-- The alerts - when something goes wrong --%>
        <c:if test="${alert_level == 'warning'}">
          <collectiontags:warning />
        </c:if>
        
        <c:if test="${alert_level == 'error'}">
          <collectiontags:error />
        </c:if>
        <%-- /The alerts --%>
        
        <c:if test="${collection != null}">
        <h1 class="collection-headcolumn"><small>Collection</small> ${collection.name}</h1>

        <div class="row">  
        
          <!-- Collection -->

        <c:choose>
          <c:when test="${fn:length(collection.publications) == 0}">
          <div class="col-sm-push-1 col-sm-9">
            <div class="alert alert-info" role="alert"><strong>Collection is empty!</strong> This collection doesn't contain any publications.</div>
          </div>
          </c:when>
          <c:otherwise>

        <%-- Sorting, filtering, collection-management --%>
        <collectiontags:options />
        <%-- /Sorting, filtering, collection-management --%>

          <div class="col-sm-9">
        <%-- Sorting, filtering, collection-management --%>
        <collectiontags:items />
        <%-- /Sorting, filtering, collection-management --%>
          </div>
          
          <!-- /Collection -->
          
        <%-- Sorting, filtering, collection-management --%>
        <collectiontags:sidebar />
        <%-- /Sorting, filtering, collection-management --%>
          
          </c:otherwise>
        </c:choose>

        </div>




        </c:if>
    </jsp:body>
</t:layout>
