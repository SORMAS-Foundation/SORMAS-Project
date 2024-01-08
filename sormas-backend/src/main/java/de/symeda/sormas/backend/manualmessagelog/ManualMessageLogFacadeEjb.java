/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.manualmessagelog;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogCriteria;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogFacade;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "ManualMessageLogFacade")
public class ManualMessageLogFacadeEjb implements ManualMessageLogFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private ManualMessageLogService manualMessageLogService;
	@EJB
	private UserService userService;

	@RightsAllowed({
			UserRight._SEND_MANUAL_EXTERNAL_MESSAGES,
			UserRight._EXTERNAL_EMAIL_SEND})
	public List<ManualMessageLogIndexDto> getIndexList(ManualMessageLogCriteria criteria) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<ManualMessageLogIndexDto> cq = cb.createQuery(ManualMessageLogIndexDto.class);
		final Root<ManualMessageLog> root = cq.from(ManualMessageLog.class);

		ManualMessageLogJoins joins = new ManualMessageLogJoins(root);

		cq.multiselect(
				root.get(ManualMessageLog.UUID),
				root.get(ManualMessageLog.MESSAGE_TYPE),
				root.get(ManualMessageLog.SENT_DATE),
				joins.getSendingUser().get(User.UUID),
				joins.getSendingUser().get(User.FIRST_NAME),
				joins.getSendingUser().get(User.LAST_NAME),
				root.get(ManualMessageLog.EMAIL_ADDRESS),
				root.get(ManualMessageLog.USED_TEMPLATE),
				root.get(ManualMessageLog.ATTACHED_DOCUMENTS),
				JurisdictionHelper.booleanSelector(cb, manualMessageLogService.inJurisdictionOrOwned(cq, cb, root)),
				JurisdictionHelper.booleanSelector(cb, userService.inJurisdictionOrOwned(cb, joins.getSendungUserJoins())));

		Predicate filter = manualMessageLogService.buildCriteriaFilter(criteria, root, cb);
		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(root.get(ManualMessageLog.SENT_DATE)));
		// distinct needed because of the jurisdiction selection,
		// it can bring multiple results for one manual message log because the person has multiple assigned entities that can have email
		cq.distinct(true);

		List<ManualMessageLogIndexDto> resultList = em.createQuery(cq).getResultList();

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(
				ManualMessageLogIndexDto.class,
				resultList,
				ManualMessageLogIndexDto::isInJurisdiction,
				(m, inJurisdiction) -> {
					pseudonymizer.pseudonymizeUser(m.isSenderInJurisdiction(), m::setSendingUser);
				});

		return resultList;
	}

	@LocalBean
	@Stateless
	public static class ManualMessageLogFacadeEjbLocal extends ManualMessageLogFacadeEjb {

	}
}
