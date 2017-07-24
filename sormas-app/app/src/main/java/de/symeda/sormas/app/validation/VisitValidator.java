package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.databinding.VisitDataFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */

public class VisitValidator {

    public static void clearErrorsForVisitData(VisitDataFragmentLayoutBinding binding) {
        binding.visitVisitDateTime.setError(null);
        binding.visitVisitStatus.setError(null);
    }

    public static boolean validateVisitData(Visit visit, Contact contact, VisitDataFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Visit date & time
        if (visit.getVisitDateTime() == null) {
            binding.visitVisitDateTime.setError(resources.getString(R.string.validation_visit_date_time));
            success = false;
        } else {
            if (visit.getVisitDateTime().before(contact.getLastContactDate()) &&
                    DateHelper.getDaysBetween(visit.getVisitDateTime(), contact.getLastContactDate()) > 10) {
                binding.visitVisitDateTime.setError(resources.getString(R.string.validation_visit_10_days_before));
                success = false;
            } else {
                if (contact.getFollowUpUntil() != null && visit.getVisitDateTime().after(contact.getFollowUpUntil()) &&
                        DateHelper.getDaysBetween(contact.getFollowUpUntil(), visit.getVisitDateTime()) > 10) {
                    binding.visitVisitDateTime.setError(resources.getString(R.string.validation_visit_10_days_after));
                    success = false;
                }
            }
        }

        // Visit status
        if (visit.getVisitStatus() == null) {
            binding.visitVisitStatus.setError(resources.getString(R.string.validation_visit_status));
            success = false;
        }

        return success;
    }

}
