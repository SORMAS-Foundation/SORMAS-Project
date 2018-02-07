package de.symeda.sormas.app.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.LoginActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.UpdateAppActivity;
import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Created by Mate Strysewske on 05.02.2018.
 */

public class UpdateAppDialog {

    private Tracker tracker;

    private final Activity callingActivity;
    private final AlertDialog dialog;

    public UpdateAppDialog(final Activity callingActivity, final String appUrl) {
        tracker = ((SormasApplication) callingActivity.getApplication()).getDefaultTracker();
        this.callingActivity = callingActivity;
        final Resources resources = callingActivity.getResources();
        dialog = new AlertDialog.Builder(callingActivity)
                .setTitle(resources.getString(R.string.headline_update_app))
                .setMessage(appUrl != null ? resources.getString(R.string.message_app_update_required)
                        : resources.getString(R.string.message_app_update_not_possible))
                .setCancelable(false)
                .setPositiveButton(appUrl != null ? resources.getString(R.string.action_download)
                        : resources.getString(R.string.action_share_problem), null)
                .setNegativeButton(resources.getString(R.string.action_close_app), null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (appUrl != null) {
                            Intent updateIntent = new Intent(callingActivity, UpdateAppActivity.class);
                            updateIntent.putExtra(UpdateAppActivity.APP_URL, appUrl);
                            callingActivity.startActivity(updateIntent);
                        } else {
                            // App URL retrieval failed - send error report to Google Analytics - TODO send mail to supervisor/open chat program
                            tracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("App Update Error")
                                    .setAction("URL Retrieval")
                                    .setLabel("User: " + ConfigProvider.getUser().getUuid() + " could not retrieve the URL to update the app.")
                                    .build());

                            dialog.dismiss();
                            buildAndShowReportSentDialog();
                          }
                    }
                });

                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        closeApp();
                    }
                });
            }
        });

    }

    public void show() {
        dialog.show();
    }

    private void buildAndShowReportSentDialog() {
        final Resources resources = callingActivity.getResources();
        final AlertDialog reportSentDialog = new AlertDialog.Builder(callingActivity)
                .setTitle(resources.getString(R.string.headline_report_sent))
                .setMessage(resources.getString(R.string.message_report_sent))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.action_retry), null)
                .setNegativeButton(resources.getString(R.string.action_close_app), null)
                .create();

        reportSentDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportSentDialog.dismiss();
                        Intent intent = new Intent(callingActivity, LoginActivity.class);
                        callingActivity.finish();
                        callingActivity.startActivity(intent);
                    }
                });

                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportSentDialog.dismiss();
                        closeApp();
                    }
                });
            }
        });

        reportSentDialog.show();
    }

    private void closeApp() {
        Activity finishActivity = callingActivity;
        do {
            finishActivity.finish();
            finishActivity = finishActivity.getParent();
        } while (finishActivity != null);
    }



}
