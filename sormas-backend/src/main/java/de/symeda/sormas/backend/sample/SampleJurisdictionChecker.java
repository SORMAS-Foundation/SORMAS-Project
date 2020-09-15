package de.symeda.sormas.backend.sample;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.sample.SampleJurisdictionDto;
import de.symeda.sormas.api.utils.jurisdiction.SampleJurisdictionHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless(name = "SampleJurisdictionChecker")
@LocalBean
public class SampleJurisdictionChecker {

	@EJB
	private UserService userService;

	public boolean isInJurisdictionOrOwned(Sample sample) {

		return isInJurisdictionOrOwned(JurisdictionHelper.createSampleJurisdictionDto(sample));
	}

	public boolean isInJurisdictionOrOwned(SampleJurisdictionDto sampleJurisdiction) {

		User user = userService.getCurrentUser();

		return SampleJurisdictionHelper
			.isInJurisdictionOrOwned(user.getJurisdictionLevel(), JurisdictionHelper.createUserJurisdiction(user), sampleJurisdiction);
	}

}
