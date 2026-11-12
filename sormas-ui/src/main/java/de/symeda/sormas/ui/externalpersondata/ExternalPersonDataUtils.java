package de.symeda.sormas.ui.externalpersondata;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataProviderFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;

public class ExternalPersonDataUtils {

	private ExternalPersonDataUtils() {
	}

	public static boolean isExternalPersonProviderEnabled() {
		return UiUtil.permitted(UserRight.EXTERNAL_PERSON_DATA_PROVIDER_ACCESS)
			&& Boolean.parseBoolean(
				FacadeProvider.getSystemConfigurationValueFacade()
					.getValue(ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_PROVIDER_ENABLED_KEY));
	}
}
