package de.symeda.sormas.app.lbds;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Collections;
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
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
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
		PersonDao personDao = DatabaseHelper.getPersonDao();
		List<Person> modifiedEntities = personDao.getModifiedEntities();

		// PersonDtoHelper personDtoHelper = new PersonDtoHelper();

		String payload = "[]";
		if (!modifiedEntities.isEmpty()) {
			Person firstEntry = modifiedEntities.get(0);
			PersonDto personDto = new PersonDtoHelper().adoToDto(firstEntry);
			payload = createRequest(personDto);
		}
		Log.i("SORMAS_LBDS", "Send object: " + payload);

		String authBasicCredentials = ConfigProvider.getUsername() + ":" + ConfigProvider.getPassword();
		String headers = "Authorization: Basic " + Base64.encodeToString(authBasicCredentials.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
		// "Content-Type: application/json";

		HttpMethod method = new HttpMethod(HttpMethod.MethodType.POST, "http://localhost:6070/sormas-rest/persons/push", headers, payload);
		// HttpMethod method = new HttpMethod(HttpMethod.MethodType.GET, "http://perdu.com");

		String lbdsAesSecret = ConfigProvider.getLbdsAesSecret();
		Log.i("SORMAS_LBDS", "AES secret: " + lbdsAesSecret);
		HttpContainer httpContainer = new HttpContainer(method);
		LbdsSendIntent lbdsSendIntent = new LbdsSendIntent(httpContainer, lbdsAesSecret);
		lbdsSendIntent.setComponent(LbdsRelated.componentName);

		HttpContainer httpContainerRead = lbdsSendIntent.getHttpContainer(lbdsAesSecret);
		Log.i("SORMAS_LBDS", "HttpContainer: " + httpContainerRead);

		ContextCompat.startForegroundService(context, lbdsSendIntent);
		Log.i("SORMAS_LBDS", "==========================");
	}

	private static String createRequest(PersonDto personDto) {
		try {
			LbdsDtoHelper.stripLbdsDto(personDto);
		} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
			e.printStackTrace();
		}
		Retrofit retrofit = RetroProvider.buildRetrofit("http://localhost:6070/sormas-rest/");
		PersonFacadeRetro personFacadeRetro = retrofit.create(PersonFacadeRetro.class);
		Call<List<PushResult>> call = personFacadeRetro.pushAll(Collections.singletonList(personDto));
		Buffer buffer = new Buffer();
		try {
			call.request().body().writeTo(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = buffer.readUtf8();

		Log.i("SORMAS_LBDS", "==========================");
		Log.i("SORMAS_LBDS", json);
		Log.i("SORMAS_LBDS", "==========================");

		return json;
	}
}
