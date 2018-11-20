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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.map;

public enum MarkerIcon {

	// make sure to update mapIcons in leaflet-connector.js when editing this
	
	RED_DOT("red-dot"),
	RED_DOT_SMALL("red-dot-small"), 
	RED_DOT_LARGE("red-dot-large"),
	RED_DOT_VERY_LARGE("red-dot-very-large"),
	RED_HOUSE("red-house"),
	RED_HOUSE_SMALL("red-house-small"),
	RED_HOUSE_LARGE("red-house-large"),
	RED_HOUSE_VERY_LARGE("red-house-very-large"),
	RED_CONTACT("red-contact"),
	YELLOW_DOT("yellow-dot"), 
	YELLOW_DOT_SMALL("yellow-dot-small"), 
	YELLOW_DOT_LARGE("yellow-dot-large"),
	YELLOW_DOT_VERY_LARGE("yellow-dot-very-large"),
	YELLOW_HOUSE("yellow-house"),
	YELLOW_HOUSE_SMALL("yellow-house-small"),
	YELLOW_HOUSE_LARGE("yellow-house-large"),
	YELLOW_HOUSE_VERY_LARGE("yellow-house-very-large"), 
	ORANGE_DOT("orange-dot"),
	ORANGE_DOT_SMALL("orange-dot-small"), 
	ORANGE_DOT_LARGE("orange-dot-large"),
	ORANGE_DOT_VERY_LARGE("orange-dot-very-large"),
	ORANGE_HOUSE("orange-house"),
	ORANGE_HOUSE_SMALL("orange-house-small"), 
	ORANGE_HOUSE_LARGE("orange-house-large"),
	ORANGE_HOUSE_VERY_LARGE("orange-house-very-large"),
	ORANGE_CONTACT("orange-contact"), 
	GREY_DOT("grey-dot"),
	GREY_DOT_SMALL("grey-dot-small"), 
	GREY_DOT_LARGE("grey-dot-large"), 
	GREY_DOT_VERY_LARGE("grey-dot-very-large"),
	GREY_HOUSE("grey-house"), 
	GREY_HOUSE_SMALL("grey-house-small"), 
	GREY_HOUSE_LARGE("grey-house-large"),
	GREY_HOUSE_VERY_LARGE("grey-house-very-large"), 
	GREY_CONTACT("grey-contact"), 
	GREEN_CONTACT("green-contact"),
	OUTBREAK("outbreak"), 
	RUMOR("rumor");

	private final String imgName;

	private MarkerIcon(String imgName) {
		this.imgName = imgName;
	}

	public String getThemeUrl() {
		return "icons/" + imgName + ".png";
	};
	
	public String getExternalUrl() {
		return "VAADIN/map/icons/" + imgName + ".png";
	};
}