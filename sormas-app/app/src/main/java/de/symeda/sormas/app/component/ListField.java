package de.symeda.sormas.app.component;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.util.Consumer;

@InverseBindingMethods({
        @InverseBindingMethod(type = ListField.class, attribute = "List")
})
public class ListField<FieldClass extends AbstractDomainObject> extends PropertyField<List<FieldClass>> {

    public static String ITEM_UUID = "itemUuid";

    protected ListView listView;
    protected ArrayAdapter adapter;

    protected InverseBindingListener inverseBindingListener;
    protected Consumer itemAction;

    public ListField(Context context) {
        super(context);
        initializeViews(context);
    }

    public ListField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ListField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    @Override
    public void setValue(List<FieldClass> values) {
        ArrayAdapter<FieldClass> listAdapter = (ArrayAdapter<FieldClass>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(values);
        listView.setAdapter(listAdapter);
        setListViewHeightBasedOnChildren(listView);
    }

    @Override
    public List<FieldClass> getValue() {
        List<FieldClass> items = new ArrayList<FieldClass>();
        for (int i = 0; i < getListAdapter().getCount(); i++) {
            items.add((FieldClass)getListAdapter().getItem(i));
        }
        return items;
    }

    @BindingAdapter("android:value")
    public static void setValue(ListField view, List<?> list) {
        view.setValue(list);
    }

    @InverseBindingAdapter(attribute = "android:value", event = "android:valueAttrChanged" /*default - can also be removed*/)
    public static List<?> getValue(ListField view) {
        return view.getValue();
    }

    @BindingAdapter("android:valueAttrChanged")
    public static void setListener(ListField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    public void initialize(final ArrayAdapter<FieldClass> adapter, Consumer itemAction) {
        this.adapter = adapter;
        this.itemAction = itemAction;
    }

    public void updateCaption(String newCaption) {
        caption.setText(newCaption);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.field_list_field, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        listView = (ListView) this.findViewById(R.id.list);
        caption = (TextView) this.findViewById(R.id.text_caption);
        caption.setText(getCaption());
        addCaptionHintIfDescription();
        addCaptionOnClickListener();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                itemAction.accept((FieldClass)getListAdapter().getItem(position));
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        listView.setEnabled(enabled);
        caption.setEnabled(enabled);
    }

    public ArrayAdapter getListAdapter() {
        return adapter;
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listView.getCount(); i++) {
            View childView = listView.getAdapter().getView(i, null, listView);
            childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight+= childView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * listAdapter.getCount());
        listView.setLayoutParams(params);
        listView.requestLayout();
    }



}
