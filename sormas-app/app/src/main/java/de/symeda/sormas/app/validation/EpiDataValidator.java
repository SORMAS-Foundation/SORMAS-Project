package de.symeda.sormas.app.validation;

/**
 * Created by Mate Strysewske on 24.11.2017.
 */

public final class EpiDataValidator {
//
//    public static boolean validateBurialData(EpidataBurialEditFragmentLayoutBinding binding) {
//        Resources resources = DatabaseHelper.getContext().getResources();
//
//        boolean success = true;
//
//        if (DateTimeComparator.getDateOnlyInstance().compare(binding.burialFrom.getValue(), binding.burialTo.getValue()) > 0) {
//            binding.burialFrom.setError(String.format(resources.getString(R.string.validation_date_later), binding.burialFrom.getCaption(), binding.burialTo.getCaption()));
//            success = false;
//        }
//
//        if (DateTimeComparator.getDateOnlyInstance().compare(binding.burialTo.getValue(), binding.burialFrom.getValue()) < 0) {
//            binding.burialTo.setError(String.format(resources.getString(R.string.validation_date_earlier), binding.burialTo.getCaption(), binding.burialFrom.getCaption()));
//            success = false;
//        }
//
//        return success;
//    }
//
//    public static boolean validateTravelData(EpidataTravelEditFragmentLayoutBinding binding) {
//        Resources resources = DatabaseHelper.getContext().getResources();
//
//        boolean success = true;
//
//        if (DateTimeComparator.getDateOnlyInstance().compare(binding.travelFrom.getValue(), binding.travelTo.getValue()) > 0) {
//            binding.travelFrom.setError(String.format(resources.getString(R.string.validation_date_later), binding.travelFrom.getCaption(), binding.travelTo.getCaption()));
//            success = false;
//        }
//
//        if (DateTimeComparator.getDateOnlyInstance().compare(binding.travelTo.getValue(), binding.travelFrom.getValue()) < 0) {
//            binding.travelTo.setError(String.format(resources.getString(R.string.validation_date_earlier), binding.travelTo.getCaption(), binding.travelFrom.getCaption()));
//            success = false;
//        }
//
//        return success;
//    }
//
//    public static void setSoftRequiredHintsForBurial(EpidataBurialEditFragmentLayoutBinding binding) {
//        for (DateField field : getBurialFields(binding)) {
//            field.makeFieldSoftRequired();
//        }
//    }
//
//    public static void setSoftRequiredHintsForGathering(EpidataGatheringEditFragmentLayoutBinding binding) {
//        for (DateField field : getGatheringFields(binding)) {
//            field.makeFieldSoftRequired();
//        }
//    }
//
//    public static void setSoftRequiredHintsForTravel(EpidataTravelEditFragmentLayoutBinding binding) {
//        for (DateField field : getTravelFields(binding)) {
//            field.makeFieldSoftRequired();
//        }
//    }
//
//    private static List<DateField> getBurialFields(EpidataBurialEditFragmentLayoutBinding binding) {
//        return Arrays.asList(binding.burialFrom, binding.burialTo);
//    }
//
//    private static List<DateField> getGatheringFields(EpidataGatheringEditFragmentLayoutBinding binding) {
//        return Arrays.asList(binding.gatherDate);
//    }
//
//    private static List<DateField> getTravelFields(EpidataTravelEditFragmentLayoutBinding binding) {
//        return Arrays.asList(binding.travelFrom, binding.travelTo);
//    }

}
