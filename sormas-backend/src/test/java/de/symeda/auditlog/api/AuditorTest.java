/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.auditlog.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Test;

import de.symeda.auditlog.api.sample.AnonymizedEntity;
import de.symeda.auditlog.api.sample.BaseEntity;
import de.symeda.auditlog.api.sample.CollectionEntity;
import de.symeda.auditlog.api.sample.DemoBooleanFormatter;
import de.symeda.auditlog.api.sample.Entity;
import de.symeda.auditlog.api.sample.EntityWithEmbedables;
import de.symeda.auditlog.api.sample.EntityWithEmbedables.FirstEmbeddable;
import de.symeda.auditlog.api.sample.EntityWithEmbedables.NotAuditedEmbeddable;
import de.symeda.auditlog.api.sample.EntityWithHelperAttributes;
import de.symeda.auditlog.api.sample.EntityWithIgnoredMethods;
import de.symeda.auditlog.api.sample.OverridingFormatterEntity;
import de.symeda.auditlog.api.sample.SimpleBooleanFlagEntity;
import de.symeda.auditlog.api.sample.SubClassEntity;
import de.symeda.auditlog.api.sample.SuperClassEntity;
import de.symeda.auditlog.api.sample.UnauditedMiddleClassEntity;
import de.symeda.auditlog.api.value.ValueContainer;
import de.symeda.sormas.backend.auditlog.AuditLogDateHelper;

public class AuditorTest {

	@Test
	public void shouldDetectNewEntity() {

		Entity simpleEntity = new Entity("uuid-1", false, "someValue", 2);

		Auditor auditor = new Auditor();

		ChangeEvent changeEvent = auditor.detectChanges(simpleEntity);
		Map<String, String> changes = changeEvent.getNewValues();

		assertThat(changeEvent.getChangeType(), is(ChangeType.CREATE));
		assertThat(changes.size(), is(3));
		assertThat(changes.get(Entity.STRING), is("someValue"));
		assertThat(changes.get(Entity.INTEGER), is("2"));
	}

	@Test
	public void shouldDetectChangedEntity() {

		Entity simpleEntity = new Entity("uuid-1", false, "someValue", 2);

		Auditor auditor = new Auditor();
		auditor.register(simpleEntity);

		simpleEntity.setString("otherValue");
		simpleEntity.setInteger(3);

		ChangeEvent changeEvent = auditor.detectChanges(simpleEntity);
		Map<String, String> changes = changeEvent.getNewValues();

		assertThat(changeEvent.getChangeType(), is(ChangeType.UPDATE));
		assertThat(changes.size(), is(2));
		assertThat(changes.get(Entity.STRING), is("otherValue"));
		assertThat(changes.get(Entity.INTEGER), is("3"));
	}

	@Test
	public void shouldBehaveNeutralWithNull() {

		final Auditor auditor = new Auditor();
		auditor.register(null);

		Map<String, String> changes = auditor.detectChanges(null).getNewValues();

		assertThat(changes.size(), is(0));
	}

	@Test
	public void shouldUseSuppliedValueFormatter() {

		Auditor auditor = new Auditor();

		// uses a BooleanFormatter that generates 0 / 1 instead of false / true.
		SimpleBooleanFlagEntity entity = new SimpleBooleanFlagEntity("uuid-1", false);

		ValueContainer annotationChanges = auditor.inspectEntity(entity);
		String formatted = annotationChanges.getAttributes().get(SimpleBooleanFlagEntity.FLAG);

		// make sure that this Formatter is actually used
		assertThat(formatted, is(equalTo(new DemoBooleanFormatter().format(false))));
	}

	@Test
	public void shouldUseOverriddenValueFormatter() {

		Auditor auditor = new Auditor();

		// uses a BooleanFormatter that generates 0 / 1 instead of false / true.
		final Date date1 = AuditLogDateHelper.from(LocalDateTime.of(2016, Month.APRIL, 8, 14, 15));
		final Date date2 = AuditLogDateHelper.from(LocalDateTime.of(2016, Month.APRIL, 13, 10, 00));
		OverridingFormatterEntity entity = new OverridingFormatterEntity("uuid-1", date1, date2);

		ValueContainer container = auditor.inspectEntity(entity);

		final SortedMap<String, String> attributes = container.getAttributes();

		assertThat(attributes.size(), is(2));
		assertThat(attributes.get(OverridingFormatterEntity.THE_DATE), is("14:15"));
		assertThat(attributes.get(OverridingFormatterEntity.THE_DATE_WITHOUT_TEMPORAL), is("2016-04-13 10:00:00.000"));

	}

	@Test
	public void shouldRespectAttributeAnonymity() {

		Auditor auditor = new Auditor();

		AnonymizedEntity entityState1 = new AnonymizedEntity("uuid-1", "qwertz");
		AnonymizedEntity entityState2 = new AnonymizedEntity("uuid-1", "securePwd");

		ValueContainer pastState = auditor.inspectEntity(entityState1);
		ValueContainer futureState = auditor.inspectEntity(entityState2);

		final SortedMap<String, String> changes = futureState.compare(pastState);

		// the change is detected...
		assertThat(changes.size(), is(1));
		// ... but the value is correctly anonymized
		assertThat(changes.get(AnonymizedEntity.PWD), is(AnonymizedEntity.ANONYMIZING));

	}

	@Test
	public void shouldRespectExcludingAnnotations() {

		Auditor auditor = new Auditor();

		EntityWithHelperAttributes helperEntity1 = new EntityWithHelperAttributes("uuid-h1");
		EntityWithHelperAttributes helperEntity2 = new EntityWithHelperAttributes("uuid-h2");
		EntityWithHelperAttributes helperEntity3 = new EntityWithHelperAttributes("uuid-h3");
		EntityWithHelperAttributes helperEntity4 = new EntityWithHelperAttributes("uuid-h4");
		EntityWithIgnoredMethods entityState1 = new EntityWithIgnoredMethods(
			"uuid-1",
			"qwertz",
			"asdf",
			"hjkl",
			"two-123",
			helperEntity1,
			Arrays.asList(helperEntity2, helperEntity3),
			helperEntity4);
		EntityWithIgnoredMethods entityState2 = new EntityWithIgnoredMethods(
			"uuid-1",
			"ztrewq",
			"fdsa",
			"lkjh",
			"321-owt",
			helperEntity2,
			Arrays.asList(helperEntity1, helperEntity4),
			helperEntity3);

		ValueContainer pastState = auditor.inspectEntity(entityState1);
		ValueContainer futureState = auditor.inspectEntity(entityState2);

		final SortedMap<String, String> changes = futureState.compare(pastState);

		assertThat(changes.size(), is(2));
		assertThat(changes.get(EntityWithIgnoredMethods.SOME_ATTRIBUTE), is("ztrewq"));
		assertThat(changes.get(EntityWithIgnoredMethods.ONE_TO_ONE_ATTRIBUTE_UNMAPPED), is("uuid-h3"));
		assertFalse(changes.containsKey(EntityWithIgnoredMethods.IGNORED_ATTRIBUTE));
		assertFalse(changes.containsKey(EntityWithIgnoredMethods.TRANSIENT_ATTRIBUTE));
		assertFalse(changes.containsKey(EntityWithIgnoredMethods.TWO_ANNOTATIONS_ATTRIBUTE));
		assertFalse(changes.containsKey(EntityWithIgnoredMethods.ONE_TO_ONE_ATTRIBUTE));
		assertFalse(changes.containsKey(EntityWithIgnoredMethods.ONE_TO_MANY_ATTRIBUTE));
	}

	@Test
	public void shouldDetectInheritedAttributes() {

		Auditor auditor = new Auditor();

		SubClassEntity entity = new SubClassEntity("uuid-1", "Adam", 25);
		ValueContainer pastState = auditor.inspectEntity(entity);

		entity.setName("Eva");
		entity.setAge(21);
		entity.setWeight(BigDecimal.ONE);
		entity.increaseVersion();
		ValueContainer futureState = auditor.inspectEntity(entity);

		final SortedMap<String, String> changes = futureState.compare(pastState);

		assertThat(changes.size(), is(2));
		assertThat(changes.get(SuperClassEntity.NAME), is("Eva"));
		assertThat(changes.get(SubClassEntity.AGE), is("21"));
		assertFalse(changes.containsKey(BaseEntity.VERSION));
		assertFalse(changes.containsKey(UnauditedMiddleClassEntity.WEIGHT));
	}

	@Test
	public void shouldProperlyLogCollectionChanges() {

		Auditor auditor = new Auditor();

		CollectionEntity ce = new CollectionEntity("uuid-1");
		ce.getMonth().add(Month.JANUARY);
		ce.getMonth().add(Month.DECEMBER);

		ce.getStrings().add("first");
		ce.getStrings().add("second");

		ce.getSimpleEntities().add(new SimpleBooleanFlagEntity("uuid-2", false));
		ce.getSimpleEntities().add(new SimpleBooleanFlagEntity("uuid-1", true));

		SortedMap<String, String> changes = auditor.inspectEntity(ce).getAttributes();
		assertThat(changes.size(), is(4));

		assertThat(changes.get(CollectionEntity.STRINGS), is("2 [first;second]"));
		assertThat(changes.get(CollectionEntity.MONTH), is("2 [JANUARY;DECEMBER]"));
		assertThat(changes.get(CollectionEntity.SIMPLEENTITIES), is("2 [uuid-1;uuid-2]"));
		assertThat(changes.get(CollectionEntity.NULL_COLLECTION), is("[null]"));

	}

	@Test
	public void shouldProperlyLogEmbeddables() {

		NotAuditedEmbeddable notAudited = new NotAuditedEmbeddable(42);
		FirstEmbeddable fe = new FirstEmbeddable(4711, new SimpleBooleanFlagEntity("uuid-2", false), "notAudited");

		EntityWithEmbedables entity = new EntityWithEmbedables("uuid-1", "someValue", fe, notAudited);

		Auditor cut = new Auditor();

		SortedMap<String, String> changes = cut.inspectEntity(entity).getAttributes();

		assertThat(changes.size(), is(3));
		assertThat(changes.get("firstEmbeddable.integer"), is("4711"));
		assertThat(changes.get("firstEmbeddable.entity"), is("uuid-2"));
		assertThat(changes.get("someAttribute"), is("someValue"));
	}

	@Test
	public void testDetectAnnotationChangesFast() {

		Auditor auditor = new Auditor();

		Entity entity = new Entity("uuid-1", false, "changed", 42);

		// Unterschiedliche Initialisierungszeiten ausmerzen, indem inspectEntity einmal vorweg aufgerufen wird.
		long t0 = System.nanoTime();
		auditor.inspectEntity(entity);
		long t1 = System.nanoTime() - t0;

		// Hier ist der relevante Aufruf für den Test
		ValueContainer annotationChanges = auditor.inspectEntity(entity);
		long t2 = System.nanoTime() - t0 - t1;

		//sicherstellen, dass beide Varianten auch der Erwartung entsprechen (und nicht nur gleich leer sind)
		final SortedMap<String, String> annotationAttributes = annotationChanges.getAttributes();
		assertThat(annotationAttributes.size(), is(3));
		assertThat(annotationAttributes.get(Entity.FLAG), is(equalTo("false")));
		assertThat(annotationAttributes.get(Entity.STRING), is(equalTo("changed")));
		assertThat(annotationAttributes.get(Entity.INTEGER), is(equalTo("42")));
		assertThat(t2, lessThan(1000_000L));
		System.out.println("AuditorTest.testDetectAnnotationChangesFast(): t1= " + t1 + " nanos, t2= " + t2 + " nanos");
	}
}
