package de.symeda.sormas.app.component.menu;

import android.animation.Animator;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.OnSwipeTouchListener;


/**
 * Created by Orson on 25/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class LandingPageMenuControl extends LinearLayout {

    public static final String TAG = LandingPageMenuControl.class.getSimpleName();

    private OnNotificationCountChangingListener mOnNotificationCountChangingListener;
    private OnLandingPageMenuClickListener mOnLandingPageMenuClickListener;
    private OnSelectInitialActiveMenuItemListener mOnSelectInitialActiveMenuItemListener;

    private BaseAdapter adapter;
    private ArrayList<LandingPageMenuItem> menuList;

    private String dataFile;
    private int cellLayout;
    private int positionColor;
    private int positionActiveColor;
    private int titleColor;
    private int titleActiveColor;
    private FrameLayout fabFrame;
    private FloatingActionButton fab;
    private TextView taskLandingMenuTitle;
    private GridView taskLandingMenuGridView;

    private ILandingPageContext landingPageContext;
    private IMenuParser parser;
    private boolean mVisible;
    private boolean mCollapsible;
    private boolean mIsAnimatingIn;
    private boolean mFirstAnimatingIn = true;
    private boolean mConfigured = false;
    private int mMarginBottomOffsetResId = -1;

    private int mCapturedLayoutHeight = 0;
    private int mFabHeight = 0;
    private int mParentWidth = 0;
    private int mParentHeight = 0;
    private int mParentBottomOffset = 0;

    private int mOpenPositionY = 0;
    private int mClosePositionY = 0;

    private ActionType mEarlyAction;
    private ActionType mLastAnimation;


    private enum ActionType {
        SHOW,
        HIDE;
    }

    public LandingPageMenuControl(Context context) {
        super(context);
        initializeViews(context, null);
    }

    public LandingPageMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs);
    }

    protected void initializeViews(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.LandingPageMenuControl,
                    0, 0);

            try {
                dataFile = a.getString(R.styleable.LandingPageMenuControl_dataFile);
                cellLayout = a.getResourceId(R.styleable.LandingPageMenuControl_cellLayout, 0);
                positionColor = a.getResourceId(R.styleable.LandingPageMenuControl_positionColor, 0);
                positionActiveColor = a.getResourceId(R.styleable.LandingPageMenuControl_positionActiveColor, 0);
                titleColor = a.getResourceId(R.styleable.LandingPageMenuControl_titleColor, 0);
                titleActiveColor = a.getResourceId(R.styleable.LandingPageMenuControl_titleActiveColor, 0);
                mVisible = a.getBoolean(R.styleable.LandingPageMenuControl_visibility, false);
                mCollapsible = a.getBoolean(R.styleable.LandingPageMenuControl_collapsible, false);
                mMarginBottomOffsetResId = a.getResourceId(R.styleable.LandingPageMenuControl_marginBottomOffsetResId, -1);



            } finally {
                a.recycle();
            }
        }

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_landing_page_menu_layout, this);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        //super.onVisibilityChanged(changedView, visibility);

        if (changedView == this) {
            if (isVisible()) { //visibility == VISIBLE
                //configureControl();
            }
        }
    }

    public void setMenuData(String data) throws IOException, XmlPullParserException, ParserConfigurationException {
        if (data != null && !data.isEmpty()) {
            dataFile = data;
        } else {
            setFabFrameVisibility(false);
            return;
            //throw new IllegalArgumentException("The dataFile file argument is empty.");
        }

        setFabFrameVisibility(true);

        extractAndLoadMenuData();

        if (adapter != null)
            adapter.notifyDataSetChanged();

        invalidate();
        requestLayout();

        this.dataFile = data;
    }

    public void setMenuParser(IMenuParser parser) {
        this.parser = parser;
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
    }

    private void configureControl() {
        if (adapter == null) {
            Log.e(TAG, "This is no adapter configured for the menu control.");
            return;
        }

        if (adapter instanceof IPageMenuAdapter) {
            ((IPageMenuAdapter)adapter).initialize(menuList, cellLayout,
                    positionColor, positionActiveColor, titleColor, titleActiveColor);
        } else {
            Log.e(TAG, "Page menu adapters must implement IPageMenuAdapter");
            return;
        }

        taskLandingMenuGridView.setAdapter(adapter);
        taskLandingMenuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    performLandingPageMenuItemClick(parent, view, menuList.get(position), position, id);
                } catch (InstantiationException e) {
                    Log.e(TAG, e.getMessage());
                } catch (IllegalAccessException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

        mConfigured = true;
    }

    private ArrayList<LandingPageMenuItem> extractAndLoadMenuData() throws IOException, XmlPullParserException, ParserConfigurationException {
        if (dataFile == null || dataFile.isEmpty())
            throw new IllegalArgumentException("The dataFile file argument is empty.");

        if (menuList == null)
            menuList = new ArrayList<>();

        menuList.clear();

        if (parser == null)
            throw new ParserConfigurationException("This is no parser configured for the menu control.");

        AssetManager assetManager = getContext().getAssets();
        InputStream is = assetManager.open(dataFile); //data_landing_page_task_menu.xml
        //IMenuParser parser = new LandingPageMenuParser(getContext());
        LandingPageMenu menu = parser.parse(is);

        //Set Title
        setMenuTitle(menu.getTitle());

        int position = 0;
        for(LandingPageMenuItem entry: menu.getMenuItems()) {
            //Get Notification Count
            entry.setNotificationCount(performNotificationCountChange(entry, position));

            menuList.add(entry);
            position = position + 1;
        }

        if (menuList != null && menuList.size() > 0) {
            selectInitialActiveMenuItem();
            //performAllNotificationCountChange();
        }

        return menuList;
    }

    public void setOnNotificationCountChangingListener(@Nullable OnNotificationCountChangingListener listener) {
        mOnNotificationCountChangingListener = listener;
    }

    @Nullable
    public final OnNotificationCountChangingListener getOnNotificationCountChangingListener() {
        return mOnNotificationCountChangingListener;
    }

    private void performAllNotificationCountChange() {
        if (menuList == null)
            throw new NullPointerException("The menuList is null.");


        int position = 0;
        for(LandingPageMenuItem entry: menuList) {
            entry.setNotificationCount(performNotificationCountChange(entry, position));
            position = position + 1;
        }
    }

    public int performNotificationCountChange(LandingPageMenuItem menuItem, int position) {
        int result = 0;
        if (mOnNotificationCountChangingListener != null) {
            result = mOnNotificationCountChangingListener.onNotificationCountChanging(taskLandingMenuGridView, menuItem, position);
        }

        return result;
    }

    public void setOnLandingPageMenuClickListener(@Nullable OnLandingPageMenuClickListener listener) {
        mOnLandingPageMenuClickListener = listener;
    }

    @Nullable
    public final OnLandingPageMenuClickListener getOnLandingPageMenuClickListener() {
        return mOnLandingPageMenuClickListener;
    }

    public void setOnSelectInitialActiveMenuItem(@Nullable OnSelectInitialActiveMenuItemListener listener) {
        mOnSelectInitialActiveMenuItemListener = listener;
    }
    @Nullable
    public final OnSelectInitialActiveMenuItemListener getOnSelectInitialActiveMenuItemListener() {
        return mOnSelectInitialActiveMenuItemListener;
    }

    public boolean selectInitialActiveMenuItem() {
        boolean returnVal = false;
        LandingPageMenuItem result = null;
        if (mOnSelectInitialActiveMenuItemListener != null) {
            result = mOnSelectInitialActiveMenuItemListener.onSelectInitialActiveMenuItem(menuList);

            if (result != null) {
                result.setActive(true);
                returnVal = true;
            }
        }

        return returnVal;
    }

    public boolean performLandingPageMenuItemClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) throws InstantiationException, IllegalAccessException {
        boolean result = false;
        if (mOnLandingPageMenuClickListener != null) {
            result = mOnLandingPageMenuClickListener.onLandingPageMenuClick(parent, view, menuItem, position, id);

            if (result) {
                markActiveMenuItem(menuItem);
            }
        }

        return result;
    }


    public void markActiveMenuItem(LandingPageMenuItem menuItem) {
        for(LandingPageMenuItem m: menuList) {
            m.setActive(false);
        }

        menuItem.setActive(true);
        onMenuItemActive(menuItem);

        adapter.notifyDataSetChanged();

        invalidate();
        requestLayout();
    }

    private void onMenuItemActive(LandingPageMenuItem menuItem) {

    }

    public void setMenuTitle(String title) {
        if (taskLandingMenuTitle != null) {
            taskLandingMenuTitle.setText(title);
        } else if ((taskLandingMenuTitle = (TextView) this.findViewById(R.id.taskLandingMenuTitle)) == null) {
            throw new NullPointerException("The menu control title object is null.");
        }

        taskLandingMenuTitle.setText(title);
        invalidate();
        requestLayout();
    }

    public String getMenuTitle() {
        return taskLandingMenuTitle.getText().toString();
    }

    public ILandingPageContext getLandingPageContext() {
        return this.landingPageContext;
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        taskLandingMenuTitle = (TextView)findViewById(R.id.taskLandingMenuTitle);
        taskLandingMenuGridView = (GridView)findViewById(R.id.taskLandingMenuGridView);
        fabFrame = (FrameLayout)findViewById(R.id.fabFrame);
        fab = (FloatingActionButton)findViewById(R.id.fab);


        setVisibility(View.VISIBLE);

        //configureFab();

        try {
            if (dataFile != null && !dataFile.isEmpty())
                extractAndLoadMenuData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


        //Set Title
        //setMenuTitle(title);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();


        disableClipOnParents(this);

        if (!mConfigured)
            configureControl();

        setFabFrameVisibility(true);

        configureFab();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mParentWidth = MeasureSpec.getSize(widthMeasureSpec);
        mParentHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int width = r - l;
        int height = b - t;

        int w1 = getMeasuredWidth();
        int h1 = getMeasuredHeight();

        int w2 = getWidth();
        int h2 = getHeight();

        int miniW = getMinimumWidth();
        int miniH = getMinimumHeight();

        if (getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams pppp = (FrameLayout.LayoutParams)getLayoutParams();

            int wwwww = pppp.width;
            int hhhhh = pppp.height;

            String iiii = "";
        }

        Object kkk11 = getLayoutParams();
        ViewGroup.LayoutParams kkk = getLayoutParams();


        String kkkkkkk = "";
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        this.mCapturedLayoutHeight = this.getHeight();
        this.mFabHeight = calculateFabHeight();

        this.mOpenPositionY = this.mParentHeight - this.mCapturedLayoutHeight;
        this.mClosePositionY = this.mParentHeight - this.mFabHeight;

        if (this.mMarginBottomOffsetResId > 0 && getParent() instanceof ViewGroup) {
            View v = null;

            this.mParentBottomOffset = this.mCapturedLayoutHeight - this.mFabHeight;

            if (getRootView() != null)
                v = getRootView().findViewById(mMarginBottomOffsetResId);

            if (v != null) {
                if (v.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams param = (LinearLayout.LayoutParams)v.getLayoutParams();
                    param.bottomMargin = this.mParentBottomOffset;
                    v.setLayoutParams(param);
                } else if (v.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)v.getLayoutParams();
                    param.bottomMargin = this.mParentBottomOffset;
                    v.setLayoutParams(param);
                } else if (v.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams param = (FrameLayout.LayoutParams)v.getLayoutParams();
                    param.bottomMargin = this.mParentBottomOffset;
                    v.setLayoutParams(param);
                } else if (v.getLayoutParams() instanceof ScrollView.LayoutParams) {
                    ScrollView.LayoutParams param = (ScrollView.LayoutParams)v.getLayoutParams();
                    param.bottomMargin = this.mParentBottomOffset;
                    v.setLayoutParams(param);
                }
            }
        }


        if (!showPageMenu()) {
            //hideAll();
            //setY(this.getHeight());
            setVisibility(View.GONE);
        } else if (!isVisible() || mEarlyAction == ActionType.HIDE) {
            hide();
            //setY(this.getHeight() - this.mFabHeight);
            //setVisibility(View.VISIBLE);
        } else {
            show();
            //setY(0);
            //setVisibility(View.VISIBLE);
        }

        updateFabDrawable();

        /*if (isVisible()) {
            show();
        } else {
            hide();
        }*/

        //setVisibility(isVisible()? VISIBLE : GONE);

        canvas.drawARGB(0, 225, 225, 255);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SCREEN);
    }

    public void show() {
        if (this.mCapturedLayoutHeight <= 0) {
            mEarlyAction = ActionType.SHOW;
            return;
        }

        if (!showPageMenu())
            return;

        //this.onVisibilityChanged(this, View.VISIBLE);

        /*if (mCollapsible)
            setFabDownDrawable();*/

        /*if (mFirstAnimatingIn)
            setY(this.mCapturedLayoutHeight);*/

        setVisibility(View.VISIBLE);

        //mFirstAnimatingIn = true;



        if (mLastAnimation == null) {
            setY(this.mOpenPositionY);
            mLastAnimation = ActionType.SHOW;
        } else if (mLastAnimation != null && mLastAnimation == ActionType.HIDE) {
            setY(this.mClosePositionY);
            this.animate().y(this.mOpenPositionY).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setY(LandingPageMenuControl.this.mClosePositionY);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setY(LandingPageMenuControl.this.mOpenPositionY);
                    mLastAnimation = ActionType.SHOW;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }


        this.mVisible = true;
        updateFabDrawable();
        //mLastAnimation = ActionType.SHOW;



        /*this.animate().translationY(0).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setTranslationY(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();*/
    }

    public void showFab() {
        setFabFrameVisibility(true);
    }

    public void hide() {
        if (this.mCapturedLayoutHeight <= 0) {
            mEarlyAction = ActionType.HIDE;
            return;
        }

        if (!mCollapsible)
            return;

        if (!showPageMenu()) {
            setY(2000);
            return;
        }


        /*if (this.mCapturedLayoutHeight <= 0)
            return;*/

        //this.onVisibilityChanged(this, View.GONE);



        setVisibility(View.VISIBLE);


        //setY(this.mClosePositionY);
        if (mLastAnimation == null) {
            setY(this.mClosePositionY);
            mLastAnimation = ActionType.HIDE;
        } else if (mLastAnimation != null && mLastAnimation == ActionType.SHOW) {
            //Animate
            this.animate().y(this.mClosePositionY).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setY(LandingPageMenuControl.this.mClosePositionY);
                    mLastAnimation = ActionType.HIDE;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }

        this.mVisible = false;
        //mLastAnimation = ActionType.HIDE;

        //animateOut(this.mCapturedLayoutHeight - this.mFabHeight); // - this.mFabHeight


        updateFabDrawable();
    }

    public void hideAll() {
        hide();
        setFabFrameVisibility(false);
    }

    private void animateOut(int height) {
        this.animate().translationY(height).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                LandingPageMenuControl.this.mIsAnimatingIn = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                LandingPageMenuControl.this.mIsAnimatingIn = true;
                //setVisibility(View.GONE);
                //setAlpha(0);
                //setBackground(new ColorDrawable(0));
                //setBackground(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void setCollapsible(boolean collapsible) {
        this.mCollapsible = collapsible;
        setFabFrameVisibility(true);
        //configureFab();
    }

    private boolean showPageMenu() {
        return this.dataFile != null && !this.dataFile.isEmpty();
    }

    private void configureFab() {

        /*if (!mCollapsible)
            return;*/

        if (fab == null)
            return;

        /*if (!showPageMenu()) {
            hideAll();
            *//*fab.setVisibility(View.GONE);
            setFabDownDrawable();*//*
            return;
        }*/

        /*fab.setVisibility(View.VISIBLE);
        setFabUpDrawable();*/

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisible()) {
                    hide();
                } else {
                    show();
                }
            }
        });

        fab.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public boolean onSwipeTop() {
                return true;
            }
            public boolean onSwipeRight() {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)fab.getLayoutParams();
                params.gravity = Gravity.BOTTOM | Gravity.END;
                fab.setLayoutParams(params);

                return true;
            }
            public boolean onSwipeLeft() {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)fab.getLayoutParams();
                params.gravity = Gravity.BOTTOM | Gravity.START;
                fab.setLayoutParams(params);

                return true;
            }
            public boolean onSwipeBottom() {
                return true;
            }

        });
    }

    private void updateFabDrawable() {
        if (isVisible()) {
            Drawable drw = (Drawable) ContextCompat.getDrawable(fab.getContext(), R.drawable.ic_landing_menu_close_black_24dp);
            drw.setTint(fab.getContext().getResources().getColor(R.color.fabIcon));
            fab.setImageDrawable(drw);
        } else {
            Drawable drw = (Drawable) ContextCompat.getDrawable(fab.getContext(), R.drawable.ic_landing_menu_open_black_24dp);
            drw.setTint(fab.getContext().getResources().getColor(R.color.fabIcon));
            fab.setImageDrawable(drw);
        }
    }

    /*private void setFabUpDrawable() {
        Drawable drw = (Drawable) ContextCompat.getDrawable(fab.getContext(), R.drawable.ic_landing_menu_open_black_24dp);
        drw.setTint(fab.getContext().getResources().getColor(R.color.fabIcon));
        fab.setImageDrawable(drw);
    }

    private void setFabDownDrawable() {
        Drawable drw = (Drawable) ContextCompat.getDrawable(fab.getContext(), R.drawable.ic_landing_menu_close_black_24dp);
        drw.setTint(fab.getContext().getResources().getColor(R.color.fabIcon));
        fab.setImageDrawable(drw);
    }*/

    private int calculateFabHeight() {
        if (fabFrame == null) {
            return 0;
        }

        if (fab == null) {
            return 0;
        }

        return fabFrame.getHeight();

        /*FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)fab.getLayoutParams();
        this.mFabHeight = fab.getHeight() + params.topMargin + params.bottomMargin;*/
    }




    private void setFabFrameVisibility(boolean visibility) {
        if (fabFrame != null)
            fabFrame.setVisibility(mCollapsible && visibility && showPageMenu()? VISIBLE : GONE);
    }

    public void disableClipOnParents(View v) {
        if (v.getParent() == null) {
            return;
        }

        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setClipChildren(false);
        }

        if (v.getParent() instanceof View) {
            disableClipOnParents((View) v.getParent());
        }
    }
}
