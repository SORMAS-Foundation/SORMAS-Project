package de.symeda.sormas.app.user;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.databinding.FragmentEmailEditLayoutBinding;

public class EmailEditFragment extends BaseEditFragment<FragmentEmailEditLayoutBinding, User, User> {

	private User user;

	public static EmailEditFragment newInstance(User activityRootData) {
		return newInstance(EmailEditFragment.class, null, activityRootData);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_email_edit_layout;
	}

	@Override
	public User getPrimaryData() {
		return user;
	}

	@Override
	protected void prepareFragmentData() {
		user = getActivityRootData();
	}

	@Override
	protected void onLayoutBinding(FragmentEmailEditLayoutBinding contentBinding) {
		contentBinding.setUser(user);
	}
}
