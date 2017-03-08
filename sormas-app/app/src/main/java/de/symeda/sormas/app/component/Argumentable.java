package de.symeda.sormas.app.component;

import android.os.Bundle;

/**
 * Created by Stefan Szczesny on 07.03.2017.
 */

public interface Argumentable {
    void setArguments(Bundle bundle);
    Bundle getArguments();
}
