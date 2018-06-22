package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.symeda.sormas.app.R;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;

/**
 * Created by Orson on 22/12/2017.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

@BindingMethods({
        @BindingMethod(type = TeboTextImageRead.class, attribute = "valueFormat", method = "setValueFormat"),
        @BindingMethod(type = TeboTextImageRead.class, attribute = "image", method = "setImageDrawable"),
        @BindingMethod(type = TeboTextImageRead.class, attribute = "imageColor", method = "setImageColor")
})
public class TeboTextImageRead extends TeboTextRead {


    public static final int DEFAULT_IMG_WIDTH = 24;
    public static final int DEFAULT_IMG_HEIGHT = 24;

    protected ImageView imgTextImage;
    private int image;
    private Drawable imageDrawable;
    private int imageColor;
    private int imageWidth;
    private int imageHeight;


    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboTextImageRead(Context context) {
        super(context);
    }

    public TeboTextImageRead(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboTextImageRead(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Drawable getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(Drawable image) {
        if (this.imageDrawable == image) {
            return;
        }

        this.imageDrawable = image.mutate();
        if (this.imageDrawable != null) {
            this.imageDrawable.setTint(getImageColor());
        }

        imgTextImage.setBackground(this.imageDrawable);
        //requestLayout();
        //invalidate();
    }

    public int getImageColor() {
        return imageColor;
    }

    public void setImageColor(int imageColor) {
        if (this.imageColor == imageColor) {
            return;
        }

        this.imageColor = imageColor;
        if (getImageDrawable() != null)
            this.imageDrawable.setTint(imageColor);
    }

    public void setIcon(Drawable iconResource) {
        /*Drawable drw;
        Context context = imgTextImage.getContext();
        Resources resources = context.getResources();
        drw = (Drawable) ContextCompat.getDrawable(context, iconResource);*/
        imgTextImage.setBackground(iconResource);
        invalidate();
        requestLayout();
    }

    public Drawable getIcon() {
        return imgTextImage.getBackground();
    }

    public void setIconBackground(int iconResource, int tintResource) {
        if (imgTextImage != null){
            Drawable drw;
            Context context = imgTextImage.getContext();
            Resources resources = context.getResources();
            drw = (Drawable) ContextCompat.getDrawable(context, iconResource);

            if (drw != null)
                drw.setTint(resources.getColor(tintResource));

            imgTextImage.setBackground(drw);

            ViewGroup.LayoutParams params = imgTextImage.getLayoutParams();
            params.width = getImageWidth();
            params.height = getImageHeight();
        }
    }

    public void setIconBackground(int tintResource) {
        if (imgTextImage != null){
            Drawable drw;
            Context context = imgTextImage.getContext();
            Resources resources = context.getResources();
            drw = (Drawable) ContextCompat.getDrawable(context, getImage());

            if (drw != null)
                drw.setTint(resources.getColor(tintResource));

            imgTextImage.setBackground(drw);

            ViewGroup.LayoutParams params = imgTextImage.getLayoutParams();
            params.width = getImageWidth();
            params.height = getImageHeight();
        }
    }

    // </editor-fold>


    @Override
    protected void initializeView(Context context, AttributeSet attrs, int defStyle) {
        super.initializeView(context, attrs, defStyle);
        if (attrs != null) {
            int valueFormatResource;

            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboTextImageRead,
                    0, 0);

            try {
                image = a.getResourceId(R.styleable.TeboTextImageRead_image, R.drawable.blank);
                imageWidth = a.getDimensionPixelSize(R.styleable.TeboTextImageRead_imageWidth, DEFAULT_IMG_WIDTH);
                imageHeight = a.getDimensionPixelSize(R.styleable.TeboTextImageRead_imageHeight, DEFAULT_IMG_HEIGHT);

                /*if (valueFormatResource != -1)
                    valueFormat = getResources().getString(valueFormatResource);*/
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_textfield_image_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        imgTextImage = (ImageView) this.findViewById(R.id.image);

        //imgTextImage.setImeOptions(getImeOptions());
    }






    @BindingAdapter(value={"setValueAndIndicator", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setTaskPriority(TeboTextImageRead control, TaskPriority priority, String valueFormat, String defaultValue) {
        String val = defaultValue;
        Context context = control.getContext();
        Resources resources = context.getResources();

        if (priority == null) {
            control.setValue(val);
            control.updateControl(val);
        } else {

            val = priority.toString();
            control.setValue(val);

            if (priority == TaskPriority.HIGH) {
                control.setIconBackground(R.color.indicatorTaskPriorityHigh);
            } else if (priority == TaskPriority.NORMAL) {
                control.setIconBackground(R.color.indicatorTaskPriorityNormal);
            } else if (priority == TaskPriority.LOW) {
                control.setIconBackground(R.color.indicatorTaskPriorityLow);
            }

            control.updateControl(val);
        }
    }

    @BindingAdapter(value={"setValueAndIndicator", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setTaskStatus(TeboTextImageRead control, TaskStatus status, String valueFormat, String defaultValue) {
        String val = defaultValue;
        Context context = control.getContext();
        Resources resources = context.getResources();

        if (status == null) {
            control.setValue(val);
            control.updateControl(val);
        } else {

            val = status.toString();
            control.setValue(val);

            if (status == TaskStatus.PENDING) {
                control.setIconBackground(R.color.indicatorTaskPending);
            } else if (status == TaskStatus.DONE) {
                control.setIconBackground(R.color.indicatorTaskDone);
            } else if (status == TaskStatus.REMOVED) {
                control.setIconBackground(R.color.indicatorTaskRemoved);
            } else if (status == TaskStatus.NOT_EXECUTABLE) {
                control.setIconBackground(R.color.indicatorTaskNotExecutable);
            }

            control.updateControl(val);
        }
    }



}