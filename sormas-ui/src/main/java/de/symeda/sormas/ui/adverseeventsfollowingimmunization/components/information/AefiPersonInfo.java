/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.information;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.AbstractInfoLayout;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldAccessHelper;

@SuppressWarnings("serial")
public class AefiPersonInfo extends AbstractInfoLayout<PersonDto> {

	public static final String PERSON_FULL_NAME = "personFullName";
	public static final String DISEASE = "disease";

	private PersonDto personDto;
	private Disease disease;

	public AefiPersonInfo(PersonDto personDto, Disease disease) {
		super(PersonDto.class, FieldAccessHelper.getFieldAccessCheckers(personDto));

		this.personDto = personDto;
		this.disease = disease;

		setSpacing(true);
		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);
		updatePersonInfo();
	}

	private void updatePersonInfo() {

		this.removeAllComponents();

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setWidth(100, Sizeable.Unit.PERCENTAGE);
		CssStyles.style(mainLayout, CssStyles.PADDING_NONE);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Sizeable.Unit.PERCENTAGE);

		Label headingLabel = new Label(I18nProperties.getString(Strings.headingPersonInformation));
		headingLabel.addStyleName(CssStyles.H3);
		componentHeader.addComponent(headingLabel);
		componentHeader.setExpandRatio(headingLabel, 1);

		HorizontalLayout infoColumnsLayout = new HorizontalLayout();
		infoColumnsLayout.setMargin(false);
		infoColumnsLayout.setSpacing(false);
		infoColumnsLayout.setWidth(100, Sizeable.Unit.PERCENTAGE);

		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setMargin(false);
		leftColumnLayout.setSpacing(true);
		boolean hasUserRightPersonView = UserProvider.getCurrent().hasUserRight(UserRight.PERSON_VIEW);
		{
			final Label personIdLabel = addDescLabel(
				leftColumnLayout,
				PersonDto.UUID,
				DataHelper.getShortUuid(personDto.getUuid()),
				I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.UUID));
			personIdLabel.setId("personIdLabel");
			personIdLabel.setDescription(personDto.getUuid());

			if (hasUserRightPersonView) {
				addDescLabel(leftColumnLayout, PersonDto.LAST_NAME, personDto.buildCaption(), I18nProperties.getCaption(PERSON_FULL_NAME));

				HorizontalLayout ageSexLayout = new HorizontalLayout();
				ageSexLayout.setMargin(false);
				ageSexLayout.setSpacing(true);
				addCustomDescLabel(
					ageSexLayout,
					PersonDto.class,
					PersonDto.APPROXIMATE_AGE,
					ApproximateAgeType.ApproximateAgeHelper.formatApproximateAge(personDto.getApproximateAge(), personDto.getApproximateAgeType()),
					I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.APPROXIMATE_AGE));
				addCustomDescLabel(
					ageSexLayout,
					PersonDto.class,
					PersonDto.SEX,
					personDto.getSex(),
					I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
				leftColumnLayout.addComponent(ageSexLayout);
			}
		}
		infoColumnsLayout.addComponent(leftColumnLayout);

		VerticalLayout rightColumnLayout = new VerticalLayout();
		rightColumnLayout.setMargin(false);
		rightColumnLayout.setSpacing(true);
		{
			addDescLabel(rightColumnLayout, DISEASE, disease, I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.DISEASE));

			addDescLabel(
				rightColumnLayout,
				PersonDto.PHONE,
				personDto.getPhone(),
				I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PHONE));
		}
		infoColumnsLayout.addComponent(rightColumnLayout);

		mainLayout.addComponent(componentHeader);
		mainLayout.addComponent(infoColumnsLayout);

		this.addComponent(mainLayout);
	}
}
