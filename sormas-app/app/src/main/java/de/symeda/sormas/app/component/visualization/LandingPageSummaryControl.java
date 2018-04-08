package de.symeda.sormas.app.component.visualization;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 26/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class LandingPageSummaryControl extends GridLayout {

    private String data;
    private GridView summaryGrid;
    private ArrayList<SummaryInfo> list;

    private LandingPageSummaryAdapter adapter;

    public LandingPageSummaryControl(Context context) {
        super(context);
        initializeViews(context, null);
    }

    public LandingPageSummaryControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs);
    }

    protected void initializeViews(Context context, AttributeSet attrs) {


        setLayoutParams(new LayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)));
        setOrientation(HORIZONTAL);
        setColumnCount(3);










        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.LandingPageSummaryControl,
                    0, 0);

            try {
                data = a.getString(R.styleable.LandingPageSummaryControl_data);
            } finally {
                a.recycle();
            }
        }

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_landing_page_summary_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        /*for (SummaryInfo summary: getSummaryData()) {
            View layout = null;
            LandingPageSummaryViewHolder viewHolder;

            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //layout = inflater.inflate(summary.getCellLayoutResource(), this, false);

            viewHolder = new LandingPageSummaryViewHolder(layout);

            viewHolder.textView.setText(summary.getTitle());

            this.addView(layout);
        }*/


        //adapter = new LandingPageSummaryAdapter(getContext(), getSummaryData());

        //this.addView();


        /*ImageView oImageView = new ImageView(this);
        oImageView.setImageResource(R.drawable.ic_launcher);

        oImageView.setLayoutParams(new LayoutParams(100, 100));

        Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
        Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, 1);
        if (r == 0 && c == 0) {
            Log.e("", "spec");
            colspan = GridLayout.spec(GridLayout.UNDEFINED, 2);
            rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
        }
        GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                rowSpan, colspan);
        gridLayout.addView(oImageView, gridParam);*/



        /*this.setAdapter(adapter);
        taskLandingMenuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), " " + position,
                        Toast.LENGTH_SHORT).show();

                //adapter.notifyDataSetChanged();
            }
        });*/
    }

    public void loadSummaryData() {
        try {
            //Set Data
            setSummaryData(data);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public void setSummaryData(String data) throws IOException, XmlPullParserException {
        if (list == null)
            list = new ArrayList<>();


        list.clear();

        AssetManager assetManager = getContext().getAssets();
        InputStream is = assetManager.open(data);
        LandingPageSummaryParser parser = new LandingPageSummaryParser();
        List<SummaryInfo> listFromXml = parser.parse(getContext(), is);

        int position = 0;
        for(SummaryInfo summary: listFromXml) {
            //Get Notification Count
            //entry.setNotificationCount(performNotificationCountChange(entry, position));

            list.add(summary);
            position = position + 1;
        }

        if (adapter != null)
            adapter.notifyDataSetChanged();

        invalidate();
        requestLayout();
    }


    private ArrayList<SummaryInfo> getSummaryData() {
        if (list == null)
            list = new ArrayList<>();

        return list;
    }
}


class LandingSummaryCellLayout {

    private String layoutName;
    private String defType;

    public LandingSummaryCellLayout(String layoutName, String defType) {
        this.layoutName = layoutName;
        this.defType = defType;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public String getDefType() {
        return defType;
    }

    public void setDefType(String defType) {
        this.defType = defType;
    }
}

class LandingSummaryCellBackground {

    private String backgroundName;
    private String defType;

    public LandingSummaryCellBackground(String backgroundName, String defType) {
        this.backgroundName = backgroundName;
        this.defType = defType;
    }

    public String getBackgroundName() {
        return backgroundName;
    }

    public void setBackgroundName(String backgroundName) {
        this.backgroundName = backgroundName;
    }

    public String getDefType() {
        return defType;
    }

    public void setDefType(String defType) {
        this.defType = defType;
    }
}

class SummaryInfo {

    private Context context;

    private String name;
    private String title;
    private String description;
    private LandingSummaryCellLayout cellLayout;
    private LandingSummaryCellBackground cellBackground;
    private double layoutWidth;
    private int layoutColumnWeight;
    private double layoutColumnHeight;
    private int columnSpan;
    private double padding;
    private double layoutMargin;

    public SummaryInfo(String name, String title, String description,
                       LandingSummaryCellLayout cellLayout, LandingSummaryCellBackground cellBackground,
                       double layoutWidth, int layoutColumnWeight, double layoutColumnHeight,
                       int columnSpan, double padding, double layoutMargin) {
        //this.context = context;

        this.name = name;
        this.title = title;
        this.description = description;
        this.cellLayout = cellLayout;
        this.cellBackground = cellBackground;
        this.layoutWidth = layoutWidth;
        this.layoutColumnWeight = layoutColumnWeight;
        this.layoutColumnHeight = layoutColumnHeight;
        this.columnSpan = columnSpan;
        this.padding = padding;
        this.layoutMargin = layoutMargin;
    }

    public SummaryInfo(String name, String title, String description,
                       LandingSummaryCellLayout cellLayout, LandingSummaryCellBackground cellBackground,
                       int layoutColumnWeight, int columnSpan) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.cellLayout = cellLayout;
        this.cellBackground = cellBackground;
        this.layoutWidth = 0;
        this.layoutColumnWeight = layoutColumnWeight;
        this.layoutColumnHeight = 146.15;
        this.columnSpan = columnSpan;
        this.padding = 0;
        this.layoutMargin = 0;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LandingSummaryCellLayout getCellLayout() {
        return cellLayout;
    }

    public void setCellLayout(LandingSummaryCellLayout cellLayout) {
        this.cellLayout = cellLayout;
    }

    public LandingSummaryCellBackground getCellBackground() {
        return cellBackground;
    }

    public void setCellBackground(LandingSummaryCellBackground cellBackground) {
        this.cellBackground = cellBackground;
    }

    public double getLayoutWidth() {
        return layoutWidth;
    }

    public void setLayoutWidth(double layoutWidth) {
        this.layoutWidth = layoutWidth;
    }

    public int getLayoutColumnWeight() {
        return layoutColumnWeight;
    }

    public void setLayoutColumnWeight(int layoutColumnWeight) {
        this.layoutColumnWeight = layoutColumnWeight;
    }

    public double getLayoutColumnHeight() {
        return layoutColumnHeight;
    }

    public void setLayoutColumnHeight(double layoutColumnHeight) {
        this.layoutColumnHeight = layoutColumnHeight;
    }

    public int getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
    }

    public double getPadding() {
        return padding;
    }

    public void setPadding(float padding) {
        this.padding = padding;
    }

    public double getLayoutMargin() {
        return layoutMargin;
    }

    public void setLayoutMargin(float layoutMargin) {
        this.layoutMargin = layoutMargin;
    }

    /*public int getCellLayoutResource() {
        if (cellLayout != null) {
            return context.getResources().getIdentifier(cellLayout.getLayoutName(), cellLayout.getDefType(), context.getPackageName());
        }

        return 0;
    }

    public int getCellBackgroundResource() {
        if (cellLayout != null) {
            return context.getResources().getIdentifier(cellBackground.getBackgroundName(), cellBackground.getDefType(), context.getPackageName());
        }

        return 0;
    }*/
}

class LandingPageSummaryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SummaryInfo> data;

    public LandingPageSummaryAdapter(Context context, ArrayList<SummaryInfo> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        if (this.data == null)
            return 0;

        return this.data.size();
    }

    @Override
    public Object getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SummaryInfo summary = this.data.get(position);

        View layout = null;



        /*if (convertView == null) {
            // if it's not recycled, initializeDialog some attributes

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //layout = inflater.inflate(summary.getCellLayoutResource(), parent, false);

            viewHolder = new LandingPageSummaryViewHolder(layout);

        } else {
            layout = (View) convertView;
            viewHolder = new LandingPageSummaryViewHolder(layout);
        }

        viewHolder.textView.setText(summary.getTitle());*/

        return layout;
    }
}


class LandingPageSummaryViewHolder {

    private View layout;
    private SummaryInfo summary;

    public TextView textView;

    public LandingPageSummaryViewHolder(View layout) {
        this.layout = layout;

        textView = (TextView)layout.findViewById(R.id.textView);
    }
}
