package org.technologybord.tryoutdrawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by Orson on 06/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class LineDrawable extends Drawable {
    private Paint mPaint;

    public LineDrawable() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(3);
    }

    @Override
    public void draw(Canvas canvas) {
        int lvl = getLevel();
        Rect b = getBounds();
        float x = b.width() * lvl / 10000.0f;
        float y = (b.height() - mPaint.getStrokeWidth()) / 2;
        mPaint.setColor(0xffff0000);
        canvas.drawLine(0, y, x, y, mPaint);
        mPaint.setColor(0xff00ff00);
        canvas.drawLine(x, y, b.width(), y, mPaint);
    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    //Not part of class
    public void test() {
        /*View v = new View(this);
        final LineDrawable d = new LineDrawable();
        d.setLevel(4000);
        v.setBackgroundDrawable(d);
        setContentView(v);
        OnTouchListener l = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int lvl = (int) (10000 * event.getX() / v.getWidth());
                d.setLevel(lvl);
                return true;
            }
        };
        v.setOnTouchListener(l);*/



        /*Drawable drawable = editText.getBackground();
        drawable.setColorFilter(editTextColor, PorterDuff.Mode.SRC_ATOP);
        if(Build.VERSION.SDK_INT > 16) {
            editText.setBackground(drawable);
        }else{
            editText.setBackgroundDrawable(drawable);
        }*/
    }
}
