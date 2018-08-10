package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;

public class ControlTagViewField extends ControlPropertyField<String> {

    private static final int LAYOUT_WIDTH_OFFSET = 2;
    private static final int LAYOUT_COLOR = R.color.tagViewLayoutColor;
    private static final int LAYOUT_COLOR_PRESSED = R.color.tagViewLayoutColorPress;
    private static final int LAYOUT_BORDER_SIZE = R.dimen.tagViewLayoutBorderSize;
    private static final int LAYOUT_BORDER_COLOR = R.color.tagViewLayoutBorderColor;
    private static final int RADIUS = R.dimen.tagViewRadius;
    private static final int TAG_MARGIN_BOTTOM = R.dimen.tagViewLineMargin;
    private static final int TAG_MARGIN = R.dimen.tagViewTagMargin;

    private List<String> tags = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private int width;
    private boolean initialized = false;
    private int tagLayoutId;
    private RelativeLayout tagsFrame;
    private TextView emptyTagViewHint;

    // Constructors

    public ControlTagViewField(Context context) {
        super(context);
    }

    public ControlTagViewField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlTagViewField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Instance methods

    public void setTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            tagsFrame.setVisibility(GONE);
            emptyTagViewHint.setVisibility(VISIBLE);
            removeAllTags();
        } else {
            tagsFrame.setVisibility(VISIBLE);
            emptyTagViewHint.setVisibility(GONE);
            this.tags = tags;
            drawTags();
        }
    }

    private void drawTags() {
        if (!initialized || tagsFrame == null) {
            return;
        }

        // Clear existing tags from view
        tagsFrame.removeAllViews();

        float framePadding = tagsFrame.getPaddingLeft() + tagsFrame.getPaddingRight();
        int listIndex = 1; // List Index
        int indexBottom = 1; // The Tag to add below
        int indexHeader = 1; // The header tag of this line
        String previousTag = null;
        for (String tag : tags) {
            // Inflate tag layout
            ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, tagLayoutId,
                    this, false);
            View tagLayout = binding.getRoot();
            tagLayout.setId(listIndex);
            tagLayout.setBackground(getBackgroundDrawable());

            // Set text
            TextView tagView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_contain);
            tagView.setText(tag);

            // Set tag width
            float tagWidth = tagView.getPaint().measureText(tag) + tagView.getPaddingLeft() + tagView.getPaddingRight();

            RelativeLayout.LayoutParams tagParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tagParams.bottomMargin = (int) getResources().getDimension(TAG_MARGIN_BOTTOM);

            if (width <= framePadding + tagWidth + dipToPx(tagsFrame.getContext(), LAYOUT_WIDTH_OFFSET)) {
                // Tag has to be added to a new line
                if (previousTag != null) {
                    tagParams.addRule(RelativeLayout.BELOW, indexBottom);
                }
                framePadding = getPaddingLeft() + getPaddingRight();
                indexBottom = listIndex;
                indexHeader = listIndex;
            } else {
                tagParams.addRule(RelativeLayout.ALIGN_TOP, indexHeader);
                if (listIndex != indexHeader) {
                    tagParams.addRule(RelativeLayout.RIGHT_OF, listIndex - 1);
                    tagParams.leftMargin = (int) getResources().getDimension(TAG_MARGIN);
                    framePadding += getResources().getDimension(TAG_MARGIN);
                }
            }
            framePadding += tagWidth;
            tagsFrame.addView(tagLayout, tagParams);
            previousTag = tag;
            listIndex++;
        }
    }

    private Drawable getBackgroundDrawable() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        GradientDrawable drawableNormal = new GradientDrawable();
        drawableNormal.setColor(getResources().getColor(LAYOUT_COLOR));
        drawableNormal.setCornerRadius(getResources().getDimension(RADIUS));
        if (getResources().getDimension(LAYOUT_BORDER_SIZE) > 0) {
            drawableNormal.setStroke(dipToPx(getContext(), getResources().getDimension(LAYOUT_BORDER_SIZE)), getResources().getColor(LAYOUT_BORDER_COLOR));
        }
        GradientDrawable drawablePressed = new GradientDrawable();
        drawablePressed.setColor(getResources().getColor(LAYOUT_COLOR_PRESSED));
        drawablePressed.setCornerRadius(getResources().getDimension(RADIUS));
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, drawablePressed);
        stateListDrawable.addState(new int[]{}, drawableNormal);
        return stateListDrawable;
    }

    private void removeAllTags() {
        tags.clear();

        if (tagsFrame != null) {
            tagsFrame.removeAllViews();
        }
    }

    private int dipToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    // Overrides

    @Override
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!initialized) {
                    initialized = true;
                    drawTags();
                }
            }
        });
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (layoutInflater != null) {
            layoutInflater.inflate(R.layout.control_tagview_layout, this);
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tagsFrame = (RelativeLayout) this.findViewById(R.id.tags_frame);
        emptyTagViewHint = (TextView) this.findViewById(R.id.empty_tag_view_hint);
        tagLayoutId = R.layout.tagview_item;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        width = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        if (width > 0) {
            this.width = width;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTags();
    }

    @Override
    protected void setFieldValue(String value) {
        // Not needed
    }

    @Override
    protected String getFieldValue() {
        return null;
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        // Not needed
    }

}
