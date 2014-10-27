<%@tag description="Search options modal" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <div class="modal fade" id="modalSearchOptions" tabindex="-1" role="dialog" aria-labelledby="modalSearchOptionsLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h4 class="modal-title" id="modalSearchOptionsLabel"><span class="glyphicon glyphicon-cog"></span> Search Options</h4>
          </div>
          <div class="modal-body">
            <form class="form-horizontal" id="modalSearchOptionsForm">
              <div class="form-group">
                <label class="col-sm-4 control-label">Search by</label>
                <div class="col-sm-6">
                  <select name="searchfields" class="form-control">
                    <option value="publication" ${searchfields == 'publication' ? 'selected' : ''}>Title, abstract and full-text</option>
                    <option value="author" ${searchfields == 'author' ? 'selected' : ''}>Authors name</option>
                  </select>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-4 control-label">Use integration</label>
                <div class="col-sm-6">
                  <select name="integration" class="form-control">
                  <c:forEach items="${integrations}" var="integ">
                    <option value="${integ.qualifiedname}" ${integ.qualifiedname == integration ? 'selected' : ''}>${integ.label}</option>
                  </c:forEach>
                  </select>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-4 control-label">Earliest year</label>
                <div class="col-sm-3">
                  <input type="text" name="earliest" class="form-control input-sm timespan-spinner text-center" value="${earliest}" data-spinner-min="1" data-spinner-max="${thisyear}" id="timespanSpinnerLower">
                </div>
                <div class="col-sm-3">
                  <label class="checkbox-inline">
                    <input type="checkbox" name="ignore_earliest" value="true" data-form-action="disable" data-target="#timespanSpinnerLower"${ignore_earliest ? ' checked' : ''}> No limit
                  </label>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-4 control-label">Latest year</label>
                <div class="col-sm-3">
                  <input type="text" name="latest" class="form-control input-sm timespan-spinner text-center" value="${latest}" data-spinner-min="1" data-spinner-max="${thisyear}" id="timespanSpinnerUpper">
                </div>
                <div class="col-sm-3">
                  <label class="checkbox-inline">
                    <input type="checkbox" name="ignore_latest" value="true" data-form-action="disable" data-target="#timespanSpinnerUpper"${ignore_latest ? ' checked' : ''}> No limit
                  </label>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-4 control-label">Max results shown</label>
                <div class="col-sm-3">
                  <input type="text" name="limit" class="form-control input-sm input-spinner text-center" value="${limit}" data-spinner-min="10" data-spinner-max="250">
                </div>
                <div class="col-sm-1">
                  <span class="help-tip" title="Why is there even an upper limit?">
                      Theoretically there is no upper-limit for results, except there is one on the integration-side.<br />
                      The reason for the upper-limit is that there are result-sets which contain 20k results or more.
                      It would take an eternity to grab these and additionally it would slow down your browser.<br />
                      If you really need a larger set of results, please ask an administrator to increase the limit.
                  </span>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" id="modalSearchOptionsReset">Reset to default</button>
            <button type="button" class="btn btn-success" id="modalSearchOptionsStore">Apply changes</button>
          </div>
        </div>
      </div>
    </div>