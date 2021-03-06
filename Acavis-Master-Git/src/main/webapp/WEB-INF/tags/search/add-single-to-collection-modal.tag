<%@tag description="Search options modal" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <div class="modal fade" id="modalAddSingleToCollection" tabindex="-1" role="dialog" aria-labelledby="modalAddSingleToCollectionLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h4 class="modal-title" id="modalAddSingleToCollectionLabel"><span class="glyphicon glyphicon-share"></span> Add publication to collection</h4>
          </div>
          <div class="modal-body">
            <form class="form-horizontal" id="addSingleToCollectionForm">
              <div class="form-group">
                <label class="col-sm-4 control-label">Use existing collection</label>
                <div class="col-sm-6">
                  <select name="collection" class="form-control" id="collectionAddSingle">
                  <c:forEach items="${existing_collections}" var="collection">
                    <option value="${collection.id}">${collection.name}</option>
                  </c:forEach>
                  </select>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-4 control-label"></label>
                <div class="col-sm-6">
                  <label class="checkbox-inline">
                    <input type="checkbox" name="newcollection" value="true" data-form-action="biable" data-target-inphase="#newCollectionNameSingle" data-target-antiphase="#collectionAddSingle"> Create and use new collection
                  </label>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-4 control-label"></label>
                <div class="col-sm-6">
                  <input type="text" name="newcollection_name" class="form-control" placeholder="New collection's name" id="newCollectionNameSingle">
                </div>
              </div>
              <hr />
              <div class="form-group">
                <label class="col-sm-4 control-label">Use data source</label>
                <div class="col-sm-6">
                  <select name="integration" class="form-control">
                  <c:forEach items="${publication_integrations}" var="integ">
                    <option value="${integ.qualifiedname}" ${integ.qualifiedname == integration ? 'selected' : ''}>${integ.label}</option>
                  </c:forEach>
                  </select>
                </div>
              </div>
              <input type="hidden" name="publications[]" value="" />
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-danger" data-dismiss="modal">Abort</button>
            <button type="button" class="btn btn-success" id="modalAddSingleToCollectionStore">Add publication</button>
          </div>
        </div>
      </div>
    </div>