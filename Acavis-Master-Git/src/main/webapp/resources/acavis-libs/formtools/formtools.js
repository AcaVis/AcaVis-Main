/***
  Initializes the default spinners using their max and min values
  
  :Attributes (of inputs)
  value
  data-spinner-min
  data-spinner-max
***/
(function($) {
	$('.input-spinner').each(function() {
		var	$this = $(this),
			min = parseInt($this.data('spinner-min')),
			max = parseInt($this.data('spinner-max')),
			val = parseInt($this.val());
		
		$this.TouchSpin({
			min: min,
			max: max,
			value: val,
			step: 1,
			decimals: 0,
			boostat: 5,
			maxboostedstep: 3
		});
	});
})(jQuery);

/***
  Chained disable/enable using a checkbox
  
  :Attributes
  data-target={document-based selector}
  data-form-action=enable|disable
***/
(function($) {
	// Register enabler event
	$('input:checkbox[data-form-action="enable"]').change(function() {
		var	$self   = $(this),
			$target = $($self.data('target')),
			state   = $self.prop('checked');
		
		$target.prop('disabled', !state);
	})
	// Trigger once
	.change();
	
	// Register enabler event
	$('input:checkbox[data-form-action="disable"]').change(function() {
		var	$self   = $(this),
			$target = $($self.data('target')),
			state   = $self.prop('checked');
		
		$target.prop('disabled', state);
	})
	// Trigger once
	.change();
	
	// Register multi-enable-disable event
	$('input:checkbox[data-form-action="biable"]').change(function() {
		var	$self      = $(this),
			$inphase   = $($self.data('target-inphase')),
			$antiphase = $($self.data('target-antiphase')),
			state      = $self.prop('checked');
		
		$inphase.prop('disabled', !state);
		$antiphase.prop('disabled', state);
	})
	// Trigger event
	.change();
})(jQuery);

/***
  Two spinners with upper/lower constraint
  
  :Parameters
  lowerBoundSelector (should be a selector or jQuery-object for a single element)
  upperBoundSelector (should be a selector or jQuery-object for a single element)
  
  :Attributes
  data-spinner-min (absolute bound)
  data-spinner-max (absolute bound)
  value (init)
***/
function initializeRangeSpinners(lowerBoundSelector, upperBoundSelector) {
	var $spinners = $(lowerBoundSelector).add(upperBoundSelector);
	
	$spinners.each(function() {
		var	$this = $(this),
			min = parseInt($this.data('spinner-min')),
			max = parseInt($this.data('spinner-max')),
			val = parseInt($this.val());
		
		$this.TouchSpin({
			min: min,
			max: max,
			value: val,
			step: 1,
			decimals: 0,
			boostat: 5,
			maxboostedstep: 3
		});
	});
	
	/* Adjust spinner-constraints on change, to avoid negative timespans */
	$(lowerBoundSelector).on('change', function() {
		var val = parseInt($(this).val());
		
		$(upperBoundSelector).trigger('touchspin.updatesettings', { min: val });
	});
	/* Adjust spinner-constraints on change, to avoid negative timespans */
	$(upperBoundSelector).on('change', function() {
		var val = parseInt($(this).val());
		
		$(lowerBoundSelector).trigger('touchspin.updatesettings', { max: val });
	});
	
	/* Initialize the constraint that avoids negative timespans */
	$spinners.change();
}
