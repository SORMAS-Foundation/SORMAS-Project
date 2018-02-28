package de.symeda.sormas.app.util;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Orson on 28/12/2017.
 */

public class NavigationHelper {

    public static void navigateUpFrom(AppCompatActivity activity) {
        Intent upIntent = NavUtils.getParentActivityIntent(activity);
        if (NavUtils.shouldUpRecreateTask(activity, upIntent)) {
            TaskStackBuilder.create(activity)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
        } else {
            NavUtils.navigateUpTo(activity, upIntent);
        }
    }
}
