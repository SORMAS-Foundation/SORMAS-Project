package de.symeda.sormas.app.component.menu;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.percent.PercentFrameLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.OnSwipeTouchListener;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;


/**
 * Created by Orson on 25/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class LandingPageMenuControl extends LinearLayout {

    public static final String TAG = LandingPageMenuControl.class.getSimpleName();

    private AsyncTask updateNotificationCountTask;
    private OnNotificationCountChangingListener mOnNotificationCountChangingListener;
    private OnLandingPageMenuClickListener mOnLandingPageMenuClickListener;
    private OnSelectInitialActiveMenuItemListener mOnSelectInitialActiveMenuItemListener;

    private BaseAdapter adapter;
    private List<LandingPageMenuItem> menuList;

    private int dataResourceId;
    private int cellLayout;
    private int counterBackgroundColor;
    private int counterBackgroundActiveColor;
    private int iconColor;
    private int iconActiveColor;
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
                dataResourceId = a.getResourceId(R.styleable.LandingPageMenuControl_dataResource, -1);
                cellLayout = a.getResourceId(R.styleable.LandingPageMenuControl_cellLayout, 0);

                counterBackgroundColor = a.getResourceId(R.styleable.LandingPageMenuControl_counterBackgroundColor, 0);
                counterBackgroundActiveColor = a.getResourceId(R.styleable.LandingPageMenuControl_counterBackgroundActiveColor, 0);
                iconColor = a.getResourceId(R.styleable.LandingPageMenuControl_iconColor, 0);
                iconActiveColor = a.getResourceId(R.styleable.LandingPageMenuControl_iconActiveColor, 0);

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
        inflater.inflate(R.layout.sub_menu_layout, this);
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

    public void setMenuData(int dataResId) throws IOException, XmlPullParserException, ParserConfigurationException {
        if (dataResId > 0) {
            dataResourceId = dataResId;
        } else {
            setFabFrameVisibility(false);
            return;
            //throw new IllegalArgumentException("The dataResourceId file argument is empty.");
        }

        setFabFrameVisibility(true);

        extractAndLoadMenuData();

        if (adapter != null)
            adapter.notifyDataSetChanged();

        invalidate();
        requestLayout();

        this.dataResourceId = dataResId;
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
            ((IPageMenuAdapter)adapter).initialize(menuList, cellLayout, counterBackgroundColor, counterBackgroundActiveColor,
                    iconColor, iconActiveColor, positionColor, positionActiveColor, titleColor, titleActiveColor);
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

    private List<LandingPageMenuItem> extractAndLoadMenuData() throws IOException, XmlPullParserException, ParserConfigurationException {
        if (dataResourceId <= 0)
            throw new IllegalArgumentException("The dataResourceId file argument is empty.");

        if (menuList == null)
            menuList = new ArrayList<>();

        menuList.clear();

        parser = new LandingPageMenuParser(getContext());

        if (parser == null)
            throw new ParserConfigurationException("This is no parser configured for the menu control.");

        LandingPageMenu menu = parser.parse(getResources().getXml(dataResourceId));

        //Set Title
        setMenuTitle(menu.getTitle());

        menuList.addAll(menu.getMenuItems());
        updateNotificationCount(menuList, new Callback.IAction<BoolResult>() {
            @Override
            public void call(BoolResult result) {
                if (result.isSuccess())
                    adapter.notifyDataSetChanged();
            }
        });

        if (menuList != null && menuList.size() > 0) {
            selectInitialActiveMenuItem();
            //performAllNotificationCountChange();
        }

        return menuList;
    }

    private void updateNotificationCount(final List<LandingPageMenuItem> inputMenuList, final Callback.IAction<BoolResult> callback) {
        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {

                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    int position = 0;
                    for(final LandingPageMenuItem entry: inputMenuList) {
                        int result = performNotificationCountChange(entry, position);
                        entry.setNotificationCount(result);
                        //resultHolder.<LandingPageMenuItem>forEnumerable().add(entry);

                        position = position + 1;
                    }

                    resultHolder.setResultStatus(BoolResult.TRUE);
                }
            });
            updateNotificationCountTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    if (resultHolder == null){
                        return;
                    }

                    callback.call(resultStatus);

                    /*ITaskResultHolderEnumerableIterator<LandingPageMenuItem> enumerableIterator = resultHolder.forEnumerable().iterator();

                    if (enumerableIterator.hasNext()) {
                        callback.call(enumerableIterator.next());
                    }*/
                }
            });
        } catch (Exception ex) {

        }
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

        updateNotificationCount(menuList, new Callback.IAction<BoolResult>() {
            @Override
            public void call(BoolResult result) {
                if (result.isSuccess())
                    adapter.notifyDataSetChanged();
            }
        });
    }
    //Orson on Init
    public int performNotificationCountChange(LandingPageMenuItem menuItem, int position) {
        int result = 0;
        if (mOnNotificationCountChangingListener != null) {
            result = mOnNotificationCountChangingListener.onNotificationCountChangingAsync(taskLandingMenuGridView, menuItem, position);
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
            result = mOnLandingPageMenuClickListener.onLandingPageMenuClick(parent, view, menuItem, position, id); //IMPORTANT

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
        } else if ((taskLandingMenuTitle = (TextView) this.findViewById(R.id.sub_menu_title)) == null) {
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

        taskLandingMenuTitle = (TextView)findViewById(R.id.sub_menu_title);
        taskLandingMenuGridView = (GridView)findViewById(R.id.sub_menu_grid);
        fabFrame = (FrameLayout)findViewById(R.id.button_frame);
        fab = (FloatingActionButton)findViewById(R.id.sub_menu_button);


        setVisibility(View.VISIBLE);

        //configureFab();

        try {
            if (dataResourceId > 0)
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
            mEarlyAction = null;
            //setY(this.getHeight() - this.mFabHeight);
            //setVisibility(View.VISIBLE);
        } else {
            show();
            mEarlyAction = null;
            //setY(0);
            //setVisibility(View.VISIBLE);
        }

        updateFabDrawable();

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

        setVisibility(View.VISIBLE);

        if (mLastAnimation == null) {
            setY(this.mOpenPositionY);
            mLastAnimation = ActionType.SHOW;
        } else if (mLastAnimation != null && mLastAnimation == ActionType.HIDE) {
            setY(this.mClosePositionY);
            this.animate().y(this.mOpenPositionY).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
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
        setVisibility(View.VISIBLE);

        if (mLastAnimation == null) {
            setY(this.mClosePositionY);
            mLastAnimation = ActionType.HIDE;
        } else if (mLastAnimation != null && mLastAnimation == ActionType.SHOW) {
            this.animate().y(this.mClosePositionY).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
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
        updateFabDrawable();
    }

    public void hideAll() {
        hide();
        setFabFrameVisibility(false);
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
        return this.dataResourceId > 0;
    }

    private void configureFab() {
        if (fab == null)
            return;

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
                if (fab.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)fab.getLayoutParams();
                    params.gravity = Gravity.END;
                    fab.setLayoutParams(params);
                } else if (fab.getLayoutParams() instanceof PercentFrameLayout.LayoutParams) {
                    PercentFrameLayout.LayoutParams params = (PercentFrameLayout.LayoutParams)fab.getLayoutParams();
                    params.gravity = Gravity.END;
                    fab.setLayoutParams(params);
                }

                return true;
            }
            public boolean onSwipeLeft() {
                if (fab.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)fab.getLayoutParams();
                    params.gravity = Gravity.START;
                    fab.setLayoutParams(params);
                } else if (fab.getLayoutParams() instanceof PercentFrameLayout.LayoutParams) {
                    PercentFrameLayout.LayoutParams params = (PercentFrameLayout.LayoutParams)fab.getLayoutParams();
                    params.gravity = Gravity.START;
                    fab.setLayoutParams(params);
                }

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

    public void onDestroy() {
        if (updateNotificationCountTask != null && !updateNotificationCountTask.isCancelled())
            updateNotificationCountTask.cancel(true);
    }
}
