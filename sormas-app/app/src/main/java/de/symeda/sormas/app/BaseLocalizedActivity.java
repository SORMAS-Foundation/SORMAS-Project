package de.symeda.sormas.app;

import static android.content.pm.PackageManager.GET_META_DATA;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseLocalizedActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resetTitles();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LocaleManager.setLocale(base));
	}

	@Override
	public void applyOverrideConfiguration(Configuration overrideConfiguration) {
		if (overrideConfiguration != null) {
			int uiMode = overrideConfiguration.uiMode;
			overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
			overrideConfiguration.uiMode = uiMode;
		}
		super.applyOverrideConfiguration(overrideConfiguration);
	}

	protected void resetTitles() {
		try {
			ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);
			if (info.labelRes != 0) {
				setTitle(info.labelRes);
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
