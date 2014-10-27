<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div class="modal-header">
  <h4 class="modal-title" id="modalSnippetError"><span class="glyphicon glyphicon-stats"></span> An error occurred!</h4>
</div>

<div class="modal-body">
  <div class="alert alert-danger" role="alert">${message}</div>
</div>

<div class="modal-footer">
  <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
</div>
