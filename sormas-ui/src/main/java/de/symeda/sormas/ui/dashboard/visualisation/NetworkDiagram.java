package de.symeda.sormas.ui.dashboard.visualisation;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;

/*
 * Update of vis-network:
 * curl https://unpkg.com/vis-network@latest/dist/vis-network.min.js > vis-network.min.js
 * curl https://unpkg.com/vis-network@latest/dist/vis-network.min.js.map > vis-network.min.js.map
 * curl https://unpkg.com/vis-network@latest/dist/vis-network.min.css > vis-network.min.css
 */
@JavaScript({"htmlwidgets-1.5.1/htmlwidgets.js",  "vis-network-7.6.2/vis-network.min.js", "visNetwork-binding-2.0.9/visNetwork.js", "networkDiagram-connector.js" })
@StyleSheet({ "vis-network-7.6.2/vis-network.min.css"})
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
