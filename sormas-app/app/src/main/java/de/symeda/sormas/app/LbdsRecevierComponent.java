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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.lbds.LbdsSyncDao;
import de.symeda.sormas.app.rest.RetroProvider;

public class LbdsRecevierComponent extends BroadcastReceiver {

	public LbdsRecevierComponent() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
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
					processLbdsResponsePersons(context, httpContainerResponse);
				} else if (methodFromResponse.url.endsWith("cases/push")) {
					processLbdsResponseCases(context, httpContainerResponse);
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

				Toast.makeText(context, context.getResources().getString(R.string.info_lbds_key_exchange_successful), Toast.LENGTH_LONG).show();

				break;
			default:
				Log.i("SORMAS_LBDS", "unknown LBDS intent");
			}
		}
		Log.i("SORMAS_LBDS", "==========================");
	}

	private void processLbdsResponsePersons(Context context, HttpContainer httpContainerResponse) {

		HttpMethod methodFromResponse = httpContainerResponse.getMethod();
		HttpResult resultFromResponse = httpContainerResponse.getResult();

		Log.i("SORMAS_LBDS", "Request: " + methodFromResponse);
		if (resultFromResponse == null) {
			Log.i("SORMAS_LBDS", "Result is null");
			return;
		}
		Log.i("SORMAS_LBDS", "Result Headers: " + resultFromResponse.headers);
		Log.i("SORMAS_LBDS", "Result Body: " + resultFromResponse.body);

		Gson gson = RetroProvider.initGson();
		List<PersonDto> sentPersonDtos = gson.fromJson(methodFromResponse.payload, new TypeToken<List<PersonDto>>() {
		}.getType());
		List<PostResponse> responsePostResponses = gson.fromJson(resultFromResponse.body, new TypeToken<List<PostResponse>>() {
		}.getType());
		Log.i("SORMAS_LBDS", "sent " + sentPersonDtos.size() + " PersonDtos and received " + responsePostResponses.size() + " PostResponses");

		if (sentPersonDtos.size() != responsePostResponses.size()) {
			Log.i("SORMAS_LBDS", "List lengths differ, abort.");
			throw new IllegalArgumentException("Number of sent Dtos and received PostResponses must be equal");
		}

		LbdsSyncDao lbdsSyncDao = DatabaseHelper.getLbdsSyncDao();
		int successful = 0;
		int ignored = 0;

		for (int i = 0; i < sentPersonDtos.size(); i++) {
			PersonDto personDto = sentPersonDtos.get(i);
			String uuid = personDto.getUuid();
			PostResponse postResponse = responsePostResponses.get(i);
			if (postResponse.getStatusCode() == 200) {
				Log.i("SORMAS_LBDS", "Process PostResponse " + postResponse + " for PersonDto " + uuid);
				lbdsSyncDao.logLbdsReceive(uuid);
				successful++;
			} else {
				Log.i("SORMAS_LBDS", "Ignore PostResponse " + postResponse + " for PersonDto " + uuid);
				ignored++;
			}
		}

		Toast
			.makeText(
				context,
				String.format(context.getResources().getString(R.string.lbds_response_persons), successful, ignored),
				Toast.LENGTH_LONG)
			.show();
	}

	private void processLbdsResponseCases(Context context, HttpContainer httpContainerResponse) {

		HttpMethod methodFromResponse = httpContainerResponse.getMethod();
		HttpResult resultFromResponse = httpContainerResponse.getResult();

		Log.i("SORMAS_LBDS", "Request: " + methodFromResponse);
		if (resultFromResponse == null) {
			Log.i("SORMAS_LBDS", "Result is null");
			return;
		}
		Log.i("SORMAS_LBDS", "Result Headers: " + resultFromResponse.headers);
		Log.i("SORMAS_LBDS", "Result Body: " + resultFromResponse.body);

		Gson gson = RetroProvider.initGson();
		List<CaseDataDto> sentCaseDataDtos = gson.fromJson(methodFromResponse.payload, new TypeToken<List<CaseDataDto>>() {
		}.getType());
		List<PostResponse> responsePostResponses = gson.fromJson(resultFromResponse.body, new TypeToken<List<PostResponse>>() {
		}.getType());
		Log.i("SORMAS_LBDS", "sent " + sentCaseDataDtos.size() + " CaseDataDtos and received " + responsePostResponses.size() + " PostResponses");

		if (sentCaseDataDtos.size() != responsePostResponses.size()) {
			Log.i("SORMAS_LBDS", "List lengths differ, abort.");
			throw new IllegalArgumentException("Number of sent Dtos and received PostResponses must be equal");
		}

		LbdsSyncDao lbdsSyncDao = DatabaseHelper.getLbdsSyncDao();
		int successful = 0;
		int ignored = 0;

		for (int i = 0; i < sentCaseDataDtos.size(); i++) {
			CaseDataDto caseDataDto = sentCaseDataDtos.get(i);
			String uuid = caseDataDto.getUuid();
			PostResponse postResponse = responsePostResponses.get(i);
			if (postResponse.getStatusCode() == 200) {
				Log.i("SORMAS_LBDS", "Process PostResponse " + postResponse + " for CaseDataDto " + uuid);
				lbdsSyncDao.logLbdsReceive(uuid);
				successful++;
			} else {
				Log.i("SORMAS_LBDS", "Ignore PostResponse " + postResponse + " for CaseDataDto " + uuid);
				ignored++;
			}
		}

		Toast.makeText(context, String.format(context.getResources().getString(R.string.lbds_response_cases), successful, ignored), Toast.LENGTH_LONG)
			.show();
	}
}
