package de.symeda.sormas.app;

import java.security.PublicKey;
import java.util.List;

import org.hzi.sormas.lbds.core.http.HttpContainer;
import org.hzi.sormas.lbds.core.http.HttpMethod;
import org.hzi.sormas.lbds.core.http.HttpResult;
import org.hzi.sormas.lbds.messaging.Constants;
import org.hzi.sormas.lbds.messaging.IntentType;
import org.hzi.sormas.lbds.messaging.IntentTypeCarrying;
import org.hzi.sormas.lbds.messaging.LbdsPropagateKexToSormasIntent;
import org.hzi.sormas.lbds.messaging.LbdsResponseIntent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.lbds.LbdsSyncDao;
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
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		Log.i("SORMAS_LBDS", "==========================");
		final String intentType = intent.getStringExtra(Constants.INTENT_TYPE);
		Log.i("SORMAS_LBDS", "Received LBDS intent: " + intentType);
		if (intentType != null && !intentType.trim().isEmpty()) {
			IntentType type = IntentType.valueOf(intentType);
			switch (type) {
			case HTTP_RESPONSE_INTENT:
				// HTTP responses received from the LBDS service app
				LbdsResponseIntent responseIntent = (LbdsResponseIntent) IntentTypeCarrying.toStrongTypedIntent(intent);
				HttpContainer httpContainerResponse = responseIntent.getHttpContainer(ConfigProvider.getLbdsAesSecret());

				HttpMethod methodFromResponse = httpContainerResponse.getMethod();
				if (methodFromResponse == null || methodFromResponse.url == null) {
					throw new IllegalArgumentException("Missing HTTP request or HTTP request URL");
				}

				if (methodFromResponse.url.endsWith("persons/push")) {
					processLbdsResponsePersons(httpContainerResponse);
				} else if (methodFromResponse.url.endsWith("cases/push")) {
					processLbdsResponseCases(httpContainerResponse);
				} else {
					throw new IllegalArgumentException("Unknown HTTP request URL " + methodFromResponse.url);
				}

				break;
			case KEX_TO_SORMAS_INTENT:
				// Key Exchange: public key and AES secret are transferred from the LBDS service app
				LbdsPropagateKexToSormasIntent kexToSormasIntent = (LbdsPropagateKexToSormasIntent) IntentTypeCarrying.toStrongTypedIntent(intent);

				Log.i("SORMAS_LBDS", "Process LbdsPropagateKexToSormasIntent..");
				Log.i("SORMAS_LBDS", "Sormas public key: " + kexToSormasIntent.getSormasKey());

				PublicKey lbdsKey = kexToSormasIntent.getLbdsKey();
				ConfigProvider.setLbdsServicePublicKey(lbdsKey);
				Log.i("SORMAS_LBDS", "Lbds public key: " + lbdsKey);

				String aesSecret = kexToSormasIntent.getAesSecret(ConfigProvider.getLbdsSormasPrivateKey());
				ConfigProvider.setLbdsAesSecret(aesSecret);

				Toast.makeText(getApplicationContext(), getResources().getString(R.string.info_lbds_key_exchange_successful), Toast.LENGTH_LONG)
					.show();

				break;
			default:
				Log.i("SORMAS_LBDS", "unknown LBDS intent");
			}
		}
		Log.i("SORMAS_LBDS", "==========================");
	}

	private void processLbdsResponsePersons(HttpContainer httpContainerResponse) {

		HttpMethod methodFromResponse = httpContainerResponse.getMethod();
		HttpResult resultFromResponse = httpContainerResponse.getResult();

		Log.i("SORMAS_LBDS", "Request: " + methodFromResponse);
		Log.i("SORMAS_LBDS", "Result Headers: " + resultFromResponse.headers);
		Log.i("SORMAS_LBDS", "Result Body: " + resultFromResponse.body);

		Gson gson = RetroProvider.initGson();
		List<PersonDto> sentPersonDtos = gson.fromJson(methodFromResponse.payload, new TypeToken<List<PersonDto>>() {
		}.getType());
		List<PushResult> responsePushResults = gson.fromJson(resultFromResponse.body, new TypeToken<List<PushResult>>() {
		}.getType());
		Log.i("SORMAS_LBDS", "sent " + sentPersonDtos.size() + " PersonDtos and received " + responsePushResults.size() + " PushResults");

		if (sentPersonDtos.size() != responsePushResults.size()) {
			Log.i("SORMAS_LBDS", "List lengths differ, abort.");
			throw new IllegalArgumentException("Number of sent Dtos and received PushResults must be equal");
		}

		LbdsSyncDao lbdsSyncDao = DatabaseHelper.getLbdsSyncDao();
		int successful = 0;
		int ignored = 0;

		for (int i = 0; i < sentPersonDtos.size(); i++) {
			PersonDto personDto = sentPersonDtos.get(i);
			String uuid = personDto.getUuid();
			PushResult pushResult = responsePushResults.get(i);
			if (pushResult == PushResult.OK) {
				Log.i("SORMAS_LBDS", "Process PushResult " + pushResult + " for PersonDto " + uuid);
				lbdsSyncDao.logLbdsReceive(uuid);
				successful++;
			} else {
				Log.i("SORMAS_LBDS", "Ignore PushResult " + pushResult + " for PersonDto " + uuid);
				ignored++;
			}
		}

		Toast
			.makeText(
				getApplicationContext(),
				String.format(getResources().getString(R.string.lbds_response_persons), successful, ignored),
				Toast.LENGTH_LONG)
			.show();
	}

	private void processLbdsResponseCases(HttpContainer httpContainerResponse) {

		HttpMethod methodFromResponse = httpContainerResponse.getMethod();
		HttpResult resultFromResponse = httpContainerResponse.getResult();

		Log.i("SORMAS_LBDS", "Request: " + methodFromResponse);
		Log.i("SORMAS_LBDS", "Result Headers: " + resultFromResponse.headers);
		Log.i("SORMAS_LBDS", "Result Body: " + resultFromResponse.body);

		Gson gson = RetroProvider.initGson();
		List<CaseDataDto> sentCaseDataDtos = gson.fromJson(methodFromResponse.payload, new TypeToken<List<CaseDataDto>>() {
		}.getType());
		List<PushResult> responsePushResults = gson.fromJson(resultFromResponse.body, new TypeToken<List<PushResult>>() {
		}.getType());
		Log.i("SORMAS_LBDS", "sent " + sentCaseDataDtos.size() + " CaseDataDtos and received " + responsePushResults.size() + " PushResults");

		if (sentCaseDataDtos.size() != responsePushResults.size()) {
			Log.i("SORMAS_LBDS", "List lengths differ, abort.");
			throw new IllegalArgumentException("Number of sent Dtos and received PushResults must be equal");
		}

		LbdsSyncDao lbdsSyncDao = DatabaseHelper.getLbdsSyncDao();
		int successful = 0;
		int ignored = 0;

		for (int i = 0; i < sentCaseDataDtos.size(); i++) {
			CaseDataDto caseDataDto = sentCaseDataDtos.get(i);
			String uuid = caseDataDto.getUuid();
			PushResult pushResult = responsePushResults.get(i);
			if (pushResult == PushResult.OK) {
				Log.i("SORMAS_LBDS", "Process PushResult " + pushResult + " for CaseDataDto " + uuid);
				lbdsSyncDao.logLbdsReceive(uuid);
				successful++;
			} else {
				Log.i("SORMAS_LBDS", "Ignore PushResult " + pushResult + " for CaseDataDto " + uuid);
				ignored++;
			}
		}

		Toast
			.makeText(
				getApplicationContext(),
				String.format(getResources().getString(R.string.lbds_response_cases), successful, ignored),
				Toast.LENGTH_LONG)
			.show();
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
