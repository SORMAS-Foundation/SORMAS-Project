/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserRoleConfig;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.SormasProperties;

public final class ConfigProvider {

	private static String KEY_USERNAME = "username";
	private static String KEY_PASSWORD = "password";
	private static String KEY_PIN = "pin";
	private static String KEY_SERVER_REST_URL = "serverRestUrl";
	private static String KEY_ACCESS_GRANTED = "accessGranted";
	private static String KEY_REPULL_NEEDED = "repullNeeded";
	private static String LAST_NOTIFICATION_DATE = "lastNotificationDate";
	private static String LAST_ARCHIVED_SYNC_DATE = "lastArchivedSyncDate";
	private static String LAST_DELETED_SYNC_DATE = "lastDeletedSyncDate";
	private static String CURRENT_APP_DOWNLOAD_ID = "currentAppDownloadId";
	private static String SERVER_LOCALE = "locale";
	private static String INITIAL_SYNC_REQUIRED = "initialSyncRequired";

	public static ConfigProvider instance = null;

	public static void init(Context context) {
		if (instance != null) {
			Log.e(ConfigProvider.class.getName(), "ConfigProvider has already been initalized");
		}
		instance = new ConfigProvider(context);
	}

	private final Context context;

	private String serverRestUrl;
	private String username;
	private String password;
	private String pin;
	private User user;
	private Set<UserRight> userRights; // just a cache
	private Date lastNotificationDate;
	private Date lastArchivedSyncDate;
	private Date lastDeletedSyncDate;
	private Long currentAppDownloadId;
	private Boolean accessGranted;
	private String serverLocale;
	private Boolean repullNeeded;
	private Boolean initialSyncRequired;

	private ConfigProvider(Context context) {
		this.context = context;
	}

	private boolean hasDeviceEncryption() {

		// Device encryption is no longer used, because it's not reliably implemented
		// on old android devices (versions 5 and 6) - see #410
		// As a replacement the database should be encrypted #905
		return true;
//        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//        return dpm.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE ||
//                dpm.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER ||
//                dpm.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY;
	}

	/**
	 * If device encryption is not active, show a non-cancelable alert that blocks app usage
	 *
	 * @param activity
	 * @return
	 */
	public static boolean ensureDeviceEncryption(final Activity activity) {

		if (!instance.hasDeviceEncryption()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setCancelable(false);
			builder.setMessage(R.string.message_encryption);
			AlertDialog dialog = builder.create();
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.action_ok), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Activity finishActivity = activity;
					do {
						finishActivity.finish();
						finishActivity = finishActivity.getParent();
					}
					while (finishActivity != null);
				}
			});
			dialog.show();
			return false;
		}
		return true;
	}

	public static User getUser() {
		synchronized (ConfigProvider.class) {
			if (instance.user == null) {
				String username = getUsername();
				String password = getPassword(); // needed to not automatically "login" again on logout with missing server connection
				if (username != null && password != null) {
					instance.user = DatabaseHelper.getUserDao().getByUsername(username);
				}
			}
			return instance.user;
		}
	}

	public static Set<UserRight> getUserRights() {
		synchronized (ConfigProvider.class) {
			if (instance.userRights == null) {
				User user = getUser();
				if (user != null) {
					instance.userRights = new HashSet<>();

					for (UserRole userRole : user.getUserRoles()) {
						List<UserRoleConfig> userRoleConfigs = DatabaseHelper.getUserRoleConfigDao().queryForEq(UserRoleConfig.USER_ROLE, userRole);
						if (userRoleConfigs.size() > 0) {
							instance.userRights.addAll(userRoleConfigs.get(0).getUserRights());
						} else {
							instance.userRights.addAll(userRole.getDefaultUserRights());
						}
					}
				}
			}
			return instance.userRights;
		}
	}

	public static void onUserRolesConfigChanged() {
		synchronized (ConfigProvider.class) {
			instance.userRights = null;
		}
	}

	public static boolean hasUserRight(UserRight userRight) {
		Set<UserRight> userRights = getUserRights();
		return userRights != null && userRights.contains(userRight);
	}

	public static String getUsername() {
		synchronized (ConfigProvider.class) {
			if (instance.username == null) {
				Config config = DatabaseHelper.getConfigDao().queryForId(KEY_USERNAME);
				if (config != null) {
					instance.username = config.getValue();
				}
			}
			return instance.username;
		}
	}

	public static String getPassword() {
		synchronized (ConfigProvider.class) {
			if (instance.password == null) {
				Config config = DatabaseHelper.getConfigDao().queryForId(KEY_PASSWORD);
				if (config != null) {
					instance.password = config.getValue();
				}
			}
			return instance.decodeCredential(instance.password, "Password");
		}
	}

	public static String getPin() {
		if (instance.pin == null) {
			Config config = DatabaseHelper.getConfigDao().queryForId(KEY_PIN);
			if (config != null) {
				instance.pin = config.getValue();
			}
		}
		return instance.decodeCredential(instance.pin, "PIN");
	}

	public static void clearUserLogin() {
		// synchronized to make sure this does not interfere with setUserNameAndPassword or getUsername/getPassword
		synchronized (ConfigProvider.class) {

			ConfigDao configDao = DatabaseHelper.getConfigDao();
			if (configDao.queryForId(KEY_PASSWORD) != null) {
				configDao.delete(new Config(KEY_PASSWORD, ""));
			}
			instance.password = null;

			// remember the username - needed to decide whether non-infrastructure data has to be cleared
			// instance.username = null;

			instance.user = null;
			instance.userRights = null;
			instance.accessGranted = null;
			instance.lastNotificationDate = null;
			instance.lastArchivedSyncDate = null;

			// old credentials are no longer valid
			RetroProvider.disconnect();
		}
	}

	public static void clearPin() {
		instance.pin = null;

		ConfigDao configDao = DatabaseHelper.getConfigDao();
		if (configDao.queryForId(KEY_PIN) == null) {
			return;
		}

		configDao.delete(new Config(KEY_PIN, ""));

		setAccessGranted(false);
	}

	public static void setUsernameAndPassword(String username, String password) {
		synchronized (ConfigProvider.class) {
			if (username == null)
				throw new NullPointerException("username");
			if (password == null)
				throw new NullPointerException("password");

			password = instance.encodeCredential(password, "Password");

			if (username.equals(instance.username) && password.equals(instance.password)) {
				return;
			}

			if (!username.equals(instance.username)) {
				DatabaseHelper.clearTables(false);
			}

			instance.user = null;
			instance.username = username;
			instance.password = password;

			DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_USERNAME, username));
			DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_PASSWORD, password));

			setRepullNeeded(true);
		}
	}

	public static void setPin(String pin) {
		if (pin == null) {
			throw new NullPointerException("pin");
		}

		pin = instance.encodeCredential(pin, "PIN");

		if (pin.equals(instance.pin)) {
			return;
		}

		instance.pin = pin;

		DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_PIN, pin));
	}

	private String encodeCredential(String clearCredential, String keyStoreAlias) {

		if (clearCredential == null) {
			return null;
		}

		if (!hasDeviceEncryption()) {
			throw new IllegalStateException(context.getString(R.string.message_encryption));
		}

		try {
			KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
			keyStore.load(null);

			if (!keyStore.containsAlias(keyStoreAlias)) {
				Calendar start = Calendar.getInstance();
				Calendar end = Calendar.getInstance();
				end.add(Calendar.YEAR, 1);

				KeyPairGeneratorSpec credentialsSpec = new KeyPairGeneratorSpec.Builder(instance.context).setAlias(keyStoreAlias)
					.setSubject(new X500Principal("CN=SORMAS, O=symeda, C=Germany"))
					.setSerialNumber(BigInteger.ONE)
					.setStartDate(start.getTime())
					.setEndDate(end.getTime())
					.build();

				KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
				generator.initialize(credentialsSpec);
				generator.generateKeyPair();
			}

			PublicKey publicKey;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				publicKey = keyStore.getCertificate(keyStoreAlias).getPublicKey();
			} else {
				KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyStoreAlias, null);
				publicKey = privateKeyEntry.getCertificate().getPublicKey();
			}

			if (publicKey == null) {
				return null;
			}

			Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			input.init(Cipher.ENCRYPT_MODE, publicKey);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
			cipherOutputStream.write(clearCredential.getBytes("UTF-8"));
			cipherOutputStream.close();

			byte[] resultByteArray = outputStream.toByteArray();
			String result = Base64.encodeToString(resultByteArray, Base64.DEFAULT);
			return result;

		} catch (KeyStoreException
			| NoSuchAlgorithmException
			| NoSuchProviderException
			| UnrecoverableEntryException
			| IOException
			| NoSuchPaddingException
			| InvalidKeyException
			| CertificateException
			| InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
	}

	private String decodeCredential(String encodedCredential, String keyStoreAlias) {

		if (encodedCredential == null) {
			return null;
		}

		if (!hasDeviceEncryption()) {
			throw new IllegalStateException(context.getString(R.string.message_encryption));
		}

		try {
			KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
			keyStore.load(null);

			PrivateKey privateKey;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				privateKey = (PrivateKey) keyStore.getKey(keyStoreAlias, null);
			} else {
				KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyStoreAlias, null);
				privateKey = privateKeyEntry.getPrivateKey();
			}

			if (privateKey == null) {
				return null;
			}

			Cipher decryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

			byte[] encodedBytes = Base64.decode(encodedCredential, Base64.DEFAULT);
			InputStream inputStream = new ByteArrayInputStream(encodedBytes);
			CipherInputStream cipherStream = new CipherInputStream(inputStream, decryptCipher);
			byte[] passwordBytes = new byte[1024];
			int passwordByteLength = cipherStream.read(passwordBytes);
			cipherStream.close();

			String result = new String(passwordBytes, 0, passwordByteLength, "UTF-8");
			return result;

		} catch (KeyStoreException
			| UnrecoverableEntryException
			| NoSuchAlgorithmException
			| CertificateException
			| InvalidKeyException
			| NoSuchPaddingException
			| IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void saveConfigEntry(String configKey, String configValue) {
		if (configValue == null) {
			DatabaseHelper.getConfigDao().delete(new Config(configKey, ""));
		} else {
			DatabaseHelper.getConfigDao().createOrUpdate(new Config(configKey, configValue));
		}
	}

	public static String getServerRestUrl() {
		if (instance.serverRestUrl == null)
			synchronized (ConfigProvider.class) {
				if (instance.serverRestUrl == null) {
					Config config = DatabaseHelper.getConfigDao().queryForId(KEY_SERVER_REST_URL);
					if (config != null) {
						instance.serverRestUrl = config.getValue();

						// take care of old mal-formatted urls
						String properServerRestUrl = toProperRestUrl(instance.serverRestUrl);
						if (!DataHelper.equal(instance.serverRestUrl, properServerRestUrl)) {
							instance.serverRestUrl = properServerRestUrl;
							saveConfigEntry(KEY_SERVER_REST_URL, instance.serverRestUrl);
						}
					}

					if (instance.serverRestUrl == null) {
						// fallback to default

						String serverUrlDefault = toProperRestUrl(SormasProperties.getServerUrlDefault());
						if (serverUrlDefault != null) {
							instance.serverRestUrl = serverUrlDefault;
							saveConfigEntry(KEY_SERVER_REST_URL, instance.serverRestUrl);
						}
					}
				}
			}
		return instance.serverRestUrl;
	}

	public static void setServerRestUrl(String serverRestUrl) {

		serverRestUrl = toProperRestUrl(serverRestUrl);
		String currentServerRestUrl = getServerRestUrl();

		if (DataHelper.equal(serverRestUrl, currentServerRestUrl))
			return;

		boolean wasNull = currentServerRestUrl == null;

		instance.serverRestUrl = serverRestUrl;
		saveConfigEntry(KEY_SERVER_REST_URL, serverRestUrl);

		if (!wasNull) {
			// new server: clear everything
			clearUserLogin();
			DatabaseHelper.clearTables(true);
		}
	}

	private static String toProperRestUrl(String serverRestUrl) {
		// clean up url
		if (serverRestUrl != null) {
			serverRestUrl = serverRestUrl.trim();
		}
		if (serverRestUrl != null && serverRestUrl.isEmpty()) {
			serverRestUrl = null;
		}
		if (serverRestUrl != null && !serverRestUrl.endsWith("/")) {
			serverRestUrl += "/";
		}
		return serverRestUrl;
	}

	public static Date getLastNotificationDate() {
		if (instance.lastNotificationDate == null)
			synchronized (ConfigProvider.class) {
				if (instance.lastNotificationDate == null) {
					Config config = DatabaseHelper.getConfigDao().queryForId(LAST_NOTIFICATION_DATE);
					if (config != null) {
						instance.lastNotificationDate = new Date(Long.parseLong(config.getValue()));
					}

				}
			}
		return instance.lastNotificationDate;
	}

	public static void setLastNotificationDate(Date lastNotificationDate) {
		if (lastNotificationDate != null && lastNotificationDate.equals(instance.lastNotificationDate)) {
			return;
		}

		instance.lastNotificationDate = lastNotificationDate;
		saveConfigEntry(LAST_NOTIFICATION_DATE, lastNotificationDate != null ? String.valueOf(lastNotificationDate.getTime()) : null);
	}

	public static Date getLastArchivedSyncDate() {
		if (instance.lastArchivedSyncDate == null) {
			synchronized (ConfigProvider.class) {
				if (instance.lastArchivedSyncDate == null) {
					Config config = DatabaseHelper.getConfigDao().queryForId(LAST_ARCHIVED_SYNC_DATE);
					if (config != null) {
						instance.lastArchivedSyncDate = new Date(Long.parseLong(config.getValue()));
					}

				}
			}
		}

		return instance.lastArchivedSyncDate;
	}

	public static void setLastArchivedSyncDate(Date lastArchivedSyncDate) {
		if (lastArchivedSyncDate != null && lastArchivedSyncDate.equals(instance.lastArchivedSyncDate)) {
			return;
		}

		instance.lastArchivedSyncDate = lastArchivedSyncDate;
		saveConfigEntry(LAST_ARCHIVED_SYNC_DATE, lastArchivedSyncDate != null ? String.valueOf(lastArchivedSyncDate.getTime()) : null);
	}

	public static Date getLastDeletedSyncDate() {
		if (instance.lastDeletedSyncDate == null) {
			synchronized (ConfigProvider.class) {
				if (instance.lastDeletedSyncDate == null) {
					Config config = DatabaseHelper.getConfigDao().queryForId(LAST_DELETED_SYNC_DATE);
					if (config != null) {
						instance.lastDeletedSyncDate = new Date(Long.parseLong(config.getValue()));
					}

				}
			}
		}

		return instance.lastDeletedSyncDate;
	}

	public static void setLastDeletedSyncDate(Date lastDeletedSyncDate) {
		if (lastDeletedSyncDate != null && lastDeletedSyncDate.equals(instance.lastDeletedSyncDate)) {
			return;
		}

		instance.lastDeletedSyncDate = lastDeletedSyncDate;
		saveConfigEntry(LAST_DELETED_SYNC_DATE, lastDeletedSyncDate != null ? String.valueOf(lastDeletedSyncDate.getTime()) : null);
	}

	public static Long getCurrentAppDownloadId() {
		if (instance.currentAppDownloadId == null) {
			synchronized (ConfigProvider.class) {
				if (instance.currentAppDownloadId == null) {
					Config config = DatabaseHelper.getConfigDao().queryForId(CURRENT_APP_DOWNLOAD_ID);
					if (config != null) {
						instance.currentAppDownloadId = DataHelper.tryParseLong(config.getValue());
					}
				}
			}
		}
		return instance.currentAppDownloadId;
	}

	public static void setCurrentAppDownloadId(Long currentAppDownloadId) {
		if (currentAppDownloadId != null && currentAppDownloadId.equals(instance.currentAppDownloadId)) {
			return;
		}

		instance.currentAppDownloadId = currentAppDownloadId;
		saveConfigEntry(CURRENT_APP_DOWNLOAD_ID, currentAppDownloadId != null ? String.valueOf(currentAppDownloadId) : null);
	}

	public static Boolean isAccessGranted() {
		if (instance.accessGranted == null) {
			Config config = DatabaseHelper.getConfigDao().queryForId(KEY_ACCESS_GRANTED);
			if (config != null) {
				instance.accessGranted = Boolean.parseBoolean(config.getValue());
			}
		}
		return instance.accessGranted;
	}

	public static void setAccessGranted(Boolean accessGranted) {
		if (accessGranted == null) {
			throw new NullPointerException("accessGranted");
		}

		if (accessGranted.equals(instance.accessGranted)) {
			return;
		}

		instance.accessGranted = accessGranted;
		DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_ACCESS_GRANTED, String.valueOf(accessGranted)));
	}

	/**
	 * When no locale is set Locale.getDefault is set.
	 * (Actually not relevant, because the locale is always updated to the server's, so this is just a fallback).
	 * 
	 * @return Will never be null.
	 */
	public static String getServerLocale() {
		if (instance.serverLocale == null)
			synchronized (ConfigProvider.class) {
				if (instance.serverLocale == null) {
					Config config = DatabaseHelper.getConfigDao().queryForId(SERVER_LOCALE);
					if (config != null) {
						instance.serverLocale = config.getValue();
					}

					if (instance.serverLocale == null) {
						setServerLocale(Locale.getDefault().toString());
					}
				}
			}
		return instance.serverLocale;
	}

	public static boolean isGermanServer() {
		return getServerLocale().toLowerCase().startsWith("de");
	}

	/**
	 * Note: This will only take effect after the app has been restarted
	 */
	public static void setServerLocale(String serverLocale) {
		if (serverLocale != null && serverLocale.isEmpty()) {
			serverLocale = null;
		}

		if (serverLocale == instance.serverLocale || (serverLocale != null && serverLocale.equals(instance.serverLocale)))
			return;

		instance.serverLocale = serverLocale;
		saveConfigEntry(SERVER_LOCALE, serverLocale);
	}

	public static boolean isRepullNeeded() {
		if (instance.repullNeeded == null) {
			Config config = DatabaseHelper.getConfigDao().queryForId(KEY_REPULL_NEEDED);
			if (config != null) {
				instance.repullNeeded = Boolean.parseBoolean(config.getValue());
			}
		}
		return instance.repullNeeded != null && instance.repullNeeded;
	}

	public static void setRepullNeeded(boolean repullNeeded) {
		if (instance.repullNeeded != null && instance.repullNeeded == repullNeeded) {
			return;
		}

		instance.repullNeeded = repullNeeded;
		DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_REPULL_NEEDED, String.valueOf(repullNeeded)));
	}

	public static boolean isInitialSyncRequired() {
		if (instance.initialSyncRequired == null) {
			Config config = DatabaseHelper.getConfigDao().queryForId(INITIAL_SYNC_REQUIRED);
			if (config != null) {
				instance.initialSyncRequired = Boolean.parseBoolean(config.getValue());
			}
		}
		return instance.initialSyncRequired == null || instance.initialSyncRequired;
	}

	public static void setInitialSyncRequired(boolean initialSyncRequired) {
		if (instance.initialSyncRequired != null && instance.initialSyncRequired == initialSyncRequired) {
			return;
		}

		instance.initialSyncRequired = initialSyncRequired;
		DatabaseHelper.getConfigDao().createOrUpdate(new Config(INITIAL_SYNC_REQUIRED, String.valueOf(initialSyncRequired)));
	}

	public static boolean hasRole(UserRole userRoleName) {
		User user = ConfigProvider.getUser();
		Set<UserRole> userRoles = user.getUserRoles();
		return !userRoles.stream().filter(userRole -> userRole.name().equals(userRoleName.toString())).collect(Collectors.toList()).isEmpty();
	}

	public static boolean hasRole(Set<UserRole> typeRoles) {
		User user = ConfigProvider.getUser();
		Set<UserRole> userRoles = user.getUserRoles();
		return !userRoles.stream().filter(userRole -> typeRoles.contains(userRole)).collect(Collectors.toList()).isEmpty();
	}
}
