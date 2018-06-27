package de.symeda.sormas.app.rest;

import android.accounts.AuthenticatorException;
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
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import de.symeda.sormas.api.utils.CompatibilityCheckResponse;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
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
    private SampleTestFacadeRetro sampleTestFacadeRetro;
    private EventParticipantFacadeRetro eventParticipantFacadeRetro;
    private WeeklyReportFacadeRetro weeklyReportFacadeRetro;
    private WeeklyReportEntryFacadeRetro weeklyReportEntryFacadeRetro;
    private OutbreakFacadeRetro outbreakFacadeRetro;

    private RetroProvider(Context context, Interceptor... additionalInterceptors) throws ApiVersionException, ConnectException, AuthenticatorException {

        this.context = context;

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
                .create();

        // Basic auth as explained in https://futurestud.io/tutorials/android-basic-authentication-with-retrofit

        String authToken = Credentials.basic(ConfigProvider.getUsername(), ConfigProvider.getPassword());
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(5*60, TimeUnit.SECONDS); // for infrastructure data - actually 30 seconds should be enough...
        // adds "Accept-Encoding: gzip" by default
        httpClient.addInterceptor(interceptor);
        for (Interceptor additionalInterceptor : additionalInterceptors) {
            httpClient.addInterceptor(additionalInterceptor);
        }

        // check rest api version
        Response<CompatibilityCheckResponse> compatibilityResponse;
        try {

            retrofit = new Retrofit.Builder()
                    .baseUrl(ConfigProvider.getServerRestUrl())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();

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

        } catch (IllegalArgumentException e) {
            throw new ConnectException(e.getMessage());
        } catch (InterruptedException e) {
            throw new ConnectException(e.getMessage());
        } catch (ExecutionException e) {
            throw new ConnectException(e.getMessage());
        }

        if (compatibilityResponse.isSuccessful()) {
            // success - now check compatibility
            CompatibilityCheckResponse compatibilityCheckResponse = compatibilityResponse.body();
            if (compatibilityCheckResponse == CompatibilityCheckResponse.TOO_NEW) {
                throw new ConnectException("Could not synchronize because the app version is newer than the server version.");
            } else if (compatibilityCheckResponse == CompatibilityCheckResponse.TOO_OLD) {
                // get the current server version, throw an exception including the app url that is then processed in the UI
                matchAppAndApiVersions(infoFacadeRetro);
            }
        }
        else {
            switch (compatibilityResponse.code()) {
                case 401:
                    throw new AuthenticatorException(context.getResources().getString(R.string.snackbar_http_401));
                case 403:
                    throw new AuthenticatorException(context.getResources().getString(R.string.snackbar_http_403));
                case 404:
                    throw new ConnectException(String.format(context.getResources().getString(R.string.snackbar_http_404), ConfigProvider.getServerRestUrl()));
                default:
                    String error;
                    try {
                        error = compatibilityResponse.errorBody().string();
                    } catch (IOException e) {
                        error = compatibilityResponse.raw().toString();
                    }
                    throw new ConnectException(error);
            }
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

    public static void connect(Context context) throws ApiVersionException, ConnectException, AuthenticatorException {

        if (!isConnectedToNetwork(context)) {
            throw new ConnectException(context.getResources().getString(R.string.snackbar_no_connection));
        }

        try {
            instance = new RetroProvider(context);
        } catch (Exception e){
            instance = null;
            throw e;
        }
    }

    public static void disconnect() {
        instance = null;
    }

    public static void matchAppAndApiVersions() throws ConnectException, ApiVersionException {
        matchAppAndApiVersions(null);
    }

    private static void matchAppAndApiVersions(final InfoFacadeRetro localInfoFacadeRetro) throws ConnectException, ApiVersionException {
        // Retrieve the version
        Response<String> versionResponse;
        try {
            AsyncTask<Void, Void, Response<String>> asyncTask = new AsyncTask<Void, Void, Response<String>>() {

                @Override
                protected Response<String> doInBackground(Void... params) {
                    Call<String> versionCall = localInfoFacadeRetro != null ? localInfoFacadeRetro.getVersion() : getInfoFacade().getVersion();
                    try {
                        return versionCall.execute();
                    } catch (IOException e) {
                        // wrap the exception message inside a response object
                        return Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), e.getMessage()));
                    }
                }
            };
            versionResponse = asyncTask.execute().get();
        } catch (IllegalArgumentException e) {
            throw new ConnectException(e.getMessage());
        } catch (InterruptedException e) {
            throw new ConnectException(e.getMessage());
        } catch (ExecutionException e) {
            throw new ConnectException(e.getMessage());
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
                            Call<String> versionCall = localInfoFacadeRetro != null ? localInfoFacadeRetro.getAppUrl(InfoProvider.get().getVersion()) : getInfoFacade().getAppUrl(InfoProvider.get().getVersion());
                            try {
                                return versionCall.execute();
                            } catch (IOException e) {
                                // wrap the exception message inside a response object
                                return Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), e.getMessage()));
                            }
                        }
                    };
                    appUrlResponse = asyncTask.execute().get();

                } catch (IllegalArgumentException e) {
                    throw new ConnectException(e.getMessage());
                } catch (InterruptedException e) {
                    throw new ConnectException(e.getMessage());
                } catch (ExecutionException e) {
                    throw new ConnectException(e.getMessage());
                }

                if (appUrlResponse.isSuccessful()) {
                    throw new ApiVersionException("App version '" + appApiVersion + "' does not match server version '" + serverApiVersion + "'", appUrlResponse.body(), serverApiVersion);
                } else {
                    throw new ApiVersionException("App version '" + appApiVersion + "' does not match server version '" + serverApiVersion + "'");
                }
            }
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

    public static SampleTestFacadeRetro getSampleTestFacade() {
        if (instance.sampleTestFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.sampleTestFacadeRetro == null) {
                    instance.sampleTestFacadeRetro = instance.retrofit.create(SampleTestFacadeRetro.class);
                }
            }
        }
        return instance.sampleTestFacadeRetro;
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

    public static WeeklyReportEntryFacadeRetro getWeeklyReportEntryFacade() {
        if (instance.weeklyReportEntryFacadeRetro == null) {
            synchronized ((RetroProvider.class)) {
                if (instance.weeklyReportEntryFacadeRetro == null) {
                    instance.weeklyReportEntryFacadeRetro = instance.retrofit.create(WeeklyReportEntryFacadeRetro.class);
                }
            }
        }
        return instance.weeklyReportEntryFacadeRetro;
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

    public static class ApiVersionException extends Exception {
        private String appUrl;
        private String version;
        public ApiVersionException() {
            super();
        }
        public ApiVersionException(String message) {
            super(message);
        }
        public ApiVersionException(String message, String appUrl, String version) {
            super(message);
            this.appUrl = appUrl;
            this.version = version;
        }
        public ApiVersionException(String message, Throwable cause) {
            super(message, cause);
        }
        public ApiVersionException(Throwable cause) {
            super(cause);
        }
        public String getAppUrl() {
            return appUrl;
        }
        public String getVersion() {
            return version;
        }
    }
}
