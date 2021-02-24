package de.symeda.sormas.ui.dashboard.contacts.epicurve;

import de.symeda.sormas.ui.dashboard.contacts.ContactsEpiCurveMode;
import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;

public class EpiCurveBuilders {

	public static ContactsEpiCurveBuilder getEpiCurveBuilder(ContactsEpiCurveMode epiCurveContactsMode, EpiCurveGrouping epiCurveGrouping) {
		if (epiCurveContactsMode == ContactsEpiCurveMode.CONTACT_CLASSIFICATION) {
			return new ContactClassificationCurveBuilder(epiCurveGrouping);
		} else if (epiCurveContactsMode == ContactsEpiCurveMode.FOLLOW_UP_STATUS) {
			return new FollowUpStatusCurveBuilder(epiCurveGrouping);
		} else if (epiCurveContactsMode == ContactsEpiCurveMode.FOLLOW_UP_UNTIL) {
			return new FollowUpUntilCurveBuilder(epiCurveGrouping);
		}
		return null;
	}
}
