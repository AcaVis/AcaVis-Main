/*
(function($) {
	var $container = $('#searchStatisticsHist'),
	cfg = {
		timelineAnimateChanges: true,
		timelineMaxTicks: 12,
		timelineLowerLimit: 0,
		timelineUpperLimit: 0,
		
		valueScaleFromZero: true,
		valueScaleTicks: 4,
		valueZeroOffset: 5, // pixel
		valueBarFactor: 0.9,
		
		animationDuration: 500, // milliseconds
		animationEasing: 'sin-in-out',
		
		panelMargin: {top: 10, right: 15, bottom: 20, left: 30}
	},
	panel = {
		containerHeight: 200,
		containerWidth: 560,
		height: 200 - cfg.panelMargin.top - cfg.panelMargin.bottom,
		width: 560 - cfg.panelMargin.left - cfg.panelMargin.right
	},
	// Graphics
	svg, xScale, yScale, xAxis, yAxis, line, path, tip;
	
	
	
	function init(rawData) {
		// Call them once because the spinners are now initialized
		cfg.timelineLowerLimit = parseInt($('#statTimespanSpinnerLower').val());
		cfg.timelineUpperLimit = parseInt($('#statTimespanSpinnerUpper').val());
		
		var	displayData = arrayCopy(rawData),
			dataProperties = calculateDataProperties(rawData),
			rangeLimit = {
							lower: Math.max(cfg.timelineLowerLimit, dataProperties.minYear),
							upper: cfg.timelineUpperLimit
						};
		
		displayData = interpolateData(displayData, rangeLimit);
		dataProperties = calculateDataProperties(displayData);
		
		initScales();
		initAxes(dataProperties);
		setScaleDomains(displayData, dataProperties);
		
		drawDecorations(displayData);
		
		drawData(displayData);
	}
	
	function update(rawData) {
		var	displayData = arrayCopy(rawData),
			dataProperties = calculateDataProperties(rawData),
			rangeLimit = {
							lower: Math.max(cfg.timelineLowerLimit, dataProperties.minYear),
							upper: cfg.timelineUpperLimit
						};
		
		displayData = interpolateData(displayData, rangeLimit);
		dataProperties = calculateDataProperties(displayData);
		
		setScaleDomains(displayData, dataProperties);
		
		updateDecorations();
		
		drawData(displayData);
	}
	
	initializeTimespanSpinners();
	
	var overall = 0;
	searchResultsHistogramBase.forEach(function(d) { overall += d.count; });
	$('#statsInfo').text(overall + ' results, beginning from ' + d3.min(searchResultsHistogramBase, function(d) { return d.year; }));
	
	init(searchResultsHistogramBase);
	
	
	
	// The range spinners
	function initializeTimespanSpinners() {
		var	lowerYear = d3.min(searchResultsHistogramBase, function(d) { return d.year; }),
			upperYear = parseInt(new Date().getFullYear());
			//upperYear = d3.max(searchResultsHistogramBase, function(d) { return d.year; });
		
		$('#statTimespanSpinnerLower').data({ 'spinner-min': lowerYear, 'spinner-max': upperYear }).val(lowerYear);
		$('#statTimespanSpinnerUpper').data({ 'spinner-min': lowerYear, 'spinner-max': upperYear }).val(upperYear);
		
		initializeRangeSpinners('#statTimespanSpinnerLower', '#statTimespanSpinnerUpper');
		$('#statTimespanSpinnerLower').change(function() {
			cfg.timelineLowerLimit = parseInt($(this).val());
			update(searchResultsHistogramBase);
		});
		$('#statTimespanSpinnerUpper').change(function() {
			cfg.timelineUpperLimit = parseInt($(this).val());
			update(searchResultsHistogramBase);
		});
	}
	
	function arrayCopy(array) {
		return array.slice(0);
	}
	
	function calculateDataProperties(data) {
		return {
			minYear: d3.min(data, function(d) { return d.year; }),
			maxYear: d3.max(data, function(d) { return d.year; }),
			minValue: cfg.valueScaleFromZero ? 0 : d3.min(data, function(d) { return d.count; }),
			maxValue: d3.max(data, function(d) { return d.count; }),
			entries: data.length
		};
	}
	
	function interpolateData(data, rangeLimit) {
		// Filter outliers
		data = $.map(data, function(d) {
				if (d.year < rangeLimit.lower || d.year > rangeLimit.upper)
					return null;
				
				return d;
			});
		
		// Add missing years
		var containedYears = $.map(data, function(d) { return d.year; });
		for (var year=rangeLimit.lower; year<=rangeLimit.upper; year++)
			if ($.inArray(year, containedYears) === -1)
				data.push({ year: year, count: 0 });
		
		// Sort data by year (ascending)
		data.sort(function(a, b) { return d3.ascending(a.year, b.year); });
		
		// Parse the date / time
		var parseDate = d3.time.format('%Y').parse;
		data.forEach(function(d) {
			d.date = parseDate(d.year.toString());
		});
		
		return data;
	}
	
	function initScales(dataProperties) {
		// Intialize x-scale
		xScale = d3.time.scale();
		
		// Initialize y-scale
		yScale = d3.scale.linear()
				.range([panel.height, 0]);
	}
	
	function setScaleDomains(data, dataProperties, rangeLimit) {
		// Toget the ticks in the middle of the bars (based on the current data-set)
		var offset = (panel.width / dataProperties.entries / 2);
		
		// Set x axis domain
		xScale
			.range([offset, panel.width - offset])
			.domain(d3.extent(data, function(d) { return d.date; }));
		
		// Set y axis domain
		yScale.domain([
			dataProperties.minValue,
			dataProperties.maxValue
		]);
		
		// Adjust ticking
		xAxis.ticks(d3.time.year, Math.ceil((dataProperties.maxYear - dataProperties.minYear) / cfg.timelineMaxTicks));
	}
	
	function initAxes(dataProperties) {
		// X axis
		xAxis = d3.svg.axis()
			.scale(xScale)
			.orient('bottom');
		
		// Y axis
		var valueRange = dataProperties.maxValue - dataProperties.minValue;
		yAxis = d3.svg.axis()
			.scale(yScale)
			.orient('left')
			
			// Don't show decimal ticks for integer-values
			.ticks((valueRange < cfg.valueScaleTicks) ? valueRange : cfg.valueScaleTicks)
			.tickFormat(d3.format('d'))
			.tickSubdivide(0);
	}
	
	function drawDecorations(data) {
		// Draw svg base-panel
		svg = d3.select($container[0]).append('svg')
			.attr('width', panel.containerWidth)
			.attr('height', panel.containerHeight)
			.append('g')
			.attr('transform', 'translate(' + cfg.panelMargin.left + ',' + cfg.panelMargin.top + ')');
		
		// Draw axes
		svg.append('g')
			.attr('class', 'x axis')
			.attr('transform', 'translate(0,' + panel.height + ')')
			.call(xAxis);
		svg.append('g')
			.attr('class', 'y axis')
			.call(yAxis);
		
		// Initialize data-line and insert empty path
		path = svg.append('path');
		line = d3.svg.line()
			.x(function(d) { return xScale(d.date); })
			.y(function(d) { return yScale(d.count); });
		
		// Initialize and append tooltip
		tip = d3.tip()
			.attr('class', 'd3-tip')
			.offset([-10, 0])
			.html(function(d) {
				return d.year + ': ' + d.count + ((d.count == 1) ? ' result' : ' results');
			});
		svg.call(tip);
	}
	
	function updateDecorations() {
		var	xTmp = svg.select('.x.axis'),
			yTmp = svg.select('.y.axis');
		
		// Animate changes, if config set
		if (cfg.timelineAnimateChanges) {
			xTmp = xTmp.transition().duration(cfg.animationDuration).ease(cfg.animationEasing);
			yTmp = yTmp.transition().duration(cfg.animationDuration).ease(cfg.animationEasing);
		}
		
		// The real update using a new input domain
		xTmp.call(xAxis);
		yTmp.call(yAxis);
	}
	
	function drawData(data) {
		/*var thePath, theDots, theNewDots;
		// Display line
		thePath = path.datum(data);
		if (cfg.timelineAnimateChanges)
			thePath = thePath.transition().duration(cfg.animationDuration).ease(cfg.animationEasing);
		thePath
			.attr('class', 'line')
			.attr('d', line);
		
		// Display dots
		theDots = svg.selectAll('g.datadot')
			.data(data);
		
		// Update
		theNewDots = theDots.select('circle');
		if (cfg.timelineAnimateChanges)
			theNewDots = theNewDots.transition().duration(cfg.animationDuration).ease(cfg.animationEasing);
		theNewDots
			.attr('cx', function(d, i) { return xScale(d.date); })
			.attr('cy', function(d, i) { return yScale(d.count); });
		
		// Insert
		theDots.enter()
			.append('g')
			.attr('class', 'datadot')
			.append('circle')
			.attr('r', 4)
			.attr('cx', function(d, i) { return xScale(d.date); })
			.attr('cy', function(d, i) { return yScale(d.count); })
			.on('mouseover', tip.show)
			.on('mouseout', tip.hide);
		
		// Remove
		theDots.exit().remove();*/
		/*
		var	offset = (panel.width / data.length / 2),
			barOffset = offset * (1-cfg.valueBarFactor),
			bars = svg.selectAll('.bar').data(data);
		
		bars.transition().duration(cfg.animationDuration).ease(cfg.animationEasing)
			.attr('class', function(d) { return (yScale(d.count) == panel.height) ? 'bar null' : 'bar'; })
			.attr('x', function(d) { return xScale(d.date) - offset + barOffset; })
			.attr('width', (panel.width / data.length) * cfg.valueBarFactor)
			.attr('y', function(d) {
				var val = yScale(d.count);
				return (val == panel.height) ? (val - cfg.valueZeroOffset) : val;
			})
			.attr('height', function(d) {
				var val = yScale(d.count);
				return (val == panel.height) ? cfg.valueZeroOffset : panel.height - val;
			});
		
		bars.enter()
			.append('rect')
			.attr('class', function(d) { return (yScale(d.count) == panel.height) ? 'bar null' : 'bar'; })
			.attr('x', function(d) { return xScale(d.date) - offset + barOffset; })
			.attr('width', (panel.width / data.length) * cfg.valueBarFactor)
			.attr('y', function(d) {
				var val = yScale(d.count);
				return (val == panel.height) ? (val - cfg.valueZeroOffset) : val;
			})
			.attr('height', function(d) {
				var val = yScale(d.count);
				return (val == panel.height) ? cfg.valueZeroOffset : panel.height - val;
			})
			.on('mouseover', tip.show)
			.on('mouseout', tip.hide);
		
		bars.exit().remove();
	}
})(jQuery);*/



/*
  Input array will automatically be copied
  Possible Settings:
   - container,
   - width, height,
   - timelineLowerLimit, timelineUpperLimit
*/
function PublicationsPerYear(rawData, container, settings) {
	var $container = $((typeof container === 'undefined') ? '' : container),
	cfg = {
		timelineAnimateChanges: true,
		timelineMaxTicks: 12,
		
		valueRepresentation: 'bar',
		valueScaleFromZero: true,
		valueScaleTicks: 4,
		valueZeroOffset: 5, // pixel
		valueBarFactor: 0.9, // bars only take valueBarFactor (percent) of their assigned space
		
		timelineLowerLimit: 0,
		timelineUpperLimit: parseInt(new Date().getFullYear()), // Default is current year
		
		animationDuration: 500, // milliseconds
		animationEasing: 'sin-in-out',
		
		panelMargin: {
			top: 5,
			right: 15,
			bottom: 25, // Include labels and bar
			left: 30 // Include labels and bar
		},
		panel: {
			containerHeight: 245,
			containerWidth: 530
		}
	},
	// Graphics
	svg, xScale, yScale, xAxis, yAxis, line, path, tip,
	// Data
	data = arrayCopy(rawData),
	dataProperties = calculateDataProperties();
	
	// Append user-settings
	cfg = $.extend(true, cfg, settings);
	
	// Calculations aka non-primitive configurations
	cfg.panel.height = cfg.panel.containerHeight - cfg.panelMargin.top - cfg.panelMargin.bottom;
	cfg.panel.width = cfg.panel.containerWidth - cfg.panelMargin.left - cfg.panelMargin.right;
	
	// Init the panel and decorations once
	init();
	
	
	this.show = function() {
		// Make copy of raw-data and do interpolations
		data = arrayCopy(rawData);
		data = interpolateData();
		
		// Update decorations
		setScaleDomains();
		updateDecorations();
		
		// Trigger draw
		if (cfg.valueRepresentation == 'line')
			drawLineData();
		// Default is bar-representation
		else
			drawBarData();
	};
	
	this.limitTimerange = function(lower, upper) {
		// Update internal config
		cfg.timelineLowerLimit = lower;
		cfg.timelineUpperLimit = upper;
		
		// Re-draw
		this.show();
	};
	
	this.limitLower = function(lower) {
		// Update internal config
		cfg.timelineLowerLimit = lower;
		
		// Re-draw
		this.show();
	};
	
	this.limitUpper = function(upper) {
		// Update internal config
		cfg.timelineUpperLimit = upper;
		
		// Re-draw
		this.show();
	};
	
	this.toBarChart = function() {
		// Update internal config
		cfg.valueRepresentation = 'bar';
		
		// Remove lines and dots
		svg.selectAll('g.datadot').remove();
		if (path != null && typeof path !== 'undefined')
			path.remove();
		
		// Re-draw
		this.show();
	};
	
	this.toLineChart = function() {
		// Update internal config
		cfg.valueRepresentation = 'line';
		
		// Remove Bars
		svg.selectAll('.bar').remove();
		
		// Re-draw
		this.show();
	};
	
	
	function init() {
		// Make copy of raw-data and do interpolations
		cfg.timelineLowerLimit = dataProperties.minYear;
		data = arrayCopy(rawData);
		data = interpolateData();
		
		initScales();
		initAxes();
		setScaleDomains();
		
		drawDecorations();
	}
	
	function arrayCopy(array) {
		return array.slice(0);
	}
	
	function calculateDataProperties() {
		return {
			minYear: d3.min(data, function(d) { return d.year; }),
			maxYear: d3.max(data, function(d) { return d.year; }),
			minValue: cfg.valueScaleFromZero ? 0 : d3.min(data, function(d) { return d.count; }),
			maxValue: d3.max(data, function(d) { return d.count; }),
			entries: data.length
		};
	}
	
	function interpolateData() {
		// Remove outliers
		data = $.map(data, function(d) {
			if (d.year < cfg.timelineLowerLimit || d.year > cfg.timelineUpperLimit)
				return null;
			
			return d;
		});
		
		// Add missing years
		var containedYears = $.map(data, function(d) { return d.year; });
		for (var year=cfg.timelineLowerLimit; year<=cfg.timelineUpperLimit; year++)
			if ($.inArray(year, containedYears) === -1)
				data.push({ year: year, count: 0 });
		
		// Sort data by year (ascending)
		data.sort(function(a, b) { return d3.ascending(a.year, b.year); });
		
		// Parse the date / time
		var parseDate = d3.time.format('%Y').parse;
		data.forEach(function(d) {
			d.date = parseDate(d.year.toString());
		});
		
		return data;
	}
	
	function initScales(dataProperties) {
		// Intialize x-scale
		xScale = d3.time.scale();
		
		// Initialize y-scale
		yScale = d3.scale.linear()
				.range([cfg.panel.height, 0]);
	}
	
	function setScaleDomains() {
		// To get the ticks in the middle of the bars (based on the current data-set)
		var offset = (cfg.valueRepresentation == 'line') ? 0 : (cfg.panel.width / data.length / 2);
		
		// Set x axis domain
		xScale
			.range([offset, cfg.panel.width - offset])
			.domain(d3.extent(data, function(d) { return d.date; }));
		
		// Set y axis domain
		yScale.domain([
			dataProperties.minValue,
			dataProperties.maxValue
		]);
		
		// Adjust ticking
		xAxis.ticks(d3.time.year, Math.ceil((cfg.timelineUpperLimit - cfg.timelineLowerLimit) / cfg.timelineMaxTicks));
	}
	
	function initAxes() {
		// X axis
		xAxis = d3.svg.axis()
			.scale(xScale)
			.orient('bottom');
		
		// Y axis
		var valueRange = dataProperties.maxValue - dataProperties.minValue;
		yAxis = d3.svg.axis()
			.scale(yScale)
			.orient('left')
			
			// Don't show decimal ticks for integer-values
			.ticks((valueRange < cfg.valueScaleTicks) ? valueRange : cfg.valueScaleTicks)
			.tickFormat(d3.format('d'))
			.tickSubdivide(0);
	}
	
	function drawDecorations() {
		// Draw svg base-panel
		svg = d3.select($container[0]).append('svg')
			.attr('width', cfg.panel.containerWidth)
			.attr('height', cfg.panel.containerHeight)
			.append('g')
			.attr('transform', 'translate(' + cfg.panelMargin.left + ',' + cfg.panelMargin.top + ')');
		
		// Draw axes
		svg.append('g')
			.attr('class', 'x axis')
			.attr('transform', 'translate(0,' + cfg.panel.height + ')')
			.call(xAxis);
		svg.append('g')
			.attr('class', 'y axis')
			.call(yAxis);
		
		// Initialize and append tooltip
		tip = d3.tip()
			.attr('class', 'd3-tip')
			.offset([-10, 0])
			.html(function(d) {
				return d.year + ': ' + d.count + ((d.count == 1) ? ' result' : ' results');
			});
		svg.call(tip);
	}
	
	function updateDecorations() {
		var	xTmp = svg.select('.x.axis'),
			yTmp = svg.select('.y.axis');
		
		// Animate changes, if config set
		if (cfg.timelineAnimateChanges) {
			xTmp = xTmp.transition().duration(cfg.animationDuration).ease(cfg.animationEasing);
			yTmp = yTmp.transition().duration(cfg.animationDuration).ease(cfg.animationEasing);
		}
		
		// The real update using a new input domain
		xTmp.call(xAxis);
		yTmp.call(yAxis);
	}
	
	function drawBarData() {
		var	offset = (cfg.panel.width / data.length / 2),
			barOffset = offset * (1-cfg.valueBarFactor),
			bars = svg.selectAll('.bar').data(data);
		
		// Update
		bars.transition().duration(cfg.animationDuration).ease(cfg.animationEasing)
			.attr('class', function(d) { return (yScale(d.count) == cfg.panel.height) ? 'bar null' : 'bar'; })
			.attr('x', function(d) { return xScale(d.date) - offset + barOffset; })
			.attr('width', (cfg.panel.width / data.length) * cfg.valueBarFactor)
			.attr('y', function(d) {
				var val = yScale(d.count);
				return (val == cfg.panel.height) ? (val - cfg.valueZeroOffset) : val;
			})
			.attr('height', function(d) {
				var val = yScale(d.count);
				return (val == cfg.panel.height) ? cfg.valueZeroOffset : cfg.panel.height - val;
			});
		
		// Insert
		bars.enter()
			.append('rect')
			.attr('class', function(d) { return (yScale(d.count) == cfg.panel.height) ? 'bar null' : 'bar'; })
			.attr('x', function(d) { return xScale(d.date) - offset + barOffset; })
			.attr('width', (cfg.panel.width / data.length) * cfg.valueBarFactor)
			.attr('y', function(d) {
				var val = yScale(d.count);
				return (val == cfg.panel.height) ? (val - cfg.valueZeroOffset) : val;
			})
			.attr('height', function(d) {
				var val = yScale(d.count);
				return (val == cfg.panel.height) ? cfg.valueZeroOffset : cfg.panel.height - val;
			})
			.on('mouseover', tip.show)
			.on('mouseout', tip.hide);
		
		// Remove
		bars.exit().remove();
	}
	
	function drawLineData() {
		// Initialize data-line and insert empty path
		path = svg.append('path');
		line = d3.svg.line()
			.x(function(d) { return xScale(d.date); })
			.y(function(d) { return yScale(d.count); });
		
		var thePath, theDots, theNewDots;
		
		// Display line
		thePath = path.datum(data);
		if (cfg.timelineAnimateChanges)
			thePath = thePath.transition().duration(cfg.animationDuration).ease(cfg.animationEasing);
		thePath
			.attr('class', 'line')
			.attr('d', line);
		
		// Display dots
		theDots = svg.selectAll('g.datadot')
			.data(data);
		
		// Update
		theNewDots = theDots.select('circle');
		if (cfg.timelineAnimateChanges)
			theNewDots = theNewDots.transition().duration(cfg.animationDuration).ease(cfg.animationEasing);
		theNewDots
			.attr('cx', function(d, i) { return xScale(d.date); })
			.attr('cy', function(d, i) { return yScale(d.count); });
		
		// Insert
		theDots.enter()
			.append('g')
			.attr('class', 'datadot')
			.append('circle')
			.attr('r', 4)
			.attr('cx', function(d, i) { return xScale(d.date); })
			.attr('cy', function(d, i) { return yScale(d.count); })
			.on('mouseover', tip.show)
			.on('mouseout', tip.hide);
		
		// Remove
		theDots.exit().remove();
	}
}
