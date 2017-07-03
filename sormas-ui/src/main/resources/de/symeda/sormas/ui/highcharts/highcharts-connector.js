window.de_symeda_sormas_ui_highcharts_HighChart = function () {

	this.onStateChange = function () {

		// make sure to manually reload this after making changes, because it is being cached  

		// read state
		var domId = this.getState().domId;
		var hcjs = this.getState().hcjs;

		var connector = this;

		// evaluate highcharts JS which needs to define var "options"
		eval(hcjs);
		
		// set chart context
		var chart = Highcharts.chart(domId, options);
		chart.setSize(this.getElement().offsetWidth, this.getElement().offsetHeight, { duration: 0 });
		
		// resize the diagram whenever the vaadin element is resized
		this.addResizeListener(this.getElement(), function(o,b) {
			chart.setSize(o.element.offsetWidth, o.element.offsetHeight, { duration: 0 });
		});
	};
}