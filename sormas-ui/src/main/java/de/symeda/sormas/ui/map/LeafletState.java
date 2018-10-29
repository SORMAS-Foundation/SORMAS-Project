package de.symeda.sormas.ui.map;

import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * State of the map which is transferred to the web browser whenever a property
 * changed.
 */
public class LeafletState extends JavaScriptComponentState {

	private static final long serialVersionUID = -8746016099669605525L;

	public int zoom;
	public double centerLatitude;
	public double centerLongitude;

	public boolean tileLayerVisible;
	public float tileLayerOpacity;
}