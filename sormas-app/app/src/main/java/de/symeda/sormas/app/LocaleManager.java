package de.symeda.sormas.app;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.app.backend.config.ConfigProvider;

public class LocaleManager {

	private static final String LANGUAGE_KEY = "language_key";

	static void initializeI18nProperties() {
		Locale locale = new Locale(ConfigProvider.getServerLocale());
		I18nProperties.setDefaultLanguage(Language.fromLocaleString(locale.toString()));
		I18nProperties.setUserLanguage(Language.fromLocaleString(locale.toString()));
	}

	static Context setLocale(Context mContext) {
		return updateResources(mContext, getLanguagePref(mContext));
	}

	public static void setNewLocale(Context mContext, Language language) {
		setLanguagePref(mContext, language.name());
		updateResources(mContext, language);
	}

	public static Language getLanguagePref(Context mContext) {
		SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		return Language.valueOf(mPreferences.getString(LANGUAGE_KEY, Language.EN.name()));
	}

	private static void setLanguagePref(Context mContext, String localeKey) {
		SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		mPreferences.edit().putString(LANGUAGE_KEY, localeKey).apply();
	}

	private static Context updateResources(Context context, Language language) {
		Locale locale = language.getLocale();
		Locale.setDefault(locale);
		Resources res = context.getResources();
		Configuration config = new Configuration(res.getConfiguration());
		config.setLocale(locale);
		context = context.createConfigurationContext(config);
		return context;
	}
}
