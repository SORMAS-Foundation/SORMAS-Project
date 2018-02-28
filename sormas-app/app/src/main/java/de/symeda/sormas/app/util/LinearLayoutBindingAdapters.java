package de.symeda.sormas.app.util;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.R;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;

/**
 * Created by Orson on 12/01/2018.
 */

public class LinearLayoutBindingAdapters {

    public static final String TAG = LinearLayoutBindingAdapters.class.getSimpleName();


    @BindingAdapter(value={"burialInfoList", "layout"})
    public static void setBurialInfoList(LinearLayout listView, EpiDataBurial burial, int layout) {
        List<String> infoList = new ArrayList<>();

        if (burial == null) {
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);

            //burial.setBurialIll(YesNoUnknown.YES);
            //burial.setBurialTouching(YesNoUnknown.YES);

            bindViews(listView, layout, burial);

            /*if (burial.getBurialIll() == YesNoUnknown.YES) {
                infoList.add(resources.getString(R.string.label_list_item_person_ill));
            }

            if (burial.getBurialTouching() == YesNoUnknown.YES) {
                infoList.add(resources.getString(R.string.label_list_item_touched_body));
            }


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(listView.getContext(),
                    R.layout.row_bullet_string_list_item_layout, infoList);

            listView.setAdapter(adapter);*/
        }
    }

    private static void bindViews(ViewGroup parent, int layoutId, EpiDataBurial burial) {
        Resources resources = parent.getContext().getResources();

        parent.removeAllViews();
        if (layoutId == 0) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (burial.getBurialIll() == YesNoUnknown.YES) {
            String entry = resources.getString(R.string.label_list_item_person_ill);
            ViewDataBinding binding = bindLayout(inflater, parent, layoutId, entry);
            parent.addView(binding.getRoot());
        }

        if (burial.getBurialTouching() == YesNoUnknown.YES) {
            String entry = resources.getString(R.string.label_list_item_touched_body);
            ViewDataBinding binding = bindLayout(inflater, parent, layoutId, entry);
            parent.addView(binding.getRoot());
        }

    }


    private static ViewDataBinding bindLayout(LayoutInflater inflater,
                                              ViewGroup parent, int layoutId, Object entry) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater,
                layoutId, parent, false);
        if (!binding.setVariable(BR.data, entry)) {
            String layoutName = parent.getResources().getResourceEntryName(layoutId);
            Log.w(TAG, "There is no variable 'data' in layout " + layoutName);
        }
        return binding;
    }
}
