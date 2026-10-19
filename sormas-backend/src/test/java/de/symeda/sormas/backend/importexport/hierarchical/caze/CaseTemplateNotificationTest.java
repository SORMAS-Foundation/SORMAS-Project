package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade.CASE_TEMPLATE;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.messaging.MessagingService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.importexport.hierarchical.HierarchicalImportFacadeEjb.HierarchicalImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.hierarchical.engine.SheetFixtures;

public class CaseTemplateNotificationTest extends AbstractBeanTest {

	@Mock
	private FeatureConfigurationFacadeEjb configurationFacade;

	@Mock
	private MessagingService messagingService;

	@Override
	public void init() {
		super.init();
		Mockito.when(configurationFacade.isFeatureEnabled(any())).thenReturn(true);
		Mockito.when(configurationFacade.isFeatureEnabled(FeatureType.AUTH_PROVIDER_TO_SORMAS_USER_SYNC)).thenReturn(false);
	}

	@Test
	public void aPositiveResultImportedFromANestedFileNotifiesLikeTheClassicImport() throws Exception {
		useNationalAdminLogin();
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		creator.createFacility("Central Lab", rdcf.region, rdcf.district, null, FacilityType.LABORATORY);
		creator.createUser(rdcf, "Surv", "Sup", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		HierarchicalImportFacade facade = getBean(HierarchicalImportFacadeEjbLocal.class);
		ImportSheet sheet = SheetFixtures.table(
			"| CaseData.disease | CaseData.reportDate | CaseData.responsibleRegion | CaseData.responsibleDistrict | CaseData.facilityType "
				+ "| CaseData.healthFacility | Person.person.firstName | Person.person.lastName | Person.person.sex | Sample.fieldSampleID "
				+ "| Sample.sampleDateTime | Sample.sampleMaterial | Sample.lab | PathogenTest.testType | PathogenTest.testDateTime "
				+ "| PathogenTest.testResult |",
			"| TUBERCULOSIS | 9/1/2026 | Region | District | HOSPITAL | Facility | Jean | Muller | MALE | S1 | 9/2/2026 | BLOOD | Central Lab "
				+ "| CULTURE | 9/3/2026 | POSITIVE |");
		ImportPreviewDto preview = facade.checkImport(CASE_TEMPLATE, sheet);
		Mockito.clearInvocations(messagingService);

		facade.saveImport(CASE_TEMPLATE, sheet, preview.readyFingerprints());

		Mockito.verify(messagingService, Mockito.atLeastOnce()).sendEmail(any(), any(), any());
	}
}
