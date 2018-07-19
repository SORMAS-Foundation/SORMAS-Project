package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Random;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;

public class ControlSpinnerAdapter extends ArrayAdapter<Item> {

    // Spinner fields

    private ControlSpinnerField spinner;
    private List<Item> spinnerData;

    // Resources

    private LayoutInflater inflater;
    private int layoutResourceId;
    private int dropdownResourceId;
    private int textViewResourceId;

    // Constructor

    ControlSpinnerAdapter(@NonNull Context context, ControlSpinnerField spinner, @NonNull List<Item> objects,
                          int layoutResourceId, int dropdownResourceId, int textViewResourceId) {
        super(context, layoutResourceId, textViewResourceId, objects);

        this.spinner = spinner;
        this.spinnerData = objects;
        this.layoutResourceId = layoutResourceId;
        this.dropdownResourceId = dropdownResourceId;
        this.textViewResourceId = textViewResourceId;
        this.inflater = LayoutInflater.from(context);
    }

    // Overrides

    @Override
    public int getCount() {
        return spinnerData.size();
    }

    @Override
    public Item getItem(int position) {
        return spinnerData.get(position);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View view;
        final TextView textView;

        if (convertView == null) {
            view = inflater.inflate(this.layoutResourceId, parent, false);
        } else {
            view = convertView;
        }

        final Item item = getItem(position);
        textView = (TextView) view.findViewById(textViewResourceId);

        if (item != null) {
            textView.setText(item.toString());
        }

        if (textView != null && (StringUtils.isEmpty(textView.getText()))) {
            if (spinner.getHint() != null) {
                textView.setHint(spinner.getHint());
            } else {
                textView.setHint(getContext().getResources().getString(R.string.hint_select_entry));
            }
            textView.setTextColor(getContext().getResources().getColor(R.color.hintText));
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
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
                textView.setText(getContext().getResources().getString(R.string.hint_select_entry_leave_blank));

                if (position == 0) {
                    textView.setTypeface(null, Typeface.BOLD_ITALIC);
                }
            } else {
                textView.setTypeface(null, Typeface.NORMAL);
            }

            int selectedPosition = spinner.getPositionOf(spinner.getSelectedItem());
            if (selectedPosition >= 0) {
                if (position == selectedPosition) {
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

}
