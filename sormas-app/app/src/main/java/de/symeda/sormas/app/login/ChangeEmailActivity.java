package de.symeda.sormas.app.login;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.ActivityChangeEmailLayoutBinding;

public class ChangeEmailActivity extends AppCompatActivity implements NotificationContext {

	public static final String CALLED_FROM_SETTINGS = "calledFromSettings";

	private boolean calledFromSettings;

	private ActivityChangeEmailLayoutBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding.setUser(ConfigProvider.getUser());
	}

	@Override
	public View getRootView() {
		if (binding != null)
			return binding.getRoot();

		return null;
	}
}
