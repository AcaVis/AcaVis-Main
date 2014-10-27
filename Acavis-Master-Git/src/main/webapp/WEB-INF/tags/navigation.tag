<%@tag description="Navigation" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

      <div class="navbar navbar-default navbar-xs" role="navigation">
        <div class="container-fluid">
          <div class="navbar-header navbar-right">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${pageContext.request.contextPath}">AcaVis</a>
          </div>
          <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
              <li class="${section == 'search' ? 'active' : ''}"><a href="${pageContext.request.contextPath}/search">Search</a></li>
              <!--<li class="active"><a href="collection.html">Collections</a></li>-->
              <li class="dropdown ${section == 'collection' ? 'active' : ''}">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Collections <span class="caret"></span></a>
                <ul class="dropdown-menu" role="menu">
                  <!--<li><a href="${pageContext.request.contextPath}/collection?id=1">Abstract units and visualization</a></li>
                  <li class="active"><a href="${pageContext.request.contextPath}/collection?id=2">Citespace</a></li>
                  <li><a href="${pageContext.request.contextPath}/collection?id=3">Data mining</a></li>
                  <li><a href="${pageContext.request.contextPath}/collection?id=4">Edward Tuftes publications</a></li>-->
                  <li role="presentation"><a href="${pageContext.request.contextPath}/collections">Overview</a></li>
                  <li role="presentation" class="divider"></li>
                  <c:forEach items="${existing_collections}" var="collection">
                  <li role="presentation"${current_collection == collection.id ? ' class="active"' : ''}>
                    <a href="${pageContext.request.contextPath}/collections/${collection.id}">
                      ${collection.name}&nbsp;&nbsp;<small class="text-muted">${collection.statPublications} publications</small>
                    </a>
                  </li>
                  </c:forEach>
                </ul>
              </li>
              <c:if test="${section == 'publication'}"><li class="active"><a href="">Publication details</a></li></c:if>
              <c:if test="${section == 'author'}"><li class="active"><a href="">Author details</a></li></c:if>
            </ul>
          </div>
        </div>
      </div>