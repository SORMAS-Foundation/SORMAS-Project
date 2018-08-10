package de.symeda.sormas.app.component.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;

public class SimpleDialog extends AlertDialog.Builder implements IEntryItemOnClickListener {

    public static final String TAG = SimpleDialog.class.getSimpleName();

    private int layoutId;
    private Object data;
    private AlertDialog dialog;
    private IEntryItemOnClickListener onDismissClickListener;

    public SimpleDialog(Context context, int layoutId, Object data) {
        super(context);

        this.layoutId = layoutId;
        this.data = data;

        setOnDismissClickListener(this);

        ViewDataBinding binding = bindLayout(context);

        if (binding != null) {
            setView(binding.getRoot());
        }
    }

    @Override
    public void onClick(View v, Object item) {
        dialog.dismiss();
    }

    @Override
    public AlertDialog show() {
        dialog = super.show();
        return dialog;
    }

    private void setOnDismissClickListener(IEntryItemOnClickListener listener) {
        if (listener != null) {
            this.onDismissClickListener = listener;
        }
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

        if (!binding.setVariable(BR.dismissCallback, onDismissClickListener)) {
            Log.e(TAG, "There is no variable 'callback' in layout " + layoutName);
        }

        return binding;
    }

}
