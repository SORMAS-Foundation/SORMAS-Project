package de.symeda.sormas.backend.systemevent;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

@Stateless(name = "SystemEventFacade")
public class SystemEventFacadeEjb implements SystemEventFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SystemEventService systemEventService;

	private final Logger logger = LoggerFactory.getLogger(getClass());

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

		try {
			SystemEvent systemEvent = em.createQuery(cq).setMaxResults(1).getSingleResult();
			return toDto(systemEvent);
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public void saveSystemEvent(SystemEventDto dto) {
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
