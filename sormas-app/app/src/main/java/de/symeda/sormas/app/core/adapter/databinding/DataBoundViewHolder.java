package de.symeda.sormas.app.core.adapter.databinding;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BR;

/**
 * Created by Orson on 27/12/2017.
 */

public class DataBoundViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder implements ISetOnListItemClickListener, View.OnClickListener {
    public final T binding;
    public final View layout;
    public final Context context;
    private OnListItemClickListener mOnListItemClickListener;
    private Object data;
    private OnListItemClickListener callback;

    public DataBoundViewHolder(T binding, View layout) {
        super(binding.getRoot());
        this.binding = binding;
        this.layout = layout;
        this.context = layout.getContext();

        itemView.setOnClickListener(this);
    }

    /**
     * Creates a new ViewHolder for the given layout file.
     * <p>
     * The provided layout must be using data binding.
     *
     * @param parent The RecyclerView
     * @param layoutId The layout id that should be inflated. Must use data binding
     * @param <T> The type of the Binding class that will be generated for the <code>layoutId</code>.
     * @return A new ViewHolder that has a reference to the binding class
     */
    public static <T extends ViewDataBinding> DataBoundViewHolder<T> create(ViewGroup parent,
                                                                            @LayoutRes int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layoutView = inflater.inflate(layoutId, parent, false);



        T binding = DataBindingUtil.inflate(inflater, layoutId, parent, false);
        return new DataBoundViewHolder<>(binding, layoutView);
    }

    @Override
    public void onClick(View v) {
        if (this.mOnListItemClickListener != null) {
            int p = getLayoutPosition();
            this.mOnListItemClickListener.onListItemClick(v, p, this.data);
        }
    }

    @Override
    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.mOnListItemClickListener = onListItemClickListener;
    }

    public void setData(Object data) {
        this.data = data;
        this.binding.setVariable(BR.data, data);
    }

    /*@Override
    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.mOnListItemClickListener = onListItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (this.mOnListItemClickListener != null) {
            int p = getLayoutPosition();
            this.mOnListItemClickListener.onListItemClick(v, p, this.data);
        }
    }



    public void setCallback(OnListItemClickListener callback) {
        this.callback = callback;
        this.binding.setVariable(BR.callback, callback);
    }*/
}