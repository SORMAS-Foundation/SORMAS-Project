package de.symeda.sormas.app.lbds;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hzi.sormas.lbds.core.http.HttpContainer;
import org.hzi.sormas.lbds.core.http.HttpMethod;
import org.hzi.sormas.lbds.messaging.LbdsPropagateKexToLbdsIntent;
import org.hzi.sormas.lbds.messaging.LbdsRelated;
import org.hzi.sormas.lbds.messaging.LbdsSendIntent;
import org.hzi.sormas.lbds.messaging.util.KeySerializationUtil;

import com.googlecode.openbeans.IntrospectionException;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.rest.CaseFacadeRetro;
import de.symeda.sormas.app.rest.PersonFacadeRetro;
import de.symeda.sormas.app.rest.RetroProvider;
import okhttp3.Credentials;
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

	public static void sendNewCasePersonsLbds(Context context) {

		Log.i("SORMAS_LBDS", "==========================");
		Log.i("SORMAS_LBDS", "Sync Persons LBDS");
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
			Log.i(
				"SORMAS_LBDS",
				"Send persons: " + StringUtils.join(personsToSend.stream().map(PersonDto::getUuid).collect(Collectors.toList()), ", "));
			HttpMethod method = createLbdsHttpMethodPersons(personsToSend);
			sendLbdsRequest(context, method);
			NotificationHelper
				.showNotification((NotificationContext) context, NotificationType.SUCCESS, "LBDS Sync. Persons transferred: " + personsToSend.size());
		} else {
			Log.i("SORMAS_LBDS", "Nothing to send.");
			NotificationHelper.showNotification((NotificationContext) context, NotificationType.INFO, "LBDS Sync. No person to transfer");
		}
		Log.i("SORMAS_LBDS", "==========================");
	}

	public static void sendNewCasesLbds(Context context) {

		Log.i("SORMAS_LBDS", "==========================");
		Log.i("SORMAS_LBDS", "Sync Cases LBDS");
		CaseDao caseDao = DatabaseHelper.getCaseDao();
		List<Case> modifiedCases = caseDao.getModifiedEntities();
		List<CaseDataDto> casesToSend = new ArrayList<>();
		CaseDtoHelper caseDtoHelper = new CaseDtoHelper();

		for (Case caze : modifiedCases) {
			CaseDataDto caseDataDto = caseDtoHelper.adoToDto(caze);
			if (caseDataDto.getHealthFacility() == null) {
				caseDataDto.setHealthFacility(new FacilityReferenceDto(FacilityDto.NONE_FACILITY_UUID));
			}
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
			Log.i(
				"SORMAS_LBDS",
				"Send cases: " + StringUtils.join(casesToSend.stream().map(CaseDataDto::getUuid).collect(Collectors.toList()), ", "));
			HttpMethod method = createLbdsHttpMethodCases(casesToSend);
			sendLbdsRequest(context, method);
			NotificationHelper
				.showNotification((NotificationContext) context, NotificationType.SUCCESS, "LBDS Sync. Cases transferred: " + casesToSend.size());
		} else {
			Log.i("SORMAS_LBDS", "Nothing to send.");
			NotificationHelper.showNotification((NotificationContext) context, NotificationType.INFO, "LBDS Sync. No case to transfer");
		}
		Log.i("SORMAS_LBDS", "==========================");
	}

	private static void sendLbdsRequest(Context context, HttpMethod method) {

		String lbdsAesSecret = ConfigProvider.getLbdsAesSecret();
		HttpContainer httpContainer = new HttpContainer(method);
		LbdsSendIntent lbdsSendIntent = new LbdsSendIntent(httpContainer, lbdsAesSecret);
		lbdsSendIntent.setComponent(LbdsRelated.componentName);

		HttpContainer httpContainerRead = lbdsSendIntent.getHttpContainer(lbdsAesSecret);
		Log.i("SORMAS_LBDS", "HttpContainer: " + httpContainerRead);

		ContextCompat.startForegroundService(context, lbdsSendIntent);
	}

	private static String getAuthHeader() {

		String authToken = Credentials.basic(ConfigProvider.getUsername(), ConfigProvider.getPassword());
		return "Authorization: " + authToken;
	}

	private static HttpMethod createLbdsHttpMethodPersons(List<PersonDto> personsToSend) {

		Retrofit retrofit = RetroProvider.buildRetrofit(getLbdsUrl());
		PersonFacadeRetro personFacadeRetro = retrofit.create(PersonFacadeRetro.class);
		Call<List<PushResult>> call = personFacadeRetro.pushAll(personsToSend);

		return toHttpMethod(call);
	}

	private static HttpMethod createLbdsHttpMethodCases(List<CaseDataDto> casesToSend) {

		Retrofit retrofit = RetroProvider.buildRetrofit(getLbdsUrl());
		CaseFacadeRetro caseFacadeRetro = retrofit.create(CaseFacadeRetro.class);
		Call<List<PushResult>> call = caseFacadeRetro.pushAll(casesToSend);

		return toHttpMethod(call);
	}

	private static HttpMethod toHttpMethod(Call<?> call) {

		Buffer buffer = new Buffer();
		try {
			call.request().body().writeTo(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String payload = buffer.readUtf8();
		String headers = getAuthHeader();
		HttpMethod httpMethod = new HttpMethod(HttpMethod.MethodType.POST, call.request().url().toString(), headers, payload);

		return httpMethod;
	}

	private static String getLbdsUrl() {
		String lbdsUrl = ConfigProvider.getServerLbdsDebugUrl();
		if (lbdsUrl == null || lbdsUrl.isEmpty() || StringUtils.isBlank(lbdsUrl)) {
			lbdsUrl = ConfigProvider.getServerRestUrl();
		}
		return lbdsUrl;
	}
}
