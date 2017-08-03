package de.symeda.sormas.app.component;

import android.content.Context;
import android.database.DataSetObserver;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.util.Consumer;

public class ListField<FieldClass extends AbstractDomainObject> extends PropertyField<List<FieldClass>> {

    protected ImageButton addBtn;
    protected ListView listView;
    protected ArrayAdapter adapter;

    protected InverseBindingListener inverseBindingListener;
    protected Consumer itemEditAction;
    protected Consumer itemAddAction;

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
        this.adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
        });

        this.itemEditAction = itemAction;
        this.itemAddAction = itemAction;
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

        caption = (TextView) this.findViewById(R.id.text_caption);
        caption.setText(getCaption());
        addCaptionHintIfDescription();
        addCaptionOnClickListener();

        addBtn = (ImageButton) this.findViewById(R.id.add_btn);
//        addBtn.setText(getResources().getString(R.string.action_add));
//        addBtn.setBackgroundResource(R.drawable.ic_add_box_black_36dp);
        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                itemEditAction.accept(null);
            }

        });

        listView = (ListView) this.findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                itemEditAction.accept((FieldClass)getListAdapter().getItem(position));
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

    /**
     * reset the height of the ListView from given values in the array adapter
     * @param listView
     */
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

    /**
     * Replace or add the given ado to the given list.
     * Replaced when the uuids are equal.
     * @param values
     * @param insertValue
     * @return
     */
    public static List<AbstractDomainObject> updateList(List<AbstractDomainObject> values, AbstractDomainObject insertValue) {
        boolean insertNewValue = true;
        if(!values.isEmpty()) {
            int i = 0;
            for (AbstractDomainObject value: values) {
                if(value.getUuid().equals(insertValue.getUuid())) {
                    values.set(i,insertValue);
                    insertNewValue = false;
                    break;
                }
                i++;
            }
        }
        if(insertNewValue) {
            values.add(insertValue);
        }
        return values;
    }

    public static List<AbstractDomainObject> removeFromList(List<AbstractDomainObject> values, AbstractDomainObject deleteValue) {
        if(values!=null && !values.isEmpty() && deleteValue != null) {
            int i = 0;
            for (AbstractDomainObject value: values) {
                if(value.getUuid().equals(deleteValue.getUuid())) {
                    values.remove(i);
                    break;
                }
                i++;
            }
        }
        return values;
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((ListField) nextView).listView.requestFocus();
    }

}
