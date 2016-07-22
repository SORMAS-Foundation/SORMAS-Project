package de.symeda.sormas.app;

import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public abstract class SormasAppView<A extends OrmLiteBaseActivity> {

    private A context;

    public SormasAppView(A context) {
        this.context = context;
        init();
    }

    protected abstract void init();

    protected abstract String getViewName();

    protected void show() {
        TextView t = (TextView) getContext().findViewById(R.id.view_header_label);
        t.setText(getViewName());
    }

    public A getContext() {
        return this.context;
    }
}
