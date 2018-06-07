package de.symeda.sormas.app.validation;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */

public final class VisitValidator {

//    public static boolean validateVisitData(Visit visit, Contact contact, VisitDataFragmentLayoutBinding binding) {
//        Resources resources = DatabaseHelper.getContext().getResources();
//
//        boolean success = true;
//
//        // Visit date & time
//        if (visit.getVisitDateTime() == null) {
//            binding.visitVisitDateTime.setError(resources.getString(R.string.validation_visit_date_time));
//            success = false;
//        } else {
//            Date contactReferenceDate = contact.getLastContactDate() != null ? contact.getLastContactDate() : contact.getReportDateTime();
//            if (visit.getVisitDateTime().before(contactReferenceDate) &&
//                    DateHelper.getDaysBetween(visit.getVisitDateTime(), contactReferenceDate) > VisitDto.ALLOWED_CONTACT_DATE_OFFSET) {
//                binding.visitVisitDateTime.setError(resources.getString(R.string.validation_visit_30_days_before));
//                success = false;
//            } else if (contact.getFollowUpUntil() != null && visit.getVisitDateTime().after(contactReferenceDate) &&
//                    DateHelper.getDaysBetween(contactReferenceDate, visit.getVisitDateTime()) > VisitDto.ALLOWED_CONTACT_DATE_OFFSET) {
//                binding.visitVisitDateTime.setError(resources.getString(R.string.validation_visit_30_days_after));
//                success = false;
//            }
//        }
//
//        // Visit status
//        if (visit.getVisitStatus() == null) {
//            binding.visitVisitStatus.setError(resources.getString(R.string.validation_visit_status));
//            success = false;
//        }
//
//        return success;
//    }
//
//    public static void clearErrorsForVisitData(VisitDataFragmentLayoutBinding binding) {
//        for (PropertyField field : getVisitDataFields(binding)) {
//            field.clearError();
//        }
//    }
//
//    public static void setRequiredHintsForVisitData(VisitDataFragmentLayoutBinding binding) {
//        for (PropertyField field : getVisitDataFields(binding)) {
//            field.setRequiredHint(true);
//        }
//    }
//
//    private static List<PropertyField<?>> getVisitDataFields(VisitDataFragmentLayoutBinding binding) {
//        return Arrays.asList(binding.visitVisitDateTime, binding.visitVisitStatus);
//    }

}
