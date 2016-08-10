package de.symeda.sormas.app.user;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.user.UserDao;
import de.symeda.sormas.app.databinding.UserFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class UserTab extends FormTab {

    private UserFragmentLayoutBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initModel();
        binding = DataBindingUtil.inflate(inflater, R.layout.user_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();


        addUserSpinnerField(R.id.form_u_select_user);


    }

    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        return ado;
    }

    @Override
    public AbstractDomainObject getData() {
        return commit(binding.getCaze());
    }

}