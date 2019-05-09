/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.component.menu;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.percentlayout.widget.PercentFrameLayout;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.OnSwipeTouchListener;

public class PageMenuControl extends LinearLayout {

    public static final String TAG = PageMenuControl.class.getSimpleName();

    private NotificationCountChangingListener mOnNotificationCountChangingListener;
    private PageMenuClickListener pageMenuClickListener;
    private PageMenuInitialSelectionProvider pageMenuInitialSelectionProvider;

    private PageMenuAdapter adapter;
    private List<PageMenuItem> menuItems;

    private int cellLayout;
    private int counterBackgroundColor;
    private int counterBackgroundActiveColor;
    private int iconColor;
    private int iconActiveColor;
    private int titleColor;
    private int titleActiveColor;
    private FrameLayout fabFrame;
    private FloatingActionButton fab;
    private LinearLayout subMenuFrame;
    private LinearLayout filtersFrame;
    private GridView taskLandingMenuGridView;

    private boolean mVisible;
    private boolean mCollapsible;
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

    public PageMenuControl(Context context) {
        super(context);
        initializeViews(context, null);
    }

    public PageMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs);
    }

    protected void initializeViews(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.PageMenuControl,
                    0, 0);

            try {
                cellLayout = a.getResourceId(R.styleable.PageMenuControl_cellLayout, 0);

                counterBackgroundColor = a.getResourceId(R.styleable.PageMenuControl_counterBackgroundColor, 0);
                counterBackgroundActiveColor = a.getResourceId(R.styleable.PageMenuControl_counterBackgroundActiveColor, 0);
                iconColor = a.getResourceId(R.styleable.PageMenuControl_iconColor, 0);
                iconActiveColor = a.getResourceId(R.styleable.PageMenuControl_iconActiveColor, 0);

                titleColor = a.getResourceId(R.styleable.PageMenuControl_titleColor, 0);
                titleActiveColor = a.getResourceId(R.styleable.PageMenuControl_titleActiveColor, 0);
                mVisible = a.getBoolean(R.styleable.PageMenuControl_visibility, false);
                mCollapsible = a.getBoolean(R.styleable.PageMenuControl_collapsible, false);
                mMarginBottomOffsetResId = a.getResourceId(R.styleable.PageMenuControl_marginBottomOffsetResId, -1);

            } finally {
                a.recycle();
            }
        }

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sub_menu_layout, this);

        adapter = new PageMenuAdapter(context);
        adapter.initialize(cellLayout, counterBackgroundColor, counterBackgroundActiveColor,
                iconColor, iconActiveColor, titleColor, titleActiveColor);
    }

    public <T extends Enum> void setMenuData(List<PageMenuItem> menuItems) {
        this.menuItems = menuItems;

        if (!showPageMenu()) {
            setFabFrameVisibility(false);
            return;
        }
        setFabFrameVisibility(true);

        adapter.setData(this.menuItems);

        selectInitialActiveMenuItem();
        updateNotificationCount();

        invalidate();
        requestLayout();
    }

    private void updateNotificationCount() {
        int position = 0;
        for (final PageMenuItem menuItem : menuItems) {
            int result = performNotificationCountChange(menuItem, position);
            menuItem.setNotificationCount(result);
            position = position + 1;
        }
        adapter.notifyDataSetChanged();
    }

    public void setOnNotificationCountChangingListener(@Nullable NotificationCountChangingListener listener) {
        mOnNotificationCountChangingListener = listener;
    }

    @Nullable
    public final NotificationCountChangingListener getOnNotificationCountChangingListener() {
        return mOnNotificationCountChangingListener;
    }

    public int performNotificationCountChange(PageMenuItem menuItem, int position) {
        int result = 0;
        if (mOnNotificationCountChangingListener != null) {
            result = mOnNotificationCountChangingListener.onNotificationCountChangingAsync(taskLandingMenuGridView, menuItem, position);
        }

        return result;
    }

    public void setPageMenuClickListener(@Nullable PageMenuClickListener pageMenuClickListener) {
        this.pageMenuClickListener = pageMenuClickListener;
    }

    @Nullable
    public final PageMenuClickListener getPageMenuClickListener() {
        return pageMenuClickListener;
    }

    public void setPageMenuInititalSelectionProvider(@Nullable PageMenuInitialSelectionProvider pageMenuInitialSelectionProvider) {
        this.pageMenuInitialSelectionProvider = pageMenuInitialSelectionProvider;
    }

    @Nullable
    public final PageMenuInitialSelectionProvider getPageMenuInitialSelectionProvider() {
        return pageMenuInitialSelectionProvider;
    }

    public void addFilter(View filterView) {
        filtersFrame.addView(filterView);
    }

    public boolean selectInitialActiveMenuItem() {
        boolean returnVal = false;
        PageMenuItem result = null;
        if (pageMenuInitialSelectionProvider != null) {
            result = pageMenuInitialSelectionProvider.getInititalSelectedPageMenuItem(menuItems);

            if (result != null) {
                result.setActive(true);
                returnVal = true;
                adapter.notifyDataSetChanged();
            }
        }

        return returnVal;
    }

    public boolean performPageMenuItemClick(AdapterView<?> parent, View view, PageMenuItem menuItem, int position, long id) throws InstantiationException, IllegalAccessException {
        boolean result = false;
        if (pageMenuClickListener != null) {
            result = pageMenuClickListener.onPageMenuClick(parent, view, menuItem, position, id); //IMPORTANT
            if (result) {
                markActiveMenuItem(menuItem);
            }
            hide();
        }
        return result;
    }

    public void markActiveMenuItem(PageMenuItem menuItem) {
        for (PageMenuItem m : menuItems) {
            m.setActive(false);
        }

        menuItem.setActive(true);

        adapter.notifyDataSetChanged();

        invalidate();
        requestLayout();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        subMenuFrame = findViewById(R.id.sub_menu_frame);
        filtersFrame = findViewById(R.id.filters_frame);
        taskLandingMenuGridView = findViewById(R.id.sub_menu_grid);
        fabFrame = findViewById(R.id.button_frame);
        fab = findViewById(R.id.sub_menu_button);

        taskLandingMenuGridView.setAdapter(adapter);
        taskLandingMenuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    performPageMenuItemClick(parent, view, menuItems.get(position), position, id);
                } catch (InstantiationException e) {
                    Log.e(TAG, e.getMessage());
                } catch (IllegalAccessException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

        setVisibility(View.VISIBLE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        disableClipOnParents(this);

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
        if (fabFrame != null) {
            this.mFabHeight = fabFrame.getHeight();
        }
        if (subMenuFrame != null) {
            this.mCapturedLayoutHeight = subMenuFrame.getHeight() + this.mFabHeight;
        }
        if (filtersFrame != null) {
            this.mCapturedLayoutHeight = this.mCapturedLayoutHeight + filtersFrame.getHeight();
        }

        this.mOpenPositionY = this.mParentHeight - this.mCapturedLayoutHeight;
        this.mClosePositionY = this.mParentHeight - this.mFabHeight;

        if (this.mMarginBottomOffsetResId > 0 && getParent() instanceof ViewGroup) {
            View v = null;

            this.mParentBottomOffset = this.mCapturedLayoutHeight - this.mFabHeight;

            if (getRootView() != null)
                v = getRootView().findViewById(mMarginBottomOffsetResId);

            if (v != null) {
                if (v.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) v.getLayoutParams();
                    param.bottomMargin = this.mParentBottomOffset;
                    v.setLayoutParams(param);
                } else if (v.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) v.getLayoutParams();
                    param.bottomMargin = this.mParentBottomOffset;
                    v.setLayoutParams(param);
                } else if (v.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) v.getLayoutParams();
                    param.bottomMargin = this.mParentBottomOffset;
                    v.setLayoutParams(param);
                } else if (v.getLayoutParams() instanceof ScrollView.LayoutParams) {
                    ScrollView.LayoutParams param = (ScrollView.LayoutParams) v.getLayoutParams();
                    param.bottomMargin = this.mParentBottomOffset;
                    v.setLayoutParams(param);
                }
            }
        }

        if (!showPageMenu()) {
            setVisibility(View.GONE);
        } else if (!isVisible() || mEarlyAction == ActionType.HIDE) {
            hide();
            mEarlyAction = null;
        } else {
            show();
            mEarlyAction = null;
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
        subMenuFrame.setVisibility(View.VISIBLE);
        filtersFrame.setVisibility(View.VISIBLE);

        if (mLastAnimation == null) {
            setY(this.mOpenPositionY);
            mLastAnimation = ActionType.SHOW;
        } else if (mLastAnimation == ActionType.HIDE) {
            setY(this.mClosePositionY);
            this.animate().y(this.mOpenPositionY).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setY(PageMenuControl.this.mOpenPositionY);
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
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);

        if (mLastAnimation == null) {
            setY(this.mClosePositionY);
            mLastAnimation = ActionType.HIDE;
            subMenuFrame.setVisibility(View.GONE);
            filtersFrame.setVisibility(View.GONE);
        } else if (mLastAnimation != null && mLastAnimation == ActionType.SHOW) {
            this.animate().y(this.mClosePositionY).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setY(PageMenuControl.this.mClosePositionY);
                    mLastAnimation = ActionType.HIDE;
                    subMenuFrame.setVisibility(View.GONE);
                    filtersFrame.setVisibility(View.GONE);
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
        return this.menuItems != null;
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
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fab.getLayoutParams();
                    params.gravity = Gravity.END;
                    fab.setLayoutParams(params);
                } else if (fab.getLayoutParams() instanceof PercentFrameLayout.LayoutParams) {
                    PercentFrameLayout.LayoutParams params = (PercentFrameLayout.LayoutParams) fab.getLayoutParams();
                    params.gravity = Gravity.END;
                    fab.setLayoutParams(params);
                }

                return true;
            }

            public boolean onSwipeLeft() {
                if (fab.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fab.getLayoutParams();
                    params.gravity = Gravity.START;
                    fab.setLayoutParams(params);
                } else if (fab.getLayoutParams() instanceof PercentFrameLayout.LayoutParams) {
                    PercentFrameLayout.LayoutParams params = (PercentFrameLayout.LayoutParams) fab.getLayoutParams();
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

    private void setFabFrameVisibility(boolean visibility) {
        if (fabFrame != null) {
            fabFrame.setVisibility(mCollapsible && visibility && showPageMenu() ? VISIBLE : GONE);
        }

        if (visibility) {
            // make sure fab is not out of screen
            if (getY() > mClosePositionY) {
                if (mLastAnimation == ActionType.SHOW) {
                    // Force re-positioning; this only happens in lists when the soft
                    // keyboard used for text filters is hidden
                    show();
                } else {
                    setY(mClosePositionY);
                }
            }
        }
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

    public interface PageMenuInitialSelectionProvider {
        PageMenuItem getInititalSelectedPageMenuItem(List<PageMenuItem> menuList);
    }

    public interface PageMenuClickListener {
        boolean onPageMenuClick(AdapterView<?> parent, View view, PageMenuItem menuItem, int position, long id) throws IllegalAccessException, InstantiationException;
    }

    public interface NotificationCountChangingListener {
        int onNotificationCountChangingAsync(AdapterView<?> parent, PageMenuItem menuItem, int position);
    }

}
