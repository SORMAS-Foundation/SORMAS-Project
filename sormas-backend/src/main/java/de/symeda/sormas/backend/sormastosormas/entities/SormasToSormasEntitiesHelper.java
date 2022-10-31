package de.symeda.sormas.backend.sormastosormas.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.externalmessage.ExternalMessage;
import de.symeda.sormas.backend.externalmessage.ExternalMessageService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;

@Stateless
@LocalBean
public class SormasToSormasEntitiesHelper {

	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private DistrictService districtService;
	@EJB
	private ExternalMessageService externalMessageService;
	@EJB
	private PersonService personService;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	public static final String HAS_DETAILS = "hasDetails";

	public void updateIfNecessaryOccupationType(PersonDto sharedPersonData) {
		if (sharedPersonData.getOccupationType() != null) {
			String occupationTypeCaption = sharedPersonData.getOccupationType().getCaption();
			if (occupationTypeCaption != null) {
				//checking if there are persons with the given occupationType
				List<Person> personList = personService.getByOccupationType(occupationTypeCaption);
				if (personList == null) {
					OccupationType occupationType = sharedPersonData.getOccupationType();
					Map<String, Object> propertiesDetailsTrue = new HashMap<>();
					propertiesDetailsTrue.put(HAS_DETAILS, true);

					occupationType.setCaption("OTHER");
					occupationType.setValue("OTHER");
					occupationType.setProperties(propertiesDetailsTrue);

					sharedPersonData.setOccupationType(occupationType);
				}
			}
		}
	}

	public void updateReceivedCaseResponsibleDistrict(CaseDataDto caze) {
		Optional<DistrictDto> districts = getS2SDistrictReference();
		if (districts.isPresent()) {
			DistrictDto district = districts.get();

			if (caze.getRegion() == null) {
				caze.setRegion(caze.getResponsibleRegion());
				caze.setDistrict(caze.getResponsibleDistrict());
				caze.setCommunity(caze.getResponsibleCommunity());
			}

			caze.setResponsibleRegion(district.getRegion());
			caze.setResponsibleDistrict(district.toReference());
			caze.setResponsibleCommunity(null);
		}
	}

	public Optional<DistrictDto> getS2SDistrictReference() {
		String districtExternalId = configFacade.getS2SConfig().getDistrictExternalId();
		if (districtExternalId == null) {
			return Optional.empty();
		}

		List<DistrictDto> districts = districtFacade.getByExternalId(districtExternalId, false);

		return Optional.ofNullable(districts.isEmpty() ? null : districts.get(0));
	}

	public void updateSentCaseResponsibleDistrict(Case caze, String districtExternalId) {
		if (StringUtils.isNoneBlank(districtExternalId)) {
			Optional<District> districts = getS2SDistrict(districtExternalId);
			if (districts.isPresent()) {
				District district = districts.get();

				if (caze.getRegion() == null) {
					caze.setRegion(caze.getResponsibleRegion());
					caze.setDistrict(caze.getResponsibleDistrict());
					caze.setCommunity(caze.getResponsibleCommunity());
				}

				caze.setResponsibleRegion(district.getRegion());
				caze.setResponsibleDistrict(district);
				caze.setResponsibleCommunity(null);
			}
		}
	}

	private Optional<District> getS2SDistrict(String externalId) {
		List<District> districts = districtService.getByExternalId(externalId, false);

		return Optional.ofNullable(districts.isEmpty() ? null : districts.get(0));
	}

	public void updateReceivedContactResponsibleDistrict(ContactDto contact) {
		Optional<DistrictDto> districts = getS2SDistrictReference();
		if (districts.isPresent()) {
			DistrictDto district = districts.get();

			contact.setRegion(district.getRegion());
			contact.setDistrict(district.toReference());
			contact.setCommunity(null);
		}
	}

	public void updateSentContactResponsibleDistrict(Contact contact, String districtExternalId) {
		if (StringUtils.isNoneBlank(districtExternalId)) {
			Optional<District> districts = getS2SDistrict(districtExternalId);
			if (districts.isPresent()) {
				District district = districts.get();

				contact.setRegion(district.getRegion());
				contact.setDistrict(district);
				contact.setCommunity(null);
			}
		}
	}

	public void updateSampleOnShare(Sample sample, SormasToSormasShareInfo sareInfo) {
		if (sareInfo.isOwnershipHandedOver()) {
			sample.getSampleReports().forEach(r -> {
				ExternalMessage m = r.getLabMessage();
				m.setStatus(ExternalMessageStatus.FORWARDED);
				externalMessageService.ensurePersisted(m);
			});
		}
	}
}
