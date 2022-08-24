/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.sormastosormas.share.outgoing;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.util.CollectionUtils;

import de.symeda.sormas.api.sormastosormas.share.ShareRequestCriteria;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

@Stateless
@LocalBean
public class ShareRequestInfoService extends AdoServiceWithUserFilter<ShareRequestInfo> {

	public ShareRequestInfoService() {
		super(ShareRequestInfo.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, ShareRequestInfo> from) {
		// no user filter needed right now
		return null;
	}

	public Predicate buildCriteriaFilter(ShareRequestCriteria criteria, CriteriaBuilder cb, Root<ShareRequestInfo> root) {
		Predicate filter = null;
		if (criteria.getStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(ShareRequestInfo.REQUEST_STATUS), criteria.getStatus()));
		}

		if (!CollectionUtils.isEmpty(criteria.getStatusesExcepted())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.not(root.get(ShareRequestInfo.REQUEST_STATUS).in(criteria.getStatusesExcepted())));
		}

		return filter;
	}

	public List<String> getAllNonReferencedShareRequestInfo() {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<String> cq = cb.createQuery(String.class);
		final Root<ShareRequestInfo> from = cq.from(getElementClass());

		Join<ShareRequestInfo, SormasToSormasShareInfo> shareRequestJoin = from.join(ShareRequestInfo.SHARES, JoinType.LEFT);
		cq.select(from.get(ShareRequestInfo.UUID));
		cq.groupBy(from.get(ShareRequestInfo.ID));
		cq.having(cb.equal(cb.count(shareRequestJoin), 0));

		return em.createQuery(cq).getResultList();
	}

	public void executePermanentDeletion() {

	}
}
