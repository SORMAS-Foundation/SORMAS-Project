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

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.lbds.LbdsSyncDao;
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

			NotificationHelper.showNotification(
				(NotificationContext) context,
				NotificationType.INFO,
				context.getResources().getString(R.string.info_lbds_key_exchange_started));

			context.sendBroadcast(kexToLbdsIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("SORMAS_LBDS", "==========================");
	}

	public static void sendNewCasePersonsLbds(Context context) {

		Log.i("SORMAS_LBDS", "==========================");
		Log.i("SORMAS_LBDS", "Sync Persons LBDS");
		LbdsSyncDao lbdsSyncDao = DatabaseHelper.getLbdsSyncDao();
		List<PersonDto> personsToSend = getNewPersonsToSendLbds();

		if (!personsToSend.isEmpty()) {
			List<String> personUuids = personsToSend.stream().map(PersonDto::getUuid).collect(Collectors.toList());
			Log.i("SORMAS_LBDS", "Send persons: " + StringUtils.join(personUuids, ", "));
			HttpMethod method = createLbdsHttpMethodPersons(personsToSend);
			sendLbdsRequest(context, method);

			for (String personUuid : personUuids) {
				lbdsSyncDao.logLbdsSend(personUuid);
			}

			NotificationHelper.showNotification(
				(NotificationContext) context,
				NotificationType.SUCCESS,
				String.format(context.getResources().getString(R.string.lbds_persons_transferred), personsToSend.size()));
		} else {
			Log.i("SORMAS_LBDS", "Nothing to send.");
			NotificationHelper.showNotification(
				(NotificationContext) context,
				NotificationType.INFO,
				context.getResources().getString(R.string.info_lbds_no_person_to_transfer));
		}
		Log.i("SORMAS_LBDS", "==========================");
	}

	public static void sendNewCasesLbds(Context context) {

		Log.i("SORMAS_LBDS", "==========================");
		Log.i("SORMAS_LBDS", "Sync Cases LBDS");
		List<CaseDataDto> casesToSend = getNewCasesToSendLbds();
		LbdsSyncDao lbdsSyncDao = DatabaseHelper.getLbdsSyncDao();

		if (!casesToSend.isEmpty()) {
			List<String> caseUuids = casesToSend.stream().map(CaseDataDto::getUuid).collect(Collectors.toList());
			Log.i("SORMAS_LBDS", "Send cases: " + StringUtils.join(caseUuids, ", "));
			HttpMethod method = createLbdsHttpMethodCases(casesToSend);
			sendLbdsRequest(context, method);

			for (String caseUuid : caseUuids) {
				lbdsSyncDao.logLbdsSend(caseUuid);
			}

			NotificationHelper.showNotification(
				(NotificationContext) context,
				NotificationType.SUCCESS,
				String.format(context.getResources().getString(R.string.lbds_cases_transferred), casesToSend.size()));
		} else {
			Log.i("SORMAS_LBDS", "Nothing to send.");
			NotificationHelper.showNotification(
				(NotificationContext) context,
				NotificationType.INFO,
				context.getResources().getString(R.string.info_lbds_no_case_to_transfer));
		}
		Log.i("SORMAS_LBDS", "==========================");
	}

	public static List<PersonDto> getNewPersonsToSendLbds() {
		List<CaseDataDto> casesToSend = getNewCasesToSendLbds();
		PersonDao personDao = DatabaseHelper.getPersonDao();
		LbdsSyncDao lbdsSyncDao = DatabaseHelper.getLbdsSyncDao();
		PersonDtoHelper personDtoHelper = new PersonDtoHelper();
		List<PersonDto> personsToSend = new ArrayList<>();

		for (CaseDataDto caze : casesToSend) {
			Person person = personDao.queryUuid(caze.getPerson().getUuid());
			if ((!person.isNew()) || lbdsSyncDao.hasBeenSuccessfullySent(person)) {
				continue;
			}
			PersonDto personDto = personDtoHelper.adoToDto(person);
			try {
				LbdsDtoHelper.stripLbdsDto(personDto);
			} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
				throw new IllegalArgumentException("LBDS preparation failed for person " + person.getUuid(), e);
			}
			personsToSend.add(personDto);
		}

		return personsToSend;
	}

	private static List<CaseDataDto> getNewCasesToSendLbds() {
		CaseDao caseDao = DatabaseHelper.getCaseDao();
		LbdsSyncDao lbdsSyncDao = DatabaseHelper.getLbdsSyncDao();
		CaseDtoHelper caseDtoHelper = new CaseDtoHelper();
		List<Case> modifiedCases = caseDao.getModifiedEntities();
		List<CaseDataDto> casesToSend = new ArrayList<>();

		for (Case caze : modifiedCases) {
			if ((!caze.isNew()) || lbdsSyncDao.hasBeenSuccessfullySent(caze)) {
				continue;
			}
			CaseDataDto caseDataDto = caseDtoHelper.adoToDto(caze);
			if (caseDataDto.getHealthFacility() == null) {
				caseDataDto.setHealthFacility(new FacilityReferenceDto(FacilityDto.NONE_FACILITY_UUID));
			}
			try {
				LbdsDtoHelper.stripLbdsDto(caseDataDto);
			} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
				throw new IllegalArgumentException("LBDS preparation failed for case " + caze.getUuid(), e);
			}
			casesToSend.add(caseDataDto);
		}

		return casesToSend;
	}

	private static void sendLbdsRequest(Context context, HttpMethod method) {

		String lbdsAesSecret = ConfigProvider.getLbdsAesSecret();
		HttpContainer httpContainer = new HttpContainer(method);
		LbdsSendIntent lbdsSendIntent = new LbdsSendIntent(httpContainer, lbdsAesSecret);
		lbdsSendIntent.setComponent(LbdsRelated.componentName);

		HttpContainer httpContainerRead = lbdsSendIntent.getHttpContainer(lbdsAesSecret);
		Log.i("SORMAS_LBDS", "HttpContainer: " + httpContainerRead);

		context.sendBroadcast(lbdsSendIntent);
	}

	private static String getAuthHeader() {

		String authToken = Credentials.basic(ConfigProvider.getUsername(), ConfigProvider.getPassword());
		return "Authorization= " + authToken;
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
