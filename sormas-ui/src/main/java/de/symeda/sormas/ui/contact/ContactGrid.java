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

import java.util.List;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionModel.HasUserSelectionAllowed;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractGrid;
import de.symeda.sormas.ui.utils.V7UuidRenderer;

@SuppressWarnings("serial")
public class ContactGrid extends Grid implements AbstractGrid<ContactCriteria> {

	public static final String NUMBER_OF_VISITS = Captions.Contact_numberOfVisits;
	public static final String NUMBER_OF_PENDING_TASKS = Captions.columnNumberOfPendingTasks;
	public static final String DISEASE_SHORT = Captions.columnDiseaseShort;

	private ContactCriteria contactCriteria = new ContactCriteria();

	public ContactGrid() {
		setSizeFull();

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
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

					return String.format(I18nProperties.getCaption(Captions.formatNumberOfVisitsFormat),
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
				return String.format(I18nProperties.getCaption(Captions.formatSimpleNumberFormat), 
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
		getColumn(ContactIndexDto.UUID).setRenderer(new V7UuidRenderer());

		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixCaption(
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
		contactCriteria.caze(caseRef);
		reload();
	}

	public void setDiseaseFilter(Disease disease) {
		contactCriteria.caseDisease(disease);
		reload();
	}

	public void setReportedByFilter(UserRole reportingUserRole) {
		contactCriteria.reportingUserRole(reportingUserRole);
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

	public void setNameUuidCaseLike(String text) {
		contactCriteria.nameUuidCaseLike(text);
		reload();
	}

	@SuppressWarnings("unchecked")
	public BeanItemContainer<ContactIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<ContactIndexDto>) container.getWrappedContainer();
	}

	public void reload() {
		if (getSelectionModel() instanceof HasUserSelectionAllowed) {
			deselectAll();
		}
		
		List<ContactIndexDto> entries = FacadeProvider.getContactFacade().getIndexList(UserProvider.getCurrent().getUserReference().getUuid(), contactCriteria);

		getContainer().removeAllItems();
		getContainer().addAll(entries);  
	}

	@Override
	public void setCriteria(ContactCriteria contactCriteria) {
		this.contactCriteria = contactCriteria;
	}
	
	@Override
	public ContactCriteria getCriteria() {
		return contactCriteria;
	}

}


