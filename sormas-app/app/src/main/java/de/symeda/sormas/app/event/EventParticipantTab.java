package de.symeda.sormas.app.event;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.databinding.EventParticipantFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

public class EventParticipantTab extends FormTab {

    private EventParticipantFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.event_participant_fragment_layout, container, false);
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
//        binding.configServerUrl.setValue((String)ConfigProvider.getServerRestUrl());
    }

//    public User getUser() {
//        return (User)binding.configUser.getValue();
//    }

//    public String getServerUrl() {
//        return binding.configServerUrl.getValue();
//    }

    @Override
    public AbstractDomainObject getData() {
        return null;
    }
}