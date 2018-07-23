package de.symeda.sormas.app.validation;

import android.content.Context;
import android.content.res.Resources;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.DialogCaseEpidBurialEditLayoutBinding;
import de.symeda.sormas.app.databinding.DialogCaseEpidTravelEditLayoutBinding;

public final class CaseValidator {

    public static void initializeEpiDataBurialValidation(Context context, final DialogCaseEpidBurialEditLayoutBinding contentBinding) {
        final Resources resources = context.getResources();

        Callback.IAction<NotificationContext> burialDateFromCallback = new Callback.IAction<NotificationContext>() {
            public void call(NotificationContext notificationContext) {
                if (contentBinding.epiDataBurialBurialDateTo.getValue() != null) {
                    if (contentBinding.epiDataBurialBurialDateFrom.getValue().after(contentBinding.epiDataBurialBurialDateTo.getValue())) {
                        contentBinding.epiDataBurialBurialDateFrom.enableErrorState(notificationContext,
                                String.format(resources.getString(R.string.validation_date_before),
                                        contentBinding.epiDataBurialBurialDateFrom.getCaption(),
                                        contentBinding.epiDataBurialBurialDateTo.getCaption()));
                    } else {
                        contentBinding.epiDataBurialBurialDateFrom.disableErrorState();
                    }
                } else {
                    contentBinding.epiDataBurialBurialDateFrom.disableErrorState();
                }
            }
        };

        Callback.IAction<NotificationContext> burialDateToCallback = new Callback.IAction<NotificationContext>() {
            public void call(NotificationContext notificationContext) {
                if (contentBinding.epiDataBurialBurialDateFrom.getValue() != null) {
                    if (contentBinding.epiDataBurialBurialDateTo.getValue().before(contentBinding.epiDataBurialBurialDateFrom.getValue())) {
                        contentBinding.epiDataBurialBurialDateTo.enableErrorState(notificationContext,
                                String.format(resources.getString(R.string.validation_date_after),
                                        contentBinding.epiDataBurialBurialDateTo.getCaption(),
                                        contentBinding.epiDataBurialBurialDateFrom.getCaption()));
                    } else {
                        contentBinding.epiDataBurialBurialDateTo.disableErrorState();
                    }
                } else {
                    contentBinding.epiDataBurialBurialDateTo.disableErrorState();
                }
            }
        };

        contentBinding.epiDataBurialBurialDateFrom.setValidationCallback(burialDateFromCallback);
        contentBinding.epiDataBurialBurialDateTo.setValidationCallback(burialDateToCallback);
    }

    public static void initializeEpiDataTravelValidation(Context context, final DialogCaseEpidTravelEditLayoutBinding contentBinding) {
        final Resources resources = context.getResources();

        Callback.IAction<NotificationContext> travelDateFromCallback = new Callback.IAction<NotificationContext>() {
            @Override
            public void call(NotificationContext notificationContext) {
                if (contentBinding.epiDataTravelTravelDateTo.getValue() != null) {
                    if (contentBinding.epiDataTravelTravelDateFrom.getValue().after(contentBinding.epiDataTravelTravelDateTo.getValue())) {
                        contentBinding.epiDataTravelTravelDateFrom.enableErrorState(notificationContext,
                                String.format(resources.getString(R.string.validation_date_before),
                                        contentBinding.epiDataTravelTravelDateFrom.getCaption(),
                                        contentBinding.epiDataTravelTravelDateTo.getCaption()));
                    } else {
                        contentBinding.epiDataTravelTravelDateFrom.disableErrorState();
                    }
                } else {
                    contentBinding.epiDataTravelTravelDateFrom.disableErrorState();
                }
            }
        };

        Callback.IAction<NotificationContext> burialDateToCallback = new Callback.IAction<NotificationContext>() {
            @Override
            public void call(NotificationContext notificationContext) {
                if (contentBinding.epiDataTravelTravelDateFrom.getValue() != null) {
                    if (contentBinding.epiDataTravelTravelDateTo.getValue().before(contentBinding.epiDataTravelTravelDateFrom.getValue())) {
                        contentBinding.epiDataTravelTravelDateTo.enableErrorState(notificationContext,
                                String.format(resources.getString(R.string.validation_date_after),
                                        contentBinding.epiDataTravelTravelDateTo.getCaption(),
                                        contentBinding.epiDataTravelTravelDateFrom.getCaption()));
                    } else {
                        contentBinding.epiDataTravelTravelDateTo.disableErrorState();
                    }
                } else {
                    contentBinding.epiDataTravelTravelDateTo.disableErrorState();
                }
            }
        };

        contentBinding.epiDataTravelTravelDateFrom.setValidationCallback(travelDateFromCallback);
        contentBinding.epiDataTravelTravelDateTo.setValidationCallback(burialDateToCallback);
    }

}
