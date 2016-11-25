package de.symeda.sormas.app.user;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.databinding.UserFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 *
 * ATTENTION: This is currently not used the way it is meant to be...
 */
public class UserTab extends FormTab {

    // TODO create settings POJO?!
    private UserFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initModel();

        binding = DataBindingUtil.inflate(inflater, R.layout.user_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {

        getModel().put(R.id.form_u_select_user, ConfigProvider.getUser());

        super.onResume();

        addUserSpinnerField(R.id.form_u_select_user, Arrays.asList(UserRole.INFORMANT, UserRole.SURVEILLANCE_OFFICER, UserRole.CASE_OFFICER, UserRole.CONTACT_OFFICER));

        // TODO move to settings screen?
        TextView serverUrl = (TextView) getView().findViewById(R.id.form_server_url);
        serverUrl.setText((String)ConfigProvider.getServerRestUrl());
    }

    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        return ado;
    }

    public User getUser() {
        return (User)getModel().get(R.id.form_u_select_user);
    }

    public String getServerUrl() {
        TextView serverUrl = (TextView) getView().findViewById(R.id.form_server_url);
        return serverUrl.getText().toString();
    }

    @Override
    public AbstractDomainObject getData() {
        return null;
    }
}