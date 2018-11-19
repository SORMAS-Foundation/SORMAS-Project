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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.contact;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class ContactGrid extends Grid {

	public static final String NUMBER_OF_VISITS = "numberOfVisits";
	public static final String NUMBER_OF_PENDING_TASKS = "numberOfPendingTasks";
	public static final String DISEASE_SHORT = "diseaseShort";

	private final ContactCriteria contactCriteria = new ContactCriteria();

	public ContactGrid(boolean isSubList) {
		setSizeFull();

		if (!isSubList) {
			contactCriteria.archived(false);
		}

		if (LoginHelper.hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}

		BeanItemContainer<ContactIndexDto> container = new BeanItemContainer<ContactIndexDto>(ContactIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		generatedContainer.addGeneratedProperty(NUMBER_OF_VISITS, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				ContactIndexDto indexDto = (ContactIndexDto) itemId;
				if (DiseaseHelper.hasContactFollowUp(indexDto.getCaseDisease(), null)) {
					int numberOfVisits = FacadeProvider.getVisitFacade().getNumberOfVisits(indexDto.toReference(), null);
					int numberOfRequiredVisits = ContactLogic.getNumberOfRequiredVisitsSoFar(indexDto.getReportDate(), indexDto.getFollowUpUntil());
					int numberOfMissedVisits = numberOfRequiredVisits - numberOfVisits;
					// Set number of missed visits to 0 when more visits than expected have been done
					if (numberOfMissedVisits < 0) {
						numberOfMissedVisits = 0;
					}

					return String.format(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, "numberOfVisitsFormat"),
							numberOfVisits, numberOfMissedVisits);
				} else {
					return "-";
				}
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});

		generatedContainer.addGeneratedProperty(NUMBER_OF_PENDING_TASKS, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				ContactIndexDto contactIndexDto = (ContactIndexDto)itemId;
				return String.format(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, NUMBER_OF_PENDING_TASKS + "Format"), 
						FacadeProvider.getTaskFacade().getPendingTaskCountByContact(contactIndexDto.toReference()));
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});

		generatedContainer.addGeneratedProperty(DISEASE_SHORT, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				ContactIndexDto contactIndexDto = (ContactIndexDto) itemId;
				return contactIndexDto.getCaseDisease() != Disease.OTHER 
						? contactIndexDto.getCaseDisease().toShortString()
								: DataHelper.toStringNullable(contactIndexDto.getCaseDiseaseDetails());
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});

		setColumns(ContactIndexDto.UUID, DISEASE_SHORT, ContactIndexDto.CONTACT_CLASSIFICATION, ContactIndexDto.CONTACT_STATUS,
				ContactIndexDto.PERSON, ContactIndexDto.CONTACT_PROXIMITY,
				ContactIndexDto.FOLLOW_UP_STATUS, NUMBER_OF_VISITS, NUMBER_OF_PENDING_TASKS);
		getColumn(ContactIndexDto.CONTACT_PROXIMITY).setWidth(200);
		getColumn(ContactIndexDto.UUID).setRenderer(new UuidRenderer());

		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
					ContactIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}

		addItemClickListener(e -> {
			if (e.getPropertyId() != null && (e.getPropertyId().equals(ContactIndexDto.UUID) || e.isDoubleClick())) {
				ContactIndexDto contactIndexDto = (ContactIndexDto)e.getItemId();
				ControllerProvider.getContactController().editData(contactIndexDto.getUuid());
			}
		});	
	}

	public void setCaseFilter(CaseReferenceDto caseRef) {
		contactCriteria.caseEquals(caseRef);
		reload();
	}

	public void setDiseaseFilter(Disease disease) {
		contactCriteria.caseDiseaseEquals(disease);
		reload();
	}

	public void setReportedByFilter(UserRole reportingUserRole) {
		contactCriteria.reportingUserHasRole(reportingUserRole);
		reload();
	}

	public void setRegionFilter(RegionReferenceDto region) {
		contactCriteria.caseRegion(region);
		reload();
	}

	public void setDistrictFilter(DistrictReferenceDto district) {
		contactCriteria.caseDistrict(district);
		reload();
	}

	public void setHealthFacilityFilter(FacilityReferenceDto facility) {
		contactCriteria.caseFacility(facility);
		reload();
	}

	public void setContactOfficerFilter(UserReferenceDto contactOfficer) {
		contactCriteria.contactOfficer(contactOfficer);
		reload();
	}

	public void setClassificationFilter(ContactClassification contactClassification) {
		contactCriteria.contactClassification(contactClassification);
		reload();
	}

	public void setStatusFilter(ContactStatus status) {
		contactCriteria.contactStatus(status);
		reload();
	}

	public void setFollowUpStatusFilter(FollowUpStatus status) {
		if (status == FollowUpStatus.NO_FOLLOW_UP) {
			this.getColumn(NUMBER_OF_VISITS).setHidden(true);
		} else {
			this.getColumn(NUMBER_OF_VISITS).setHidden(false);
		}
		contactCriteria.followUpStatus(status);
		reload();
	}

	public void filterByText(String text) {
		getContainer().removeContainerFilters(ContactIndexDto.UUID);
		getContainer().removeContainerFilters(ContactIndexDto.PERSON);
		getContainer().removeContainerFilters(ContactIndexDto.CAZE);

		if (text != null && !text.isEmpty()) {
			List<Filter> orFilters = new ArrayList<Filter>();
			String[] words = text.split("\\s+");
			for (String word : words) {
				orFilters.add(new SimpleStringFilter(ContactIndexDto.UUID, word, true, false));
				orFilters.add(new SimpleStringFilter(ContactIndexDto.PERSON, word, true, false));
				orFilters.add(new SimpleStringFilter(ContactIndexDto.CAZE, word, true, false));
			}
			getContainer().addContainerFilter(new Or(orFilters.stream().toArray(Filter[]::new)));
		}
	}

	public ContactCriteria getFilterCriteria() {
		return contactCriteria;
	}

	@SuppressWarnings("unchecked")
	public BeanItemContainer<ContactIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<ContactIndexDto>) container.getWrappedContainer();
	}

	public void reload() {
		List<ContactIndexDto> entries = FacadeProvider.getContactFacade().getIndexList(LoginHelper.getCurrentUserAsReference().getUuid(), contactCriteria);

		getContainer().removeAllItems();
		getContainer().addAll(entries);  
	}
}


