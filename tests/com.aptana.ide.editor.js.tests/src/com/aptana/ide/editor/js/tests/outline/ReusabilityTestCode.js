function PopUpCal() {
	this._nextId = 0; 
	this._inst = []; 
	this._curInst = null; 
	this._disabledInputs = []; 
	this._popUpShowing = false; 
	this._inDialog = false; 
	this.regional = []; 
	this.regional[''] = { 
		clearText: 'Clear', 
		closeText: 'Close', 
		prevText: '&lt;Prev', 
		nextText: 'Next&gt;', 
		currentText: 'Today', 
		dayNames: ['Su','Mo','Tu','We','Th','Fr','Sa'],
		monthNames: ['January','February','March','April','May','June',
			'July','August','September','October','November','December'], 
		dateFormat: 'DMY/' 
			
	};
	this._defaults = { 
		autoPopUp: 'focus', 
			
		defaultDate: null, 
			
		appendText: '', 
		buttonText: '...', 
		buttonImage: '', 
		buttonImageOnly: false, 
		closeAtTop: true, 
			
		hideIfNoPrevNext: false, 
			
		changeMonth: true, 
		changeYear: true, 
		yearRange: '-10:+10', 
			
		firstDay: 0, 
		changeFirstDay: true, 
		showOtherMonths: false, 
		minDate: null, 
		maxDate: null, 
		speed: 'medium', 
		customDate: null,
			
		fieldSettings: null, 
			
		onSelect: null 
	};
	$.extend(this._defaults, this.regional['']);
	this._calendarDiv = $('<div id="calendar_div"></div>');
	$(document.body).append(this._calendarDiv);
	$(document.body).mousedown(this._checkExternalClick);
}