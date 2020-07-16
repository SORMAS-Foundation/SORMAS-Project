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
package de.symeda.sormas.api;

import de.symeda.sormas.api.region.GeoLatLon;

import javax.ejb.Remote;

@Remote
public interface ConfigFacade {

	String getCountryName();

	String getCountryLocale();

	boolean isGermanServer();

	String getEpidPrefix();

	String getAppUrl();

	boolean isFeatureAutomaticCaseClassification();

	String getEmailSenderAddress();

	String getEmailSenderName();

	String getSmsSenderName();

	String getSmsAuthKey();

	String getSmsAuthSecret();

	String getTempFilesPath();

	String getGeneratedFilesPath();

	String getCustomFilesPath();

	String getRScriptExecutable();

	char getCsvSeparator();

	String getAppLegacyUrl();

	void validateAppUrls();

	boolean isDevMode();

	boolean isCustomBranding();

	String getCustomBrandingName();

	String getCustomBrandingLogoPath();

	String getSormasInstanceName();

	double getNameSimilarityThreshold();

	int getInfrastructureSyncThreshold();

	int getDaysAfterCaseGetsArchived();

	int getDaysAfterEventGetsArchived();

	GeoLatLon getCountryCenter();

	int getMapZoom();

	String getGeocodingOsgtsEndpoint();

	String getPIAUrl();

	void validateExternalUrls();
}
