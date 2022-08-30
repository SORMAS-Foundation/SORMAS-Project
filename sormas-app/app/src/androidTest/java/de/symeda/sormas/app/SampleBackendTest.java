/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.hzi.sormas.lbds.core.http.HttpContainer;
import org.hzi.sormas.lbds.core.http.HttpResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.Gson;

import android.util.Base64;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;

/**
 * Created by Mate Strysewske on 16.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class SampleBackendTest {

	@Rule
	public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

	@Before
	public void initTest() {
		TestHelper.initTestEnvironment(false);
	}

	@Test
	public void shouldCreateSample() {
		assertThat(DatabaseHelper.getSampleDao().queryForAll().size(), is(0));
		TestEntityCreator.createSample(null);

		assertThat(DatabaseHelper.getSampleDao().queryForAll().size(), is(1));
	}

	@Test
	public void deserializesLbdsIntent() {
		String serializedContainer =
			"{\"httpMethod\":{\"headers\":\"Authorization\\u003d Basic SGVpbk1laWU6eHFRV2VqQTNDVDJp\",\"methodType\":\"POST\",\"payload\":\"[{\\\"addresses\\\":[],\\\"covidCodeDelivered\\\":false,\\\"firstName\\\":\\\"Ernestina\\\",\\\"hasCovidApp\\\":false,\\\"lastName\\\":\\\"Cravello\\\",\\\"personContactDetails\\\":[],\\\"sex\\\":\\\"FEMALE\\\",\\\"pseudonymized\\\":false,\\\"changeDate\\\":0,\\\"creationDate\\\":1657131679225,\\\"uuid\\\":\\\"VXCH6N-YKUJHP-DLCLER-WLS5CHB4\\\"},{\\\"addresses\\\":[],\\\"covidCodeDelivered\\\":false,\\\"firstName\\\":\\\"Lucia\\\",\\\"hasCovidApp\\\":false,\\\"lastName\\\":\\\"Sanchez Saornil\\\",\\\"personContactDetails\\\":[],\\\"sex\\\":\\\"FEMALE\\\",\\\"pseudonymized\\\":false,\\\"changeDate\\\":0,\\\"creationDate\\\":1657306786054,\\\"uuid\\\":\\\"VBGYYQ-SABBGD-AYSU2D-DTW52IOY\\\"}]\",\"url\":\"https://lbds.sormas.netzlink.com/sormas-rest/persons/push\"},\"httpResult\":{\"body\":\"[\\\"TRANSACTION_ROLLED_BACK_EXCEPTION\\\",\\\"OK\\\"]\",\"headers\":\"Server\\u003dnginx\\nDate\\u003dFri, 08 Jul 2022 19:03:18 GMT\\nContent-Type\\u003dapplication/json;charset\\u003dUTF-8\\nContent-Length\\u003d42\\nConnection\\u003dkeep-alive\\nX-Frame-Options\\u003dSAMEORIGIN\\nStrict-Transport-Security\\u003dmax-age\\u003d31536000; includeSubDomains\\nX-XSS-Protection\\u003d1; mode\\u003dblock\\nReferrer-Policy\\u003dno-referrer\\nX-Content-Type-Options\\u003dnosniff\\nAuthorization\\u003d Basic SGVpbk1laWU6eHFRV2VqQTNDVDJp\\n\",\"httpCode\":200},\"id\":\"8c20b4e9-28f5-4109-a171-576f6b8f2ed2\"}";
		HttpContainer httpContainer = new Gson().fromJson(serializedContainer, HttpContainer.class);

		HttpResult httpResult = httpContainer.getResult();

		String encryptedContainer = "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";

		String decryptedContainer = decryptContainer("PV2ZHBoLljpMZuKxmrAG9nyrUEnwCgaCBuPmllY5LDE=", encryptedContainer);
		assertThat(decryptedContainer, is(serializedContainer));

		String headers =
			"Server\u003dnginx\nDate\u003dFri, 08 Jul 2022 19:03:18 GMT\nContent-Type\u003dapplication/json;charset\u003dUTF-8\nContent-Length\u003d42\nConnection\u003dkeep-alive\nX-Frame-Options\u003dSAMEORIGIN\nStrict-Transport-Security\u003dmax-age\u003d31536000; includeSubDomains\nX-XSS-Protection\u003d1; mode\u003dblock\nReferrer-Policy\u003dno-referrer\nX-Content-Type-Options\u003dnosniff\nAuthorization\u003d Basic SGVpbk1laWU6eHFRV2VqQTNDVDJp\n";
		String body = "[\"TRANSACTION_ROLLED_BACK_EXCEPTION\",\"OK\"]";

		HttpContainer httpContainerFromDecrypted = new Gson().fromJson(decryptedContainer, HttpContainer.class);
		HttpResult httpResultFromDecrypted = httpContainerFromDecrypted.getResult();

		assertThat(httpResultFromDecrypted.headers, is(headers));
		assertThat(httpResultFromDecrypted.body, is(body));

		String serializedHttpResult = new Gson().toJson(httpResult);
		System.out.println(serializedHttpResult);

		HttpResult deserializedHttpResult = new Gson().fromJson(serializedHttpResult, HttpResult.class);

		assertThat(deserializedHttpResult.headers, is(headers));
		assertThat(deserializedHttpResult.body, is(body));
	}

	private String decryptContainer(String secret, String encryptedString) {
		try {
			byte[] key = secret.getBytes(StandardCharsets.UTF_8);
			final byte[] bytes = Base64.decode(encryptedString.getBytes(StandardCharsets.UTF_8), 0);
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(bytes), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void shouldCreateSampleTest() {
		assertThat(DatabaseHelper.getSampleTestDao().queryForAll().size(), is(0));

		Sample sample = TestEntityCreator.createSample(null);
		TestEntityCreator.createSampleTest(sample);

		assertThat(DatabaseHelper.getSampleTestDao().queryForAll().size(), is(1));
	}

	@Test
	public void shouldMergeSamplesAsExpected() throws DaoException {
		Sample sample = TestEntityCreator.createSample(null);
		PathogenTest pathogenTest = TestEntityCreator.createSampleTest(sample);

		sample.setComment("AppSampleComment");
		pathogenTest.setTestResult(PathogenTestResultType.NEGATIVE);

		DatabaseHelper.getSampleDao().saveAndSnapshot(sample);
		DatabaseHelper.getSampleDao().accept(sample);
		DatabaseHelper.getSampleTestDao().saveAndSnapshot(pathogenTest);
		DatabaseHelper.getSampleTestDao().accept(pathogenTest);

		Sample mergeSample = (Sample) sample.clone();
		mergeSample.setAssociatedCase((Case) sample.getAssociatedCase().clone());
		mergeSample.setId(null);
		mergeSample.setComment("ServerSampleComment");

		PathogenTest mergePathogenTest = (PathogenTest) pathogenTest.clone();
		mergePathogenTest.setId(null);
		mergePathogenTest.setTestResult(PathogenTestResultType.POSITIVE);

		DatabaseHelper.getSampleDao().mergeOrCreate(mergeSample);
		DatabaseHelper.getSampleTestDao().mergeOrCreate(mergePathogenTest);

		Sample updatedSample = DatabaseHelper.getSampleDao().queryUuid(sample.getUuid());
		assertThat(updatedSample.getComment(), is("ServerSampleComment"));
		PathogenTest updatedPathogenTest = DatabaseHelper.getSampleTestDao().queryUuid(pathogenTest.getUuid());
		assertThat(updatedPathogenTest.getTestResult(), is(PathogenTestResultType.POSITIVE));
	}

	@Test
	public void shouldAcceptAsExpected() throws DaoException {
		Sample sample = TestEntityCreator.createSample(null);
		assertThat(sample.isModified(), is(false));

		sample.setComment("NewSampleComment");

		DatabaseHelper.getSampleDao().saveAndSnapshot(sample);
		sample = DatabaseHelper.getSampleDao().queryUuid(sample.getUuid());

		assertThat(sample.isModified(), is(true));
		assertNotNull(DatabaseHelper.getSampleDao().querySnapshotByUuid(sample.getUuid()));

		DatabaseHelper.getSampleDao().accept(sample);
		sample = DatabaseHelper.getSampleDao().queryUuid(sample.getUuid());

		assertNull(DatabaseHelper.getSampleDao().querySnapshotByUuid(sample.getUuid()));
		assertThat(sample.isModified(), is(false));
	}
}
