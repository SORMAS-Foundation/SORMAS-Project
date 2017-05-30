package de.symeda.sormas.app.user;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDao;
import de.symeda.sormas.app.databinding.UserFragmentLayoutBinding;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.util.SyncInfrastructureTask;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 *
 * ATTENTION: This is currently not used the way it is meant to be...
 */
public class UserForm extends FormTab {

    // TODO create settings POJO?!
    private UserFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.user_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

//        User user = ConfigProvider.getUser();
//
//        List<UserRole> userRoles = Arrays.asList(UserRole.INFORMANT, UserRole.SURVEILLANCE_OFFICER, UserRole.CASE_OFFICER, UserRole.CONTACT_OFFICER);
//        UserDao userDao = DatabaseHelper.getUserDao();
//        List<Item> items = null;
//        if (userRoles.size() == 0) {
//            items = DataUtils.toItems(userDao.queryForAll());
//        } else {
//            for (UserRole userRole : userRoles) {
//                if (items == null) {
//                    items = DataUtils.toItems(userDao.queryForEq(User.USER_ROLE, userRole));
//                } else {
//                    items = DataUtils.addItems(items, userDao.queryForEq(User.USER_ROLE, userRole));
//                }
//            }
//        }
//
//        binding.configUser.setSpinnerAdapter(items);
//        binding.configUser.setValue(user);
        binding.configServerUrl.setValue((String)ConfigProvider.getServerRestUrl());

        binding.configDropData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dropData();
            }
        });
    }

    private void dropData() {
        binding.configProgressBar.setVisibility(View.VISIBLE);

        DatabaseHelper.clearTables(true);
        SyncInfrastructureTask.syncAll(new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                UserForm.this.onResume();
                binding.configProgressBar.setVisibility(View.GONE);
            }
        }, getContext());
    }

//    public User getUser() {
//        return (User)binding.configUser.getValue();
//    }

    public String getServerUrl() {
        return binding.configServerUrl.getValue();
    }

    @Override
    public AbstractDomainObject getData() {
        return null;
    }
}