package de.symeda.sormas.app.component.tagview;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.TeboPropertyField;

/**
 * Created by Orson on 03/01/2018.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

@BindingMethods({
        @BindingMethod(type = TagView.class, attribute = "textSize", method = "setTagTextSize"),
        @BindingMethod(type = TagView.class, attribute = "textColor", method = "setTagTextColor")
})
public class TagView extends TeboPropertyField<String> {

    public static final String TAG = TagView.class.getSimpleName();

    /**
     * tag list
     */
    private List<Tag> mTags = new ArrayList<>();

    /**
     * System Service
     */
    private LayoutInflater mInflater;
    private ViewTreeObserver mViewTreeObserber;

    /**
     * listeners
     */

    private OnTagClickListener mClickListener;
    private OnTagDeleteListener mDeleteListener;
    private OnTagLongClickListener mTagLongClickListener;
    private OnTagInitListener mTagInitListener;

    /**
     * view size param
     */
    private int mWidth;

    /**
     * layout initializeDialog flag
     */
    private boolean mInitialized = false;

    /**
     * custom layout param
     */
    private int lineMargin;
    private int tagMargin;
    private int textPaddingLeft;
    private int textPaddingRight;
    private int textPaddingTop;
    private int textPaddingBottom;


    private float textSize;
    private float layoutBorderSize;
    private int layoutColor;
    private int layoutColorPress;
    private int textColor;
    private int deleteIndicatorColor;
    private int layoutBorderColor;
    private boolean isDeletable;

    private int rowLayout = 0;

    private float radius;
    private float deleteIndicatorSize;
    private String deleteIcon;
    private Drawable background;
    private RelativeLayout tagsFrame;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // </editor-fold>

    @Override
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        mViewTreeObserber = getViewTreeObserver();
        mViewTreeObserber.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mInitialized) {
                    mInitialized = true;
                    drawTags();
                }
            }
        });

        // get AttributeSet
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.TagView, defStyle, defStyle);


        try {

            /*this.description = typeArray.getString(R.styleable.TagView_description);
            this.caption = typeArray.getString(R.styleable.TagView_labelCaption);
            captionColor = typeArray.getColor(R.styleable.TagView_labelColor, getResources().getColor(R.color.controlReadLabelColor));
            textAlignment = typeArray.getInt(R.styleable.TagView_textAlignment, View.TEXT_ALIGNMENT_VIEW_START);
            gravity = typeArray.getInt(R.styleable.TagView_gravity, Gravity.LEFT | Gravity.CENTER_VERTICAL);*/
            this.lineMargin = (int) typeArray.getDimension(R.styleable.TagView_lineMargin, Utils.dipToPx(this.getContext(), Constants.DEFAULT_LINE_MARGIN));
            this.tagMargin = (int) typeArray.getDimension(R.styleable.TagView_tagMargin, Utils.dipToPx(this.getContext(), Constants.DEFAULT_TAG_MARGIN));
            this.textPaddingLeft = (int) typeArray.getDimension(R.styleable.TagView_textPaddingLeft, Utils.dipToPx(this.getContext(), Constants.DEFAULT_TAG_TEXT_PADDING_LEFT));
            this.textPaddingRight = (int) typeArray.getDimension(R.styleable.TagView_textPaddingRight, Utils.dipToPx(this.getContext(), Constants.DEFAULT_TAG_TEXT_PADDING_RIGHT));
            this.textPaddingTop = (int) typeArray.getDimension(R.styleable.TagView_textPaddingTop, Utils.dipToPx(this.getContext(), Constants.DEFAULT_TAG_TEXT_PADDING_TOP));
            this.textPaddingBottom = (int) typeArray.getDimension(R.styleable.TagView_textPaddingBottom, Utils.dipToPx(this.getContext(), Constants.DEFAULT_TAG_TEXT_PADDING_BOTTOM));


            this.textSize = typeArray.getDimension(R.styleable.TagView_textSize, Constants.DEFAULT_TAG_TEXT_SIZE);
            this.layoutBorderSize = typeArray.getDimension(R.styleable.TagView_layoutBorderSize, Utils.dipToPx(this.getContext(), Constants.DEFAULT_TAG_LAYOUT_BORDER_SIZE));
            this.layoutColor = typeArray.getColor(R.styleable.TagView_layoutColor, Constants.DEFAULT_TAG_LAYOUT_COLOR);
            this.layoutColorPress = typeArray.getColor(R.styleable.TagView_layoutColorPress, Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS);
            this.textColor = typeArray.getColor(R.styleable.TagView_textColor, Constants.DEFAULT_TAG_TEXT_COLOR);
            this.deleteIndicatorColor = typeArray.getColor(R.styleable.TagView_deleteIndicatorColor, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR);
            this.layoutBorderColor = typeArray.getColor(R.styleable.TagView_layoutBorderColor, Constants.DEFAULT_TAG_LAYOUT_BORDER_COLOR);
            this.isDeletable = typeArray.getBoolean(R.styleable.TagView_deletable, Constants.DEFAULT_TAG_IS_DELETABLE);
            this.rowLayout = typeArray.getResourceId(R.styleable.TagView_rowLayout, Constants.DEFAULT_TAG_ROW_LAYOUT);

            this.radius = typeArray.getDimension(R.styleable.TagView_radius, Constants.DEFAULT_TAG_RADIUS);
            this.deleteIndicatorSize = typeArray.getDimension(R.styleable.TagView_deleteIndicatorSize, Constants.DEFAULT_TAG_DELETE_INDICATOR_SIZE);
            this.deleteIcon = typeArray.getString(R.styleable.TagView_deleteIcon);
            this.background = typeArray.getDrawable(R.styleable.TagView_background);

            if (this.deleteIcon == null || this.deleteIcon == "")
                this.deleteIcon = Constants.DEFAULT_TAG_DELETE_ICON;
        } finally {
            typeArray.recycle();
        }

    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.control_tagview_read_layout, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tagsFrame = (RelativeLayout) this.findViewById(R.id.tagsFrame);

    }

    /**
     * onSizeChanged
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        if (width <= 0)
            return;
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTags();
    }


    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    private ViewDataBinding bindLayout(LayoutInflater inflater, TagView parent, int layoutId, Object entry) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, layoutId, parent, false);
        if (!binding.setVariable(BR.data, entry)) {
            String layoutName = parent.getResources().getResourceEntryName(layoutId);
            Log.w(TAG, "There is no variable 'data' in layout " + layoutName);
        }

        return binding;
    }

    private void performTagInit(Tag tag, int position) {
        TagView.OnTagInitListener initListener = getTagInitListener();

        if (initListener != null) {
            initListener.onTagInit(tag, position);
        }
    }

    private void configTag(Tag tag, int position) {
        tag.setTagTextColor(this.getTextColor());
        tag.setTagTextSize(this.getTextSize());
        tag.setLayoutColor(this.getLayoutColor());
        tag.setLayoutColorPress(this.getLayoutColorPress());
        tag.setDeletable(this.isDeletable());
        tag.setDeleteIndicatorColor(this.getDeleteIndicatorColor());
        tag.setDeleteIndicatorSize(this.getDeleteIndicatorSize());
        tag.setRadius(this.getRadius());
        tag.setDeleteIcon(this.getDeleteIcon());
        tag.setLayoutBorderSize(this.getLayoutBorderSize());
        tag.setLayoutBorderColor(this.getLayoutBorderColor());
        tag.setBackground(this.getBackground());

        int kkk = this.rowLayout;

        performTagInit(tag, position);
    }

    /**
     * tag draw
     */
    private void drawTags() {

        if (!mInitialized) {
            return;
        }

        // clear all tag
        if (tagsFrame != null)
            tagsFrame.removeAllViews();


        // layout padding left & layout padding right
        float total = tagsFrame.getPaddingLeft() + tagsFrame.getPaddingRight();

        int listIndex = 1;// List Index
        int indexBottom = 1;// The Tag to add below
        int indexHeader = 1;// The header tag of this line
        Tag tagPre = null;
        for (Tag item : mTags) {
            final int position = listIndex - 1;
            final Tag tag = item;

            configTag(tag, position);

            // inflate tag layout
            ViewDataBinding binding = bindLayout(mInflater, this,  tag.getRowLayout(), tag);
            View tagLayout = binding.getRoot();


            //View tagLayout = mInflater.inflate(this.rowLayout, null);
            tagLayout.setId(listIndex);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                tagLayout.setBackgroundDrawable(getSelector(tag));
            } else {
                tagLayout.setBackground(getSelector(tag));
            }

            // tag text
            TextView tagView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_contain);
            //tagView.setText(tag.text);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tagView.getLayoutParams();
            params.setMargins(textPaddingLeft, textPaddingTop, textPaddingRight, textPaddingBottom);
            tagView.setLayoutParams(params);
            tagView.setTextColor(tag.tagTextColor);
            //tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, tag.tagTextSize);
            tagView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tag.tagTextSize);

            tagLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onTagClick(tag, position);
                    }
                }
            });

            tagLayout.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mTagLongClickListener != null) {
                        mTagLongClickListener.onTagLongClick(tag, position);
                    }
                    return true;
                }
            });

            // calculate　of tag layout width
            float tagWidth = tagView.getPaint().measureText(tag.text) + textPaddingLeft + textPaddingRight;
            // tagView padding (left & right)

            // deletable text
            TextView deletableView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_delete);
            if (tag.isDeletable) {
                deletableView.setVisibility(View.VISIBLE);
                deletableView.setText(tag.deleteIcon);
                int offset = Utils.dipToPx(tagsFrame.getContext(), 2f);
                deletableView.setPadding(offset, textPaddingTop, textPaddingRight + offset, textPaddingBottom);
                deletableView.setTextColor(tag.deleteIndicatorColor);
                deletableView.setTextSize(TypedValue.COMPLEX_UNIT_SP, tag.deleteIndicatorSize);
                deletableView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDeleteListener != null) {
                            mDeleteListener.onTagDeleted(TagView.this, tag, position);
                        }
                    }
                });
                tagWidth += deletableView.getPaint().measureText(tag.deleteIcon) + textPaddingLeft + textPaddingRight;
                // deletableView Padding (left & right)
            } else {
                deletableView.setVisibility(View.GONE);
            }

            RelativeLayout.LayoutParams tagParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            //add margin of each line
            tagParams.bottomMargin = lineMargin;

            if (mWidth <= total + tagWidth + Utils.dipToPx(tagsFrame.getContext(), Constants.LAYOUT_WIDTH_OFFSET)) {
                //need to add in new line
                if (tagPre != null) tagParams.addRule(RelativeLayout.BELOW, indexBottom);
                // initializeDialog total param (layout padding left & layout padding right)
                total = getPaddingLeft() + getPaddingRight();
                indexBottom = listIndex;
                indexHeader = listIndex;
            } else {
                //no need to new line
                tagParams.addRule(RelativeLayout.ALIGN_TOP, indexHeader);
                //not header of the line
                if (listIndex != indexHeader) {
                    tagParams.addRule(RelativeLayout.RIGHT_OF, listIndex - 1);
                    tagParams.leftMargin = tagMargin;
                    total += tagMargin;
                    if (tagPre.tagTextSize < tag.tagTextSize) {
                        indexBottom = listIndex;
                    }
                    /*if (tagPre.tagTextSize < this.textSize) {
                        indexBottom = listIndex;
                    }*/
                }


            }
            total += tagWidth;
            tagsFrame.addView(tagLayout, tagParams);
            tagPre = tag;
            listIndex++;

        }

    }

    private Drawable getSelector(Tag tag) {
        if (tag.background != null)
            return tag.background;

        StateListDrawable states = new StateListDrawable();
        GradientDrawable gdNormal = new GradientDrawable();
        gdNormal.setColor(tag.layoutColor);
        gdNormal.setCornerRadius(tag.radius);
        if (tag.layoutBorderSize > 0) {
            gdNormal.setStroke(Utils.dipToPx(getContext(), tag.layoutBorderSize), tag.layoutBorderColor);
        }
        GradientDrawable gdPress = new GradientDrawable();
        gdPress.setColor(tag.layoutColorPress);
        gdPress.setCornerRadius(tag.radius);
        states.addState(new int[]{android.R.attr.state_pressed}, gdPress);
        //must add state_pressed first，or state_pressed will not take effect
        states.addState(new int[]{}, gdNormal);
        return states;
    }

    private void resetViews(List<Tag> tags, int layoutId) {
        if (layoutId == 0)
            return;

        if (tags == null)
            return;

        this.rowLayout = layoutId;
        this.mTags = new ArrayList<>();

        for (Tag item : tags) {
            item.setRowLayout(layoutId);
            this.mTags.add(item);
        }

        drawTags();



        /*for (int i = 0; i < this.mTags.size(); i++) {
            Tag entry = (Tag)this.mTags.get(i);
            entry.setRowLayout(layoutId);
        }*/
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Methods">

    /**
     * @param tag
     */
    public void addTag(Tag tag) {
        tag.setRowLayout(this.rowLayout);
        mTags.add(tag);
        drawTags();
    }

    public void addTag(Tag tag, int layoutId) {
        tag.setRowLayout(layoutId);
        mTags.add(tag);
        drawTags();
    }

    public void insertTag(Tag tag, int position) {
        mTags.add(position, tag);
        drawTags();
    }

    public void insertTag(Tag tag, int layoutId, int position) {
        tag.setRowLayout(layoutId);
        mTags.add(position, tag);
        drawTags();
    }

    public void addTags(List<Tag> tags) {
        if (tags == null) return;
        mTags = new ArrayList<>();
        if (tags.isEmpty())
            drawTags();
        for (Tag item : tags) {
            mTags.add(item);
        }
        drawTags();
    }

    public void addTags(String[] tags) {
        if (tags == null) return;
        for (String item : tags) {
            Tag tag = new Tag(item);
            mTags.add(tag);
        }
        drawTags();
    }

    /**
     * get tag list
     *
     * @return mTags TagObject List
     */
    public List<Tag> getTags() {
        return this.mTags;
    }

    public Tag getTag(int position) {
        Tag tag = null;
        if (position < mTags.size()) {
            tag = mTags.get(position);
        }

        return tag;
    }

    /**
     * remove tag
     *
     * @param position
     */
    public void remove(int position) {
        if (position < mTags.size()) {
            mTags.remove(position);
            drawTags();
        }
    }

    /**
     * remove all views
     */
    public void removeAll() {
        mTags.clear(); //clear all of tags

        if (tagsFrame != null)
            tagsFrame.removeAllViews();
    }



    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overrides">

    @Override
    public void setValue(String value) {

    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        //((TextReadControl)nextView).txtControlInput.requestFocus();
        //((TextReadControl) nextView).setCursorToRight();

    }

    @Override
    public Drawable getBackground() {
        return background;
    }

    @Override
    public void setBackground(Drawable background) {
        this.background = background;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event Listeners">

    public void removeOnTagLongClickListener(OnTagLongClickListener longClickListener) {
        if (mTagLongClickListener == longClickListener)
            mTagLongClickListener = null;
    }

    public void removeOnTagClickListener(OnTagClickListener clickListener) {
        if (mClickListener == clickListener)
            mClickListener = null;
    }

    public void removeOnTagDeleteListener(OnTagDeleteListener deleteListener) {
        if (mDeleteListener == deleteListener)
            mDeleteListener = null;
    }

    public void removeOnTagInitListener(OnTagInitListener initListener) {
        if (mTagInitListener == initListener)
            mTagInitListener = null;
    }


    public OnTagClickListener getClickListener() {
        return mClickListener;
    }

    public OnTagDeleteListener getDeleteListener() {
        return mDeleteListener;
    }

    public OnTagLongClickListener getTagLongClickListener() {
        return mTagLongClickListener;
    }

    public OnTagInitListener getTagInitListener() {
        return mTagInitListener;
    }


    /**
     * Listeners
     */
    public interface OnTagDeleteListener {
        void onTagDeleted(TagView view, Tag tag, int position);
    }

    public interface OnTagClickListener {
        void onTagClick(Tag tag, int position);
    }

    public interface OnTagLongClickListener {
        void onTagLongClick(Tag tag, int position);
    }

    public interface OnTagInitListener {
        void onTagInit(Tag tag, int position);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters">

    public int getTagWidth() {
        return mWidth;
    }

    public int getLineMargin() {
        return lineMargin;
    }

    public int getTagMargin() {
        return tagMargin;
    }

    public int getTextPaddingLeft() {
        return textPaddingLeft;
    }

    public int getTextPaddingRight() {
        return textPaddingRight;
    }

    public int getTextPaddingTop() {
        return textPaddingTop;
    }

    public int getTextPaddingBottom() {
        return textPaddingBottom;
    }

    public float getTextSize() {
        return textSize;
    }

    public float getLayoutBorderSize() {
        return layoutBorderSize;
    }

    public int getLayoutColor() {
        return layoutColor;
    }

    public int getLayoutColorPress() {
        return layoutColorPress;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getDeleteIndicatorColor() {
        return deleteIndicatorColor;
    }

    public int getLayoutBorderColor() {
        return layoutBorderColor;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public float getRadius() {
        return radius;
    }

    public float getDeleteIndicatorSize() {
        return deleteIndicatorSize;
    }

    public String getDeleteIcon() {
        return deleteIcon;
    }

    public int getRowLayout() {
        return rowLayout;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">

    public void setTags(List<Tag> tags) {
        if (this.rowLayout == 0)
            throw new IllegalArgumentException("The row layout must be set before the tags.");

        if (mTags == tags) {
            return; // nothing has changed
        }

        if (tags == null) {
            removeAll();
        } else {
            resetViews(tags, this.rowLayout);
        }
    }

    public void setRowLayout(int rowLayout) {
        if (this.rowLayout == rowLayout) {
            return; // nothing has changed
        }

        if (getTags() != null)
            resetViews(getTags(), rowLayout);
    }

    public void setTextPaddingBottom(float textPaddingBottom) {
        this.textPaddingBottom = Utils.dipToPx(getContext(), textPaddingBottom);
    }

    public void setTextPaddingRight(float textPaddingRight) {
        this.textPaddingRight = Utils.dipToPx(getContext(), textPaddingRight);
    }

    public void setTextPaddingTop(float textPaddingTop) {
        this.textPaddingTop = Utils.dipToPx(getContext(), textPaddingTop);
    }

    public void setTextPaddingLeft(float textPaddingLeft) {
        this.textPaddingLeft = Utils.dipToPx(getContext(), textPaddingLeft);
    }

    public void setTagMargin(float tagMargin) {
        this.tagMargin = Utils.dipToPx(getContext(), tagMargin);
    }

    public void setLineMargin(float lineMargin) {
        this.lineMargin = Utils.dipToPx(getContext(), lineMargin);
    }

    public void setOnTagLongClickListener(OnTagLongClickListener longClickListener) {
        mTagLongClickListener = longClickListener;
    }

    public void setOnTagClickListener(OnTagClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setOnTagDeleteListener(OnTagDeleteListener deleteListener) {
        mDeleteListener = deleteListener;
    }

    public void setOnTagInitListener(OnTagInitListener initListener) {
        mTagInitListener = initListener;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setLayoutColor(int layoutColor) {
        this.layoutColor = layoutColor;
    }

    public void setLayoutColorPress(int layoutColorPress) {
        this.layoutColorPress = layoutColorPress;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setLayoutBorderColor(int layoutBorderColor) {
        this.layoutBorderColor = layoutBorderColor;
    }

    public void setDeletable(boolean deletable) {
        isDeletable = deletable;
    }

    public void setLineMargin(int lineMargin) {
        this.lineMargin = lineMargin;
    }

    public void setTagMargin(int tagMargin) {
        this.tagMargin = tagMargin;
    }

    public void setTextPaddingLeft(int textPaddingLeft) {
        this.textPaddingLeft = textPaddingLeft;
    }

    public void setTextPaddingRight(int textPaddingRight) {
        this.textPaddingRight = textPaddingRight;
    }

    public void setTextPaddingTop(int textPaddingTop) {
        this.textPaddingTop = textPaddingTop;
    }

    public void setTextPaddingBottom(int textPaddingBottom) {
        this.textPaddingBottom = textPaddingBottom;
    }

    public void setLayoutBorderSize(float layoutBorderSize) {
        this.layoutBorderSize = layoutBorderSize;
    }

    public void setDeleteIndicatorColor(int deleteIndicatorColor) {
        this.deleteIndicatorColor = deleteIndicatorColor;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setDeleteIndicatorSize(float deleteIndicatorSize) {
        this.deleteIndicatorSize = deleteIndicatorSize;
    }

    public void setDeleteIcon(String deleteIcon) {
        this.deleteIcon = deleteIcon;
    }

    // </editor-fold>

}
