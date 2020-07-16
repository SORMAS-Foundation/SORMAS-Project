/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

@RunWith(MockitoJUnitRunner.class)
public class EventFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;

	@Override
	public void init() {

		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator
			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator
			.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");
	}

	@Test
	public void testEventInJurisdiction() {
		EventDto event = createEvent(user2, rdcf2);

		assertNotPseudonymized(getEventFacade().getEventByUuid(event.getUuid()));
	}

	@Test
	public void testEventOutsideJurisdiction() {
		EventDto event = createEvent(user1, rdcf1);

		assertPseudonymized(getEventFacade().getEventByUuid(event.getUuid()));
	}

	@Test
	public void testUpdateOutsideJurisdiction() {
		EventDto event = createEvent(user1, rdcf1);

		event.setEventDesc(null);
		event.setSrcFirstName(null);
		event.setSrcLastName(null);

		event.getEventLocation().setAddress(null);
		event.getEventLocation().setPostalCode(null);

		getEventFacade().saveEvent(event);

		Event savedEvent = getEventService().getByUuid(event.getUuid());

		assertThat(savedEvent.getEventDesc(), is("Test Description"));
		assertThat(savedEvent.getSrcFirstName(), is("John"));
		assertThat(savedEvent.getSrcLastName(), is("Smith"));
		assertThat(savedEvent.getEventLocation().getAddress(), is("Test address"));
		assertThat(savedEvent.getEventLocation().getPostalCode(), is("123456"));
	}

	@Test
	public void testUpdateWithPseudonymizedDto() {
		EventDto event = createEvent(user2, rdcf2);

		event.setPseudonymized(true);
		event.setEventDesc(null);
		event.setSrcFirstName(null);
		event.setSrcLastName(null);

		event.getEventLocation().setPseudonymized(true);
		event.getEventLocation().setAddress(null);
		event.getEventLocation().setPostalCode(null);

		getEventFacade().saveEvent(event);

		Event savedEvent = getEventService().getByUuid(event.getUuid());

		assertThat(savedEvent.getEventDesc(), is("Test Description"));
		assertThat(savedEvent.getSrcFirstName(), is("John"));
		assertThat(savedEvent.getSrcLastName(), is("Smith"));
		assertThat(savedEvent.getEventLocation().getAddress(), is("Test address"));
		assertThat(savedEvent.getEventLocation().getPostalCode(), is("123456"));
	}

	@Test
	public void testPseudonymizeGetByUuids() {
		EventDto event1 = createEvent(user2, rdcf2);
		EventDto event2 = createEvent(user1, rdcf1);

		List<EventDto> events = getEventFacade().getByUuids(Arrays.asList(event1.getUuid(), event2.getUuid()));

		assertNotPseudonymized(events.stream().filter(e -> e.getUuid().equals(event1.getUuid())).findFirst().get());
		assertPseudonymized(events.stream().filter(e -> e.getUuid().equals(event2.getUuid())).findFirst().get());
	}

	private EventDto createEvent(UserDto user, TestDataCreator.RDCF rdcf) {
		return creator.createEvent(EventStatus.POSSIBLE, "Test Description", user.toReference(), e -> {
			e.setSrcFirstName("John");
			e.setSrcLastName("Smith");
			e.setSrcTelNo("12345678");
			e.setSrcEmail("test@email.com");

			e.setSurveillanceOfficer(user.toReference());

			e.setReportLat(46.432);
			e.setReportLon(23.234);
			e.setReportLatLonAccuracy(10F);

			e.getEventLocation().setRegion(rdcf.region);
			e.getEventLocation().setDistrict(rdcf.district);
			e.getEventLocation().setCommunity(rdcf.community);
			e.getEventLocation().setAddress("Test address");
			e.getEventLocation().setPostalCode("123456");
			e.getEventLocation().setDetails("Test address details");
		});
	}

	private void assertNotPseudonymized(EventDto event) {
		assertThat(event.getEventDesc(), is("Test Description"));
		assertThat(event.getSrcFirstName(), is("John"));
		assertThat(event.getSrcLastName(), is("Smith"));
		assertThat(event.getSrcTelNo(), is("12345678"));
		assertThat(event.getSrcEmail(), is("test@email.com"));
		assertThat(event.getSurveillanceOfficer(), is(user2));
		assertThat(event.getReportLat(), is(46.432));
		assertThat(event.getReportLon(), is(23.234));
		assertThat(event.getEventLocation().getCommunity(), is(rdcf2.community));
		assertThat(event.getEventLocation().getAddress(), is("Test address"));
		assertThat(event.getEventLocation().getPostalCode(), is("123456"));
		assertThat(event.getEventLocation().getDetails(), is("Test address details"));
	}

	private void assertPseudonymized(EventDto event) {
		assertThat(event.getEventDesc(), isEmptyString());
		assertThat(event.getSrcFirstName(), isEmptyString());
		assertThat(event.getSrcLastName(), isEmptyString());
		assertThat(event.getSrcTelNo(), isEmptyString());
		assertThat(event.getSrcEmail(), isEmptyString());
		assertThat(event.getSurveillanceOfficer(), is(nullValue()));
		assertThat(event.getReportLat().toString(), startsWith("46."));
		assertThat(event.getReportLat(), is(not(46.432)));
		assertThat(event.getReportLon().toString(), startsWith("23."));
		assertThat(event.getReportLon(), is(not(23.234)));
		assertThat(event.getEventLocation().getCommunity(), is(nullValue()));
		assertThat(event.getEventLocation().getAddress(), isEmptyString());
		assertThat(event.getEventLocation().getPostalCode(), is("123"));
		assertThat(event.getEventLocation().getDetails(), isEmptyString());
	}
}
