package de.symeda.sormas.app.contact.edit;

import java.util.Date;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentContactNewLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentVisitEditLayoutBinding;
import de.symeda.sormas.app.util.Callback;

public final class ContactValidator {

    public static void initializeValidation(final Contact contact, final FragmentContactEditLayoutBinding contentBinding) {
        if (contact != null) {
            Callback lastContactDateCallback = new Callback() {
                public void call() {
                    Date lastContactDate = contentBinding.contactLastContactDate.getValue();
                    Date contactReferenceDate = contact.getReportDateTime();
                    if (lastContactDate.after(contactReferenceDate)) {
                        contentBinding.contactLastContactDate.enableErrorState(I18nProperties.getValidationError("beforeDate", contentBinding.contactLastContactDate.getCaption(),
                                contentBinding.contactReportDateTime.getCaption()));
                    } else {
                        contentBinding.contactLastContactDate.disableErrorState();
                    }
                }
            };
            contentBinding.contactLastContactDate.setValidationCallback(lastContactDateCallback);
        }
    }

    public static void initializeValidation(final Contact contact, final FragmentContactNewLayoutBinding contentBinding) {
        if (contact != null) {
            Callback lastContactDateCallback = new Callback() {
                public void call() {
                    Date lastContactDate = contentBinding.contactLastContactDate.getValue();
                    Date contactReferenceDate = contact.getReportDateTime();
                    if (lastContactDate.after(contactReferenceDate)) {
                        contentBinding.contactLastContactDate.enableErrorState(I18nProperties.getValidationError("beforeDate", contentBinding.contactLastContactDate.getCaption(), I18nProperties.getPrefixFieldCaption(ContactDto.I18N_PREFIX, ContactDto.REPORT_DATE_TIME)));
                    } else {
                        contentBinding.contactLastContactDate.disableErrorState();
                    }
                }
            };
            contentBinding.contactLastContactDate.setValidationCallback(lastContactDateCallback);
        }
    }

}
