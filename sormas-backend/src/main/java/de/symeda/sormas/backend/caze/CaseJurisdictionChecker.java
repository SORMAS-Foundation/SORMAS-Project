package de.symeda.sormas.backend.caze;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.utils.jurisdiction.caze.CaseJurisdictionHelper;
import de.symeda.sormas.backend.sample.SampleJurisdictionChecker;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless(name = "CaseJurisdictionChecker")
@LocalBean
public class CaseJurisdictionChecker {

	@EJB
	private UserService userService;
	@EJB
	private SampleJurisdictionChecker sampleJurisdictionChecker;

	public Boolean isInJurisdictionOrOwned(Case caze) {

		return isInJurisdictionOrOwned(JurisdictionHelper.createCaseJurisdictionDto(caze))
			|| caze.getSamples().stream().anyMatch(sample -> sampleJurisdictionChecker.isInJurisdictionOrOwned(sample)); //
	}

	public Boolean isInJurisdictionOrOwned(CaseJurisdictionDto caseJurisdictionDto) {
		final User user = userService.getCurrentUser();
		return CaseJurisdictionHelper
			.isInJurisdictionOrOwned(user.getJurisdictionLevel(), JurisdictionHelper.createUserJurisdiction(user), caseJurisdictionDto);

	}
}
