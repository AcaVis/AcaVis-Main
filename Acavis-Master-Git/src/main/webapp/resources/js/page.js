/**
 * Page-loader
 */
var Pageloader = (function($) {
	var	$pageloader       = $('.pageloader-overlay');
	
	return {
			show: function() {
				$pageloader.css({
					display: 'block',
					opacity: 1
				});
			},
			hide: function() {
				$pageloader.css({ opacity: 0 });
				
				setTimeout(function() {
					$pageloader.css({ display: 'none' });
				}, 200);
			}
		};
})(jQuery);


/**
 * Help-tips
 * @param $ The jQuery object in a local manner to avoid naming-conflicts
 */
(function($) {
	$('.help-tip')
		.each(function() {
			$(this)
				.data('content', $(this).html())
				.html('<span class="glyphicon glyphicon-question-sign"></span>')
				.addClass('ready');
		})
		.popover({
			container: 'body',
			html: true,
			placement: 'right',
			trigger: 'hover',
			viewport: { selector: 'body', padding: '2em' }
		});
})(jQuery);
