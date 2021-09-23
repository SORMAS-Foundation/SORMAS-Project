package de.symeda.sormas.backend.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.sample.AdditionalTestCriteria;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.AdditionalTestFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.QueryContext;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "AdditionalTestFacade")
public class AdditionalTestFacadeEjb implements AdditionalTestFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private AdditionalTestService service;
	@EJB
	private SampleService sampleService;
	@EJB
	private UserService userService;

	@Override
	public AdditionalTestDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	@Override
	public List<AdditionalTestDto> getAllBySample(String sampleUuid) {

		if (sampleUuid == null) {
			return Collections.emptyList();
		}

		Sample sample = sampleService.getByUuid(sampleUuid);
		return service.getAllBySample(sample).stream().map(s -> toDto(s)).collect(Collectors.toList());
	}

	public List<AdditionalTestDto> getIndexList(
		AdditionalTestCriteria additionalTestCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AdditionalTestDto> cq = cb.createQuery(AdditionalTestDto.class);
		Root<AdditionalTest> from = cq.from(AdditionalTest.class);

		QueryContext additionalTestQueryContext = new AdditionalTestQueryContext(cb, cq, from);
		AdditionalTestJoins<AdditionalTest> joins = (AdditionalTestJoins<AdditionalTest>) additionalTestQueryContext.getJoins();

		List<Selection<?>> selections = new ArrayList<>(Arrays.asList(from.get(AdditionalTest.UUID), joins.getSample().get(Sample.UUID)));
		cq.multiselect(selections);

		Predicate filter = null;
		if (additionalTestCriteria != null) {
			filter = service.buildCriteriaFilter(additionalTestCriteria, cb, from);
		}
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(from.get(AdditionalTest.CHANGE_DATE)));
		cq.distinct(true);

		List<Order> order = new ArrayList<>();
		if (sortProperties != null && sortProperties.size() > 0) {
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case AdditionalTestDto.UUID:
				case AdditionalTestDto.SAMPLE:
				case AdditionalTestDto.TEST_DATE_TIME:
					expression = from.get(sortProperty.propertyName);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
		}
		order.add(cb.desc(from.get(AdditionalTest.UUID)));
		cq.orderBy(order);

		return QueryHelper.getResultList(em, cq, first, max);
	}

	public long count(AdditionalTestCriteria additionalTestCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AdditionalTest> additionalTestRoot = cq.from(AdditionalTest.class);

		Predicate filter = null;

		if (additionalTestCriteria != null) {
			Predicate criteriaFilter = service.buildCriteriaFilter(additionalTestCriteria, cb, additionalTestRoot);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(additionalTestRoot));
		return em.createQuery(cq).getSingleResult();
	}

	public Page<AdditionalTestDto> getIndexPage(
		AdditionalTestCriteria additionalTestCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {

		List<AdditionalTestDto> additionalTestList = getIndexList(additionalTestCriteria, offset, size, sortProperties);
		long totalElementCount = count(additionalTestCriteria);
		return new Page<>(additionalTestList, offset, size, totalElementCount);

	}

	@Override
	public AdditionalTestDto saveAdditionalTest(@Valid AdditionalTestDto additionalTest) {
		return saveAdditionalTest(additionalTest, true);
	}

	public AdditionalTestDto saveAdditionalTest(@Valid AdditionalTestDto additionalTest, boolean checkChangeDate) {

		AdditionalTest entity = fromDto(additionalTest, checkChangeDate);
		service.ensurePersisted(entity);
		return toDto(entity);
	}

	@Override
	public void deleteAdditionalTest(String additionalTestUuid) {

		if (!userService.hasRight(UserRight.ADDITIONAL_TEST_DELETE)) {
			throw new UnsupportedOperationException("Your user is not allowed to delete additional tests");
		}

		AdditionalTest additionalTest = service.getByUuid(additionalTestUuid);
		service.delete(additionalTest);
	}

	@Override
	public List<AdditionalTestDto> getAllActiveAdditionalTestsAfter(Date date) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveAdditionalTestsAfter(date, user).stream().map(e -> toDto(e)).collect(Collectors.toList());
	}

	@Override
	public List<AdditionalTestDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllActiveUuids() {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveUuids(user);
	}

	public AdditionalTestDto convertToDto(AdditionalTest source, Pseudonymizer pseudonymizer) {
		AdditionalTestDto dto = toDto(source);

		pseudonymizer
			.pseudonymizeDto(AdditionalTestDto.class, dto, sampleService.inJurisdictionOrOwned(source.getSample()).getInJurisdiction(), null);

		return dto;
	}

	public AdditionalTest fromDto(@NotNull AdditionalTestDto source, boolean checkChangeDate) {

		AdditionalTest target = DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), AdditionalTest::new, checkChangeDate);

		target.setSample(sampleService.getByReferenceDto(source.getSample()));
		target.setTestDateTime(source.getTestDateTime());
		target.setHaemoglobinuria(source.getHaemoglobinuria());
		target.setProteinuria(source.getProteinuria());
		target.setHematuria(source.getHematuria());
		target.setArterialVenousGasPH(source.getArterialVenousGasPH());
		target.setArterialVenousGasPco2(source.getArterialVenousGasPco2());
		target.setArterialVenousGasPao2(source.getArterialVenousGasPao2());
		target.setArterialVenousGasHco3(source.getArterialVenousGasHco3());
		target.setGasOxygenTherapy(source.getGasOxygenTherapy());
		target.setAltSgpt(source.getAltSgpt());
		target.setAstSgot(source.getAstSgot());
		target.setCreatinine(source.getCreatinine());
		target.setPotassium(source.getPotassium());
		target.setUrea(source.getUrea());
		target.setHaemoglobin(source.getHaemoglobin());
		target.setTotalBilirubin(source.getTotalBilirubin());
		target.setConjBilirubin(source.getConjBilirubin());
		target.setWbcCount(source.getWbcCount());
		target.setPlatelets(source.getPlatelets());
		target.setProthrombinTime(source.getPlatelets());
		target.setOtherTestResults(source.getOtherTestResults());

		return target;
	}

	public static AdditionalTestDto toDto(AdditionalTest source) {

		if (source == null) {
			return null;
		}

		AdditionalTestDto target = new AdditionalTestDto();
		DtoHelper.fillDto(target, source);

		target.setSample(SampleFacadeEjb.toReferenceDto(source.getSample()));
		target.setTestDateTime(source.getTestDateTime());
		target.setHaemoglobinuria(source.getHaemoglobinuria());
		target.setProteinuria(source.getProteinuria());
		target.setHematuria(source.getHematuria());
		target.setArterialVenousGasPH(source.getArterialVenousGasPH());
		target.setArterialVenousGasPco2(source.getArterialVenousGasPco2());
		target.setArterialVenousGasPao2(source.getArterialVenousGasPao2());
		target.setArterialVenousGasHco3(source.getArterialVenousGasHco3());
		target.setGasOxygenTherapy(source.getGasOxygenTherapy());
		target.setAltSgpt(source.getAltSgpt());
		target.setAstSgot(source.getAstSgot());
		target.setCreatinine(source.getCreatinine());
		target.setPotassium(source.getPotassium());
		target.setUrea(source.getUrea());
		target.setHaemoglobin(source.getHaemoglobin());
		target.setTotalBilirubin(source.getTotalBilirubin());
		target.setConjBilirubin(source.getConjBilirubin());
		target.setWbcCount(source.getWbcCount());
		target.setPlatelets(source.getPlatelets());
		target.setProthrombinTime(source.getPlatelets());
		target.setOtherTestResults(source.getOtherTestResults());

		return target;
	}

	@LocalBean
	@Stateless
	public static class AdditionalTestFacadeEjbLocal extends AdditionalTestFacadeEjb {

	}

}
