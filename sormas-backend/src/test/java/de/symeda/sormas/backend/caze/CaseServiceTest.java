package de.symeda.sormas.backend.caze;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class CaseServiceTest extends AbstractBeanTest {

    private TestDataCreator.RDCF rdcf;
    private UserDto nationalUser;

    @Override
    public void init() {
        super.init();
        rdcf = creator.createRDCF("Region", "District", "Community", "Facility", "Point of entry");
        nationalUser = creator.createUser(
                rdcf.region.getUuid(),
                rdcf.district.getUuid(),
                rdcf.community.getUuid(),
                rdcf.facility.getUuid(),
                "Nat",
                "User",
                creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
    }

    @Test
    public void testGetRegionAndDistrictRefsOf() {

        creator.createPerson();
        CaseDataDto caze = creator.createCase(nationalUser.toReference(), creator.createPerson().toReference(), rdcf);
        TestDataCreator.RDCF responsibleRdcf = creator.createRDCF();
        caze.setResponsibleRegion(responsibleRdcf.region); // this shall be returned, not the caze.region
        caze.setResponsibleDistrict(responsibleRdcf.district); // this shall be returned, not the caze.district
        caze.setResponsibleCommunity(responsibleRdcf.community);  // necessary for validation
        caze.setHealthFacility(responsibleRdcf.facility);  // necessary for validation
        getCaseFacade().save(caze);

        // create noise
        creator.createCase(nationalUser.toReference(), creator.createPerson().toReference(), rdcf);

        // test
        DataHelper.Pair<RegionReferenceDto, DistrictReferenceDto> regionAndDistrictRefs =
                getCaseService().getRegionAndDistrictRefsOf(caze.toReference());
        assertEquals(responsibleRdcf.region, regionAndDistrictRefs.getElement0());
        assertEquals(responsibleRdcf.district, regionAndDistrictRefs.getElement1());

    }
}
