/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.component.menu;

import java.util.List;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.core.content.ContextCompat;
import androidx.percentlayout.widget.PercentFrameLayout;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.OnSwipeTouchListener;
import de.symeda.sormas.app.util.Consumer;

public class PageMenuControl extends LinearLayout {

	public static final String TAG = PageMenuControl.class.getSimpleName();

	//    private NotificationCountChangingListener mOnNotificationCountChangingListener;
	private Consumer<PageMenuItem> pageMenuItemClickCallback;

	private PageMenuAdapter adapter;
	private List<PageMenuItem> menuItems;

	private int cellLayout;
	//    private int counterBackgroundColor;
	//    private int counterBackgroundActiveColor;

	private FrameLayout fabFrame;
	private FloatingActionButton fab;
	private LinearLayout subMenuFrame;
	private LinearLayout filtersFrame;

	private boolean visible;
	private boolean collapsible;

	private int marginBottomOffsetResId = -1;
	private int capturedLayoutHeight = 0;
	private int fabHeight = 0;
	private int parentHeight = 0;
	private int openPositionY = 0;
	private int closePositionY = 0;

	private ActionType earlyAction;
	private ActionType lastAnimation;

	public PageMenuControl(Context context) {
		super(context);
		initializeViews(context, null);
	}

	public PageMenuControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeViews(context, attrs);
	}

	protected void initializeViews(Context context, AttributeSet attrs) {
		int iconColor = 0, iconActiveColor = 0, titleColor = 0, titleActiveColor = 0;

		if (attrs != null) {
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PageMenuControl, 0, 0);

			try {
				cellLayout = a.getResourceId(R.styleable.PageMenuControl_cellLayout, 0);

//                counterBackgroundColor = a.getResourceId(R.styleable.PageMenuControl_counterBackgroundColor, 0);
//                counterBackgroundActiveColor = a.getResourceId(R.styleable.PageMenuControl_counterBackgroundActiveColor, 0);
				iconColor = a.getResourceId(R.styleable.PageMenuControl_iconColor, 0);
				iconActiveColor = a.getResourceId(R.styleable.PageMenuControl_iconActiveColor, 0);
				titleColor = a.getResourceId(R.styleable.PageMenuControl_titleColor, 0);
				titleActiveColor = a.getResourceId(R.styleable.PageMenuControl_titleActiveColor, 0);
				visible = a.getBoolean(R.styleable.PageMenuControl_visibility, false);
				collapsible = a.getBoolean(R.styleable.PageMenuControl_collapsible, false);
				marginBottomOffsetResId = a.getResourceId(R.styleable.PageMenuControl_marginBottomOffsetResId, -1);
			} finally {
				a.recycle();
			}
		}

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.sub_menu_layout, this);

		adapter = new PageMenuAdapter(context, cellLayout, iconColor, iconActiveColor, titleColor, titleActiveColor);
	}

	public <T extends Enum> void setMenuData(List<PageMenuItem> menuItems) {
		this.menuItems = menuItems;

		if (!showPageMenu()) {
			setFabFrameVisibility(false);
			return;
		}
		setFabFrameVisibility(true);

		adapter.setData(this.menuItems);

//        updateNotificationCount();

		invalidate();
		requestLayout();
	}

//    private void updateNotificationCount() {
//        int position = 0;
//        for (final PageMenuItem menuItem : menuItems) {
//            int result = performNotificationCountChange(menuItem, position);
//            menuItem.setNotificationCount(result);
//            position = position + 1;
//        }
//        adapter.notifyDataSetChanged();
//    }
//
//    public void setOnNotificationCountChangingListener(@Nullable NotificationCountChangingListener listener) {
//        mOnNotificationCountChangingListener = listener;
//    }
//
//    @Nullable
//    public final NotificationCountChangingListener getOnNotificationCountChangingListener() {
//        return mOnNotificationCountChangingListener;
//    }
//
//    public int performNotificationCountChange(PageMenuItem menuItem, int position) {
//        int result = 0;
//        if (mOnNotificationCountChangingListener != null) {
//            result = mOnNotificationCountChangingListener.onNotificationCountChangingAsync(taskLandingMenuGridView, menuItem, position);
//        }
//
//        return result;
//    }

	public void setPageMenuItemClickCallback(Consumer<PageMenuItem> pageMenuItemClickCallback) {
		this.pageMenuItemClickCallback = pageMenuItemClickCallback;
	}

	public void addFilter(View filterView) {
		filtersFrame.addView(filterView);
	}

	public void markActiveMenuItem(PageMenuItem menuItem) {
		for (PageMenuItem m : menuItems) {
			if (m != null) {
				m.setActive(false);
			}
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
		fabFrame = findViewById(R.id.button_frame);
		fab = findViewById(R.id.sub_menu_button);

		GridView taskLandingMenuGridView = findViewById(R.id.sub_menu_grid);
		taskLandingMenuGridView.setAdapter(adapter);
		taskLandingMenuGridView.setOnItemClickListener((parent, view, position, id) -> {
			if (pageMenuItemClickCallback != null) {
				pageMenuItemClickCallback.accept(menuItems.get(position));
				markActiveMenuItem(menuItems.get(position));
				hide();
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
		parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (fabFrame != null) {
			this.fabHeight = fabFrame.getHeight();
		}
		if (subMenuFrame != null) {
			this.capturedLayoutHeight = subMenuFrame.getHeight() + this.fabHeight;
		}
		if (filtersFrame != null) {
			this.capturedLayoutHeight = this.capturedLayoutHeight + filtersFrame.getHeight();
		}

		this.openPositionY = this.parentHeight - this.capturedLayoutHeight;
		this.closePositionY = this.parentHeight - this.fabHeight;

		if (this.marginBottomOffsetResId > 0 && getParent() instanceof ViewGroup) {
			View v = null;

			int parentBottomOffset = this.capturedLayoutHeight - this.fabHeight;

			if (getRootView() != null)
				v = getRootView().findViewById(marginBottomOffsetResId);

			if (v != null) {
				if (v.getLayoutParams() instanceof LinearLayout.LayoutParams) {
					LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) v.getLayoutParams();
					param.bottomMargin = parentBottomOffset;
					v.setLayoutParams(param);
				} else if (v.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
					RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) v.getLayoutParams();
					param.bottomMargin = parentBottomOffset;
					v.setLayoutParams(param);
				} else if (v.getLayoutParams() instanceof FrameLayout.LayoutParams) {
					FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) v.getLayoutParams();
					param.bottomMargin = parentBottomOffset;
					v.setLayoutParams(param);
				} else if (v.getLayoutParams() instanceof ScrollView.LayoutParams) {
					ScrollView.LayoutParams param = (ScrollView.LayoutParams) v.getLayoutParams();
					param.bottomMargin = parentBottomOffset;
					v.setLayoutParams(param);
				}
			}
		}

		if (!showPageMenu()) {
			setVisibility(View.GONE);
		} else if (!isVisible() || earlyAction == ActionType.HIDE) {
			hide();
			earlyAction = null;
		} else {
			show();
			earlyAction = null;
		}

		updateFabDrawable();

		canvas.drawARGB(0, 225, 225, 255);
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SCREEN);
	}

	public void show() {
		if (this.capturedLayoutHeight <= 0) {
			earlyAction = ActionType.SHOW;
			return;
		}

		if (!showPageMenu())
			return;

		setVisibility(View.VISIBLE);
		subMenuFrame.setVisibility(View.VISIBLE);
		filtersFrame.setVisibility(View.VISIBLE);

		if (lastAnimation == null) {
			setY(this.openPositionY);
			lastAnimation = ActionType.SHOW;
		} else if (lastAnimation == ActionType.HIDE) {
			setY(this.closePositionY);
			this.animate().y(this.openPositionY).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					setY(PageMenuControl.this.openPositionY);
					lastAnimation = ActionType.SHOW;
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}
			}).start();
		}

		this.visible = true;
		updateFabDrawable();
	}

	public void showFab() {
		setFabFrameVisibility(true);
	}

	public void hide() {
		if (this.capturedLayoutHeight <= 0) {
			earlyAction = ActionType.HIDE;
			return;
		}

		if (!collapsible)
			return;

		if (!showPageMenu()) {
			setVisibility(View.GONE);
			return;
		}
		setVisibility(View.VISIBLE);

		if (lastAnimation == null) {
			setY(this.closePositionY);
			lastAnimation = ActionType.HIDE;
			subMenuFrame.setVisibility(View.GONE);
			filtersFrame.setVisibility(View.GONE);
		} else if (lastAnimation == ActionType.SHOW) {
			this.animate().y(this.closePositionY).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					setY(PageMenuControl.this.closePositionY);
					lastAnimation = ActionType.HIDE;
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

		this.visible = false;
		updateFabDrawable();
	}

	public void hideAll() {
		hide();
		setFabFrameVisibility(false);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
		setFabFrameVisibility(true);
		//configureFab();
	}

	private boolean showPageMenu() {
		return this.menuItems != null;
	}

	private void configureFab() {
		if (fab == null) {
			return;
		}

		fab.setOnClickListener(view -> {
			if (isVisible()) {
				hide();
			} else {
				show();
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
			Drawable drw = ContextCompat.getDrawable(fab.getContext(), R.drawable.ic_landing_menu_close_black_24dp);
			drw.setTint(fab.getContext().getResources().getColor(R.color.fabIcon));
			fab.setImageDrawable(drw);
		} else {
			Drawable drw = ContextCompat.getDrawable(fab.getContext(), R.drawable.ic_landing_menu_open_black_24dp);
			drw.setTint(fab.getContext().getResources().getColor(R.color.fabIcon));
			fab.setImageDrawable(drw);
		}
	}

	private void setFabFrameVisibility(boolean visibility) {
		if (fabFrame != null) {
			fabFrame.setVisibility(collapsible && visibility && showPageMenu() ? VISIBLE : GONE);
		}

		if (visibility) {
			// make sure fab is not out of screen
			if (getY() > closePositionY) {
				if (lastAnimation == ActionType.SHOW) {
					// Force re-positioning; this only happens in lists when the soft
					// keyboard used for text filters is hidden
					show();
				} else {
					setY(closePositionY);
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

	public interface NotificationCountChangingListener {
//        int onNotificationCountChangingAsync(AdapterView<?> parent, PageMenuItem menuItem, int position);
	}

	private enum ActionType {
		SHOW,
		HIDE;
	}
}
