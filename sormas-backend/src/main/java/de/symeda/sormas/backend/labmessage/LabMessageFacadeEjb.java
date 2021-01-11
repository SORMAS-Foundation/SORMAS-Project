package de.symeda.sormas.backend.labmessage;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.labmessage.ExternalLabResultsFacade;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageFacade;
import de.symeda.sormas.api.labmessage.LabMessageIndexDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "LabMessageFacade")
public class LabMessageFacadeEjb implements LabMessageFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private LabMessageService labMessageService;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	private LabMessage fromDto(@NotNull LabMessageDto source, LabMessage target) {

		if (target == null) {
			target = new LabMessage();
			target.setUuid(source.getUuid());
		}

		DtoHelper.validateDto(source, target);

		target.setLabMessageDetails(source.getLabMessageDetails());
		target.setLabSampleId(source.getLabSampleId());
		target.setMessageDateTime(source.getMessageDateTime());
		target.setPersonBirthDateDD(source.getPersonBirthDateDD());
		target.setPersonBirthDateMM(source.getPersonBirthDateMM());
		target.setPersonBirthDateYYYY(source.getPersonBirthDateYYYY());
		target.setPersonCity(source.getPersonCity());
		target.setPersonFirstName(source.getPersonFirstName());
		target.setPersonHouseNumber(source.getPersonHouseNumber());
		target.setPersonLastName(source.getPersonLastName());
		target.setPersonPostalCode(source.getPersonPostalCode());
		target.setPersonSex(source.getPersonSex());
		target.setPersonStreet(source.getPersonStreet());
		target.setProcessed(source.isProcessed());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleReceivedDate(source.getSampleReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setTestDateTime(source.getTestDateTime());
		target.setTestedDisease(source.getTestedDisease());
		target.setTestLabCity(source.getTestLabCity());
		target.setTestLabExternalId(source.getTestLabExternalId());
		target.setTestLabName(source.getTestLabName());
		target.setTestLabPostalCode(source.getTestLabPostalCode());
		target.setTestResult(source.getTestResult());
		target.setTestType(source.getTestType());

		return target;
	}

	@Override
	public void save(LabMessageDto dto) {

		LabMessage labMessage = labMessageService.getByUuid(dto.getUuid());

		labMessage = fromDto(dto, labMessage);
		labMessageService.ensurePersisted(labMessage);
	}

	public LabMessageDto toDto(LabMessage source) {

		if (source == null) {
			return null;
		}
		LabMessageDto target = new LabMessageDto();
		DtoHelper.fillDto(target, source);

		target.setLabMessageDetails(source.getLabMessageDetails());
		target.setLabSampleId(source.getLabSampleId());
		target.setMessageDateTime(source.getMessageDateTime());
		target.setPersonBirthDateDD(source.getPersonBirthDateDD());
		target.setPersonBirthDateMM(source.getPersonBirthDateMM());
		target.setPersonBirthDateYYYY(source.getPersonBirthDateYYYY());
		target.setPersonCity(source.getPersonCity());
		target.setPersonFirstName(source.getPersonFirstName());
		target.setPersonHouseNumber(source.getPersonHouseNumber());
		target.setPersonLastName(source.getPersonLastName());
		target.setPersonPostalCode(source.getPersonPostalCode());
		target.setPersonSex(source.getPersonSex());
		target.setPersonStreet(source.getPersonStreet());
		target.setProcessed(source.isProcessed());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleReceivedDate(source.getSampleReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setTestDateTime(source.getTestDateTime());
		target.setTestedDisease(source.getTestedDisease());
		target.setTestLabCity(source.getTestLabCity());
		target.setTestLabExternalId(source.getTestLabExternalId());
		target.setTestLabName(source.getTestLabName());
		target.setTestLabPostalCode(source.getTestLabPostalCode());
		target.setTestResult(source.getTestResult());
		target.setTestType(source.getTestType());

		return target;
	}

	@Override
	public LabMessageDto getByUuid(String uuid) {
		return toDto(labMessageService.getByUuid(uuid));
	}

	@Override
	public long count(LabMessageCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<LabMessage> labMessage = cq.from(LabMessage.class);

		Predicate filter = null;

		if (criteria != null) {
			Predicate statusFilter = labMessageService.createStatusFilter(cb, labMessage, criteria);
			filter = CriteriaBuilderHelper.and(cb, filter, statusFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(labMessage));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<LabMessageIndexDto> getIndexList(LabMessageCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LabMessageIndexDto> cq = cb.createQuery(LabMessageIndexDto.class);
		Root<LabMessage> labMessage = cq.from(LabMessage.class);

		cq.multiselect(
			labMessage.get(LabMessage.UUID),
			labMessage.get(LabMessage.MESSAGE_DATE_TIME),
			labMessage.get(LabMessage.TESTED_DISEASE),
			labMessage.get(LabMessage.TEST_RESULT),
			labMessage.get(LabMessage.PERSON_FIRST_NAME),
			labMessage.get(LabMessage.PERSON_LAST_NAME),
			labMessage.get(LabMessage.PROCESSED));

		Predicate filter = null;
		if (criteria != null) {
			Predicate statusFilter = labMessageService.createStatusFilter(cb, labMessage, criteria);
			filter = CriteriaBuilderHelper.and(cb, filter, statusFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		// Distinct is necessary here to avoid duplicate results due to the user role join in taskService.createAssigneeFilter
		cq.distinct(true);

		List<Order> order = new ArrayList<Order>();
		if (sortProperties != null && sortProperties.size() > 0) {
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case LabMessageIndexDto.UUID:
				case LabMessageIndexDto.PERSON_FIRST_NAME:
				case LabMessageIndexDto.PERSON_LAST_NAME:
				case LabMessageIndexDto.MESSAGE_DATE_TIME:
				case LabMessageIndexDto.PROCESSED:
				case LabMessageIndexDto.TEST_RESULT:
				case LabMessageIndexDto.TESTED_DISEASE:
					expression = labMessage.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
		}
		order.add(cb.desc(labMessage.get(LabMessage.MESSAGE_DATE_TIME)));
		cq.orderBy(order);
		List<LabMessageIndexDto> labMessages;
		if (first != null && max != null) {
			labMessages = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			labMessages = em.createQuery(cq).getResultList();
		}

		return labMessages;
	}

	@Override
	public void fetchExternalLabMessages(boolean onlyNew) {
		List<LabMessageDto> newMessages = null;
		try {
			InitialContext ic = new InitialContext();
			String jndiName = configFacade.getDemisJndiName();
			// Maybe catch that jndiName can be null
			ExternalLabResultsFacade labResultsFacade = (ExternalLabResultsFacade) ic.lookup(jndiName);
			newMessages = labResultsFacade.getExternalLabMessages(onlyNew);
		} catch (NamingException e) {
			// That should be handled properly
			e.printStackTrace();
		}
		if (newMessages != null) {
			newMessages.stream().forEach(labMessageDto -> save(labMessageDto));
		}
	}

	@LocalBean
	@Stateless
	public static class LabMessageFacadeEjbLocal extends LabMessageFacadeEjb {

	}
}
