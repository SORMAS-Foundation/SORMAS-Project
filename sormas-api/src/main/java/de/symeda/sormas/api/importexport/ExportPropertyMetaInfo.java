/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.importexport;

public class ExportPropertyMetaInfo {

	private final String propertyId;

	private final String caption;

	private final ExportGroupType exportGroupType;

	public ExportPropertyMetaInfo(String propertyId, String caption, ExportGroupType exportGroupType) {
		this.propertyId = propertyId;
		this.caption = caption;
		this.exportGroupType = exportGroupType;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public String getCaption() {
		return caption;
	}

	public ExportGroupType getExportGroupType() {
		return exportGroupType;
	}
}
