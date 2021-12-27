/**
 * 
 */

window.de_symeda_sormas_ui_dashboard_visualisation_NetworkDiagram = function () {

	var self = this;
	var htmlwidgetId = "htmlwidget-" + randomString(20, '0123456789abcdef');
	var container = self.getElement();
	container.classList.add('htmlwidget_container');
	
	var jsonData;
	
	function randomString(length, chars) {
		var result = '';
		for (var i = length; i > 0; --i) result += chars[Math.floor(Math.random() * chars.length)];
		return result;
	}
	
	this.onStateChange = function() {
        jsonData = this.getState().jsonData;
		self.updateWidget();
    };
	
	this.updateWidget = function () {
		
		if (jsonData == null) {
			container.innerHTML = '<div class="v-label v-label-h2 h2">No data available</div>';
			return;
		}
		
		container.innerHTML = '';
		
		var el = document.createElement("div");
		el.id = htmlwidgetId;
		el.classList.add('visNetwork', 'html-widget');
		el.style.cssText = "width:100%; height:100%";
		container.appendChild(el);
		
		var jsonEl = document.createElement("script");
		jsonEl.setAttribute("type", "application/json");
		jsonEl.setAttribute("data-for", htmlwidgetId);
		jsonEl.innerHTML = jsonData;
		container.appendChild(jsonEl);
		
		var sizingEl = document.createElement("script");
		sizingEl.setAttribute("type", "htmlwidget-sizing");
		sizingEl.setAttribute("data-for", htmlwidgetId);
		sizingEl.innerHTML = '{"viewer":{"width":"100%","height":"100%","padding":15,"fill":false},"browser":{"width":"100%","height":"100%","padding":40,"fill":false}}';
		container.appendChild(sizingEl);
		
		window.HTMLWidgets.staticRender();
	};
};
