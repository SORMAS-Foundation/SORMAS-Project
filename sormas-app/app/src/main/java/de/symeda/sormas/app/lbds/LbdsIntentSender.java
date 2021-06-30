package de.symeda.sormas.app.lbds;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.hzi.sormas.lbds.core.http.HttpContainer;
import org.hzi.sormas.lbds.core.http.HttpMethod;
import org.hzi.sormas.lbds.messaging.LbdsPropagateKexToLbdsIntent;
import org.hzi.sormas.lbds.messaging.LbdsRelated;
import org.hzi.sormas.lbds.messaging.LbdsSendIntent;
import org.hzi.sormas.lbds.messaging.util.KeySerializationUtil;

import com.googlecode.openbeans.IntrospectionException;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.ContextCompat;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.rest.CaseFacadeRetro;
import de.symeda.sormas.app.rest.PersonFacadeRetro;
import de.symeda.sormas.app.rest.RetroProvider;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Retrofit;

public class LbdsIntentSender {

	public static void sendKexLbdsIntent(Context context) {
		Log.i("SORMAS_LBDS", "==========================");
		Log.i("SORMAS_LBDS", "Key Exchange LBDS");

		try {
			PublicKey lbdsSormasPublicKey = ConfigProvider.getLbdsSormasPublicKey();
			Log.i("SORMAS_LBDS", "send SORMAS public key: " + KeySerializationUtil.serializePublicKey(lbdsSormasPublicKey));
			LbdsPropagateKexToLbdsIntent kexToLbdsIntent = new LbdsPropagateKexToLbdsIntent(lbdsSormasPublicKey);
			ContextCompat.startForegroundService(context, kexToLbdsIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("SORMAS_LBDS", "==========================");
	}

	public static void sendLbdsSendIntent(Context context) {
		Log.i("SORMAS_LBDS", "==========================");
		Log.i("SORMAS_LBDS", "Sync LBDS");
		sendPersonsLbds(context);
		sendCasesLbds(context);
		Log.i("SORMAS_LBDS", "==========================");
	}

	private static void sendPersonsLbds(Context context) {

		Log.i("SORMAS_LBDS", "Persons-------------------");
		PersonDao personDao = DatabaseHelper.getPersonDao();
		List<Person> modifiedPersons = personDao.getModifiedEntities();
		List<PersonDto> personsToSend = new ArrayList<>();
		PersonDtoHelper personDtoHelper = new PersonDtoHelper();

		for (Person person : modifiedPersons) {
			PersonDto personDto = personDtoHelper.adoToDto(person);
			Person snapshot = personDao.querySnapshotByUuid(person.getUuid());

			boolean isModifiedLbds = false;
			try {
				LbdsDtoHelper.stripLbdsDto(personDto);
				isModifiedLbds = LbdsDtoHelper.isModifiedLbds(snapshot, personDto, false);
				if (isModifiedLbds) {
					personsToSend.add(personDto);
				}
			} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
				throw new IllegalArgumentException("LBDS preparation failed for person " + person.getUuid(), e);
			}
		}

		if (!personsToSend.isEmpty()) {
			String headers = getAuthHeader();
			String payload = createLbdsPayloadPersons(personsToSend);
			Log.i("SORMAS_LBDS", "Send persons: " + payload);
			HttpMethod method = new HttpMethod(HttpMethod.MethodType.POST, "http://localhost:6070/sormas-rest/persons/push", headers, payload);
			sendLbdsRequest(context, method);
		} else {
			Log.i("SORMAS_LBDS", "Nothing to send.");
		}
	}

	private static void sendCasesLbds(Context context) {

		Log.i("SORMAS_LBDS", "Cases---------------------");
		CaseDao caseDao = DatabaseHelper.getCaseDao();
		List<Case> modifiedCases = caseDao.getModifiedEntities();
		List<CaseDataDto> casesToSend = new ArrayList<>();
		CaseDtoHelper caseDtoHelper = new CaseDtoHelper();

		for (Case caze : modifiedCases) {
			CaseDataDto caseDataDto = caseDtoHelper.adoToDto(caze);
			Case snapshot = caseDao.querySnapshotByUuid(caze.getUuid());

			boolean isModifiedLbds = false;
			try {
				LbdsDtoHelper.stripLbdsDto(caseDataDto);
				isModifiedLbds = LbdsDtoHelper.isModifiedLbds(snapshot, caseDataDto, false);
				if (isModifiedLbds) {
					casesToSend.add(caseDataDto);
				}
			} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
				throw new IllegalArgumentException("LBDS preparation failed for case " + caze.getUuid(), e);
			}
		}

		if (!casesToSend.isEmpty()) {
			String headers = getAuthHeader();
			String payload = createLbdsPayloadCases(casesToSend);
			Log.i("SORMAS_LBDS", "Send cases: " + payload);
			HttpMethod method = new HttpMethod(HttpMethod.MethodType.POST, "http://localhost:6070/sormas-rest/cases/push", headers, payload);
			sendLbdsRequest(context, method);
		} else {
			Log.i("SORMAS_LBDS", "Nothing to send.");
		}
	}

	private static void sendLbdsRequest(Context context, HttpMethod method) {

		String lbdsAesSecret = ConfigProvider.getLbdsAesSecret();
		Log.i("SORMAS_LBDS", "AES secret: " + lbdsAesSecret);
		HttpContainer httpContainer = new HttpContainer(method);
		LbdsSendIntent lbdsSendIntent = new LbdsSendIntent(httpContainer, lbdsAesSecret);
		lbdsSendIntent.setComponent(LbdsRelated.componentName);

		HttpContainer httpContainerRead = lbdsSendIntent.getHttpContainer(lbdsAesSecret);
		Log.i("SORMAS_LBDS", "HttpContainer: " + httpContainerRead);

		ContextCompat.startForegroundService(context, lbdsSendIntent);
	}

	private static String getAuthHeader() {

		String authBasicCredentials = ConfigProvider.getUsername() + ":" + ConfigProvider.getPassword();
		return "Authorization: Basic " + Base64.encodeToString(authBasicCredentials.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
	}

	private static String createLbdsPayloadPersons(List<PersonDto> personsToSend) {

		Retrofit retrofit = RetroProvider.buildRetrofit("http://localhost:6070/sormas-rest/");
		PersonFacadeRetro personFacadeRetro = retrofit.create(PersonFacadeRetro.class);
		Call<List<PushResult>> call = personFacadeRetro.pushAll(personsToSend);
		Buffer buffer = new Buffer();
		try {
			call.request().body().writeTo(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = buffer.readUtf8();
		return json;
	}

	private static String createLbdsPayloadCases(List<CaseDataDto> casesToSend) {

		Retrofit retrofit = RetroProvider.buildRetrofit("http://localhost:6070/sormas-rest/");
		CaseFacadeRetro caseFacadeRetro = retrofit.create(CaseFacadeRetro.class);
		Call<List<PushResult>> call = caseFacadeRetro.pushAll(casesToSend);
		Buffer buffer = new Buffer();
		try {
			call.request().body().writeTo(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = buffer.readUtf8();
		return json;
	}
}
