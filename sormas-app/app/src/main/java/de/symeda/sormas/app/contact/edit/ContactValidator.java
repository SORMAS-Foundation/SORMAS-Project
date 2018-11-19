/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
