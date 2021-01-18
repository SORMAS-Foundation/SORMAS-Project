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
	 * @return The date of the latest SystemEvent of the specified type with SystemEventStatus == SUCCESS.
	 */
	@Override
	public Date getLatestSuccessByType(SystemEventType type) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemEvent> cq = cb.createQuery(SystemEvent.class);
		Root<SystemEvent> systemEventRoot = cq.from(SystemEvent.class);

		cq.where(cb.equal(systemEventRoot.get(SystemEvent.STATUS), SystemEventStatus.SUCCESS));
		cq.orderBy(cb.desc(systemEventRoot.get(SystemEvent.START_DATE)));

		try {
			SystemEvent systemEvent = em.createQuery(cq).setMaxResults(1).getSingleResult();
			return systemEvent.getStartDate();
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

	public SystemEvent fromDto(@NotNull SystemEventDto source, SystemEvent target, boolean checkChangeDate) {

		if (target == null) {
			target = new SystemEvent();
			target.setUuid(source.getUuid());
		}

		DtoHelper.validateDto(source, target, checkChangeDate);

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
