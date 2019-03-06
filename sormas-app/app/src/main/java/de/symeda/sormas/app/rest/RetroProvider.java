/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.FragmentActivity;
import de.symeda.sormas.api.caze.classification.ClassificationAllOfCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationCaseCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationEpiDataCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationNoneOfCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationNotInStartDateRangeCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestPositiveResultCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPersonAgeBetweenYearsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationSymptomsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto;
import de.symeda.sormas.api.utils.CompatibilityCheckResponse;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.util.AppUpdateController;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.Consumer;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public final class RetroProvider {

    private static RetroProvider instance = null;

    private final Context context;
    private final Retrofit retrofit;

    private InfoFacadeRetro infoFacadeRetro;
    private CaseFacadeRetro caseFacadeRetro;
    private PersonFacadeRetro personFacadeRetro;
    private CommunityFacadeRetro communityFacadeRetro;
    private DistrictFacadeRetro districtFacadeRetro;
    private RegionFacadeRetro regionFacadeRetro;
    private FacilityFacadeRetro facilityFacadeRetro;
    private UserFacadeRetro userFacadeRetro;
    private TaskFacadeRetro taskFacadeRetro;
    private ContactFacadeRetro contactFacadeRetro;
    private VisitFacadeRetro visitFacadeRetro;
    private EventFacadeRetro eventFacadeRetro;
    private SampleFacadeRetro sampleFacadeRetro;
    private PathogenTestFacadeRetro pathogenTestFacadeRetro;
    private EventParticipantFacadeRetro eventParticipantFacadeRetro;
    private WeeklyReportFacadeRetro weeklyReportFacadeRetro;
    private OutbreakFacadeRetro outbreakFacadeRetro;
    private ClassificationFacadeRetro classificationFacadeRetro;
    private UserRoleConfigFacadeRetro userRoleConfigFacadeRetro;

    private RetroProvider(Context context, Interceptor... additionalInterceptors) throws ServerConnectionException, ServerCommunicationException, ApiVersionException {

        this.context = context;

        RuntimeTypeAdapterFactory<ClassificationCriteriaDto> classificationCriteriaFactory = RuntimeTypeAdapterFactory
                .of(ClassificationCriteriaDto.class, "type")
                .registerSubtype(ClassificationAllOfCriteriaDto.class, "ClassificationAllOfCriteriaDto")
                .registerSubtype(ClassificationCaseCriteriaDto.class, "ClassificationCaseCriteriaDto")
                .registerSubtype(ClassificationNoneOfCriteriaDto.class, "ClassificationNoneOfCriteriaDto")
                .registerSubtype(ClassificationPersonAgeBetweenYearsCriteriaDto.class, "ClassificationPersonAgeBetweenYearsCriteriaDto")
                .registerSubtype(ClassificationPathogenTestPositiveResultCriteriaDto.class, "ClassificationPathogenTestPositiveResultCriteriaDto")
                .registerSubtype(ClassificationXOfCriteriaDto.class, "ClassificationXOfCriteriaDto")
                .registerSubtype(ClassificationEpiDataCriteriaDto.class, "ClassificationEpiDataCriteriaDto")
                .registerSubtype(ClassificationNotInStartDateRangeCriteriaDto.class, "ClassificationNotInStartDateRangeCriteriaDto")
                .registerSubtype(ClassificationSymptomsCriteriaDto.class, "ClassificationSymptomsCriteriaDto")
                .registerSubtype(ClassificationPathogenTestCriteriaDto.class, "ClassificationPathogenTestCriteriaDto")
                .registerSubtype(ClassificationXOfCriteriaDto.ClassificationXOfSubCriteriaDto.class, "ClassificationXOfSubCriteriaDto")
                .registerSubtype(ClassificationXOfCriteriaDto.ClassificationOneOfCompactCriteriaDto.class, "ClassificationOneOfCompactCriteriaDto")
                .registerSubtype(ClassificationAllOfCriteriaDto.ClassificationAllOfCompactCriteriaDto.class, "ClassificationAllOfCompactCriteriaDto");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        if (json.isJsonNull()) {
                            return null;
                        }
                        long milliseconds = json.getAsLong();
                        return new Date(milliseconds);
                    }
                })
                .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                    @Override
                    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                        if (src == null) {
                            return JsonNull.INSTANCE;
                        }
                        return new JsonPrimitive(src.getTime());
                    }
                })
                .registerTypeAdapterFactory(classificationCriteriaFactory)
                .create();

        // Basic auth as explained in https://futurestud.io/tutorials/android-basic-authentication-with-retrofit

        String authToken = Credentials.basic(ConfigProvider.getUsername(), ConfigProvider.getPassword());
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(5 * 60, TimeUnit.SECONDS); // for infrastructure data - actually 30 seconds should be enough...
        // adds "Accept-Encoding: gzip" by default
        httpClient.addInterceptor(interceptor);
        for (Interceptor additionalInterceptor : additionalInterceptors) {
            httpClient.addInterceptor(additionalInterceptor);
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(ConfigProvider.getServerRestUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        checkCompatibility();

        updateLocale();
    }

    private void updateLocale() throws ServerCommunicationException, ServerConnectionException {
        Response<String> localeResponse;
        try {
            infoFacadeRetro = retrofit.create(InfoFacadeRetro.class);
            AsyncTask<Void, Void, Response<String>> asyncTask = new AsyncTask<Void, Void, Response<String>>() {

                @Override
                protected Response<String> doInBackground(Void... params) {
                    Call<String> localeCall = infoFacadeRetro.getLocale();
                    try {
                        return localeCall.execute();
                    } catch (IOException e) {
                        Log.w(RetroProvider.class.getSimpleName(), e.getMessage());
                        // wrap the exception message inside a response object
                        return Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), e.getMessage()));
                    }
                }
            };
            localeResponse = asyncTask.execute().get();

        } catch (InterruptedException | ExecutionException e) {
            throw new ServerCommunicationException(e);
        }

        if (localeResponse.isSuccessful()) {
            // success - now check compatibility
            String localeStr = localeResponse.body();
            ConfigProvider.setLocale(localeStr);
        } else {
            throwException(localeResponse);
        }
    }

    private void checkCompatibility() throws ServerCommunicationException, ServerConnectionException, ApiVersionException {

        Response<CompatibilityCheckResponse> compatibilityResponse;
        try {

            // make call to get version info
            infoFacadeRetro = retrofit.create(InfoFacadeRetro.class);
            AsyncTask<Void, Void, Response<CompatibilityCheckResponse>> asyncTask = new AsyncTask<Void, Void, Response<CompatibilityCheckResponse>>() {

                @Override
                protected Response<CompatibilityCheckResponse> doInBackground(Void... params) {
                    Call<CompatibilityCheckResponse> compatibilityCall = infoFacadeRetro.isCompatibleToApi(InfoProvider.get().getVersion());
                    try {
                        return compatibilityCall.execute();
                    } catch (IOException e) {
                        Log.w(RetroProvider.class.getSimpleName(), e.getMessage());
                        // wrap the exception message inside a response object
                        return Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), e.getMessage()));
                    }
                }
            };
            compatibilityResponse = asyncTask.execute().get();

        } catch (InterruptedException | ExecutionException e) {
            throw new ServerCommunicationException(e);
        }

        if (compatibilityResponse.isSuccessful()) {
            // success - now check compatibility
            CompatibilityCheckResponse compatibilityCheckResponse = compatibilityResponse.body();
            if (compatibilityCheckResponse == CompatibilityCheckResponse.TOO_NEW) {
                throw new ServerConnectionException(601);
            } else if (compatibilityCheckResponse == CompatibilityCheckResponse.TOO_OLD) {
                // get the current server version, throw an exception including the app url that is then processed in the UI
                matchAppAndApiVersions(infoFacadeRetro);
            }
        } else {
            throwException(compatibilityResponse);
        }
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static boolean isConnected() {
        return instance != null && isConnectedToNetwork(instance.context);
    }

    public static void connect(Context context) throws ApiVersionException, ServerConnectionException, ServerCommunicationException {

        if (!isConnectedToNetwork(context)) {
            throw new ServerConnectionException(600);
        }

        try {
            instance = new RetroProvider(context);
        } catch (Exception e) {
            instance = null;
            throw e;
        }
    }

    public static void connectAsyncHandled(FragmentActivity activity, final boolean showUpgradePrompt, final boolean matchExactVersion, final Consumer<Boolean> callback) {
        if (!(activity instanceof NotificationContext)) {
            throw new UnsupportedOperationException("Activity needs to implement NotificationContext: " + activity.toString());
        }

        if (!RetroProvider.isConnected()) {
            new DefaultAsyncTask(activity.getApplicationContext()) {

                WeakReference<FragmentActivity> activityReference;
                boolean versionCompatible = false;

                @Override
                protected void doInBackground(TaskResultHolder resultHolder) throws ServerConnectionException, ServerCommunicationException, ApiVersionException {
                    RetroProvider.connect(getApplicationReference().get());
                    versionCompatible = true;
                    if (matchExactVersion) {
                        RetroProvider.matchAppAndApiVersions();
                    }
                }

                @Override
                protected AsyncTaskResult handleException(Exception e) {
                    if (e instanceof ServerConnectionException
                            || e instanceof ApiVersionException)
                        return new AsyncTaskResult<>(e); // expected exceptions
                    return super.handleException(e);
                }

                @Override
                protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                    if (taskResult.getResultStatus().isSuccess()) {
                        callback.accept(true);
                    } else {
                        if (taskResult.getError() instanceof ApiVersionException) {
                            ApiVersionException e = (ApiVersionException) taskResult.getError();
                            if (showUpgradePrompt
                                    && !DataHelper.isNullOrEmpty(e.getAppUrl())
                                    && activityReference.get() != null) {
                                boolean canWorkOffline = ConfigProvider.getUser() != null;
                                AppUpdateController.getInstance().updateApp(activityReference.get(), e.getAppUrl(), e.getVersion(), versionCompatible || canWorkOffline,
                                        new Callback() {
                                            @Override
                                            public void call() {
                                                callback.accept(false);
                                            }
                                        });
                            } else {
                                if (activityReference.get() != null) {
                                    NotificationHelper.showNotification((NotificationContext) activityReference.get(), NotificationType.ERROR, e.getMessage());
                                }
                                callback.accept(false);
                            }
                        } else if (taskResult.getError() instanceof ServerConnectionException) {
                            ServerConnectionException exception = (ServerConnectionException)taskResult.getError();

                            if (exception.getCustomHtmlErrorCode() == 401) {
                                // could not authenticate
                                ConfigProvider.clearUsernameAndPassword();
                            }

                            if (activityReference.get() != null) {
                                NotificationHelper.showNotification((NotificationContext) activityReference.get(), NotificationType.ERROR,
                                        exception.getMessage(activityReference.get().getApplicationContext()));
                            }
                            callback.accept(false);
                        } else {
                            if (activityReference.get() != null) {
                                NotificationHelper.showNotification((NotificationContext) activityReference.get(), NotificationType.ERROR,
                                        activityReference.get().getResources().getString(R.string.error_server_connection));
                            }
                            callback.accept(false);
                        }
                    }
                }

                private DefaultAsyncTask init(FragmentActivity activity) {
                    activityReference = new WeakReference<>(activity);
                    return this;
                }
            }.init(activity).executeOnThreadPool();
        } else {
            callback.accept(true);
        }
    }


    public static void disconnect() {
        instance = null;
    }

    public static void matchAppAndApiVersions() throws ServerCommunicationException, ServerConnectionException, ApiVersionException {
        matchAppAndApiVersions(getInfoFacade());
    }

    private static void matchAppAndApiVersions(final InfoFacadeRetro infoFacadeRetro) throws ServerCommunicationException, ServerConnectionException, ApiVersionException {
        // Retrieve the version
        Response<String> versionResponse;
        try {
            AsyncTask<Void, Void, Response<String>> asyncTask = new AsyncTask<Void, Void, Response<String>>() {

                @Override
                protected Response<String> doInBackground(Void... params) {
                    Call<String> versionCall = infoFacadeRetro.getVersion();
                    try {
                        return versionCall.execute();
                    } catch (IOException e) {
                        // wrap the exception message inside a response object
                        return Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), e.getMessage()));
                    }
                }
            };
            versionResponse = asyncTask.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerCommunicationException(e);
        }

        if (versionResponse.isSuccessful()) {
            // Check if the versions match
            String serverApiVersion = versionResponse.body();
            String appApiVersion = InfoProvider.get().getVersion();
            if (!serverApiVersion.equals(appApiVersion)) {
                // Retrieve the app URL
                Response<String> appUrlResponse;
                try {
                    AsyncTask<Void, Void, Response<String>> asyncTask = new AsyncTask<Void, Void, Response<String>>() {

                        @Override
                        protected Response<String> doInBackground(Void... params) {
                            Call<String> versionCall = infoFacadeRetro.getAppUrl(InfoProvider.get().getVersion());
                            try {
                                return versionCall.execute();
                            } catch (IOException e) {
                                // wrap the exception message inside a response object
                                return Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), e.getMessage()));
                            }
                        }
                    };
                    appUrlResponse = asyncTask.execute().get();

                } catch (InterruptedException | ExecutionException e) {
                    throw new ServerCommunicationException(e);
                }

                if (appUrlResponse.isSuccessful()) {
                    throw new ApiVersionException("App version '" + appApiVersion + "' does not match server version '" + serverApiVersion + "'", appUrlResponse.body(), serverApiVersion);
                } else {
                    throwException(appUrlResponse);
                }
            }
        } else {
            throwException(versionResponse);
        }
    }

    public static InfoFacadeRetro getInfoFacade() {
        return instance.infoFacadeRetro;
    }

    public static CaseFacadeRetro getCaseFacade() {
        if (instance.caseFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.caseFacadeRetro == null) {
                    instance.caseFacadeRetro = instance.retrofit.create(CaseFacadeRetro.class);
                }
            }
        }
        return instance.caseFacadeRetro;
    }

    public static PersonFacadeRetro getPersonFacade() {
        if (instance.personFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.personFacadeRetro == null) {
                    instance.personFacadeRetro = instance.retrofit.create(PersonFacadeRetro.class);
                }
            }
        }
        return instance.personFacadeRetro;
    }

    public static CommunityFacadeRetro getCommunityFacade() {
        if (instance.communityFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.communityFacadeRetro == null) {
                    instance.communityFacadeRetro = instance.retrofit.create(CommunityFacadeRetro.class);
                }
            }
        }
        return instance.communityFacadeRetro;
    }

    public static DistrictFacadeRetro getDistrictFacade() {
        if (instance.districtFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.districtFacadeRetro == null) {
                    instance.districtFacadeRetro = instance.retrofit.create(DistrictFacadeRetro.class);
                }
            }
        }
        return instance.districtFacadeRetro;
    }

    public static RegionFacadeRetro getRegionFacade() {
        if (instance.regionFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.regionFacadeRetro == null) {
                    instance.regionFacadeRetro = instance.retrofit.create(RegionFacadeRetro.class);
                }
            }
        }
        return instance.regionFacadeRetro;
    }

    public static FacilityFacadeRetro getFacilityFacade() {
        if (instance.facilityFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.facilityFacadeRetro == null) {
                    instance.facilityFacadeRetro = instance.retrofit.create(FacilityFacadeRetro.class);
                }
            }
        }
        return instance.facilityFacadeRetro;
    }

    public static UserFacadeRetro getUserFacade() {
        if (instance.userFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.userFacadeRetro == null) {
                    instance.userFacadeRetro = instance.retrofit.create(UserFacadeRetro.class);
                }
            }
        }
        return instance.userFacadeRetro;
    }

    public static TaskFacadeRetro getTaskFacade() {
        if (instance.taskFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.taskFacadeRetro == null) {
                    instance.taskFacadeRetro = instance.retrofit.create(TaskFacadeRetro.class);
                }
            }
        }
        return instance.taskFacadeRetro;
    }

    public static ContactFacadeRetro getContactFacade() {
        if (instance.contactFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.contactFacadeRetro == null) {
                    instance.contactFacadeRetro = instance.retrofit.create(ContactFacadeRetro.class);
                }
            }
        }
        return instance.contactFacadeRetro;
    }

    public static VisitFacadeRetro getVisitFacade() {
        if (instance.visitFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.visitFacadeRetro == null) {
                    instance.visitFacadeRetro = instance.retrofit.create(VisitFacadeRetro.class);
                }
            }
        }
        return instance.visitFacadeRetro;
    }

    public static EventFacadeRetro getEventFacade() {
        if (instance.eventFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.eventFacadeRetro == null) {
                    instance.eventFacadeRetro = instance.retrofit.create(EventFacadeRetro.class);
                }
            }
        }
        return instance.eventFacadeRetro;
    }

    public static SampleFacadeRetro getSampleFacade() {
        if (instance.sampleFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.sampleFacadeRetro == null) {
                    instance.sampleFacadeRetro = instance.retrofit.create(SampleFacadeRetro.class);
                }
            }
        }
        return instance.sampleFacadeRetro;
    }

    public static PathogenTestFacadeRetro getSampleTestFacade() {
        if (instance.pathogenTestFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.pathogenTestFacadeRetro == null) {
                    instance.pathogenTestFacadeRetro = instance.retrofit.create(PathogenTestFacadeRetro.class);
                }
            }
        }
        return instance.pathogenTestFacadeRetro;
    }

    public static EventParticipantFacadeRetro getEventParticipantFacade() {
        if (instance.eventParticipantFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.eventParticipantFacadeRetro == null) {
                    instance.eventParticipantFacadeRetro = instance.retrofit.create(EventParticipantFacadeRetro.class);
                }
            }
        }
        return instance.eventParticipantFacadeRetro;
    }

    public static WeeklyReportFacadeRetro getWeeklyReportFacade() {
        if (instance.weeklyReportFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.weeklyReportFacadeRetro == null) {
                    instance.weeklyReportFacadeRetro = instance.retrofit.create(WeeklyReportFacadeRetro.class);
                }
            }
        }
        return instance.weeklyReportFacadeRetro;
    }

    public static OutbreakFacadeRetro getOutbreakFacade() {
        if (instance.outbreakFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.outbreakFacadeRetro == null) {
                    instance.outbreakFacadeRetro = instance.retrofit.create(OutbreakFacadeRetro.class);
                }
            }
        }
        return instance.outbreakFacadeRetro;
    }

    public static ClassificationFacadeRetro getClassificationFacade() {
        if (instance.classificationFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.classificationFacadeRetro == null) {
                    instance.classificationFacadeRetro = instance.retrofit.create(ClassificationFacadeRetro.class);
                }
            }
        }
        return instance.classificationFacadeRetro;
    }

    public static UserRoleConfigFacadeRetro getUserRoleConfigFacade() {
        if (instance.userRoleConfigFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.userRoleConfigFacadeRetro == null) {
                    instance.userRoleConfigFacadeRetro = instance.retrofit.create(UserRoleConfigFacadeRetro.class);
                }
            }
        }
        return instance.userRoleConfigFacadeRetro;
    }

    public static void throwException(Response<?> response) throws ServerConnectionException, ServerCommunicationException {

        if (ServerConnectionException.RelatedErrorCodes.contains(response.code())) {
            throw new ServerConnectionException(response.code());
        } else {
            String responseErrorBodyString;
            try {
                responseErrorBodyString = response.errorBody().string();
            } catch (IOException e) {
                throw new RuntimeException("Exception accessing error body", e);
            }

            throw new ServerCommunicationException(responseErrorBodyString);
        }
    }
}
