package org.technologybord.tryoutdrawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Orson on 05/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class DrawableView extends View {

    private Drawable mDrawable;

    public DrawableView(Context context) {
        super(context);
    }

    public void setDrawable(Drawable d) {
        if (mDrawable != d) {

            if (mDrawable != null) {
                mDrawable.setCallback(null);
            }

            mDrawable = d;
            if (d != null) {
                d.setCallback(this);
                updateDrawableBounds();
            }
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || who == mDrawable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDrawable != null) {
            mDrawable.draw(canvas);
        }
    }

    private void updateDrawableBounds() {

    }
}
