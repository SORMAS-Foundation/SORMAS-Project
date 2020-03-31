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

package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.Item;

public class ControlSearchableSpinnerAdapter extends ArrayAdapter<Item> implements Filterable {

    // Spinner fields

    private ControlSearchableSpinnerField spinner;
    private List<Item> spinnerData;
    private boolean excludeEmptyItem;
    List<Item> mOriginalValues;
    private Filter mFilter;
    private final Object lock = new Object();
    Item selectedItem=null;

    // Resources

    private LayoutInflater inflater;
    private int layoutResourceId;
    private int dropdownResourceId;
    private int textViewResourceId;

    // Constructor

    ControlSearchableSpinnerAdapter(@NonNull Context context, ControlSearchableSpinnerField spinner, @NonNull List<Item> objects,
                                    int layoutResourceId, int dropdownResourceId, int textViewResourceId, boolean excludeEmptyItem) {
        super(context, layoutResourceId, textViewResourceId, objects);

        this.spinner = spinner;
        this.spinnerData = objects;
        this.layoutResourceId = layoutResourceId;
        this.dropdownResourceId = dropdownResourceId;
        this.textViewResourceId = textViewResourceId;
        this.inflater = LayoutInflater.from(context);
        this.excludeEmptyItem = excludeEmptyItem;
    }

    // Overrides

    @Override
    public int getCount() {
        return spinnerData.size();
    }

    @Override
    public Item getItem(int position) {
        if(position==-1)
            return null;
        return spinnerData.get(position);
    }

    public void setSelection(int position){
//        if(spinnerData.size()<position)
            selectedItem=position!=-1?spinnerData.get(position): null;
    }

    public Item getSelectedItem(){
        return selectedItem;
    }



    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View view;
        final TextView textView;

        if (spinnerData.size() <= 0) {
            return null;
        }

        if (convertView == null) {
            view = inflater.inflate(this.dropdownResourceId, parent, false);
        } else {
            view = convertView;
        }

        final Item item = getItem(position);
        textView = (TextView) view.findViewById(textViewResourceId);

        if (item != null) {
            textView.setText(item.toString());
        }

        if (textView != null) {
            if (StringUtils.isEmpty(textView.getText())) {
                textView.setText(getContext().getResources().getString(R.string.hint_clear));
            }

            if (position == 0 && !excludeEmptyItem) {
                textView.setTypeface(null, Typeface.BOLD_ITALIC);
            } else {
                textView.setTypeface(null, Typeface.NORMAL);
            }

//            int selectedPosition = spinner.getPositionOf(spinner.getSelectedItem());
            if (selectedItem !=null) {
                if (selectedItem.equals(item)) {
                    textView.setTextColor(getContext().getResources().getColor(R.color.spinnerDropdownItemTextActive));
                    view.setBackgroundColor(getContext().getResources().getColor(R.color.spinnerDropdownItemBackgroundActive));
                } else {
                    textView.setTextColor(getContext().getResources().getColor(R.color.controlTextColor));
                    view.setBackground(getContext().getResources().getDrawable(R.drawable.background_spinner_item));
                }
            }
        }

        return view;
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = new ArrayList<Item>(spinnerData);
                }
            }


            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<Item> list = new ArrayList(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                List<Item> values = mOriginalValues;
                int count = values.size();

                ArrayList<Item> newValues = new ArrayList(count);

                for (int i = 0; i < count; i++) {
                    Item item = values.get(i);

                    String[] words = item.toString().toLowerCase().split(" ");
                    int wordCount = words.length;

                    for (int k = 0; k < wordCount; k++) {
                        final String word = words[k];

                        if (word.startsWith(prefixString)) {
                            newValues.add(item);
                            break;
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            spinnerData = (List<Item>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }



}
