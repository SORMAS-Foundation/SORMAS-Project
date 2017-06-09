package de.symeda.auditlog.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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

import de.symeda.auditlog.api.sample.Entity;
import de.symeda.auditlog.api.sample.AnonymizedEntity;
import de.symeda.auditlog.api.sample.BaseEntity;
import de.symeda.auditlog.api.sample.CollectionEntity;
import de.symeda.auditlog.api.sample.DemoBooleanFormatter;
import de.symeda.auditlog.api.sample.EntityWithEmbedables;
import de.symeda.auditlog.api.sample.EntityWithHelperAttributes;
import de.symeda.auditlog.api.sample.EntityWithIgnoredMethods;
import de.symeda.auditlog.api.sample.EntityWithEmbedables.FirstEmbeddable;
import de.symeda.auditlog.api.sample.EntityWithEmbedables.NotAuditedEmbeddable;
import de.symeda.auditlog.api.sample.OverridingFormatterEntity;
import de.symeda.auditlog.api.sample.SimpleBooleanFlagEntity;
import de.symeda.auditlog.api.sample.SimpleEntity;
import de.symeda.auditlog.api.sample.SubClassEntity;
import de.symeda.auditlog.api.sample.SuperClassEntity;
import de.symeda.auditlog.api.sample.UnauditedMiddleClassEntity;
import de.symeda.auditlog.api.value.ValueContainer;
import de.symeda.sormas.backend.auditlog.AuditLogDateHelper;

public class AuditorTest {

	private static final String FIRST_ATTRIBUTE = "firstAttribute";
	private static final String ID = "Id";

	@Test
	public void shouldDetectNewEntity() {

		final String entityId = "1";
		final String firstValue = "someValue";

		AuditedEntity simpleEntity = new SimpleEntity("uuid-1");
		simpleEntity.inspectAttributes().put(ID, entityId);
		simpleEntity.inspectAttributes().put(FIRST_ATTRIBUTE, firstValue);

		ChangeEvent changeEvent = new Auditor().detectChanges(simpleEntity);
		Map<String, String> changes = changeEvent.getNewValues();

		assertThat(changeEvent.getChangeType(), is(ChangeType.CREATE));
		assertThat(changes.size(), is(2));
		assertThat(changes.get(ID), is(entityId));
		assertThat(changes.get(FIRST_ATTRIBUTE), is(firstValue));
	}

	@Test
	public void shouldDetectChangedEntity() {

		final String entityId = "1";
		final String firstValue = "someValue";

		AuditedEntity simpleEntity = new SimpleEntity("uuid-1");
		simpleEntity.inspectAttributes().put(ID, entityId);
		simpleEntity.inspectAttributes().put(FIRST_ATTRIBUTE, firstValue);

		final Auditor auditor = new Auditor();
		auditor.register(simpleEntity);

		simpleEntity.inspectAttributes().put(FIRST_ATTRIBUTE, "someChangedValue");
		ChangeEvent changeEvent = auditor.detectChanges(simpleEntity);
		Map<String, String> changes = changeEvent.getNewValues();

		assertThat(changeEvent.getChangeType(), is(ChangeType.UPDATE));
		assertThat(changes.size(), is(1));
		assertThat(changes.get(FIRST_ATTRIBUTE), is("someChangedValue"));
	}

	@Test
	public void shouldBehaveNeutralWithNull() {

		final Auditor auditor = new Auditor();
		auditor.register(null);

		Map<String, String> changes = auditor.detectChanges(null).getNewValues();

		assertThat(changes.size(), is(0));
	}

	@Test
	public void shouldDetectAnnotationChanges() {

		Auditor auditor = new Auditor();

		Entity entity = new Entity("uuid-1", false, "changed", 42);

		ValueContainer annotationChanges = auditor.inspectEntity(entity);
		ValueContainer inspectAttributes = entity.inspectAttributes();

		//beide Varianten der ChangeDetection vergleichen 
		SortedMap<String, String> comparedAttributes = inspectAttributes.compare(annotationChanges);
		assertThat(comparedAttributes.size(), is(0));

		//sicherstellen, dass beide Varianten auch der Erwartung entsprechen (und nicht nur gleich leer sind)
		final SortedMap<String, String> annotationAttributes = annotationChanges.getAttributes();
		assertThat(annotationAttributes.size(), is(3));
		assertThat(annotationAttributes.get(Entity.FLAG), is(equalTo("false")));
		assertThat(annotationAttributes.get(Entity.STRING), is(equalTo("changed")));
		assertThat(annotationAttributes.get(Entity.INTEGER), is(equalTo("42")));
	}

	@Test
	public void shouldUseSuppliedValueFormatter() {

		Auditor auditor = new Auditor();

		//benutzt einen BooleanFormatter, der 0 / 1 generiert statt false / true.
		SimpleBooleanFlagEntity entity = new SimpleBooleanFlagEntity("uuid-1", false);

		ValueContainer annotationChanges = auditor.inspectEntity(entity);
		String formatted = annotationChanges.getAttributes().get(SimpleBooleanFlagEntity.FLAG);

		//sicherstellen, dass dieser Formatter auch genutzt wird
		assertThat(formatted, is(equalTo(new DemoBooleanFormatter().format(false))));
	}

	@Test
	public void shouldUseOverriddenValueFormatter() {

		Auditor auditor = new Auditor();

		//benutzt einen BooleanFormatter, der 0 / 1 generiert statt false / true.
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

		//der change wird festgestellt...
		assertThat(changes.size(), is(1));
		//jedoch wird der Wert korrekt anonymisiert
		assertThat(changes.get(AnonymizedEntity.PWD), is(AnonymizedEntity.ANONYMIZING));

	}
	
	@Test
	public void shouldRespectExcludingAnnotations() {
		
		Auditor auditor = new Auditor();
		
		EntityWithHelperAttributes helperEntity1 = new EntityWithHelperAttributes("uuid-h1");
		EntityWithHelperAttributes helperEntity2 = new EntityWithHelperAttributes("uuid-h2");
		EntityWithHelperAttributes helperEntity3 = new EntityWithHelperAttributes("uuid-h3");
		EntityWithHelperAttributes helperEntity4 = new EntityWithHelperAttributes("uuid-h4");
		EntityWithIgnoredMethods entityState1 = new EntityWithIgnoredMethods("uuid-1", "qwertz", "asdf", "hjkl", "two-123", helperEntity1, Arrays.asList(helperEntity2, helperEntity3), helperEntity4);
		EntityWithIgnoredMethods entityState2 = new EntityWithIgnoredMethods("uuid-1", "ztrewq", "fdsa", "lkjh", "321-owt", helperEntity2, Arrays.asList(helperEntity1, helperEntity4), helperEntity3);
		
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
}
