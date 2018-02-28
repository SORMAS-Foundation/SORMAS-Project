package de.symeda.sormas.app.component.menu;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.NotImplementedException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;


/**
 * Created by Orson on 25/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class LandingPageMenuControl extends LinearLayout {

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
    private TextView taskLandingMenuTitle;
    private GridView taskLandingMenuGridView;

    private ILandingPageContext landingPageContext;
    private IMenuParser parser;

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

                /**/
            } finally {
                a.recycle();
            }
        }

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_landing_page_menu_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        taskLandingMenuTitle = (TextView)findViewById(R.id.taskLandingMenuTitle);
        taskLandingMenuGridView = (GridView)findViewById(R.id.taskLandingMenuGridView);

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


    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (changedView == this) {
            if (visibility == VISIBLE) {
                configureControl();
            }
        }
    }

    public void setMenuData(String data) throws IOException, XmlPullParserException, ParserConfigurationException {
        if (data != null && !data.isEmpty()) {
            dataFile = data;
        } else {
            throw new IllegalArgumentException("The dataFile file argument is empty.");
        }

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

        if (adapter == null)
            throw new IllegalArgumentException("This is no adapter configured for the menu control.");

        if (adapter instanceof IPageMenuAdapter) {
            ((IPageMenuAdapter)adapter).initialize(menuList, cellLayout,
                    positionColor, positionActiveColor, titleColor, titleActiveColor);
        } else {
            throw new NotImplementedException("Page menu adapters must implement IPageMenuAdapter");
        }

        taskLandingMenuGridView.setAdapter(adapter);
        taskLandingMenuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    performLandingPageMenuItemClick(parent, view, menuList.get(position), position, id);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
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


    private void markActiveMenuItem(LandingPageMenuItem menuItem) {
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


}
