<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-header">
  <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
  <h4 class="modal-title" id="modalCollectionPubnetLabel"><span class="glyphicon glyphicon-cloud"></span> Publication-Citation-Network &ndash; &ldquo;<c:out value="${collection.name}" escapeXml="true" />&rdquo;</h4>
</div>

<div class="modal-body">
  <div class="row">
    <div class="col-sm-4">
      <div class="input-group input-group-sm">
        <input type="text" class="form-control" id="pubnetFilterText" placeholder="Title or author">
        <span class="input-group-btn">
          <button class="btn btn-default" type="button" id="pubnetFilterSubmit"><span class="glyphicon glyphicon-filter"></span> Filter</button>
          <button class="btn btn-default" type="button" id="pubnetFilterClear"><span class="glyphicon glyphicon-remove"></span> Clear</button>
        </span>
      </div>
    </div>
  </div>
  
  <p>
    Node-metric:
    <select class="selectpicker" data-style="btn-sm btn-default" data-width="12em" id="pubnetMetricSelection">
      <option value="none">None</option>
      <option data-divider="true"></option>
      <option value="pagerank" selected>PageRank</option>
      <option value="hub" data-subtext="HITS">Hub score</option>
      <option value="authority" data-subtext="HITS">Authority score</option>
      <option value="eigenvector">Eigenvector centrality</option>
      <option value="betweenness">Betweenness centrality</option>
      <option value="closeness">Closeness centrality</option>
    </select>&nbsp;&nbsp;<label class="checkbox-inline">
      <input type="checkbox" id="pubnetClusteringSwitch" checked> Show clustering
    </label>&nbsp;&nbsp;<label class="checkbox-inline">
      <input type="checkbox" id="pubnetLabelsSwitch" checked> Show labels
    </label>&nbsp;&nbsp;<label class="checkbox-inline">
      <input type="checkbox" id="pubnetFixSwitch"> Fixed layout
    </label>
  </p>
  
  <div id="collection-pubnet" style="height:457px;width:850px;"></div>
  
  <div class="collectionpubnet-timecontrols clearfix">
    <button type="button" class="btn btn-primary pull-left" id="pubnetTimelineEarlier"><span class="glyphicon glyphicon-chevron-left"></span> Earlier</button>
    <button type="button" class="btn btn-primary pull-right" id="pubnetTimelineLater">Later <span class="glyphicon glyphicon-chevron-right"></span></button>
  </div>
  <!--<h5>Limit timespan</h5>
  <div class="row">
    <div class="col-xs-6 col-sm-3">
      <input type="text" class="form-control input-sm text-center" value="0" data-spinner-min="0" data-spinner-max="0" id="histogramTimespanSpinnerLower">
    </div>
    <div class="col-xs-6 col-sm-3">
      <input type="text" class="form-control input-sm text-center" value="0" data-spinner-min="0" data-spinner-max="0" id="histogramTimespanSpinnerUpper">
    </div>
  </div>-->
</div>

<script src="${pageContext.request.contextPath}/resources/acavis-libs/publication-network/publicationNetwork.js"></script>
<script>
  initCollectionPubnet("${pageContext.request.contextPath}/collections/${collectionid}/citationData");
</script>
