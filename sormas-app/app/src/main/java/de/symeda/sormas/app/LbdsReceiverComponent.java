package de.symeda.sormas.app;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.hzi.sormas.lbds.core.http.HttpContainer;
import org.hzi.sormas.lbds.core.http.HttpMethod;
import org.hzi.sormas.lbds.messaging.Constants;
import org.hzi.sormas.lbds.messaging.IntentType;
import org.hzi.sormas.lbds.messaging.LbdsPropagateKexToLbdsIntent;
import org.hzi.sormas.lbds.messaging.util.KeySerializationUtil;

import java.security.PublicKey;
import java.util.Collections;
import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LbdsReceiverComponent extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public LbdsReceiverComponent() {
        super("LbdsReceiverComponent");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("LBDS", "Test");
        final String intentType = intent.getStringExtra(Constants.INTENT_TYPE);
        if (intentType != null && !intentType.trim().isEmpty()) {
            IntentType type = IntentType.valueOf(intentType);
            switch (type) {
                case HTTP_SEND_INTENT:
                    final String httpContainer = intent.getStringExtra(Constants.HTTP_CONTAINER);
                    HttpContainer container = HttpContainer.deserializePackedHttpContainer(httpContainer);
                    HttpMethod method = container.getMethod();
                    String url = method.url;
                    String payload = method.payload;
                    Log.i("LBDS", "url=" + url + ", payload=" + payload);

                    BaseActivity activeActivity = BaseActivity.getActiveActivity();
                    if (activeActivity == null || !activeActivity.isEditing()) {
                        if (ConfigProvider.getUser() != null && !RetroProvider.isConnectedOrConnecting()) {
                            RetroProvider.connectAsync(getApplicationContext(), false, (result, versionCompatible) -> {
                                if (result.getResultStatus().isSuccess()) {
                                    pushData(url, payload);
                                }
                            });
                        } else {
                            pushData(url, payload);
                        }
                    }
                    break;
                case KEX_TO_LBDS_INTENT:
                    final String publicKey = intent.getStringExtra(Constants.SORMAS_KEY);
                    final PublicKey key = KeySerializationUtil.deserializePublicKey(publicKey);

                    break;
                case HTTP_RESPONSE_INTENT:
                    break;
                case KEX_TO_SORMAS_INTENT:
                    break;
            }
        }
    }

    private void pushData(final String url, final String payload) {
        try {
            if (url.contains("/persons/push")) {
                PersonDtoHelper personDtoHelper = new PersonDtoHelper();
                PersonDto personDto = new Gson().fromJson(payload, PersonDto.class);
                Call<List<PushResult>> listCall = personDtoHelper.pushAll(Collections.singletonList(personDto));
                listCall.enqueue(new PushResultCallback());
            } else if (url.contains("/cases/push")) {
                CaseDtoHelper caseDtoHelper = new CaseDtoHelper();
                CaseDataDto caseDataDto = new Gson().fromJson(payload, CaseDataDto.class);
                Call<List<PushResult>> listCall = caseDtoHelper.pushAll(Collections.singletonList(caseDataDto));
                listCall.enqueue(new PushResultCallback());
            }
        } catch (NoConnectionException e) {
            Log.e("LBDS", "Error connecting to backend: " + e.getMessage());
        }
    }

    private static class PushResultCallback implements Callback<List<PushResult>> {

        @Override
        public void onResponse(Call<List<PushResult>> call, Response<List<PushResult>> response) {

        }

        @Override
        public void onFailure(Call<List<PushResult>> call, Throwable t) {

        }
    }

}
