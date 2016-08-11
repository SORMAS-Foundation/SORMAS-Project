package de.symeda.sormas.app.backend.config;

import android.util.Log;

import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 10.08.2016.
 */
public final class ConfigProvider {

    private static String KEY_USER_UUID = "userUuid";

    public static ConfigProvider instance = null;

    public static void init() {
        if (instance != null) {
            Log.e(ConfigProvider.class.getName(), "ConfigProvider has already been initalized");
        }
        instance = new ConfigProvider();
    }

    private User user;

    public static User getUser() {
        if (instance.user == null)
            synchronized (ConfigProvider.class) {
                if (instance.user == null) {

                    // get user from config
                    Config config = DatabaseHelper.getConfigDao().queryForId(KEY_USER_UUID);
                    if (config != null) {
                        instance.user = DatabaseHelper.getUserDao().queryUuid(config.getValue());
                    }

                    if (instance.user == null) {
                        // no user found. Take first surveillance officer...
                        List<User> users = DatabaseHelper.getUserDao().queryForAll();
                        if (users.size() == 0) {
                            // no user existing yet? Try to load them from the server
                            new UserDtoHelper().pullEntities(new DtoGetInterface<UserDto>() {
                                @Override
                                public Call<List<UserDto>> getAll(long since) {
                                    return RetroProvider.getUserFacade().getAll(since);
                                }
                            }, DatabaseHelper.getUserDao());
                            users = DatabaseHelper.getUserDao().queryForAll();
                        }
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

    public static synchronized void setUser(User user) {
        if (user != null && user.equals(instance.user))
            return;

        instance.user = user;
        DatabaseHelper.getConfigDao().createOrUpdate(new Config(KEY_USER_UUID, user.getUuid()));

        try {
            TableUtils.clearTable(DatabaseHelper.getCaseDao().getConnectionSource(), Case.class);
        } catch (SQLException e) {
            Log.e(ConfigProvider.class.getName(), "User switch: Clearing cases failed");
        }

        try {
            new SyncCasesTask().execute().get();
        } catch (InterruptedException e) {
            Log.e(ConfigProvider.class.getName(), e.toString(), e);
        } catch (ExecutionException e) {
            Log.e(ConfigProvider.class.getName(), e.toString(), e);
        }
    }
}
