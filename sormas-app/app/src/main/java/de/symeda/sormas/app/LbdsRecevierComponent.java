package de.symeda.sormas.app;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Collections;
import java.util.List;

import org.hzi.sormas.lbds.core.http.HttpContainer;
import org.hzi.sormas.lbds.core.http.HttpMethod;
import org.hzi.sormas.lbds.core.http.HttpResult;
import org.hzi.sormas.lbds.messaging.Constants;
import org.hzi.sormas.lbds.messaging.IntentType;
import org.hzi.sormas.lbds.messaging.IntentTypeCarrying;
import org.hzi.sormas.lbds.messaging.LbdsPropagateKexToSormasIntent;
import org.hzi.sormas.lbds.messaging.LbdsResponseIntent;
import org.hzi.sormas.lbds.messaging.util.KeySerializationUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.IntentService;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;

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

public class LbdsRecevierComponent extends IntentService {

	/**
	 * Creates an IntentService. Invoked by your subclass's constructor.
	 */
	public LbdsRecevierComponent() {
		super("LbdsRecevierComponent");
		// super(SormasRelated.SORMAS_RECEIVER_COMPONENT);
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		Log.i("SORMAS_LBDS", "==========================");
		final String intentType = intent.getStringExtra(Constants.INTENT_TYPE);
		Log.i("SORMAS_LBDS", "Received LBDS intent: " + intentType);
		if (intentType != null && !intentType.trim().isEmpty()) {
			IntentType type = IntentType.valueOf(intentType);
			switch (type) {
			case HTTP_SEND_INTENT:
				final String httpContainer = intent.getStringExtra(Constants.HTTP_CONTAINER);
				HttpContainer container = HttpContainer.deserializePackedHttpContainer(httpContainer);
				HttpMethod method = container.getMethod();
				String url = method.url;
				String payload = method.payload;
				Log.i("SORMAS_LBDS", "url=" + url + ", payload=" + payload);

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
				LbdsResponseIntent responseIntent = (LbdsResponseIntent) IntentTypeCarrying.toStrongTypedIntent(intent);
				HttpContainer httpContainerResponse = responseIntent.getHttpContainer(ConfigProvider.getLbdsAesSecret());
				HttpMethod methodFromResponse = httpContainerResponse.getMethod();
				HttpResult resultFromResponse = httpContainerResponse.getResult();
				Log.i("SORMAS_LBDS", "Request: " + methodFromResponse);
				Log.i("SORMAS_LBDS", "Result Headers: " + resultFromResponse.headers);
				Log.i("SORMAS_LBDS", "Result Body: " + resultFromResponse.body);

				try {
					Gson gson = RetroProvider.initGson();
					List<PersonDto> sendPersonDtos = gson.fromJson(methodFromResponse.payload, new TypeToken<List<PersonDto>>() {
					}.getType());
					List<PushResult> responsePushResults = gson.fromJson(resultFromResponse.body, new TypeToken<List<PushResult>>() {
					}.getType());
					Log.i("SORMAS_LBDS", "sent " + sendPersonDtos.size() + " PersonDtos and received " + responsePushResults.size() + " PushResults");
				} catch (Exception e) {
					Log.i("SORMAS_LBDS", e.getMessage());
					e.printStackTrace();
				}

				break;
			case KEX_TO_SORMAS_INTENT:
				LbdsPropagateKexToSormasIntent kexToSormasIntent = (LbdsPropagateKexToSormasIntent) IntentTypeCarrying.toStrongTypedIntent(intent);

				Log.i("SORMAS_LBDS", "Process LbdsPropagateKexToSormasIntent..");

				Log.i("SORMAS_LBDS", "Sormas public key: " + kexToSormasIntent.getSormasKey());

				PublicKey lbdsKey = kexToSormasIntent.getLbdsKey();
				ConfigProvider.setLbdsServicePublicKey(lbdsKey);
				Log.i("SORMAS_LBDS", "Lbds public key: " + lbdsKey);

				String aesSecret = kexToSormasIntent.getAesSecret(ConfigProvider.getLbdsSormasPrivateKey());
				ConfigProvider.setLbdsAesSecret(aesSecret);
				Log.i("SORMAS_LBDS", "Lbds AES secret: " + Base64.encodeToString(aesSecret.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
				break;
			default:
				Log.i("SORMAS_LBDS", "unknown LBDS intent");
			}
		}
		Log.i("SORMAS_LBDS", "==========================");
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
