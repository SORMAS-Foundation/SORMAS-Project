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
package de.symeda.sormas.api.importexport;

import java.io.IOException;

import javax.ejb.Remote;

@Remote
public interface ImportFacade {

	/**
	 * Creates a .csv file with one row containing all relevant column names of the case entity
	 * and its sub-entities and returns the path to the .csv file that can then be used to offer
	 * it as a download.
	 */
	void generateCaseImportTemplateFile() throws IOException;
	
	void generateCaseContactImportTemplateFile() throws IOException;

	void generateCaseLineListingImportTemplateFile() throws IOException;

	void generatePointOfEntryImportTemplateFile() throws IOException;
	
	void generatePopulationDataImportTemplateFile() throws IOException;
	
	void generateRegionImportTemplateFile() throws IOException;

	void generateDistrictImportTemplateFile() throws IOException;

	void generateCommunityImportTemplateFile() throws IOException;

	void generateFacilityLaboratoryImportTemplateFile() throws IOException;

	void generateContactImportTemplateFile() throws IOException;

	String getCaseImportTemplateFilePath();
	
	String getPointOfEntryImportTemplateFilePath();
	
	String getPopulationDataImportTemplateFilePath();

	String getCaseLineListingImportTemplateFilePath();

	String getRegionImportTemplateFilePath();

	String getDistrictImportTemplateFilePath();

	String getCommunityImportTemplateFilePath();

	String getFacilityLaboratoryImportTemplateFilePath();

	String getCaseContactImportTemplateFilePath();

	String getContactImportTemplateFilePath();
}
