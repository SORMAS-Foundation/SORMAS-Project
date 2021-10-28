package de.symeda.sormas.backend.systemevent;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventFacade;
import de.symeda.sormas.api.systemevents.SystemEventStatus;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "SystemEventFacade")
public class SystemEventFacadeEjb implements SystemEventFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private SystemEventService systemEventService;

	public boolean existsStartedEvent(SystemEventType type) {
		return systemEventService.exists(
			(cb, root, cq) -> cb.and(cb.equal(root.get(SystemEvent.STATUS), SystemEventStatus.STARTED), cb.equal(root.get(SystemEvent.TYPE), type)));
	}

	/**
	 * 
	 * @param type
	 * @return the latest SystemEvent of the specified type with SystemEventStatus == SUCCESS.
	 */
	@Override
	public SystemEventDto getLatestSuccessByType(SystemEventType type) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemEvent> cq = cb.createQuery(SystemEvent.class);
		Root<SystemEvent> systemEventRoot = cq.from(SystemEvent.class);

		cq.where(cb.equal(systemEventRoot.get(SystemEvent.STATUS), SystemEventStatus.SUCCESS));
		cq.orderBy(cb.desc(systemEventRoot.get(SystemEvent.START_DATE)));

		return QueryHelper.getFirstResult(em, cq, this::toDto);
	}


	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveSystemEvent(@Valid SystemEventDto dto) {
		SystemEvent systemEvent = systemEventService.getByUuid(dto.getUuid());

		systemEvent = fromDto(dto, systemEvent, true);
		systemEventService.ensurePersisted(systemEvent);

	}

	@Override
	public void reportSuccess(SystemEventDto systemEvent, String message, Date end) {
		systemEvent.setAdditionalInfo(message);
		reportSuccess(systemEvent, end);
	}

	@Override
	public void reportSuccess(SystemEventDto systemEvent, Date end) {
		systemEvent.setStatus(SystemEventStatus.SUCCESS);
		systemEvent.setEndDate(end);
		systemEvent.setChangeDate(new Date());
		saveSystemEvent(systemEvent);
	}

	@Override
	public void reportError(SystemEventDto systemEvent, String errorMessage, Date end) {
		systemEvent.setStatus(SystemEventStatus.ERROR);
		systemEvent.setAdditionalInfo(errorMessage);
		systemEvent.setEndDate(end);
		systemEvent.setChangeDate(new Date());
		saveSystemEvent(systemEvent);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void markPreviouslyStartedAsUnclear(SystemEventType type) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<SystemEvent> cu = cb.createCriteriaUpdate(SystemEvent.class);
		Root<SystemEvent> root = cu.from(SystemEvent.class);
		cu.set(root.get(SystemEvent.STATUS), SystemEventStatus.UNCLEAR);
		cu.where(cb.equal(root.get(SystemEvent.STATUS), SystemEventStatus.STARTED), cb.equal(root.get(SystemEvent.TYPE), type));
		em.createQuery(cu).executeUpdate();
	}

	public SystemEvent fromDto(@NotNull SystemEventDto source, SystemEvent target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, SystemEvent::new, checkChangeDate);

		target.setType(source.getType());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setStatus(source.getStatus());
		target.setAdditionalInfo(source.getAdditionalInfo());

		return target;

	}

	public SystemEventDto toDto(SystemEvent source) {

		if (source == null) {
			return null;
		}

		SystemEventDto target = new SystemEventDto();
		DtoHelper.fillDto(target, source);

		target.setType(source.getType());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setStatus(source.getStatus());
		target.setAdditionalInfo(source.getAdditionalInfo());

		return target;
	}

	/**
	 * Deletes all SystemEvents unchanged since the specified number of days.
	 * Does not vacuum the db, so deleted SystemEvents may still take space and be recoverable.
	 *
	 * @param daysAfterSystemEventGetsDeleted
	 */
	@Override
	public void deleteAllDeletableSystemEvents(int daysAfterSystemEventGetsDeleted) {
		deleteAllDeletableSystemEvents(LocalDateTime.now().minusDays(daysAfterSystemEventGetsDeleted));
	}

	public void deleteAllDeletableSystemEvents(LocalDateTime notChangedUntil) {

		long startTime = DateHelper.startTime();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemEvent> cq = cb.createQuery(SystemEvent.class);
		Root<SystemEvent> systemEvent = cq.from(SystemEvent.class);

		Timestamp notChangedTimestamp = Timestamp.valueOf(notChangedUntil);
		cq.where(cb.not(systemEventService.createChangeDateFilter(cb, systemEvent, notChangedTimestamp)));

		List<SystemEvent> resultList = em.createQuery(cq).getResultList();
		for (SystemEvent event : resultList) {
			em.remove(event);
		}

		logger.debug(
			"deleteAllDeletableSystemEvents() finished. systemEvent count = {}, {}ms",
			resultList.size(),
			DateHelper.durationMillies(startTime));
	}

	@LocalBean
	@Stateless
	public static class SystemEventFacadeEjbLocal extends SystemEventFacadeEjb {

	}

}
