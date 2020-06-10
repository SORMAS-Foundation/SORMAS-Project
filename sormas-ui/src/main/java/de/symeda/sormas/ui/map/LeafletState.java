/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.map;

import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * State of the map which is transferred to the web browser whenever a property
 * changed.
 */
public class LeafletState extends JavaScriptComponentState {

	private static final long serialVersionUID = -8746016099669605525L;

	private int zoom;
	private double centerLatitude;
	private double centerLongitude;

	private boolean tileLayerVisible;
	private float tileLayerOpacity;

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public double getCenterLatitude() {
		return centerLatitude;
	}

	public void setCenterLatitude(double centerLatitude) {
		this.centerLatitude = centerLatitude;
	}

	public double getCenterLongitude() {
		return centerLongitude;
	}

	public void setCenterLongitude(double centerLongitude) {
		this.centerLongitude = centerLongitude;
	}

	public boolean isTileLayerVisible() {
		return tileLayerVisible;
	}

	public void setTileLayerVisible(boolean tileLayerVisible) {
		this.tileLayerVisible = tileLayerVisible;
	}

	public float getTileLayerOpacity() {
		return tileLayerOpacity;
	}

	public void setTileLayerOpacity(float tileLayerOpacity) {
		this.tileLayerOpacity = tileLayerOpacity;
	}
}
