
/***
  Creates a clustered community network with citations/references.
  Every cluster (community) is surrounded by a hull.
***/
function CommunityNetwork(rawData, container, settings) {
	var	$container = $((typeof container === 'undefined') ? '' : container),
		// Override default config with user-settings
		cfg = $.extend(true, {
			height: 600, // Pixel
			width: 960, // Pixel
			
			paddingClusters: 35, // Pixel
			paddingNodes: 15, // Pixel
			
			nodeMinRadius: 3, // Pixel
			nodeMaxRadius: 12, // Pixel
			nodeUniformRadius: 8, // Pixel
			
			nodeSizeMetric: false,
			nodeHoverScaleFactor: 1.25,
			
			zoomStep: 0.4, // addition to zoom-factor
			dragStep: 200, // Pixel (zoomed)
			
			animationDuration: 400,
			animationEasing: 'sin-in-out'
		}, settings),
		
		// Graphics
		svg, force, drag, clusterPath, clusterFill,
		color = d3.scale.category20(),
		zoom, zoomDrag,
		
		// Data
		allNodes = rawData.nodes,
		allLinks = rawData.links,
		nodes = rawData.nodes,
		links = rawData.links,
		
		clusters, clusterFoci,
		
		network = this;
	
	init();
	
	
	/**
	  The grafical updater
	  PUBLIC
	**/
	this.update = function() {
		interpolateData();
		
		drawData();
	}
	this.useNodeMetric = function(name) {
		cfg.nodeSizeMetric = name;
		this.update();
	}
	this.setFixed = function(state) {
		nodes.forEach(function(node) {
			node.fixed = state;
		});
		force.start();
	}
	this.showLabels = function(state) {
		svg.selectAll('.node-label')
			.classed('hidden-label', !state);
	}
	
	
	/**
	  Zoom and drag
	  PUBLIC
	**/
	this.zoomIn = function() {
		var o = zoom.translate(),
			t = (o[1] - cfg.height * cfg.zoomStep * .5),
			l = (o[0] - cfg.width * cfg.zoomStep * .5);
		svg.call(zoom.scale(zoom.scale() + cfg.zoomStep).translate([l, t]).event);
	}
	this.zoomOut = function() {
		var o = zoom.translate(),
			e = zoom.scaleExtent(),
			s = zoom.scale(),
			// Without a lower limit we run into a negative scale-factor,
			// which is equivalent to a 180 degree rotation and scale-up.. (maths *sigh*)
			z = Math.max(s - cfg.zoomStep, e[0]),
			t = (o[1] - cfg.height * (s-z) * -0.5),
			l = (o[0] - cfg.width * (s-z) * -0.5);
		svg.call(zoom.scale(z).translate([l, t]).event);
	}
	
	this.top = function() {
		var offset = zoom.translate();
		svg.call(zoom.translate([offset[0], offset[1] + cfg.dragStep]).event);
	}
	this.right = function() {
		var offset = zoom.translate();
		svg.call(zoom.translate([offset[0] - cfg.dragStep, offset[1]]).event);
	}
	this.bottom = function() {
		var offset = zoom.translate();
		svg.call(zoom.translate([offset[0], offset[1] - cfg.dragStep]).event);
	}
	this.left = function() {
		var offset = zoom.translate();
		svg.call(zoom.translate([offset[0] + cfg.dragStep, offset[1]]).event);
	}
	
	this.back = function() {
		svg.call(zoom.scale(1).translate([0, 0]).event);
	}
	
	
	function init() {
		interpolateData();
		
		initZoom();
		initDecoration();
		
		initForce();
		
		
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
	
	function initForce() {
		var padding = cfg.paddingClusters + cfg.paddingNodes + 5,
			bounds  = {
				top:    padding,
				bottom: cfg.height - padding,
				left:   padding,
				right:  cfg.width - padding
			};
		
		force = d3.layout.force()
			.nodes(nodes)
			.links(links)
			.size([cfg.width, cfg.height])
			.gravity(.1)
			.charge(0)
			.linkStrength(0.2)
			.on('tick', function(e) {
				svg.selectAll('.node')
					.each(cluster(10 * e.alpha * e.alpha))
					.each(collide(.1))
					// Apply panel-bounds
					/*.each(function(node) {
						node.x = numberRange(node.x, bounds.left, bounds.right);
						node.y = numberRange(node.y, bounds.top, bounds.bottom);
					})*/
					// Node-movement
					.attr('transform', function(n) { return 'translate(' + n.x + ',' + n.y + ')'; });
				
				// Link-movement
				svg.selectAll('.link')
					.attr('x1', function(n) { return n.source.x; })
					.attr('y1', function(n) { return n.source.y; })
					.attr('x2', function(n) { return n.target.x; })
					.attr('y2', function(n) { return n.target.y; });
				
				// Hull transformation
				svg.select('.hull-container').selectAll('.hull').data(clusters).attr('d', clusterPath);
			})
			.start();
		
		drag = force.drag()
			.on('dragstart', function() {
				d3.select(this).classed('dragactive', true);
			})
			.on('dragend', function(n) {
				d3.select(this).classed('dragactive', false)
					.select('.node-label').classed('hover', false);
			});
	}
	
	// Credits: Mike Bostock; http://bl.ocks.org/mbostock/7881887
	// Move d to be adjacent to the cluster node.
	function cluster(alpha) {
		return function(d) {
			var cluster = clusterFoci[d.cluster];
			if (cluster === d) return;
			if (cluster.fixed) return;
			
			var	x = d.x - cluster.x,
				y = d.y - cluster.y,
				l = Math.sqrt(x * x + y * y),
				r = d.radius + cluster.radius;
			if (l != r) {
				l = (l - r) / l * alpha;
				d.x -= x *= l;
				d.y -= y *= l;
				cluster.x += x;
				cluster.y += y;
			}
		};
	}

	// Credits: Mike Bostock; http://bl.ocks.org/mbostock/7881887
	// Resolves collisions between d and all other circles.
	function collide(alpha) {
		var quadtree = d3.geom.quadtree(nodes);
		return function(d) {
			var r = d.radius + cfg.nodeMaxRadius + Math.max(cfg.paddingNodes, cfg.paddingClusters),
				nx1 = d.x - r,
				nx2 = d.x + r,
				ny1 = d.y - r,
				ny2 = d.y + r;
			
			quadtree.visit(function(quad, x1, y1, x2, y2) {
				if (quad.point && (quad.point !== d)) {
					if (quad.point.fixed)
						return false;
					
					var	x = d.x - quad.point.x,
						y = d.y - quad.point.y,
						l = Math.sqrt(x * x + y * y),
						r = d.radius + quad.point.radius + (d.cluster === quad.point.cluster ? cfg.paddingNodes : cfg.paddingClusters);
					if (l < r) {
						l = (l - r) / l * alpha;
						d.x -= x *= l;
						d.y -= y *= l;
						quad.point.x += x;
						quad.point.y += y;
					}
				}
				return x1 > nx2 || x2 < nx1 || y1 > ny2 || y2 < ny1;
			});
		};
	}
	
	function initZoom() {
		zoom = d3.behavior.zoom()
			.size([cfg.width, cfg.height])
			.center([cfg.width / 2, cfg.height / 2])
			.scaleExtent([0.3, 10])
			.on('zoom', function() {
				// Avoid transition when dragging
				if (d3.event.sourceEvent !== null && d3.event.sourceEvent.type == 'mousemove')
					svg.attr('transform', 'translate(' + d3.event.translate + ')scale(' + d3.event.scale + ')');
				else
					svg.transition().duration(cfg.animationDuration).ease(cfg.animationEasing)
					.attr('transform', 'translate(' + d3.event.translate + ')scale(' + d3.event.scale + ')');
			});
		
		zoomDrag = d3.behavior.drag()
			.origin(function(d) { return d; })
			.on('dragstart', function(d) {
				d3.event.sourceEvent.stopPropagation();
				d3.select(this).classed('dragging', true);
			})
			.on('drag', function(d) {
				d3.select(this).attr('cx', d.x = d3.event.x).attr('cy', d.y = d3.event.y);
			})
			.on('dragend', function(d) {
				d3.select(this).classed('dragging', false);
			});
	}
	
	function interpolateData() {
		var	orderRadius = 200 + nodes.length / 3,
			orderTop    = cfg.height / 2,
			orderLeft   = cfg.width / 2,
			nodeSizes   = d3.scale.linear();
		
		nodes.forEach(function(node) {
			// Cluster-fallback
			node.cluster = (typeof node === 'undefined') ? 0 : node.cluster;
			
			// Metric-fallback
			if (cfg.nodeSizeMetric !== false && typeof node[cfg.nodeSizeMetric] === 'undefined')
				node[cfg.nodeSizeMetric] = 0;
		});
		
		updateClusters();
		
		nodeSizes
			.range([cfg.nodeMinRadius, cfg.nodeMaxRadius])
			// TODO!!!! nodes vs allNodes
			// We use all nodes because the metric doesn't change for hidden nodes
			.domain(d3.extent(nodes, function(n) { return n[cfg.nodeSizeMetric]; }));
		
		// Initialize clusters on a circle (r=200) around the central force
		// Reduces jitter and avoids mixup between clusters
		// Credits: Mike Bostock; http://bl.ocks.org/mbostock/7881887
		nodes.forEach(function(node) {
			// Radius according to metric
			if (cfg.nodeSizeMetric)
				node.radius = nodeSizes(node[cfg.nodeSizeMetric]);
			else
				node.radius = cfg.nodeUniformRadius;
			
			// Store the largest node as cluster-focus
			var focus = clusterFoci[node.cluster];
			if (!focus || (node.radius > focus.radius))
				clusterFoci[node.cluster] = node;
			
			node.x = Math.cos(node.cluster / clusters.length * 2 * Math.PI) * orderRadius + orderLeft + Math.random(),
			node.y = Math.sin(node.cluster / clusters.length * 2 * Math.PI) * orderRadius + orderTop + Math.random()
		});
	}
	
	function updateClusters() {
		clusters = d3.nest()
			.key(function(node) { return node.cluster; })
			.entries(nodes);
		
		color.domain(d3.range(clusters.length));
		clusterFoci = Array(clusters.length);
	}
	
	function initDecoration() {
		// SVG-panel
		svg = d3.select($container[0])
			.append('svg')
			.attr('class', 'communityNetwork')
			.attr('width', cfg.width)
			.attr('height', cfg.height)
			
			.append('g')
			.call(zoom);
			
		svg.append('rect')
			.attr('width', cfg.width)
			.attr('height', cfg.height)
			.style('fill', 'none')
			.style('pointer-events', 'all');
		
		svg = svg.append('g')
				.attr('transform', 'translate(0,0)scale(1.0)');
		
		// Containers
		svg.append('g').attr('class', 'link-container');
		svg.append('g').attr('class', 'hull-container');
		svg.append('g').attr('class', 'node-container');
	}
	
	function drawData() {
		var existingNodes = svg.select('.node-container').selectAll('.node').data(nodes),
			existingLinks = svg.select('.link-container').selectAll('.link').data(links),
			existingPaths = svg.select('.hull-container').selectAll('.hull').data(clusters),
			nodeContainers;
		
		// Update nodes
		existingNodes.select('circle')
			.transition().duration(cfg.animationDuration).ease(cfg.animationEasing)
			.attr('r', function(n) { return n.radius; })
			.style('fill', function(d) { return color(d.cluster); })
			.style('stroke', function(d) { return d3.rgb(color(d.cluster)).darker(); });
		
		// Insert nodes
		nodeContainers = existingNodes.enter()
			.append('g')
			.attr('class', 'node')
			.attr('transform', function(n) { return 'translate(' + n.x + ',' + n.y + ')'; })
			.call(drag)
			// To avoid interference between node-drag and navigation-drag
			.on('mousedown', function() { d3.event.stopPropagation(); })
			.on('mouseenter', nodeEnterEvent)
			.on('mouseleave', nodeLeaveEvent);
		
		// Draw nodes
		nodeContainers
			.append('circle')
			.attr('r', function(n) { return n.radius; })
			.style('fill', function(n) { return color(n.cluster); })
			.style('stroke', function(n) { return d3.rgb(color(n.cluster)).darker(); });
		// Draw node-labels
		nodeContainers
			.append('text')
			.attr('x', 0)
			.attr('y', function(n) { return -(n.radius + 3); })
			.attr('class', 'node-label')
			.text(function(n) { return n.name; });
		
		// Remove nodes
		existingNodes.exit().remove();
		
		// Update links
		existingLinks
			.attr('x1', function(d) { return d.source.x; })
			.attr('y1', function(d) { return d.source.y; })
			.attr('x2', function(d) { return d.target.x; })
			.attr('y2', function(d) { return d.target.y; });
		
		// Insert links
		existingLinks.enter()
			.append('line')
			.attr('class', 'link')
			.attr('x1', function(d) { return d.source.x; })
			.attr('y1', function(d) { return d.source.y; })
			.attr('x2', function(d) { return d.target.x; })
			.attr('y2', function(d) { return d.target.y; });
		
		// Remove links
		existingLinks.exit().remove();
		
		// Update paths
		existingPaths
			.style('fill', clusterFill)
			.style('stroke', clusterFill)
			.attr('d', clusterPath);
		
		// Insert paths
		existingPaths
			.enter()
			.insert('path', 'circle')
			.attr('class', 'hull')
			.style('fill', clusterFill)
			.style('stroke', clusterFill)
			.style('stroke-width', cfg.nodeMaxRadius + cfg.paddingNodes * 1.5)
			.attr('d', clusterPath);
		
		// Remove paths
		existingPaths.exit().remove();
	}
	
	function nodeEnterEvent() {
		d3.select(this).select('.node-label').classed('hover', true);
	}
	
	function nodeLeaveEvent() {
		var $this = d3.select(this);
		
		if (!$this.classed('dragactive'))
			$this.select('.node-label').classed('hover', false);
	}
	
	// Credits: donaldh; http://bl.ocks.org/donaldh/2920551
	function clusterPath(cluster) {
		return 'M' + 
			d3.geom.hull(cluster.values.map(function(node) { return [node.x, node.y]; }))
				.join('L')
			+ 'Z';
	}
	
	// Credits: donaldh; http://bl.ocks.org/donaldh/2920551
	function clusterFill(cluster, i) {
		return color(parseInt(cluster.key));
	}
}
