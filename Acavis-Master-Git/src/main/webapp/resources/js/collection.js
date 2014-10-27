
// Convert selects to nice bootstrap dropdowns
$('.selectpicker').selectpicker();

initAbstracts();
initTagcloud();



function initAbstracts() {
	// Register event
	$('button[data-action="show"]').click(function() {
		var	$self = $(this);
		$($self.data('target')).animate({ height: 'toggle', paddingTop: 'toggle', opacity: 'toggle' }, 300);
		$self.toggleClass('active');
	})
	
	// Synchronize state of button and target
	.each(function() {
		var	$self = $(this),
			$target = $($self.data('target'));
		
		if ($self.hasClass('active'))
			$target.show();
		else
			$target.hide();
	});
}

function initTagcloud() {
	new TagCloud('#collection-minitagcloud', { colorRangeEnd: '#F81549', colorRangeStart: '#853E4F' }).restyle();
	
	$('#collection-minitagcloud > span').tooltip({ container: 'body' });
}

$(function() {
	initializeRangeSpinners('#timespanSpinnerLower', '#timespanSpinnerUpper');
});

function initCollectionHistogram(url) {
	Pageloader.show();
	
	// Avoid caching manually by bogus-parameter
	$.getJSON(url, { '_': new Date().getTime() })
	
		// Everything went fine
		.done(function(jsonData) {
			var histogram = new PublicationsPerYear(jsonData, '#collectionHistogram', { panel: { containerHeight: 205, containerWidth: 860 } }),
				overall = 0,
				lowerYear = d3.min(jsonData, function(d) { return d.year; }),
				upperYear = parseInt(new Date().getFullYear());

			// Insert text-stats
			jsonData.forEach(function(d) { overall += d.count; });
			$('#statsInfo').text(overall + ' results, beginning from ' + lowerYear);

			// Init the timespan-spinners
			$('#histogramTimespanSpinnerLower').data({ 'spinner-min': lowerYear, 'spinner-max': upperYear }).val(lowerYear);
			$('#histogramTimespanSpinnerUpper').data({ 'spinner-min': lowerYear, 'spinner-max': upperYear }).val(upperYear);
			initializeRangeSpinners('#histogramTimespanSpinnerLower', '#histogramTimespanSpinnerUpper');

			// Register upper/lower events
			$('#histogramTimespanSpinnerLower').change(function() {
				histogram.limitLower(parseInt($(this).val()));
			});
			$('#histogramTimespanSpinnerUpper').change(function() {
				histogram.limitUpper(parseInt($(this).val()));
			});
			
			// Finally show the histogram
			histogram.show();
		})
		
		// An error occurred
		.fail(function(jqxhr, textStatus, error) {
			console.log('error:' + textStatus + ', ' + error);
		})
		
		// Hide load stuff and finish operation
		.always(function() {
			Pageloader.hide();
		});

}

function initCollectionPubnet(url) {
	Pageloader.show();
	$('#pubnetMetricSelection').selectpicker();
	$('#modalCollectionPubnet *:input').prop('disabled', true);
	
	$.getJSON(url, { '_': new Date().getTime() })
		.done(function(data) {
			var net = new PublicationNetwork(data, '#collection-pubnet', { width: 850, height: 450 });
			
			$('#modalCollectionPubnet *:input').prop('disabled', false);
			
			initMetricSelection(net);
			initClusteringSwitch(net);
			initTimelineNavigation(net);
			initFixSwitch(net);
			initLabelsSwitch(net);
			initFilter(net);
		})
		.fail(function(jqxhr, err) {
			alert("Citation-data couldn't be loaded, a network-error occurred! Please reload the whole page or try again later.");
		})
		.always(function() {
			Pageloader.hide();
		});
	
	function initMetricSelection(network) {
		$('#pubnetMetricSelection')
			.change(function() {
				var $this = $(this),
					metric = $this.val();
				
				if (metric == 'none')
					network.useNodeMetric(false);
				else
					network.useNodeMetric(metric);
			})
			// Synchronize control and network-state
			.change();
	}
	
	function initClusteringSwitch(network) {
		$('#pubnetClusteringSwitch').change(function() {
			network.setClustering($(this).prop('checked'));
		})
		// Synchronize control and network-state
		.change();
	}
	
	function initFixSwitch(network) {
		$('#pubnetFixSwitch').change(function() {
			network.fixLayout($(this).prop('checked'));
		})
		// Synchronize control and network-state
		.change();
	}
	
	function initLabelsSwitch(network) {
		$('#pubnetLabelsSwitch').change(function() {
			network.showLabels($(this).prop('checked'));
		})
		// Synchronize control and network-state
		.change();
	}
	
	function initTimelineNavigation(network) {
		$('#pubnetTimelineEarlier').click(function() {
			network.scrollLeft();
		});
		
		$('#pubnetTimelineLater').click(function() {
			network.scrollRight();
		});
	}
	
	function initFilter(network) {
		$('#pubnetFilterSubmit').click(function() {
			var filterText = $('#pubnetFilterText').val().toLowerCase();
			network.filter(function(node) {
				return node.name.toLowerCase().contains(filterText);
			}, true);
		});
		$('#pubnetFilterClear').click(function() {
			network.showAll();
		});
	}
}

function initCollectionCommnet(url) {
	//$.getJSON('pubnet_demo.json', { '_': new Date().getTime() })
	$.getJSON(url, { '_': new Date().getTime() })
		.done(function(data) {
			/*
			// Fake nodes
			var nodes = d3.range(100).map(function() {
				return { name:'', cluster: Math.floor(10 * Math.random()), metric: Math.random() };
			});
			data = {nodes:nodes, links:[] };*/
			
			var net = new CommunityNetwork(data, '#collection-commnet', { width: 866, height: 500 });
			
			initControls(net);
			initLabelsSwitch(net);
			initFixSwitch(net);
		})
		.fail(function(jqxhr, err) {
			console.log('Network error: ' + err);
		});
	
	function initControls(network) {
		$('.communitynetwork-controls > .ctrl-top').click(network.top);
		$('.communitynetwork-controls > .ctrl-bottom').click(network.bottom);
		$('.communitynetwork-controls > .ctrl-left').click(network.left);
		$('.communitynetwork-controls > .ctrl-right').click(network.right);
		$('.communitynetwork-controls > .ctrl-zoomin').click(network.zoomIn);
		$('.communitynetwork-controls > .ctrl-zoomout').click(network.zoomOut);
		$('.communitynetwork-controls > .ctrl-reset').click(network.back);
	}
	
	function initLabelsSwitch(network) {
		$('#commnetLabelsSwitch').change(function() {
			network.showLabels($(this).prop('checked'));
		})
		// Synchronize control and network-state
		.change();
	}
	
	function initFixSwitch(network) {
		$('#commnetFixSwitch').change(function() {
			network.setFixed($(this).prop('checked'));
		})
		// Synchronize control and network-state
		.change();
	}
}


/*
 * Sort collections
 */
(function($) {
	var	$sort  = $('#collectionSortItems'),
		$order = $('#collectionOrderItems'),
		animationDuration = 400,
		
		$elementContainer = $('ul.collection'),
		$elements = $elementContainer.children('li');
	
//	function doSort(sortfield, sortorder) {
//		$elements.sort(function(a, b) {
//			var	aValue = $(a).data('sort-' + sortfield),
//				bValue = $(b).data('sort-' + sortfield),
//				order  = (sortorder === 'desc') ? -1 : 1;
//			
//			if (aValue > bValue)
//				return 1 * order;
//			
//			if (aValue < bValue)
//				return -1 * order;
//			
//			return 0;
//		});
//		
//		$elements.detach().appendTo($elementContainer);
//	}
	
	$elementContainer.mixItUp({
		animation: {
			effects: 'fade',
			duratiuon: 850
		},
		selectors: {
			target: 'li'
		}
	});
	
	$sort.add($order)
		.change(function() {
//			$elementContainer
//				.animate({ opacity: 0 }, animationDuration)
//				.queue(function(next) {
//					doSort($sort.val(), $order.val());
//					next();
//				})
//				.animate({ opacity: 1 }, animationDuration);
			
			$elementContainer.mixItUp('sort', 'sort-' + $sort.val() + ':' + $order.val());
		})
		.first().change();
	
})(jQuery);
