package de.symeda.sormas.ui.dashboard.visualisation;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;

@JavaScript({"htmlwidgets-1.5.1/htmlwidgets.js",  "vis-4.20.1/vis.min.js", "visNetwork-binding-2.0.9/visNetwork.js", "networkDiagram-connector.js" })
@StyleSheet({ "vis-4.20.1/vis.css"})
public class NetworkDiagram extends AbstractJavaScriptComponent {
	
	private static final long serialVersionUID = 1L;

	public NetworkDiagram() {
	}

	@Override
	protected NetworkDiagramState getState() {
		return (NetworkDiagramState) super.getState();
	}

	public void updateDiagram(final String jsonData) {
		getState().jsonData = jsonData;
	}
}
