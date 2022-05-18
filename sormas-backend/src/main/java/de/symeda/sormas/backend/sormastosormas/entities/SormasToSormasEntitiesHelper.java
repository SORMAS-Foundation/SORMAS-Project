package de.symeda.sormas.backend.sormastosormas.entities;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import org.apache.commons.lang3.StringUtils;

@Stateless
@LocalBean
public class SormasToSormasEntitiesHelper {

	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private DistrictService districtService;

	public void updateCaseResponsibleDistrict(CaseDataDto caze, String districtExternalId) {
		if (StringUtils.isNoneBlank(districtExternalId)) {
			List<DistrictDto> districts = districtFacade.getByExternalId(districtExternalId, false);
			if (!districts.isEmpty()) {
				DistrictDto district = districts.get(0);

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
	}

	public void updateCaseResponsibleDistrict(Case caze, String districtExternalId) {
		if (StringUtils.isNoneBlank(districtExternalId)) {
			List<District> districts = districtService.getByExternalId(districtExternalId, false);
			if (!districts.isEmpty()) {
				District district = districts.get(0);

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

	public void updateContactResponsibleDistrict(ContactDto contact, String districtExternalId) {
		if (StringUtils.isNoneBlank(districtExternalId)) {
			List<DistrictDto> districts = districtFacade.getByExternalId(districtExternalId, false);
			if (!districts.isEmpty()) {
				DistrictDto district = districts.get(0);

				contact.setRegion(district.getRegion());
				contact.setDistrict(district.toReference());
				contact.setCommunity(null);
			}
		}
	}

	public void updateContactResponsibleDistrict(Contact contact, String districtExternalId) {
		if (StringUtils.isNoneBlank(districtExternalId)) {
			List<District> districts = districtService.getByExternalId(districtExternalId, false);
			if (!districts.isEmpty()) {
				District district = districts.get(0);

				contact.setRegion(district.getRegion());
				contact.setDistrict(district);
				contact.setCommunity(null);
			}
		}
	}
}
