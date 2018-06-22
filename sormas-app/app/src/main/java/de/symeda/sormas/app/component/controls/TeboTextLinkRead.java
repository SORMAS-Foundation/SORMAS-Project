package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 31/12/2017.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboTextLinkRead extends TeboTextRead {

    private OnClickListener onClickListener;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboTextLinkRead(Context context) {
        super(context);
    }

    public TeboTextLinkRead(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboTextLinkRead(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // </editor-fold>

    @Override
    protected void initializeView(Context context, AttributeSet attrs, int defStyle) {
        super.initializeView(context, attrs, defStyle);

    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_link_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        txtControlInput.setPaintFlags(txtControlInput.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        txtControlInput.setImeOptions(getImeOptions());

        txtControlInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null)
                    onClickListener.onClick(v);
            }
        });
    }

    @BindingAdapter("onLinkClick")
    public static void setLinkClickListener(TeboTextLinkRead view, OnClickListener listener) {
        if (listener != null)
            view.setOnClickListener(listener);
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }
}
