<%@tag description="Collection sidebar" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

          <!-- Sidebar -->
          <div class="col-sm-3">
          
            <!-- Tag cloud -->
            <div class="panel panel-default">
              <div class="panel-heading">
                <div class="pull-right">
                  <span class="help-tip" title="How these tags are generated">
                    The tags are built from titles, abstracts, keywords and (sub-) disciplines of the publications in this collection. Only tags that occur <strong>at least twice</strong> are displayed. The results are sorted randomly and at most 25 of the largest tags are displayed.
                  </span>
                </div>
                Tag-cloud
              </div>
              <div class="panel-body text-center" id="collection-minitagcloud">
                <c:forEach items="${tagcloud}" var="tag"><span data-wordcount="${tag.scoreInt}" data-toggle="tooltip" title="${tag.scoreInt} occurrences">${tag.name}</span>
                </c:forEach>
              </div>
            </div>
            <!-- /Tag cloud -->
            
            <!-- Stats -->
            <div class="panel panel-default">
              <div class="panel-heading">Overview</div>
              <div class="panel-body">
                <dl>
                  <dt>Total</dt>
                  <dd>${collection.statPublications} ${collection.statPublications == 1 ? 'publication' : 'publications'}, ${collection.statAuthors} ${collection.statAuthors == 1 ? 'author' : 'authors'}</dd>
                  <dt>Timespan</dt>
                  <c:choose>
                  <c:when test="${collection.statEarliestYear == collection.statLatestYear}">${collection.statEarliestYear} only</c:when>
                  <c:otherwise><dd>${collection.statEarliestYear} &ndash; ${collection.statLatestYear}</dd></c:otherwise>
                  </c:choose>
                </dl>
                
                <h5><strong>Visualizations</strong></h5>
                <button type="button" class="btn btn-primary btn-block" data-remote="${pageContext.request.contextPath}/collections/${collection.id}/distribution" data-toggle="modal"  data-target="#modalCollectionHistogram">
                  <span class="glyphicon glyphicon-stats"></span> Timeline of publications
                </button>
                
                <button type="button" class="btn btn-default btn-block" data-remote="${pageContext.request.contextPath}/collections/${collection.id}/citations" data-toggle="modal"  data-target="#modalCollectionPubnet">
                  <span class="glyphicon glyphicon-cloud"></span> Publication-Citation-Network
                </button>
                
                <!-- <button type="button" class="btn btn-default btn-block">
                  <span class="glyphicon glyphicon-tags"></span>&nbsp;&nbsp;Co-Keyword Analysis
                </button> -->
                
                <button type="button" class="btn btn-default btn-block" data-remote="${pageContext.request.contextPath}/collections/${collection.id}/community" data-toggle="modal"  data-target="#modalCollectionCommnet">
                  <span class="glyphicon glyphicon-user"></span>&nbsp;&nbsp;Community Analysis
                </button>
                
              </div>
            </div>
            <!-- /Stats -->
            
          </div>
          <!-- /Sidebar -->