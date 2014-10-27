<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-header">
  <h4 class="modal-title" id="modalCollectionHistogramLabel"><span class="glyphicon glyphicon-stats"></span> Distribution of publications per year &ndash; &ldquo;${collection.name}&rdquo;</h4>
</div>

<div class="modal-body">
  <p class="searchstatistics-results text-muted">
    <span class="glyphicon glyphicon-info-sign"></span> <span id="statsInfo"></span> (Excluding publications without year)
  </p>
  <div id="collectionHistogram" class="yearlyHistogram"></div>
  <h5>Limit timespan</h5>
  <div class="row">
    <div class="col-xs-6 col-sm-3">
      <input type="text" class="form-control input-sm text-center" value="0" data-spinner-min="0" data-spinner-max="0" id="histogramTimespanSpinnerLower">
    </div>
    <div class="col-xs-6 col-sm-3">
      <input type="text" class="form-control input-sm text-center" value="0" data-spinner-min="0" data-spinner-max="0" id="histogramTimespanSpinnerUpper">
    </div>
  </div>
</div>

<div class="modal-footer">
  <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
</div>

<script src="${pageContext.request.contextPath}/resources/libs/d3tip/d3tip.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/acavis-libs/yearly-histogram/yearlyHistogram.js"></script>
<script>
  initCollectionHistogram("${pageContext.request.contextPath}/collections/${collectionid}/distributionData");
</script>
