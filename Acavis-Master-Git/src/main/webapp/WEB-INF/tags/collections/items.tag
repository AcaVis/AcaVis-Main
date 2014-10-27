<%@tag description="Collection sidebar" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

            <ul class="collection">
            <c:forEach items="${collection.publications}" var="collectionitem">
              <li data-sort-year="${collectionitem.year}" data-sort-title="${fn:escapeXml(collectionitem.title)}" data-sort-added="${collectionitem.created}" data-sort-citations="${collectionitem.citationCount}">
                <label class="selection"><input type="checkbox" class="collectionitems[]" value=""></label>
                <div class="collectionitem-metricwrapper">
                  <div class="collectionitem-metric">
                    <div class="metricvalue">${collectionitem.citationCount}</div>
                    <div class="metricname">citations</div>
                  </div>
                </div>
                <h3 class="collectionitem-title">
                  <a href="${pageContext.request.contextPath}/publication?pubid=${collectionitem.serializedIdentifier}">${collectionitem.title}</a> <c:if test="${collectionitem.year gt 0}"><small>(${collectionitem.year})</small></c:if>
                </h3>
                <p class="collectionitem-authors">
                  <c:choose>
                    <c:when test="${fn:length(collectionitem.authors) eq 1}">Author: </c:when>
                    <c:when test="${fn:length(collectionitem.authors) gt 1}">Authors: </c:when>
                    <c:otherwise>Author: <span class="text-muted">Unknown</span></c:otherwise>
                  </c:choose>
                  <c:forEach items="${collectionitem.authors}" var="author" varStatus="loop">${author}${!loop.last ? ', ' : ''}</c:forEach>
                </p>
                <dl class="collectionitem-tagging">
                  <c:if test="${fn:length(collectionitem.disciplines) gt 0 or fn:length(collectionitem.subDisciplines) gt 0}">
                  <dt>Disciplines</dt>
                  <dd>
                    <c:forEach items="${collectionitem.disciplines}" var="discipline">
                    <span class="label label-primary">${discipline}</span>
                    </c:forEach>
                    <c:forEach items="${collectionitem.subDisciplines}" var="subdiscipline">
                    <span class="label label-default">${subdiscipline}</span>
                    </c:forEach>
                  </dd>
                  </c:if>
                  <c:if test="${fn:length(collectionitem.keywords) gt 0}">
                  <dt>Keywords</dt>
                  <dd>
                    <c:forEach items="${collectionitem.keywords}" var="keyword">
                    <span class="label label-default">${keyword}</span>
                    </c:forEach>
                  </dd>
                  </c:if>
                  <dt>Connectivity</dt>
                  <dd>Cited by ${collectionitem.citationCount} publications overall &ndash; Citing ${collectionitem.referenceCount} other publications</dd>
                </dl>
                <c:if test="${not empty collectionitem.abstractText}">
                <div class="collectionitem-actionbar">
                  <button class="btn btn-default btn-xs" data-action="show" data-target="#abstractText0">Show abstract</button>
                  <p class="collectionitem-abstract" id="abstractText0">
${collectionitem.abstractText}
                  </p>
                </div>
                </c:if>
              </li>
            </c:forEach>
            </ul>