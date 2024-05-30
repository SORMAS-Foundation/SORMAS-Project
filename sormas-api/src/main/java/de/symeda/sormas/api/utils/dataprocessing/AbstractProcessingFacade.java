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

package de.symeda.sormas.api.utils.dataprocessing;

import java.util.Collections;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.feature.FeatureConfigurationFacade;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.community.CommunityFacade;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictFacade;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;

public abstract class AbstractProcessingFacade {

	private final FeatureConfigurationFacade featureConfigurationFacade;
	protected final CaseFacade caseFacade;
	protected final ContactFacade contactFacade;
	protected final RegionFacade regionFacade;
	protected final DistrictFacade districtFacade;
	protected final CommunityFacade communityFacade;

	public AbstractProcessingFacade(
		FeatureConfigurationFacade featureConfigurationFacade,
		CaseFacade caseFacade,
		ContactFacade contactFacade,
		RegionFacade regionFacade,
		DistrictFacade districtFacade,
		CommunityFacade communityFacade) {
		this.featureConfigurationFacade = featureConfigurationFacade;
		this.caseFacade = caseFacade;
		this.contactFacade = contactFacade;
		this.regionFacade = regionFacade;
		this.districtFacade = districtFacade;
		this.communityFacade = communityFacade;
	}

	public boolean isFeatureDisabled(FeatureType featureType) {
		return featureConfigurationFacade.isFeatureDisabled(featureType);
	}

	public boolean isFeatureEnabled(FeatureType featureType) {
		return featureConfigurationFacade.isFeatureEnabled(featureType);
	}

	public abstract boolean hasAllUserRights(UserRight... userRights);

	public List<CaseSelectionDto> getSimilarCases(PersonReferenceDto person, Disease disease) {
		if (isFeatureDisabled(FeatureType.CASE_SURVEILANCE) || !hasAllUserRights(UserRight.CASE_CREATE, UserRight.CASE_EDIT)) {
			return Collections.emptyList();
		}

		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.person(person);
		caseCriteria.disease(disease);
		CaseSimilarityCriteria caseSimilarityCriteria = new CaseSimilarityCriteria();
		caseSimilarityCriteria.caseCriteria(caseCriteria);
		caseSimilarityCriteria.personUuid(person.getUuid());

		return caseFacade.getSimilarCases(caseSimilarityCriteria);

	}

	public List<SimilarContactDto> getSimilarContacts(PersonReferenceDto selectedPerson, Disease disease) {

		if (isFeatureDisabled(FeatureType.CONTACT_TRACING) || !hasAllUserRights(UserRight.CONTACT_CREATE, UserRight.CONTACT_EDIT)) {
			return Collections.emptyList();
		}
		ContactSimilarityCriteria contactSimilarityCriteria = new ContactSimilarityCriteria();
		contactSimilarityCriteria.setPerson(selectedPerson);
		contactSimilarityCriteria.setDisease(disease);

		return contactFacade.getMatchingContacts(contactSimilarityCriteria);
	}

	public CaseDataDto getCaseDataByUuid(String uuid) {
		return caseFacade.getCaseDataByUuid(uuid);
	}

	public List<SimilarContactDto> getMatchingContacts(ContactSimilarityCriteria contactSimilarityCriteria) {
		return contactFacade.getMatchingContacts(contactSimilarityCriteria);
	}

	public ContactDto getContactByUuid(String uuid) {
		return contactFacade.getByUuid(uuid);
	}

	public RegionReferenceDto getDefaultRegionReference() {
		return regionFacade.getDefaultInfrastructureReference();
	}

	public DistrictReferenceDto getDefaultDistrictReference() {
		return districtFacade.getDefaultInfrastructureReference();
	}

	public CommunityReferenceDto getDefaultCommunityReference() {
		return communityFacade.getDefaultInfrastructureReference();
	}
}
