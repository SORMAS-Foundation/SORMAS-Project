package de.symeda.sormas.app.component;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Created by Mate Strysewske on 27.04.2017.
 */

public class UserReportDialog extends AlertDialog.Builder {

    public UserReportDialog(final FragmentActivity activity, final String viewName, final String uuid) {
        super(activity);
        this.setTitle(activity.getResources().getString(R.string.headline_user_report));

        final View dialogView = activity.getLayoutInflater().inflate(R.layout.user_report_fragment_layout, null);
        this.setView(dialogView);

        this.setPositiveButton(activity.getResources().getString(R.string.action_send), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String description = ((EditText) dialogView.findViewById(R.id.user_report_description)).getText().toString();
                Tracker tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("User Report")
                        .setAction("Error Report")
                        .setLabel("Location: " + viewName + (uuid!=null?" - UUID: " + uuid:"") + (ConfigProvider.getUser()!=null?" - User: " + ConfigProvider.getUser().getUuid():"") + " - Description: " + description)
                        .build());
                Snackbar.make(activity.findViewById(R.id.base_layout), activity.getString(R.string.snackbar_report_sent), Snackbar.LENGTH_LONG).show();

            }
        });

        this.setNegativeButton(activity.getResources().getString(R.string.action_dimiss), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
    }

}
