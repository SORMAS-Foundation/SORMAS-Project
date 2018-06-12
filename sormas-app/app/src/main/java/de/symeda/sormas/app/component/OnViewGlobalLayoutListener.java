package de.symeda.sormas.app.component;

import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Orson on 25/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class OnViewGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private final static int maxHeight = 96;
    private View view;

    public OnViewGlobalLayoutListener(View view) {
        this.view = view;
    }

    @Override
    public void onGlobalLayout() {
        if (view.getHeight() > maxHeight)
            view.getLayoutParams().height = maxHeight;
    }
}
