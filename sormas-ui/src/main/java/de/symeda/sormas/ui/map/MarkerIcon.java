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
	
	CASE_CONFIRMED,
	CASE_SUSPECT,
	CASE_PROBABLE,
	CASE_UNCLASSIFIED,
	FACILITY_CONFIRMED,
	FACILITY_SUSPECT,
	FACILITY_PROBABLE,
	FACILITY_UNCLASSIFIED,
	CONTACT_LONG_OVERDUE,
	CONTACT_OVERDUE,
	CONTACT_OK,
	EVENT_OUTBREAK,
	EVENT_RUMOR,	
	

//	public String getThemeUrl() {
//		return "icons/" + imgName + ".png";
//	};
//	
//	public String getExternalUrl() {
//		return "VAADIN/map/icons/" + imgName + ".png";
//	};
}