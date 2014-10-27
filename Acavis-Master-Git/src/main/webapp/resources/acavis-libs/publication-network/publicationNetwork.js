function PublicationNetwork(rawData, container, settings) {
	var	$container = $((typeof container === 'undefined') ? '' : container),
		// Override default config with user-settings
		cfg = $.extend(true, {
			height: 500, // Pixel
			width: 750, // Pixel
			
			marginTop: 0,
			marginBottom: 26,
			marginLeft: 50,
			marginRight: 50,
			
			timelineTickLength: 6, // Pixel
			timelineTickPadding: 5, // Pixel
			nodeMinRadiusFactor: 0.2, // Percent of interval-height
			nodeMaxRadiusFactor: 0.7, // Percent of interval-height
			nodeSizeMetric: false,
			nodeHoverScaleFactor: 1.25,
			
			advancedNodeInfos: true,
			
			smoothScroll: true,
			
			animationDuration: 300,
			animationEasing: 'sin-in-out',
			
			timesliceMinWidth: 40, // Pixel
			timesliceStrokeWidth: 1 // Pixel
		}, settings),
		
		// Graphics
		svg, xScale, xAxis, gridAxis, force, scrollBase, drag,
		color = d3.scale.category10().domain(d3.range(rawData.clusters.length)),
		scrollValue = 0,
		network = this,
		
		// Date helper
		parseDate = d3.time.format('%Y').parse,
		
		// Data
		clusters = [],
		allNodes = [], allLinks = [],
		nodes = [], links = [],
		nodeProperties, properties;
	
	
	init();
	
	
	/**
	  Data-manipulation
	  PUBLIC
	**/
	this.addCluster = function(clusterObject) {
		addCluster(clusterObject);
		return this;
	}
	this.addNode = function(nodeObject) {
		/* Node needs field 'id' */
		addNode(nodeObject);
		return this;
	}
	this.addLink = function(linkObject) {
		/* Links' source and target have to match a node-id */
		addLink(linkObject);
		return this;
	}
	
	
	this.useNodeMetric = function(name) {
		cfg.nodeSizeMetric = name;
		this.update();
	}
	
	
	/**
	  Scroll-functions
	  PUBLIC
	**/
	this.scrollLeft = function() {
		scroll(scrollValue + (0.5 * cfg.width));
	}
	this.scrollFarLeft = function() {
		scroll(properties.scrollMax);
	}
	this.scrollRight = function() {
		scroll(scrollValue - (0.5 * cfg.width));
	}
	this.scrollFarRight = function() {
		scroll(properties.scrollMin);
	}
	
	
	/**
	  The graphical updater
	  PUBLIC
	**/
	this.update = function() {
		// Calculated using intermediate infos
		nodeProperties = calculateNodeProperties();
		properties = calculateVisProperties();
		
		setScaleDomain();
		interpolateData();
		
		color.domain(d3.range(clusters.length));
		
		updateForce();
		updateDecorations();
		
		drawData();
	}
	
	
	/**
	  Filter functions
	  PUBLIC
	**/
	this.filter = function(fn, keepRoots) {
		// Call the user-given filter
		nodes = arrayCopy(allNodes).filter(fn);
		
		// Keep the root nodes too
		if (keepRoots)
			allNodes.forEach(function(node) {
				if (node.root === true)
					nodes.push(node);
			});
		
		// Keep only fully connected links
		links = allLinks.filter(function(link) {
			// True, if both sides of a link still exist
			return (nodes.indexOf(link.source) !== -1 && nodes.indexOf(link.target) !== -1);
		});
		
		this.update();
		scrollIntoViewport();
	}
	this.showTop = function(num) {
		// Keep the top num entries
		nodes = arrayCopy(allNodes)
			.sort(function(a, b) {
				return d3.descending(a[cfg.nodeSizeMetric], b[cfg.nodeSizeMetric]);
			}).slice(0, num);
		
		// Keep the root nodes too
		allNodes.forEach(function(node) {
			if (node.root === true)
				nodes.push(node);
		});
		
		// Keep only fully connected links
		links = allLinks.filter(function(link) {
			// True, if both sides of a link still exist
			return (nodes.indexOf(link.source) !== -1 && nodes.indexOf(link.target) !== -1);
		});
		
		this.update();
	}
	/*this.showTimespan = function(lower, upper) {
		// Filter nodes by years
		nodes = arrayCopy(allNodes).filter(function(node) {
			return (node.year >= lower && node.year <= upper);
		});
		
		// Keep only fully connected links
		links = allLinks.filter(function(link) {
			// True, if both sides of a link still exist
			return (nodes.indexOf(link.source) !== -1 && nodes.indexOf(link.target) !== -1);
		});
		
		this.update();
	}*/
	this.showAll = function() {
		nodes = allNodes;
		links = allLinks;
		
		this.update();
	}
	this.setClustering = function(state) {
		svg.selectAll('.node').classed('unclustered', !state);
	}
	this.fixLayout = function(state) {
		nodes.forEach(function(n) {
			n.fixed = state;
		});
		
		if (state)
			force.resume();
	}
	this.showLabels = function(state) {
		svg.selectAll('.node-label')
			.classed('hidden', !state);
	}
	
	
	
	function init() {
		// Add initial clusters
		rawData.clusters.forEach(function(cluster) {
			addCluster(cluster);
		});
		
		// Add initial nodes
		rawData.nodes.forEach(function(node) {
			addNode(node);
		});
		
		// Add initial links
		rawData.links.forEach(function(link) {
			addLink(link);
		});
		
		// Calculated using intermediate infos
		nodeProperties = calculateNodeProperties();
		properties = calculateVisProperties();
		
		initScale();
		setScaleDomain();
		
		initForce();
		initAxis();
		initDataEvents();
		
		interpolateData();
		
		
		drawDecorations();
		
		// Temp
		/*nodes.forEach(function(n) {
			$('body').append('    { "id": '+n.id+', "name": "'+n.name+'", "pagerank": '+n.pagerank+', "year": '+n.year+' },'+"\r\n");
		});
		return;*/
		
		
		drawData();
	}
	
	function arrayCopy(array) {
		return array.slice(0);
	}
	
	function numberRange(value, min, max) {
		return Math.min(max, Math.max(min, value));
	}
	
	function domNodesToTop(selector) {
		$(selector).each(function() {
			this.parentNode.appendChild(this);
		});
	}
	
	function linksTo(a, b) {
		return links.some(function(d) {
			return (d.source.id === a.id && d.target.id === b.id);
		});
	}
	
	function addCluster(clusterObject) {
		clusters.push(clusterObject);
	}
	
	function addNode(nodeObject) {
		/* Node needs field 'id' */
		nodes.push(nodeObject);
		allNodes.push(nodeObject);
	}
	
	function addLink(linkObject) {
		/* Links' source and target have to match a node-id */
		linkObject.source = findNode(linkObject.source);
		linkObject.target = findNode(linkObject.target);
		
		links.push(linkObject);
		allLinks.push(linkObject);
	}
	
	function findNode(id) {
		for (var i in allNodes)
			if (allNodes[i]['id'] === id)
				return allNodes[i];
	}
	
	function calculateNodeProperties() {
		var properties = {
			size: nodes.length,
			minYear: d3.min(nodes, function (n) { return n.year; }),
			maxYear: d3.max(nodes, function (n) { return n.year; })
		};
		
		if (typeof properties.minYear === "undefined")
			properties.minYear = properties.maxYear = new Date().getFullYear();
		
		properties.rangeYears = properties.maxYear - properties.minYear;
		
		return properties;
	}
	
	function calculateVisProperties() {
		var properties = {
				timesliceWidth: cfg.timesliceMinWidth + cfg.timesliceStrokeWidth,
				panelHeight: cfg.height - cfg.marginTop - cfg.marginBottom,
				scrollMax: 0
			},
			available = cfg.width - cfg.marginRight - cfg.marginLeft,
			needed = nodeProperties.rangeYears * properties.timesliceWidth;
		
		properties.timesliceWidth = (needed < available) ? available/nodeProperties.rangeYears : properties.timesliceWidth;
		
		properties.panelWidth = nodeProperties.rangeYears * properties.timesliceWidth;
		properties.scrollMin = -(properties.panelWidth + cfg.marginRight + cfg.marginLeft - cfg.width);
		properties.nodeMinRadius = 5;//properties.timesliceWidth * cfg.nodeMinRadiusFactor * 0.5;
		properties.nodeMaxRadius = properties.timesliceWidth * cfg.nodeMaxRadiusFactor * 0.5;
		properties.nodeDefaultRadius = properties.nodeMinRadius;
		
		return properties;
	}
	
	function interpolateData() {
		// Create timestamps from years
		nodes.forEach(function(n) {
			if (typeof n.date === 'undefined')
				n.date = parseDate(n.year.toString());
		});
		
		// Other interpolations
		nodes.forEach(function (n) {
			// Sometimes we need some random value to avoid division by zero
			n.random = Math.random();
			
			n.py = n.y = n.random * properties.panelHeight;
			n.xfix = n.px = n.x = xScale(n.date);
			
			if (typeof n.cluster === 'undefined')
				n.cluster = 0;
			
			if (cfg.nodeSizeMetric !== false && typeof n[cfg.nodeSizeMetric] === 'undefined')
				n[cfg.nodeSizeMetric] = 0;
		});
	}
	
	function initScale() {
		// Intialize x-scale
		xScale = d3.time.scale();
	}
	
	function setScaleDomain() {
		// Set x axis domain
		xScale
			.range([0, properties.panelWidth])
			.domain([parseDate(nodeProperties.minYear.toString()), parseDate(nodeProperties.maxYear.toString())]);
	}
	
	function initAxis() {
		// X axis
		xAxis = d3.svg.axis()
			.scale(xScale)
			.orient('bottom')
			.innerTickSize(cfg.timelineTickLength)
			.tickPadding(cfg.timelineTickPadding)
			// Adjust ticking of x-axis, create a tick for every year
			.ticks(d3.time.year, 1);
		
		// Grid Axis
		gridAxis = d3.svg.axis()
			.scale(xScale)
			.tickSize(-properties.panelHeight, 0, 0)
			.tickFormat('')
			.ticks(d3.time.year, 1);
	}
	
	function initForce() {
		// 15 pixel safety for labels (and their offset)
		var	maxTop = properties.nodeMaxRadius + 15,
			maxBottom = properties.panelHeight - properties.nodeMaxRadius;
		
		// Initialize force-layout
		force = d3.layout.force()
			.size([properties.panelWidth, properties.panelHeight])
			.gravity(.05)
			.charge(-50)
			// Taking links into account would affect the node position negatively (overlapping)
			.linkStrength(0)
			.nodes(nodes)
			.links(links)
			.on('tick', function(e) {
				/*
				var q = d3.geom.quadtree(nodes);
				drawnNodes.each(function(node) {
					q.visit(collide(node));
				});*/
				
				svg.selectAll('.node')
					// Fix x position according to timeline and limit y position to panel bounds (and some padding)
					.each(function(n) {
						n.x = n.xfix;
						n.y = numberRange(n.y, maxTop, maxBottom);
					})
					// Node-movement
					.attr('transform', function(n) { return 'translate(' + n.x + ',' + n.y + ')'; });
				
				// Link-movement
				svg.selectAll('.link')
					.attr('x1', function(n) { return n.source.x; })
					.attr('y1', function(n) { return n.source.y; })
					.attr('x2', function(n) { return n.target.x; })
					.attr('y2', function(n) { return n.target.y; });
			})
			.start();
		
		// Make dragging available
		drag = force.drag();
	}

	/*function collide(node) {
		var	r = node.radius + properties.nodeMaxRadius + 15,
			ny1 = node.y - r,
			ny2 = node.y + r;
		
		return function(quad, x1, y1, x2, y2) {
			if (quad.point && (quad.point !== node)) {
				var	y = node.y - quad.point.y,
					l = Math.abs(y),
					r = node.radius + quad.point.radius + 15;
				
				if (l < r) {
					l = (l - r) / (l+0.00001) * 0.05;
					node.y -= y *= l;
					quad.point.y += y;
				}
			}
			return y1 > ny2 || y2 < ny1;
		};
	}*/
	
	function updateForce() {
		force
			.size([properties.panelWidth, properties.panelHeight])
			.start();
	}
	
	function drawDecorations() {
		var outerWidth = properties.panelWidth + cfg.marginRight + cfg.marginLeft;
		
		svg = d3.select($container[0])
			
			// Draw-base
			.append('svg')
			.attr('class', 'publicationNetwork')
			.attr('width', cfg.width)
			.attr('height', cfg.height);
		
			// Wrapper
		scrollBase = svg.append('g')
			.attr('class', 'scrollwrapper')
			.attr('height', cfg.height)
			.attr('width', outerWidth);
		
		// Do scroll
		scroll(properties.scrollMin);
		
		// Panel
		// Hack this, everything must be inserted in the panel
		svg = scrollBase.append('g')
			.attr('class', 'pane')
			.attr('height', cfg.height)
			.attr('width', properties.panelWidth)
			.attr('transform', 'translate(' +  cfg.marginLeft + ',' + cfg.marginTop + ')');
		
		// Draw axis
		svg.append('g')
			.attr('class', 'x axis')
			.attr('transform', 'translate(0,' + properties.panelHeight + ')')
			.call(xAxis);
		
		// Pad area for scrolling (above timeline)
		d3.select($container[0]).select('svg')
			.append('rect')
			.attr('class', 'scrollpad')
			.attr('width', cfg.width)
			.attr('height', cfg.marginBottom)
			.attr('transform', 'translate(0,' + properties.panelHeight + ')')
			.call(d3.behavior.drag()
			.on('drag', function(d) {
				scrollImmediate(scrollValue + (d3.event.dx * 1.4));
			}));
		
		// Grid
		svg.append('g')
			.attr('class', 'grid')
			.attr('transform', 'translate(0,' + properties.panelHeight + ')')
			.transition()
			.duration(cfg.animationDuration)
			.ease(cfg.animationEasing)
			.call(gridAxis);
		
		// Node and link container
		svg.append('g').attr('class', 'link-container');
		svg.append('g').attr('class', 'node-container');
	}
	
	function updateDecorations() {
		// Axis
		svg.select('.x.axis')
			.transition()
			.duration(cfg.animationDuration)
			.ease(cfg.animationEasing)
			.call(xAxis);
		
		// Grid
		svg.select('.grid')
			.transition()
			.duration(cfg.animationDuration)
			.ease(cfg.animationEasing)
			.call(gridAxis);
	}
	
	function initDataEvents() {
		// Highlight dragged nodes as dragged
		drag
			.on('dragstart', function() {
				d3.select(this).classed('dragactive', true);
			})
			.on('dragend', function(n) {
				d3.select(this)
					.classed('dragactive', false)
					.select('circle')
					.attr('transform', '');
				
				unhighlightLinks();
				unhighlightNodes();
			});
	}
	
	function drawData() {
		var existingNodes = svg.select('.node-container').selectAll('.node').data(nodes),
			existingLinks = svg.select('.link-container').selectAll('.link').data(links),
			newNodes,
			nodeSize = d3.scale.linear()
				.range([properties.nodeMinRadius, properties.nodeMaxRadius])
				// We use all nodes because the metric doesn't change for hidden nodes
				.domain(d3.extent(allNodes, function(n) { return n[cfg.nodeSizeMetric]; }));
		
		// Update links
		existingLinks
			.attr('x1', function(d) { return d.source.x; })
			.attr('y1', function(d) { return d.source.y; })
			.attr('x2', function(d) { return d.target.x; })
			.attr('y2', function(d) { return d.target.y; });
		
		// Insert links
		existingLinks
			.enter()
			.append('line')
			.attr('class', 'link')
			.attr('x1', function(d) { return d.source.x; })
			.attr('y1', function(d) { return d.source.y; })
			.attr('x2', function(d) { return d.target.x; })
			.attr('y2', function(d) { return d.target.y; });
		
		// Remove links
		existingLinks.exit().remove();
		
		// Assign node-radi
		nodes.forEach(function(n) {
			if (cfg.nodeSizeMetric === false)
				n.radius = properties.nodeDefaultRadius;
			else
				n.radius = nodeSize(n[cfg.nodeSizeMetric]);
		});
		
		// Update nodes
		existingNodes
			.select('circle')
			.style('fill', function(n) { return color(n.cluster); })
			.style('stroke', function(n) { return d3.rgb(color(n.cluster)).darker() })
			.transition().duration(cfg.animationDuration).ease(cfg.animationEasing)
			.attr('r', function(n) { return n.radius; });
		
		// Update labels
		existingNodes
			.select('.node-label')
			.attr('y', function(n) { return -(n.radius + 3); })
			.text(nodeLabelText);
		
		// Insert node-containers
		newNodes = existingNodes
			.enter()
			.append('g')
			.attr('class', 'node')
			.call(drag)
			.on('mouseenter', nodeEnterEvent)
			.on('mouseleave', nodeLeaveEvent);
		
		// Draw nodes
		newNodes
			.append('circle')
			.transition().duration(cfg.animationDuration).ease(cfg.animationEasing)
			.style('fill', function(n) { return color(n.cluster); })
			.style('stroke', function(n) { return d3.rgb(color(n.cluster)).darker() })
			.attr('r', function(n) { return n.radius; });
		
		// Draw node-labels
		newNodes
			.append('text')
			.attr('x', 0)
			.attr('y', function(n) { return -(n.radius + 3); })
			.attr('class', 'node-label')
			.text(nodeLabelText);
		
		// Remove node-containers
		existingNodes.exit().remove();
	}
	
	function nodeLabelText(node) {
		return node.firstAuthorsLastname + " (" + node.year + ")";
	}
	
	function nodeEnterEvent(n) {
		d3.select(this).select('circle')
			.attr('transform', 'scale(' + cfg.nodeHoverScaleFactor + ')');
		
		highlightLinks(n);
		highlightNeighbors(n);
	}
	
	function nodeLeaveEvent(n) {
		var $this = d3.select(this);
		
		if (!$this.classed('dragactive')) {
			$this.select('circle')
				.attr('transform', '');
			
			unhighlightLinks();
			unhighlightNodes();
		}
	}
	
	function highlightLinks(node) {
		svg.selectAll('.link')
			.classed('reference', function(link) {
				return link.source.index == node.index;
			})
			.classed('citation', function(link) {
				return link.target.index == node.index;
			})
			.classed('silent', function(link) {
				return !(link.source.index == node.index || link.target.index == node.index);
			});
		
		// Move links to top
		domNodesToTop('.reference, .citation');
	}
	
	function unhighlightLinks() {
		svg.selectAll('.link')
			.classed('highlighted', false)
			.classed('silent', false)
			.classed('reference', false)
			.classed('citation', false);
	}
	
	function highlightNeighbors(node) {
		svg.selectAll('.node')
			.classed('root', function(root) {
				return root.id == node.id;
			})
			.classed('cited', function(cited) {
				return linksTo(node, cited);
			})
			.classed('referencing', function(referencing) {
				return linksTo(referencing, node);
			})
			.classed('silent', function(link) {
				var $this = d3.select(this);
				return !($this.classed('cited') || $this.classed('referencing') || $this.classed('root'));
			});
		
		// Move links to top
		domNodesToTop('.referencing, .cited');
	}
	
	function unhighlightNodes() {
		svg.selectAll('.node')
			.classed('silent', false)
			.classed('referencing', false)
			.classed('cited', false)
			.classed('root', false);
	}
	
	function scroll(value) {
		value = Math.max(properties.scrollMin, Math.min(properties.scrollMax, value));
		
		if (cfg.smoothScroll)
			scrollBase.transition().duration(500).ease('sin-in-out').attr('transform', 'translate(' + value + ',0)');
		else
			scrollBase.attr('transform', 'translate(' + value + ',0)');
		
		scrollValue = value;
	}
	
	function scrollImmediate(value) {
		value = Math.max(properties.scrollMin, Math.min(properties.scrollMax, value));
		
		scrollBase.attr('transform', 'translate(' + value + ',0)');
		
		scrollValue = value;
	}
	
	function scrollIntoViewport() {
		scroll(scrollValue);
	}
}