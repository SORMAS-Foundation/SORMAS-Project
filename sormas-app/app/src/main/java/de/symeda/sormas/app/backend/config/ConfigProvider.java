package de.symeda.sormas.app.backend.config;

import android.util.Log;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.task.SyncTasksTask;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ConnectionHelper;
import de.symeda.sormas.app.util.SyncInfrastructureTask;

/**
 * Created by Martin Wahnschaffe on 10.08.2016.
 */
public final class ConfigProvider {

    private static String KEY_USER_UUID = "userUuid";
    private static String KEY_USERNAME = "username";
    private static String KEY_PASSWORD = "password";
    private static String KEY_SERVER_REST_URL = "serverRestUrl";
    private static String LAST_NOTIFICATION_DATE = "lastNotificationDate";

    public static ConfigProvider instance = null;

    public static void init() {
        if (instance != null) {
            Log.e(ConfigProvider.class.getName(), "ConfigProvider has already been initalized");
        }
        instance = new ConfigProvider();
    }

    private String serverRestUrl;
    private User user;
    private String username;
    private String password;
    private Date lastNotificationDate;

    public static User getUser() {
        if (instance.user == null)
            synchronized (ConfigProvider.class) {
                if (instance.user == null) {
                    Config config = DatabaseHelper.getConfigDao().queryForId(KEY_USER_UUID);
                    if (config != null) {
                        instance.user = DatabaseHelper.getUserDao().queryUuid(config.getValue());
                    }
                    // TODO check if username == user.name
                    // TODO remove this as soon as server stuff has been added, see #180
                    if (instance.user == null) {
                        // no user found. Take first surveillance officer...
                        List<User> users = DatabaseHelper.getUserDao().queryForAll();

                        for (User dbUser : users) {
                            if (UserRole.SURVEILLANCE_OFFICER.equals(dbUser.getUserRole())) {
                                // got it
                                setUser(dbUser);
                                break;
                            }
                        }
                    }
                }
            }
        return instance.user;
    }

    public static void setUser(User user) {
        if (user != null && user.equals(instance.user))
            return;

        boolean wasNull = instance.user == null;
        instance.user = user;
        if (user == null) {
            DatabaseHelper.getConfigDao().delete(new Config(KEY_USER_UUID, ""));
        } else {
            DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_USER_UUID, user.getUuid()));
        }

        if (!wasNull) {
            DatabaseHelper.clearTables(false);
        }
    }

    public static String getUsername() {
        return instance.username;

    }

    public static void setUsername(String username) {
        if (username != null && username.equals(instance.username))
            return;

        instance.username = username;
        if (username == null) {
            DatabaseHelper.getConfigDao().delete(new Config(KEY_USERNAME, ""));
        } else {
            DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_USERNAME, username));
        }
    }

    public static String getPassword() {
        return instance.password;
    }

    public static void setPassword(String password) {
        if (password != null && password.equals(instance.password))
            return;

        instance.password = password;
        if (password == null) {
            DatabaseHelper.getConfigDao().delete(new Config(KEY_PASSWORD, ""));
        } else {
            DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_PASSWORD, password));
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
                        setServerUrl("https://sormas.symeda.de/sormas-rest/");
                    }
                }
            }
        return instance.serverRestUrl;
    }

    public static void setServerUrl(String serverRestUrl) {
        if (serverRestUrl != null && serverRestUrl.isEmpty()) {
            serverRestUrl = null;
        }

        if (serverRestUrl == instance.serverRestUrl
                || (serverRestUrl != null && serverRestUrl.equals(instance.serverRestUrl)))
            return;

        // make sure no wrong user is set
        setUser(null);

        boolean wasNull = instance.serverRestUrl == null;
        instance.serverRestUrl = serverRestUrl;

        if (serverRestUrl == null) {
            DatabaseHelper.getConfigDao().delete(new Config(KEY_SERVER_REST_URL, ""));
        } else {
            DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_SERVER_REST_URL, serverRestUrl));
        }

        if (!wasNull) {
            // reset everything
            RetroProvider.reset();
            DatabaseHelper.clearTables(true);
            ConfigProvider.setUser(DatabaseHelper.getUserDao().getByUsername(ConfigProvider.getUsername()));
            SyncInfrastructureTask.syncAll(null, null);
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
