package de.symeda.sormas.app.backend.config;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.security.KeyPairGeneratorSpec;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;

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
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Martin Wahnschaffe on 10.08.2016.
 */
public final class ConfigProvider {

    private static String KEY_USERNAME = "username";
    private static String KEY_PASSWORD = "password";
    private static String KEY_PIN = "pin";
    private static String KEY_SERVER_REST_URL = "serverRestUrl";
    private static String KEY_ACCESS_GRANTED = "accessGranted";
    private static String LAST_NOTIFICATION_DATE = "lastNotificationDate";
    private static String CURRENT_APP_DOWNLOAD_ID = "currentAppDownloadId";

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
    private Date lastNotificationDate;
    private Long currentAppDownloadId;
    private Boolean accessGranted;

    private ConfigProvider(Context context) {
        this.context = context;
    }

    private boolean hasDeviceEncryption() {

        // TODO solve #410
        return true;
//        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//        return dpm.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE ||
//                dpm.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER ||
//                dpm.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY;
    }

    /**
     * If device encryption is not active, show a non-cancelable alert that blocks app usage
     * @param activity
     * @return
     */
    public static boolean ensureDeviceEncryption(final Activity activity) {

        if (!instance.hasDeviceEncryption()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setMessage(R.string.alert_encryption);
            AlertDialog dialog = builder.create();
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.action_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Activity finishActivity = activity;
                            do {
                                finishActivity.finish();
                                finishActivity = finishActivity.getParent();
                            } while (finishActivity != null);
                        }
                    });
            dialog.show();
            return false;
        }
        return true;
    }

    public static User getUser() {
        synchronized(ConfigProvider.class) {
            if (instance.user == null) {
                synchronized (ConfigProvider.class) {
                    if (instance.user == null) {
                        String username = getUsername();
                        if (username != null) {
                            instance.user = DatabaseHelper.getUserDao().getByUsername(username);
                        }
                    }
                }
            }
            return instance.user;
        }
    }

    public static String getUsername() {
        synchronized(ConfigProvider.class) {
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
        synchronized(ConfigProvider.class) {
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

    public static void clearUsernameAndPassword() {
        // synchronized to make sure this does not interfere with setUserNameAndPassword or getUsername/getPassword
        synchronized(ConfigProvider.class) {

            ConfigDao configDao = DatabaseHelper.getConfigDao();
            if (configDao.queryForId(KEY_PASSWORD) != null) {
                configDao.delete(new Config(KEY_PASSWORD, ""));
            }
            if (configDao.queryForId(KEY_USERNAME) != null) {
                configDao.delete(new Config(KEY_USERNAME, ""));
            }

            instance.username = null;
            instance.password = null;
            instance.user = null;
        }
    }

    public static void clearPin() {
        instance.pin = null;

        ConfigDao configDao = DatabaseHelper.getConfigDao();
        if (configDao.queryForId(KEY_PIN) == null) {
            return;
        }

        configDao.delete(new Config(KEY_PIN, ""));
    }

    public static void setUsernameAndPassword(String username, String password) {
        synchronized(ConfigProvider.class) {
            if (username == null)
                throw new NullPointerException("username");
            if (password == null)
                throw new NullPointerException("password");

            password = instance.encodeCredential(password, "Password");

            if (username.equals(instance.username) && password.equals(instance.password))
                return;

            instance.user = null;
            instance.username = username;
            instance.password = password;

            DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_USERNAME, username));
            DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_PASSWORD, password));

            DatabaseHelper.clearTables(false);
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
            throw new IllegalStateException(context.getString(R.string.alert_encryption));
        }

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias(keyStoreAlias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);

                KeyPairGeneratorSpec credentialsSpec = new KeyPairGeneratorSpec.Builder(instance.context)
                        .setAlias(keyStoreAlias)
                        .setSubject(new X500Principal("CN=SORMAS, O=symeda, C=Germany"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();

                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(credentialsSpec);
                generator.generateKeyPair();
            }

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyStoreAlias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
            cipherOutputStream.write(clearCredential.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] resultByteArray = outputStream.toByteArray();
            String result = Base64.encodeToString(resultByteArray, Base64.DEFAULT);
            return result;

        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableEntryException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    private String decodeCredential(String encodedCredential, String keyStoreAlias) {

        if (encodedCredential == null) {
            return null;
        }

        if (!hasDeviceEncryption()) {
            throw new IllegalStateException(context.getString(R.string.alert_encryption));
        }

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyStoreAlias, null);
            if (privateKeyEntry == null) {
                return null;
            }
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();

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

        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableEntryException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getServerRestUrl() {
        if (instance.serverRestUrl == null)
            synchronized (ConfigProvider.class) {
                if (instance.serverRestUrl == null) {
                    Config config = DatabaseHelper.getConfigDao().queryForId(KEY_SERVER_REST_URL);
                    if (config != null) {
                        instance.serverRestUrl = config.getValue();
                    }

                    if (instance.serverRestUrl == null) {
                        setServerRestUrl("http://192.168.1.6:6080/sormas-rest/");
                    }
                }
            }
        return instance.serverRestUrl;
    }

    public static void setServerRestUrl(String serverRestUrl) {
        if (serverRestUrl != null && serverRestUrl.isEmpty()) {
            serverRestUrl = null;
        }

        if (serverRestUrl == instance.serverRestUrl
                || (serverRestUrl != null && serverRestUrl.equals(instance.serverRestUrl)))
            return;

        boolean wasNull = instance.serverRestUrl == null;
        instance.serverRestUrl = serverRestUrl;

        if (serverRestUrl == null) {
            DatabaseHelper.getConfigDao().delete(new Config(KEY_SERVER_REST_URL, ""));
        } else {
            DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_SERVER_REST_URL, serverRestUrl));
        }

        if (!wasNull) {
            // clear everything
            clearUsernameAndPassword();
            DatabaseHelper.clearTables(true);
        }
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
        if (lastNotificationDate != null && lastNotificationDate.equals(instance.lastNotificationDate))
            return;

        boolean wasNull = instance.lastNotificationDate == null;
        instance.lastNotificationDate = lastNotificationDate;
        if (lastNotificationDate == null) {
            DatabaseHelper.getConfigDao().delete(new Config(LAST_NOTIFICATION_DATE, ""));
        } else {
            DatabaseHelper.getConfigDao().createOrUpdate(new Config(LAST_NOTIFICATION_DATE, String.valueOf(lastNotificationDate.getTime())));
        }
    }

    public static Long getCurrentAppDownloadId() {
        if (instance.currentAppDownloadId == null) {
            synchronized (ConfigProvider.class) {
                if (instance.currentAppDownloadId == null) {
                    Config config = DatabaseHelper.getConfigDao().queryForId(CURRENT_APP_DOWNLOAD_ID);
                    if (config != null) {
                        instance.currentAppDownloadId = Long.parseLong(config.getValue());
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
        if (currentAppDownloadId == null) {
            DatabaseHelper.getConfigDao().delete(new Config(CURRENT_APP_DOWNLOAD_ID, ""));
        } else {
            DatabaseHelper.getConfigDao().createOrUpdate(new Config(CURRENT_APP_DOWNLOAD_ID, String.valueOf(currentAppDownloadId)));
        }
    }

    public static Boolean isAccessGranted() {
        return true;
//        if (instance.accessGranted == null) {
//            Config config = DatabaseHelper.getConfigDao().queryForId(KEY_ACCESS_GRANTED);
//            if (config != null) {
//                instance.accessGranted = Boolean.parseBoolean(config.getValue());
//            }
//        }
//        return instance.accessGranted;
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


}
