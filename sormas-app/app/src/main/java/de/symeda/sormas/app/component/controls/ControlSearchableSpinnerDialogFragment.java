package de.symeda.sormas.app.component.controls;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import de.symeda.sormas.app.R;

public class ControlSearchableSpinnerDialogFragment extends DialogFragment {

    // Views

    protected ControlTextEditFieldWithClear txtSearch;
    protected ListView listView;
    protected Button btnCancel;

    // Other fields

    protected AdapterView.OnItemSelectedListener itemSelectedListener;
    protected ControlSearchableSpinnerAdapter adapter;
    protected int selectedPosition = -1;

    static ControlSearchableSpinnerDialogFragment newInstance() {
        ControlSearchableSpinnerDialogFragment fragment = new ControlSearchableSpinnerDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_AppCompat_Light_Dialog_Alert);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.control_searchable_spinner_dialog_fragment_layout, container, false);
        txtSearch = v.findViewById(R.id.txt_search);
        btnCancel = v.findViewById(R.id.btn_cancel);
        listView = v.findViewById(R.id.lvItems);

        txtSearch.setAsListSearch();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);

        txtSearch.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                adapter.getFilter().filter(txtSearch.getValue());
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.e("TEST", "posotion: " + position + ", l: " + l);
                setSelection(position);
                itemSelectedListener.onItemSelected(adapterView, view, position, l);

                //reset dialog
                txtSearch.setFieldValue("");
                selectedPosition = -1;
                adapter.resetList();

                //close dialog
                if (txtSearch.getInput().isFocused())
                    txtSearch.clearFocus();
                ControlSearchableSpinnerDialogFragment.this.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reset dialog
                txtSearch.setFieldValue("");
                selectedPosition = -1;
                adapter.resetList();

                //close dialog
                if (txtSearch.getInput().isFocused())
                    txtSearch.clearFocus();
                ControlSearchableSpinnerDialogFragment.this.dismiss();
            }
        });
        if (selectedPosition > -1)
            adapter.setSelection(selectedPosition);

        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return v;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public Object getSelectedItem() {
        if (adapter != null) {
            return adapter.getSelectedItem();
        } else {
            return null;
        }
    }

    public void setSelection(int position) {
            selectedPosition = position;
        if (adapter != null)
            adapter.setSelection(position);
    }

    public void setAdapter(ControlSearchableSpinnerAdapter adapter) {
        if (this.listView != null) {
            listView.setAdapter(adapter);
        }
        this.adapter = adapter;
    }

    public ControlSearchableSpinnerAdapter getAdapter() {
        return adapter;
    }
}
