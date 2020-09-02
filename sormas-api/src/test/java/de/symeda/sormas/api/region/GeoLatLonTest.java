package de.symeda.sormas.api.region;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.Test;

public class GeoLatLonTest {

	@Test
	public void testGetters() {

		double lat = 2;
		double lon = 3;
		GeoLatLon ll = new GeoLatLon(lat, lon);

		assertThat(ll.getLat(), is(lat));
		assertThat(ll.getLon(), is(lon));
	}

	@Test
	public void testClone() {

		double lat = 2;
		double lon = 3;
		GeoLatLon ll = new GeoLatLon(lat, lon);

		GeoLatLon c = ll.clone();

		assertThat(c.getLat(), is(lat));
		assertThat(c.getLon(), is(lon));

		assertThat(c, is(ll));
		assertThat(c, not(sameInstance(ll)));
	}
}
