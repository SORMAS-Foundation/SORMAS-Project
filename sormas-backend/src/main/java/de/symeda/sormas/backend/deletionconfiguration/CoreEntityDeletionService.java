package de.symeda.sormas.backend.deletionconfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import de.symeda.sormas.api.deletionconfiguration.CoreEntityFacade;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;

// @LocalBean
@Singleton
@RunAs(UserRole._SYSTEM)
public class CoreEntityDeletionService {

	private final List<DataHelper.Pair<CoreEntityType, CoreEntityFacade>> coreEntityFacades = new ArrayList<>();
	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacadeEjb;

	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacadeEjb;

	@EJB
	private CaseService caseService;

	public CoreEntityDeletionService() {
		coreEntityFacades.add(new DataHelper.Pair<>(CoreEntityType.CASE, caseService));
		coreEntityFacades.add(new DataHelper.Pair<>(CoreEntityType.CONTACT, contactFacadeEjb));

	}

	public void executeAutomaticDeletion() {

		coreEntityFacades.stream().forEach(coreEntityType -> {

//			DeletionConfiguration forEntityType = deletionConfigurationService.getForEntityType(coreEntityType.getElement0());
			DeletionConfiguration forEntityType = new DeletionConfiguration();
//			DeletionConfiguration forEntityType = new DeletionConfiguration();
			forEntityType.setEntityType(CoreEntityType.CASE);
			forEntityType.setDeletionReference(DeletionReference.CREATION);

			coreEntityType.getElement1().executeAutomaticDeletion(forEntityType.deletionReference, new Date());

		});

	}

}
