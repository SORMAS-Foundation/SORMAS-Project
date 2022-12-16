package de.symeda.sormas.ui.configuration.generate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Binder;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public abstract class EntityGenerator<T> {

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	private Object[] diseaseEnumConstants;
	private Map<Class<? extends EntityDto>, List<Method>> setters = new HashMap<>();
	private Map<Method, Optional<Method>> getters = new HashMap<>();
	protected FieldVisibilityCheckers fieldVisibilityCheckers;
	private boolean useManualSeed = false;
	private Random randomGenerator;
	private long manualSeed = 0;

	private final String[] maleFirstNames = new String[] {
		"Nelson",
		"Malik",
		"Thato",
		"Omar",
		"Dion",
		"Darius",
		"Bandile",
		"Demarco" };
	private final String[] femaleFirstNames = new String[] {
		"Ayana",
		"Shaka",
		"Shaniqua",
		"Charlize",
		"Zari",
		"Jayla",
		"Aisha",
		"Iminathi" };
	private final String[] lastNames = new String[] {
		"Ajanlekoko",
		"Omiata",
		"Apeloko",
		"Adisa",
		"Abioye",
		"Chipo",
		"Baako",
		"Akua",
		"Ekua",
		"Katlego",
		"Furaha",
		"Chuks",
		"Babak",
		"Tinibu",
		"Okar",
		"Egwu" };

	public abstract void generate(Binder<T> binder);

	private Object[] getEnumConstants(Class<?> parameterType) {
		if (parameterType == Disease.class) {
			if (diseaseEnumConstants == null) {
				diseaseEnumConstants = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true).toArray();
			}
			return diseaseEnumConstants;
		} else {
			return parameterType.getEnumConstants();
		}
	}

	private List<Method> setters(Class<? extends EntityDto> entityClass) {

		return setters.computeIfAbsent(
			entityClass,
			c -> Arrays.stream(c.getDeclaredMethods())
				.filter(method -> method.getName().startsWith("set") && method.getParameterTypes().length == 1)
				.collect(Collectors.toList()));
	}

	private Optional<Method> getter(Method setter) throws NoSuchMethodException {

		return getters.computeIfAbsent(setter, s -> {
			try {
				return Optional.of(s.getDeclaringClass().getDeclaredMethod(s.getName().replaceFirst("set", "get")));
			} catch (NoSuchMethodException | SecurityException e) {
				return Optional.empty();
			}
		});
	}

	protected LocalDateTime getReferenceDateTime(int i, int count, float baseOffset, Disease disease, LocalDate startDate, int daysBetween) {

		float x = (float) i / count;
		x += baseOffset;
		x += 0.13f * disease.ordinal();
		x += 0.5f * random().nextFloat();
		x = (float) (Math.asin((x % 2) - 1) / Math.PI / 2) + 0.5f;

		return startDate.atStartOfDay().plusMinutes((int) (x * 60 * 24 * daysBetween));
	}

	protected void setPersonName(PersonDto person) {

		Sex sex = Sex.values()[random().nextInt(2)];
		person.setSex(sex);
		if (sex == Sex.MALE) {
			person.setFirstName(random(maleFirstNames) + " " + random(maleFirstNames));
		} else {
			person.setFirstName(random(femaleFirstNames) + " " + random(femaleFirstNames));
		}
		person.setLastName(random(lastNames) + "-" + random(lastNames));

	}

	public void fillEntity(EntityDto entity, LocalDateTime referenceDateTime) {

		try {
			Class<? extends EntityDto> entityClass = entity.getClass();
			List<Method> setters = setters(entityClass);
			for (Method setter : setters) {
				String propertyId = setter.getName().substring(3, 4).toLowerCase() + setter.getName().substring(4);
				// leave some empty/default
				if (randomPercent(40) || !fieldVisibilityCheckers.isVisible(entityClass, propertyId)) {
					continue;
				}
				Class<?> parameterType = setter.getParameterTypes()[0];
				// doesn't make sense
				//				if (parameterType.isAssignableFrom(String.class)) {
				//					setter.invoke(entity, words[random.nextInt(words.length)]);
				//				}
				//				else
				if (parameterType.isAssignableFrom(Date.class)) {
					setter.invoke(entity, randomDate(referenceDateTime));
				} else if (parameterType.isEnum()) {
					Object[] enumConstants;
					// Only use active primary diseases
					enumConstants = getEnumConstants(parameterType);
					// Generate more living persons
					if (parameterType == PresentCondition.class && randomPercent(50)) {
						setter.invoke(entity, PresentCondition.ALIVE);
					} else {
						setter.invoke(entity, random(enumConstants));
					}
				} else if (EntityDto.class.isAssignableFrom(parameterType)) {
					getter(setter).ifPresent(g -> {
						Object subEntity;
						try {
							subEntity = g.invoke(entity);
							if (subEntity instanceof EntityDto) {
								fillEntity((EntityDto) subEntity, referenceDateTime);
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException(e.getMessage(), e);
						}
					});
				}
			}
			if (entity.getClass() == CaseDataDto.class) {
				if (((CaseDataDto) entity).getQuarantineTo() != null
					&& ((CaseDataDto) entity).getQuarantineFrom() != null
					&& ((CaseDataDto) entity).getQuarantineTo().before(((CaseDataDto) entity).getQuarantineFrom())) {
					Date quarantineTo = ((CaseDataDto) entity).getQuarantineTo();
					((CaseDataDto) entity).setQuarantineTo(((CaseDataDto) entity).getQuarantineFrom());
					((CaseDataDto) entity).setQuarantineFrom(quarantineTo);
				}

				if (((CaseDataDto) entity).getProhibitionToWorkFrom() != null
					&& ((CaseDataDto) entity).getProhibitionToWorkUntil() != null
					&& ((CaseDataDto) entity).getProhibitionToWorkUntil().before(((CaseDataDto) entity).getProhibitionToWorkFrom())) {
					Date prohibitionToWorkUntil = ((CaseDataDto) entity).getProhibitionToWorkUntil();
					((CaseDataDto) entity).setProhibitionToWorkUntil(((CaseDataDto) entity).getProhibitionToWorkFrom());
					((CaseDataDto) entity).setProhibitionToWorkFrom(prohibitionToWorkUntil);
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	protected void initializeRandomGenerator() {
		if (useManualSeed) {
			randomGenerator = new Random(manualSeed);
		} else {
			randomGenerator = new Random();
		}
	}

	protected Random random() {
		return randomGenerator;
	}

	protected boolean randomPercent(int p) {
		return random().nextInt(100) <= p;
	}

	protected int randomInt(int min, int max) {
		if (max <= min) {
			return min;
		}
		return min + random().nextInt(max - min);
	}

	protected <T> T random(List<T> list) {
		return list.get(random().nextInt(list.size()));
	}

	protected <T> T random(T[] a) {
		return a[random().nextInt(a.length)];
	}

	protected Date randomDate(LocalDateTime referenceDateTime) {
		return Date.from(referenceDateTime.plusMinutes(random().nextInt(60 * 24 * 5)).atZone(ZoneId.systemDefault()).toInstant());
	}
}
