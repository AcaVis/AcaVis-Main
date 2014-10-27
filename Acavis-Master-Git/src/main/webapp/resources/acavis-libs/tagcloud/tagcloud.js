function TagCloud(container, settings) {
	var $container = $((typeof container === 'undefined') ? '' : container).first(),
		// Override default config with user-settings
		cfg = $.extend(true, {
			valueAttribute: 'wordcount',
			
			colorRangeStart: '#cde',
			colorRangeEnd: '#f52',
			
			fontsizeRangeStart: '1',
			fontsizeRangeEnd: '2',
			fontsizeUnit: 'em'
		}, settings),
		
		colorScale = d3.scale.linear().range([cfg.colorRangeStart, cfg.colorRangeEnd]),
		fontsizeScale = d3.scale.linear().range([cfg.fontsizeRangeStart, cfg.fontsizeRangeEnd]);
	
	
	this.restyle = function() {
		var	$words     = $container.children(),
			wordExtent = d3.extent($words, function(word) { return $(word).data(cfg.valueAttribute); });
		
		// Update domains
		colorScale.domain(wordExtent);
		fontsizeScale.domain(wordExtent);
		
		// Update styles
		$words.each(function() {
			var $this = $(this),
				value = $this.data(cfg.valueAttribute);
			
			$this
				.css('color', colorScale(value))
				.css('font-size', fontsizeScale(value)+cfg.fontsizeUnit);
		}).shuffle();
	}
}

(function($){
	$.fn.shuffle = function() {
		var allElems = this.get(),
			getRandom = function(max) {
				return Math.floor(Math.random() * max);
			},
			shuffled = $.map(allElems, function(){
				var random = getRandom(allElems.length),
					randEl = $(allElems[random]).clone(true)[0];
				allElems.splice(random, 1);
				return randEl;
			});
		this.each(function(i){
			$(this).replaceWith($(shuffled[i]));
		});
		return $(shuffled);
	};
})(jQuery);
