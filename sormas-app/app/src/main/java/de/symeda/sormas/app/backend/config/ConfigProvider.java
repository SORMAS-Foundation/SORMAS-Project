package de.symeda.sormas.app.backend.config;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
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
    private static String LAST_NOTIFICATION_DATE = "lastNotificationDate";

    public static ConfigProvider instance = null;

    public static void init(Context context) {
        if (instance != null) {
            Log.e(ConfigProvider.class.getName(), "ConfigProvider has already been initalized");
        }
        instance = new ConfigProvider(context);

        instance.ensureDeviceEncryption();
    }

    private final Context context;

    private String serverRestUrl;
    private String username;
    private String password;
    private String pin;
    private User user;
    private Date lastNotificationDate;

    private ConfigProvider(Context context) {
        this.context = context;
    }

    private void ensureDeviceEncryption() {
        // If device encryption is not active, show a non-cancelable alert that blocks app usage
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE ||
                dpm.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setMessage(R.string.alert_encryption);
            AlertDialog dialog = builder.create();
            dialog.show();

            // TODO close app
        }
    }

    public static User getUser() {
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

    public static String getUsername() {
        if (instance.username == null) {
            Config config = DatabaseHelper.getConfigDao().queryForId(KEY_USERNAME);
            if (config != null) {
                instance.username = config.getValue();
            }
        }
        return instance.username;
    }

    public static String getPassword() {
        if (instance.password == null) {
            Config config = DatabaseHelper.getConfigDao().queryForId(KEY_PASSWORD);
            if (config != null) {
                instance.password = config.getValue();
            }
        }
        return decodeCredential(instance.password, "Password");
    }

    public static String getPin() {
        if (instance.pin == null) {
            Config config = DatabaseHelper.getConfigDao().queryForId(KEY_PIN);
            if (config != null) {
                instance.pin = config.getValue();
            }
        }
        return decodeCredential(instance.pin, "PIN");
    }

    public static void clearUsernameAndPassword() {

        instance.user = null;
        instance.username = null;
        instance.password = null;

        ConfigDao configDao = DatabaseHelper.getConfigDao();
        if (configDao.queryForId(KEY_PASSWORD) == null && configDao.queryForId(KEY_USERNAME) == null) {
            return;
        }

        configDao.delete(new Config(KEY_USERNAME, ""));
        configDao.delete(new Config(KEY_PASSWORD, ""));
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

        if (username == null)
            throw new NullPointerException("username");
        if (password == null)
            throw new NullPointerException("password");

        password = encodeCredential(password, "Password");

        if (username.equals(instance.username) && password.equals(instance.password))
            return;

        instance.user = null;
        instance.username = username;
        instance.password = password;

        DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_USERNAME, username));
        DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_PASSWORD, password));

        DatabaseHelper.clearTables(false);
    }

    public static void setPin(String pin) {
        if (pin == null) {
            throw new NullPointerException("pin");
        }

        pin = encodeCredential(pin, "PIN");

        if (pin.equals(instance.pin)) {
            return;
        }

        instance.pin = pin;

        DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_PIN, pin));
    }

    private static String encodeCredential(String clearCredential, String keyStoreAlias) {

        if (clearCredential == null) {
            return null;
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
//    } catch (Exception e) {
//        Log.e(getClass().getName(), "Error while trying to write credentials to key store", e);
//        Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_login_failed, Snackbar.LENGTH_LONG).show();
//        ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
    }

    private static String decodeCredential(String encodedCredential, String keyStoreAlias) {

        if (encodedCredential == null) {
            return null;
        }

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyStoreAlias, null);
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
                        setServerRestUrl("https://sormas.symeda.de/sormas-rest/");
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
}
