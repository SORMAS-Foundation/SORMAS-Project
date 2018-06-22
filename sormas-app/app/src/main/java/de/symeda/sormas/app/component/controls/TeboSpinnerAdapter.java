package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.Item;

/**
 * Created by Orson on 30/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboSpinnerAdapter extends ArrayAdapter<Item> {

    private TeboSpinner spinner;
    private List<Item> lstData;
    private LayoutInflater inflater;
    private int resource;
    private int mDropDownResource;
/*
    private int textViewResourceId;
    private List<Item> objects;
    private Context context;*/

    public TeboSpinnerAdapter(TeboSpinner spinner, @NonNull Context context, int resource, int textViewResourceId, @NonNull List<Item> objects) {
        super(context, resource, textViewResourceId, objects);

        this.resource = this.mDropDownResource = resource;
        this.spinner = spinner;
        this.lstData = objects;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lstData.size();
    }

    @Override
    public Item getItem(int position) {
        return lstData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        final TextView tv;

        if (convertView == null) {
            view = inflater.inflate(this.resource, parent, false);
        } else {
            view = convertView;
        }


        final Item item = getItem(position);
        tv = (TextView) view.findViewById(R.id.text);

        if (item instanceof CharSequence) {
            tv.setText((CharSequence) item);
        } else {
            tv.setText(item.toString());
        }

        if (tv != null && (tv.getText() == null || tv.getText().length() == 0)) {
            tv.setHint(getContext().getResources().getString(R.string.hint_select_entry));
            tv.setText(getContext().getResources().getString(R.string.hint_select_entry));
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final View view;
        final TextView tv;

        if (lstData.size() <= 0)
            return null;

        if (convertView == null) {
            view = inflater.inflate(this.mDropDownResource, parent, false);
        } else {
            view = convertView;
        }

        final Item item = getItem(position);
        tv = (TextView) view.findViewById(R.id.text);

        if (item instanceof CharSequence) {
            tv.setText((CharSequence) item);
        } else {
            tv.setText(item.toString());
        }


        if (tv != null && (tv.getText() == null || tv.getText().length() == 0)) {
            tv.setText(getContext().getResources().getString(R.string.hint_select_entry_leave_blank));
        }

        if (position == 0)
            tv.setTypeface(null, Typeface.BOLD_ITALIC);
        else
            tv.setTypeface(null, Typeface.NORMAL);

        int selectedPosition = spinner.getPositionOf(spinner.getSelectedItem());
        if (selectedPosition < 0)
            return view;

        if (position == selectedPosition) {
            tv.setTextColor(getContext().getResources().getColor(R.color.spinnerDropdownItemTextActive));
            view.setBackgroundColor(getContext().getResources().getColor(R.color.spinnerDropdownItemBackgroundActive));
        } else {
            tv.setTextColor(getContext().getResources().getColor(R.color.controlTextColor));
            view.setBackground(getContext().getResources().getDrawable(R.drawable.background_spinner_item));
        }

        return view;
    }

    public void setDropDownViewResource(@LayoutRes int resource) {
        this.mDropDownResource = resource;
    }
}
