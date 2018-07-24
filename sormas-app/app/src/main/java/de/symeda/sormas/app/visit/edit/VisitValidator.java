package de.symeda.sormas.app.visit.edit;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.FragmentVisitEditLayoutBinding;
import de.symeda.sormas.app.util.Callback;

public final class VisitValidator {

    public static void initializeVisitValidation(final Contact contact, final FragmentVisitEditLayoutBinding contentBinding) {
        if (contact != null) {
            Callback visitDateTimeCallback = new Callback() {
                public void call() {
                    Date visitDateTime = (Date) contentBinding.visitVisitDateTime.getValue();
                    Date contactReferenceDate = contact.getLastContactDate() != null ? contact.getLastContactDate() : contact.getReportDateTime();
                    if (visitDateTime.before(contactReferenceDate) && DateHelper.getDaysBetween(visitDateTime, contactReferenceDate) > VisitDto.ALLOWED_CONTACT_DATE_OFFSET) {
                        contentBinding.visitVisitDateTime.enableErrorState(
                                contact.getLastContactDate() != null ? R.string.validation_visit_date_time_before_contact_date
                                        : R.string.validation_visit_date_time_before_report_date);
                    } else if (contact.getFollowUpUntil() != null && visitDateTime.after(contact.getFollowUpUntil()) && DateHelper.getDaysBetween(contact.getFollowUpUntil(), visitDateTime) > VisitDto.ALLOWED_CONTACT_DATE_OFFSET) {
                        contentBinding.visitVisitDateTime.enableErrorState(
                                R.string.validation_visit_date_time_after_followup);
                    } else {
                        contentBinding.visitVisitDateTime.disableErrorState();
                    }
                }
            };

            contentBinding.visitVisitDateTime.setValidationCallback(visitDateTimeCallback);
        }
    }

}
