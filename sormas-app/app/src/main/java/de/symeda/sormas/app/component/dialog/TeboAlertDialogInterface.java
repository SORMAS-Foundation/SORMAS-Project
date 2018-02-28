package de.symeda.sormas.app.component.dialog;

import android.view.View;

/**
 * Created by Orson on 04/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface TeboAlertDialogInterface {


    interface NegativeOnClickListener {

        void onDismissClick(View v, Object item, View viewRoot);
    }

    interface PositiveOnClickListener {

        void onOkClick(View v, Object item, View viewRoot);
    }

    interface DeleteOnClickListener {

        void onDeleteClick(View v, Object item, View viewRoot);
    }
}
