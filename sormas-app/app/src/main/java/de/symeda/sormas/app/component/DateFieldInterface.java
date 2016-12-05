package de.symeda.sormas.app.component;

import android.view.View.*;

/**
 * Created by Mate Strysewske on 30.11.2016.
 */

public interface DateFieldInterface {

    public void setInputType(int type);
    public void setOnClickListener(OnClickListener listener);
    public void setOnFocusChangeListener(OnFocusChangeListener listener);

}
