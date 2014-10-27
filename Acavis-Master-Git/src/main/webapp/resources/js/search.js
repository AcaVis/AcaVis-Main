
// Convert selects to nice bootstrap dropdowns
$('.selectpicker').selectpicker();

// Initialize storage functions and reset functions of the search options modal
initSearchOptionStorage();

initializeRangeSpinners('#timespanSpinnerLower', '#timespanSpinnerUpper');

handleAddSingleToCollection();
handleAddAllToCollection();
handleAddSelectedToCollection();


function handleAddSingleToCollection() {
	// Modal trigger
	$('.publication-to-collection').click(function() {
		var $modal = $('#modalAddSingleToCollection');
		
		$modal.find('input[name^="publications"]').val($(this).data('publication-identifier'));
		$modal.modal('show');
	});
	
	// Submit-action
	$('#modalAddSingleToCollectionStore').click(function() {
		var dataObject = {};
		$.each($('#addSingleToCollectionForm').serializeArray(), function(key, formElement) {
			dataObject[formElement.name] = formElement.value;
		});
		
		$.post(contextPath + 'collections/addto', dataObject)
			.done(function(data) {
				$('#modalAddSingleToCollection').modal('hide');
			})
			.fail(function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR);
			});
	});
}


function handleAddSelectedToCollection() {
	$('#modalAddSelectedToCollectionStore').click(function() {
		var allPublications = $('.searchresults input:checked[name^="publications"]')
			.map(function() {
				return $(this).val();
			}).get();
		
		var dataObject = { 'publications[]': allPublications };
		
		$.each($('#addSelectedToCollectionForm').serializeArray(), function(key, formElement) {
			dataObject[formElement.name] = formElement.value;
		});
		
		$.post(contextPath + 'collections/addto', dataObject)
			.done(function(data) {
				$('#modalAddSelectedToCollection').modal('hide');
			})
			.fail(function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR);
			});
	});
}


function handleAddAllToCollection() {
	$('#modalAddAllToCollectionStore').click(function() {
		var allPublications = $('.searchresults input[name^="publications"]')
			.map(function() {
				return $(this).val();
			}).get();
		
		var dataObject = { 'publications[]': allPublications };
		$.each($('#addAllToCollectionForm').serializeArray(), function(key, formElement) {
			dataObject[formElement.name] = formElement.value;
		});
		
		$.post(contextPath + 'collections/addto', dataObject)
			.done(function(data) {
				$('#modalAddAllToCollection').modal('hide');
			})
			.fail(function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR);
			});
	});
}


function initSearchOptionStorage() {
	$('#modalSearchOptionsStore')
		.click(function() {
			var postfields = {};
			
			$.each($('#modalSearchOptionsForm').serializeArray(), function(i, param) {
				postfields[param.name] = param.value;
			});
			
			$.post(contextPath + "search/options", postfields)
				.done(function(data) {
					$('#modalSearchOptions').modal('hide');
				})
				.fail(function(jqXHR, textStatus) {
					alert("We're sorry but the operation failed. ("+textStatus+")");
				});
		});
	
	$('#modalSearchOptionsReset')
		.click(function() {
			$('#modalSearchOptionsForm')[0].reset();
			$('#modalSearchOptions').modal('hide');
		});
}

function initSearchHistogram(url) {
	// Avoid caching manually by bogus-parameter
	$('#searchStatisticsHist').height(205);
	$.getJSON(url, { '_': new Date().getTime() })
	
		// Everything went fine
		.done(function(jsonData) {
			var histogram = new PublicationsPerYear(jsonData, '#searchStatisticsHist', { panel: { containerHeight: 205, containerWidth: 568 } }),
				overall = 0,
				lowerYear = d3.min(jsonData, function(d) { return d.year; }),
				upperYear = parseInt(new Date().getFullYear());

			// Insert text-stats
			jsonData.forEach(function(d) { overall += d.count; });
			$('#statsInfo').text(overall + ' results, beginning from ' + lowerYear);

			// Init the timespan-spinners
			$('#statTimespanSpinnerLower').data({ 'spinner-min': lowerYear, 'spinner-max': upperYear }).val(lowerYear);
			$('#statTimespanSpinnerUpper').data({ 'spinner-min': lowerYear, 'spinner-max': upperYear }).val(upperYear);
			initializeRangeSpinners('#statTimespanSpinnerLower', '#statTimespanSpinnerUpper');

			// Register upper/lower events
			$('#statTimespanSpinnerLower').change(function() {
				histogram.limitLower(parseInt($(this).val()));
			});
			$('#statTimespanSpinnerUpper').change(function() {
				histogram.limitUpper(parseInt($(this).val()));
			});
			
			// Finally show the histogram
			histogram.show();
		})
		
		// An error occurred
		.fail(function(jqxhr, textStatus, error) {
			console.log('error:' + textStatus + ', ' + error);
		});

}
