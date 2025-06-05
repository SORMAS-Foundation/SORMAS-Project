/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.symptoms;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

public enum MinorInfectionSite {

	NOT_APPLICABLE,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	BONES_JOINTS_OTHER_THAN_VERTEBRAE,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	CNS_EXCEPT_MENINGES,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	DISSEMINATED_FORM,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	EXTRAPULMONARY_SITE_UNKNOWN,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	EXTRA_THORACIC_LYMPH_NODES,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	INTRATHORACIC_LYMPH_NODES,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	MENINGES,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	PERITONEUM_DIGESTIVE_TRACT,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	PLEURA,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	UROGENITAL_SYSTEM,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	VERTEBRAE,
	UNKNOWN,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
