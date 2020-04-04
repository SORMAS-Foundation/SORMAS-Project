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
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.symeda.sormas.api.caze.classification.ClassificationAllOfCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationCaseCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationEpiDataCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationNoneOfCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationNotInStartDateRangeCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestNegativeResultCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestOtherPositiveResultCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPathogenTestPositiveResultCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationPersonAgeBetweenYearsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationSymptomsCriteriaDto;
import de.symeda.sormas.api.caze.classification.ClassificationXOfCriteriaDto;
import de.symeda.sormas.api.utils.CompatibilityCheckResponse;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.util.AppUpdateController;
import de.symeda.sormas.app.util.BiConsumer;
import de.symeda.sormas.app.util.Consumer;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetroProvider {

    private static int lastConnectionId = 0;
    private static RetroProvider instance = null;
    private static boolean connecting = false;

    private final Context context;
    private final Retrofit retrofit;

    private InfoFacadeRetro infoFacadeRetro;
    private CaseFacadeRetro caseFacadeRetro;
    private PersonFacadeRetro personFacadeRetro;
    private CommunityFacadeRetro communityFacadeRetro;
    private DistrictFacadeRetro districtFacadeRetro;
    private RegionFacadeRetro regionFacadeRetro;
    private FacilityFacadeRetro facilityFacadeRetro;
    private PointOfEntryFacadeRetro pointOfEntryFacadeRetro;
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
    private PrescriptionFacadeRetro prescriptionFacadeRetro;
    private TreatmentFacadeRetro treatmentFacadeRetro;
    private AdditionalTestFacadeRetro additionalTestFacadeRetro;
    private ClinicalVisitFacadeRetro clinicalVisitFacadeRetro;
    private DiseaseConfigurationFacadeRetro diseaseConfigurationFacadeRetro;
    private InfrastructureFacadeRetro infrastructureFacadeRetro;
    private FeatureConfigurationFacadeRetro featureConfigurationFacadeRetro;
    private AggregateReportFacadeRetro aggregateReportFacadeRetro;

    private RetroProvider(Context context) throws ServerConnectionException, ServerCommunicationException, ApiVersionException {

        lastConnectionId = this.hashCode();

        this.context = context;

        String serverUrl = ConfigProvider.getServerRestUrl();
        if (DataHelper.isNullOrEmpty(serverUrl)) {
            throw new ServerConnectionException(404);
        }

        RuntimeTypeAdapterFactory<ClassificationCriteriaDto> classificationCriteriaFactory = RuntimeTypeAdapterFactory
                .of(ClassificationCriteriaDto.class, "type")
                .registerSubtype(ClassificationAllOfCriteriaDto.class, "ClassificationAllOfCriteriaDto")
                .registerSubtype(ClassificationCaseCriteriaDto.class, "ClassificationCaseCriteriaDto")
                .registerSubtype(ClassificationNoneOfCriteriaDto.class, "ClassificationNoneOfCriteriaDto")
                .registerSubtype(ClassificationPersonAgeBetweenYearsCriteriaDto.class, "ClassificationPersonAgeBetweenYearsCriteriaDto")
                .registerSubtype(ClassificationPathogenTestPositiveResultCriteriaDto.class, "ClassificationPathogenTestPositiveResultCriteriaDto")
                .registerSubtype(ClassificationPathogenTestNegativeResultCriteriaDto.class, "ClassificationPathogenTestNegativeResultCriteriaDto")
                .registerSubtype(ClassificationPathogenTestOtherPositiveResultCriteriaDto.class, "ClassificationPathogenTestOtherPositiveResultCriteriaDto")
                .registerSubtype(ClassificationXOfCriteriaDto.class, "ClassificationXOfCriteriaDto")
                .registerSubtype(ClassificationEpiDataCriteriaDto.class, "ClassificationEpiDataCriteriaDto")
                .registerSubtype(ClassificationNotInStartDateRangeCriteriaDto.class, "ClassificationNotInStartDateRangeCriteriaDto")
                .registerSubtype(ClassificationSymptomsCriteriaDto.class, "ClassificationSymptomsCriteriaDto")
                .registerSubtype(ClassificationPathogenTestCriteriaDto.class, "ClassificationPathogenTestCriteriaDto")
                .registerSubtype(ClassificationXOfCriteriaDto.ClassificationXOfSubCriteriaDto.class, "ClassificationXOfSubCriteriaDto")
                .registerSubtype(ClassificationXOfCriteriaDto.ClassificationOneOfCompactCriteriaDto.class, "ClassificationOneOfCompactCriteriaDto")
                .registerSubtype(ClassificationAllOfCriteriaDto.ClassificationAllOfCompactCriteriaDto.class, "ClassificationAllOfCompactCriteriaDto");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context1) -> {
                    if (json.isJsonNull()) {
                        return null;
                    }
                    long milliseconds = json.getAsLong();
                    return new Date(milliseconds);
                })
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (src, typeOfSrc, context12) -> {
                    if (src == null) {
                        return JsonNull.INSTANCE;
                    }
                    return new JsonPrimitive(src.getTime());
                })
                .registerTypeAdapterFactory(classificationCriteriaFactory)
                .create();

        // Basic auth as explained in https://futurestud.io/tutorials/android-basic-authentication-with-retrofit

        String authToken = Credentials.basic(ConfigProvider.getUsername(), ConfigProvider.getPassword());
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(20, TimeUnit.SECONDS);
        httpClient.readTimeout(60, TimeUnit.SECONDS); // for infrastructure data
        httpClient.writeTimeout(30, TimeUnit.SECONDS);

        // adds "Accept-Encoding: gzip" by default
        httpClient.addInterceptor(interceptor);

        // header for logging purposes
        httpClient.addInterceptor(chain -> {

            Request original = chain.request();
            Request.Builder builder = original.newBuilder();

            User user = ConfigProvider.getUser();
            if (user != null) {
                builder.header("User", DataHelper.getShortUuid(user.getUuid()));
                builder.header("Connection", String.valueOf(lastConnectionId)); // not sure if this is a good solution
            }

            builder.method(original.method(), original.body());
            return chain.proceed(builder.build());
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        checkCompatibility();

        updateLocale();
    }

    public static int getLastConnectionId() { return lastConnectionId; }

    private void updateLocale() throws ServerCommunicationException, ServerConnectionException {
        Response<String> localeResponse;
        infoFacadeRetro = retrofit.create(InfoFacadeRetro.class);
        Call<String> localeCall = infoFacadeRetro.getLocale();
        try {
            localeResponse = localeCall.execute();
        } catch (IOException e) {
            Log.w(RetroProvider.class.getSimpleName(), e.getMessage());
            // wrap the exception message inside a response object
            localeResponse = Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), e.getMessage()));
        }

        if (localeResponse.isSuccessful()) {
            // success - now check compatibility
            String localeStr = localeResponse.body();
            ConfigProvider.setServerLocale(localeStr);
        } else {
            throwException(localeResponse);
        }
    }

    private void checkCompatibility() throws ServerCommunicationException, ServerConnectionException, ApiVersionException {

        Response<CompatibilityCheckResponse> compatibilityResponse;
        // make call to get version info
        infoFacadeRetro = retrofit.create(InfoFacadeRetro.class);
        Call<CompatibilityCheckResponse> compatibilityCall = infoFacadeRetro.isCompatibleToApi(InfoProvider.get().getVersion());
        try {
            compatibilityResponse = compatibilityCall.execute();
        } catch (IOException e) {
            Log.w(RetroProvider.class.getSimpleName(), e.getMessage());
            // wrap the exception message inside a response object
            compatibilityResponse = Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), e.getMessage()));
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

    public static boolean isConnectedOrConnecting() {
        return isConnected() || connecting;
    }

    public static void connect(Context context) throws ApiVersionException, ServerConnectionException, ServerCommunicationException {

        if (RetroProvider.isConnected()) {
            throw new IllegalStateException("Connection already established.");
        }
        if (connecting) {
            throw new IllegalStateException("Already connecting.");
        }
        if (!isConnectedToNetwork(context)) {
            throw new ServerConnectionException(600);
        }

        try {
            connecting = true;
            instance = new RetroProvider(context);
        } catch (Exception e) {
            instance = null;
            throw e;
        } finally {
            connecting = false;
        }
    }

    public static void connectAsyncHandled(FragmentActivity activity, final boolean showUpgradePrompt, final boolean matchExactVersion, final Consumer<Boolean> callback) {

        if (!(activity instanceof NotificationContext)) {
            throw new UnsupportedOperationException("Activity needs to implement NotificationContext: " + activity.toString());
        }

        WeakReference<FragmentActivity> activityReference = new WeakReference<>(activity);

        connectAsync(activity.getApplicationContext(), matchExactVersion, (result, versionCompatible) -> {
            if (result.getResultStatus().isSuccess()) {
                callback.accept(true);
            } else {
                if (result.getError() instanceof ApiVersionException) {
                    ApiVersionException e = (ApiVersionException) result.getError();
                    if (showUpgradePrompt
                            && !DataHelper.isNullOrEmpty(e.getAppUrl())
                            && activityReference.get() != null) {
                        boolean canWorkOffline = ConfigProvider.getUser() != null;
                        AppUpdateController.getInstance().updateApp(activityReference.get(), e.getAppUrl(), e.getVersion(), versionCompatible || canWorkOffline,
                                () -> callback.accept(false));
                    } else {
                        if (activityReference.get() != null) {
                            NotificationHelper.showNotification((NotificationContext) activityReference.get(), NotificationType.ERROR, e.getMessage());
                        }
                        callback.accept(false);
                    }
                } else if (result.getError() instanceof ServerConnectionException) {
                    ServerConnectionException exception = (ServerConnectionException)result.getError();

                    if (exception.getCustomHtmlErrorCode() == 401 || exception.getCustomHtmlErrorCode() == 403) {
                        // could not authenticate or user does not have access to the app
                        ConfigProvider.clearUserLogin();
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
        });
    }

    public static void connectAsync(Context context, final boolean matchExactVersion, final BiConsumer<AsyncTaskResult<TaskResultHolder>, Boolean> callback) {

        new DefaultAsyncTask(context) {

            boolean versionCompatible = false;

            @Override
            protected void doInBackground(TaskResultHolder resultHolder) throws NoConnectionException, ServerConnectionException, ServerCommunicationException, ApiVersionException {
                RetroProvider.connect(getApplicationReference().get());
                versionCompatible = true;
                if (matchExactVersion) {
                    RetroProvider.matchAppAndApiVersions(getInfoFacade());
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
                callback.accept(taskResult, versionCompatible);
            }

        }.executeOnThreadPool();
    }

    public static void disconnect() {
        instance = null;
    }

    private static void matchAppAndApiVersions(final InfoFacadeRetro infoFacadeRetro) throws ServerCommunicationException, ServerConnectionException, ApiVersionException {
        // Retrieve the version
        Response<String> versionResponse;
        Call<String> versionCall = infoFacadeRetro.getVersion();
        try {
            versionResponse = versionCall.execute();
        } catch (IOException e) {
            // wrap the exception message inside a response object
            versionResponse = Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), e.getMessage()));
        }

        if (versionResponse.isSuccessful()) {
            // Check if the versions match
            String serverApiVersion = versionResponse.body();
            String appApiVersion = InfoProvider.get().getVersion();
            if (!serverApiVersion.equals(appApiVersion)) {
                // Retrieve the app URL
                Response<String> appUrlResponse;
                Call<String> appUrlCall = infoFacadeRetro.getAppUrl(InfoProvider.get().getVersion());
                try {
                    appUrlResponse = appUrlCall.execute();
                } catch (IOException e) {
                    // wrap the exception message inside a response object
                    appUrlResponse = Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), e.getMessage()));
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

    public static InfoFacadeRetro getInfoFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        return instance.infoFacadeRetro;
    }

    public static CaseFacadeRetro getCaseFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.caseFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.caseFacadeRetro == null) {
                    instance.caseFacadeRetro = instance.retrofit.create(CaseFacadeRetro.class);
                }
            }
        }
        return instance.caseFacadeRetro;
    }

    public static PersonFacadeRetro getPersonFacade()throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.personFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.personFacadeRetro == null) {
                    instance.personFacadeRetro = instance.retrofit.create(PersonFacadeRetro.class);
                }
            }
        }
        return instance.personFacadeRetro;
    }

    public static CommunityFacadeRetro getCommunityFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.communityFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.communityFacadeRetro == null) {
                    instance.communityFacadeRetro = instance.retrofit.create(CommunityFacadeRetro.class);
                }
            }
        }
        return instance.communityFacadeRetro;
    }

    public static DistrictFacadeRetro getDistrictFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.districtFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.districtFacadeRetro == null) {
                    instance.districtFacadeRetro = instance.retrofit.create(DistrictFacadeRetro.class);
                }
            }
        }
        return instance.districtFacadeRetro;
    }

    public static RegionFacadeRetro getRegionFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.regionFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.regionFacadeRetro == null) {
                    instance.regionFacadeRetro = instance.retrofit.create(RegionFacadeRetro.class);
                }
            }
        }
        return instance.regionFacadeRetro;
    }

    public static FacilityFacadeRetro getFacilityFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.facilityFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.facilityFacadeRetro == null) {
                    instance.facilityFacadeRetro = instance.retrofit.create(FacilityFacadeRetro.class);
                }
            }
        }
        return instance.facilityFacadeRetro;
    }

    public static PointOfEntryFacadeRetro getPointOfEntryFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.pointOfEntryFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.pointOfEntryFacadeRetro == null) {
                    instance.pointOfEntryFacadeRetro = instance.retrofit.create(PointOfEntryFacadeRetro.class);
                }
            }
        }
        return instance.pointOfEntryFacadeRetro;
    }

    public static UserFacadeRetro getUserFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.userFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.userFacadeRetro == null) {
                    instance.userFacadeRetro = instance.retrofit.create(UserFacadeRetro.class);
                }
            }
        }
        return instance.userFacadeRetro;
    }

    public static TaskFacadeRetro getTaskFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.taskFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.taskFacadeRetro == null) {
                    instance.taskFacadeRetro = instance.retrofit.create(TaskFacadeRetro.class);
                }
            }
        }
        return instance.taskFacadeRetro;
    }

    public static ContactFacadeRetro getContactFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.contactFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.contactFacadeRetro == null) {
                    instance.contactFacadeRetro = instance.retrofit.create(ContactFacadeRetro.class);
                }
            }
        }
        return instance.contactFacadeRetro;
    }

    public static VisitFacadeRetro getVisitFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.visitFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.visitFacadeRetro == null) {
                    instance.visitFacadeRetro = instance.retrofit.create(VisitFacadeRetro.class);
                }
            }
        }
        return instance.visitFacadeRetro;
    }

    public static EventFacadeRetro getEventFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.eventFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.eventFacadeRetro == null) {
                    instance.eventFacadeRetro = instance.retrofit.create(EventFacadeRetro.class);
                }
            }
        }
        return instance.eventFacadeRetro;
    }

    public static SampleFacadeRetro getSampleFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.sampleFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.sampleFacadeRetro == null) {
                    instance.sampleFacadeRetro = instance.retrofit.create(SampleFacadeRetro.class);
                }
            }
        }
        return instance.sampleFacadeRetro;
    }

    public static PathogenTestFacadeRetro getSampleTestFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.pathogenTestFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.pathogenTestFacadeRetro == null) {
                    instance.pathogenTestFacadeRetro = instance.retrofit.create(PathogenTestFacadeRetro.class);
                }
            }
        }
        return instance.pathogenTestFacadeRetro;
    }

    public static EventParticipantFacadeRetro getEventParticipantFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.eventParticipantFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.eventParticipantFacadeRetro == null) {
                    instance.eventParticipantFacadeRetro = instance.retrofit.create(EventParticipantFacadeRetro.class);
                }
            }
        }
        return instance.eventParticipantFacadeRetro;
    }

    public static WeeklyReportFacadeRetro getWeeklyReportFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.weeklyReportFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.weeklyReportFacadeRetro == null) {
                    instance.weeklyReportFacadeRetro = instance.retrofit.create(WeeklyReportFacadeRetro.class);
                }
            }
        }
        return instance.weeklyReportFacadeRetro;
    }

    public static OutbreakFacadeRetro getOutbreakFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.outbreakFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.outbreakFacadeRetro == null) {
                    instance.outbreakFacadeRetro = instance.retrofit.create(OutbreakFacadeRetro.class);
                }
            }
        }
        return instance.outbreakFacadeRetro;
    }

    public static ClassificationFacadeRetro getClassificationFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.classificationFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.classificationFacadeRetro == null) {
                    instance.classificationFacadeRetro = instance.retrofit.create(ClassificationFacadeRetro.class);
                }
            }
        }
        return instance.classificationFacadeRetro;
    }

    public static UserRoleConfigFacadeRetro getUserRoleConfigFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.userRoleConfigFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.userRoleConfigFacadeRetro == null) {
                    instance.userRoleConfigFacadeRetro = instance.retrofit.create(UserRoleConfigFacadeRetro.class);
                }
            }
        }
        return instance.userRoleConfigFacadeRetro;
    }

    public static PrescriptionFacadeRetro getPrescriptionFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.prescriptionFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.prescriptionFacadeRetro == null) {
                    instance.prescriptionFacadeRetro = instance.retrofit.create(PrescriptionFacadeRetro.class);
                }
            }
        }
        return instance.prescriptionFacadeRetro;
    }

    public static TreatmentFacadeRetro getTreatmentFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.treatmentFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.treatmentFacadeRetro == null) {
                    instance.treatmentFacadeRetro = instance.retrofit.create(TreatmentFacadeRetro.class);
                }
            }
        }
        return instance.treatmentFacadeRetro;
    }

    public static AdditionalTestFacadeRetro getAdditionalTestFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.additionalTestFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.additionalTestFacadeRetro == null) {
                    instance.additionalTestFacadeRetro = instance.retrofit.create(AdditionalTestFacadeRetro.class);
                }
            }
        }
        return instance.additionalTestFacadeRetro;
    }

    public static ClinicalVisitFacadeRetro getClinicalVisitFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.clinicalVisitFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.clinicalVisitFacadeRetro == null) {
                    instance.clinicalVisitFacadeRetro = instance.retrofit.create(ClinicalVisitFacadeRetro.class);
                }
            }
        }
        return instance.clinicalVisitFacadeRetro;
    }

    public static DiseaseConfigurationFacadeRetro getDiseaseConfigurationFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.diseaseConfigurationFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.diseaseConfigurationFacadeRetro == null) {
                    instance.diseaseConfigurationFacadeRetro = instance.retrofit.create(DiseaseConfigurationFacadeRetro.class);
                }
            }
        }
        return instance.diseaseConfigurationFacadeRetro;
    }

    public static FeatureConfigurationFacadeRetro getFeatureConfigurationFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.featureConfigurationFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.featureConfigurationFacadeRetro == null) {
                    instance.featureConfigurationFacadeRetro = instance.retrofit.create(FeatureConfigurationFacadeRetro.class);
                }
            }
        }
        return instance.featureConfigurationFacadeRetro;
    }

    public static InfrastructureFacadeRetro getInfrastructureFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.infrastructureFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.infrastructureFacadeRetro == null) {
                    instance.infrastructureFacadeRetro = instance.retrofit.create(InfrastructureFacadeRetro.class);
                }
            }
        }
        return instance.infrastructureFacadeRetro;
    }

    public static AggregateReportFacadeRetro getAggregateReportFacade() throws NoConnectionException {
        if (instance == null) throw new NoConnectionException();
        if (instance.aggregateReportFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.aggregateReportFacadeRetro == null) {
                    instance.aggregateReportFacadeRetro = instance.retrofit.create(AggregateReportFacadeRetro.class);
                }
            }
        }
        return instance.aggregateReportFacadeRetro;
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
