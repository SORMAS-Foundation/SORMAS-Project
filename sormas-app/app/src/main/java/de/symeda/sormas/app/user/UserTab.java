package de.symeda.sormas.app.user;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDao;
import de.symeda.sormas.app.databinding.UserFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 *
 * ATTENTION: This is currently not used the way it is meant to be...
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

        getModel().put(R.id.form_u_select_user, ConfigProvider.getUser());
        addUserSpinnerField(R.id.form_u_select_user, Arrays.asList(UserRole.INFORMANT, UserRole.SURVEILLANCE_OFFICER));
    }

    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        return ado;
    }

    @Override
    public User getData() {
        return (User)commit((AbstractDomainObject) getModel().get(R.id.form_u_select_user));
    }

}