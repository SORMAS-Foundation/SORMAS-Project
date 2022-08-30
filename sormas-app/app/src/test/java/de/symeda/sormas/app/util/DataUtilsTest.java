package de.symeda.sormas.app.util;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hzi.sormas.lbds.core.http.HttpResult;
import org.junit.Test;

import com.google.gson.Gson;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.component.Item;

public class DataUtilsTest {

	@Test
	public void getEnumItems() {

		List<Item> enumItems = DataUtils.getEnumItems(YesNoUnknown.class, true);
		assertThat(enumItems.size(), is(YesNoUnknown.values().length + 1));

		enumItems = DataUtils.getEnumItems(YesNoUnknown.class, false);
		assertThat(enumItems.size(), is(YesNoUnknown.values().length));

		String headers =
			"Server\u003dnginx\nDate\u003dFri, 08 Jul 2022 19:03:18 GMT\nContent-Type\u003dapplication/json;charset\u003dUTF-8\nContent-Length\u003d42\nConnection\u003dkeep-alive\nX-Frame-Options\u003dSAMEORIGIN\nStrict-Transport-Security\u003dmax-age\u003d31536000; includeSubDomains\nX-XSS-Protection\u003d1; mode\u003dblock\nReferrer-Policy\u003dno-referrer\nX-Content-Type-Options\u003dnosniff\nAuthorization\u003d Basic SGVpbk1laWU6eHFRV2VqQTNDVDJp\n";
		String body = "[\"TRANSACTION_ROLLED_BACK_EXCEPTION\",\"OK\"]";
		HttpResult httpResult = new HttpResult(200, headers, body);

		String serializedHttpResult = new Gson().toJson(httpResult);
		System.out.println(serializedHttpResult);

		HttpResult deserializedHttpResult = new Gson().fromJson(serializedHttpResult, HttpResult.class);

		assertEquals(httpResult.body, deserializedHttpResult.body);
		assertEquals(httpResult.headers, deserializedHttpResult.headers);

		assertThat(deserializedHttpResult.headers, is(httpResult.headers));
	}

	@Test(expected = NullPointerException.class)
	public void getEnumItemsNullPointer() {
		DataUtils.getEnumItems(null, false);
	}
}
