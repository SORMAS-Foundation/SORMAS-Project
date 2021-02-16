/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api;

import javax.ejb.Remote;

import de.symeda.sormas.api.externaljournal.PatientDiaryConfig;
import de.symeda.sormas.api.externaljournal.SymptomJournalConfig;
import de.symeda.sormas.api.region.GeoLatLon;

@Remote
public interface ConfigFacade {

	String getCountryName();

	String getCountryLocale();

	String getCountryCode();

	boolean isConfiguredCountry(String countryCode);

	String getEpidPrefix();

	String getAppUrl();

	boolean isFeatureAutomaticCaseClassification();

	String getEmailSenderAddress();

	String getEmailSenderName();

	String getSmsSenderName();

	String getSmsAuthKey();

	String getSmsAuthSecret();

	String getDocumentFilesPath();

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

	boolean isUseLoginSidebar();

	String getLoginBackgroundPath();

	String getSormasInstanceName();

	double getNameSimilarityThreshold();

	int getInfrastructureSyncThreshold();

	int getDaysAfterCaseGetsArchived();

	int getDaysAfterEventGetsArchived();

	int getDaysAfterSystemEventGetsDeleted();

	GeoLatLon getCountryCenter();

	int getMapZoom();

	String getGeocodingServiceUrlTemplate();

	String getGeocodingLongitudeJsonPath();

	String getGeocodingLatitudeJsonPath();

	SymptomJournalConfig getSymptomJournalConfig();

	PatientDiaryConfig getPatientDiaryConfig();

	String getSormasToSormasUserPassword();

	void validateExternalUrls();

	SormasToSormasConfig getSormasToSormasConfig();

	String getSurvnetGatewayUrl();

	String getAuthenticationProvider();

	boolean isAuthenticationProviderUserSyncAtStartupEnabled();

	boolean isExternalJournalActive();

	int getDashboardMapMarkerLimit();

	boolean isSmsServiceSetUp();

	String getDemisJndiName();
}
