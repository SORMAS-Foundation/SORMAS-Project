package de.symeda.sormas.app.component.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.util.Log;
import android.view.LayoutInflater;

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.util.Callback;

public class InfoDialog extends AlertDialog.Builder {

    public static final String TAG = InfoDialog.class.getSimpleName();

    private int layoutId;
    private Object data;
    private AlertDialog dialog;
    private Callback dismissCallback;
    private ViewDataBinding binding;

    public InfoDialog(Context context, int layoutId, Object data) {
        super(context);

        this.layoutId = layoutId;
        this.data = data;
        dismissCallback = new Callback() {
            @Override
            public void call() {
                dialog.dismiss();
            }
        };

        binding = bindLayout(context);

        if (binding != null) {
            setView(binding.getRoot());
        }
    }

    @Override
    public AlertDialog show() {
        dialog = super.show();
        return dialog;
    }

    private ViewDataBinding bindLayout(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return null;
        }

        ViewDataBinding binding = DataBindingUtil.inflate(inflater, layoutId, null, false);
        String layoutName = context.getResources().getResourceEntryName(layoutId);

        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.dismissCallback, dismissCallback)) {
            Log.e(TAG, "There is no variable 'callback' in layout " + layoutName);
        }

        return binding;
    }

    public ViewDataBinding getBinding() {
        return binding;
    }

}
