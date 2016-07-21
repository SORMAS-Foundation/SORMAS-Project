package de.symeda.sormas.app;

import android.app.Activity;
import android.content.Context;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public abstract class SormasAppView<A extends OrmLiteBaseActivity> {

    private int viewId;
    private A context;

    public SormasAppView(A context, int viewId) {
        this.context = context;
        this.viewId = viewId;
        init();
        show();
    }

    protected abstract void init();
    protected abstract void show();

    public A getContext() {
        return this.context;
    }

    public int getViewId() {
        return this.viewId;
    }


}
