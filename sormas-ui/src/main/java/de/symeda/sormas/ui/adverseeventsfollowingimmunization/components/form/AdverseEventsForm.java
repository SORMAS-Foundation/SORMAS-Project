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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.form;

import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.ABSCESS;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.ANAPHYLAXIS;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.ENCEPHALOPATHY;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.FEVERISH_FEELING;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.I18N_PREFIX;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.OTHER_ADVERSE_EVENT_DETAILS;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.SEIZURES;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.SEIZURE_TYPE;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.SEPSIS;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.SEVERE_LOCAL_REACTION;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.SEVERE_LOCAL_REACTION_BEYOND_NEAREST_JOINT;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.SEVERE_LOCAL_REACTION_MORE_THAN_THREE_DAYS;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.THROMBOCYTOPENIA;
import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto.TOXIC_SHOCK_SYNDROME;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Arrays;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventState;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

@SuppressWarnings("deprecation")
public class AdverseEventsForm extends AbstractEditForm<AdverseEventsDto> {

	private static final long serialVersionUID = 5081846814610543073L;

	private static final String ADVERSE_EVENTS_HEADINGS_LOC = "adverseEventsHeadingLoc";
	private static final String EMPTY_LABEL_LOC = "emptyLabelLoc";

	//@formatter:off
    private static final String HTML_LAYOUT =
            loc(ADVERSE_EVENTS_HEADINGS_LOC) +
                    fluidRow(
                            fluidColumn(6, 0, locs(
                                    SEVERE_LOCAL_REACTION, SEIZURES,
                                    ABSCESS, SEPSIS, ENCEPHALOPATHY,
                                    TOXIC_SHOCK_SYNDROME, THROMBOCYTOPENIA,
                                    ANAPHYLAXIS, FEVERISH_FEELING)),
                            fluidColumn(5, 0,
                            divCss(CssStyles.VSPACE_TOP_4,
											fluidRowLocs(4, SEVERE_LOCAL_REACTION_MORE_THAN_THREE_DAYS,
                                                        6, SEVERE_LOCAL_REACTION_BEYOND_NEAREST_JOINT,
														2, EMPTY_LABEL_LOC))
                                    + divCss(CssStyles.VSPACE_TOP_3,
											fluidRowLocs(10, SEIZURE_TYPE)))
                    ) +
                    loc(OTHER_ADVERSE_EVENT_DETAILS);
    //@formatter:on

	public AdverseEventsForm(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(AdverseEventsDto.class, I18N_PREFIX, true, fieldVisibilityCheckers, fieldAccessCheckers);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		Label adverseEventsHeadingLabel = new Label(I18nProperties.getString(Strings.headingAefiAdverseEvents));
		adverseEventsHeadingLabel.addStyleName(H3);
		getContent().addComponent(adverseEventsHeadingLabel, ADVERSE_EVENTS_HEADINGS_LOC);

		addFields(
			SEVERE_LOCAL_REACTION,
			SEIZURES,
			ABSCESS,
			SEPSIS,
			ENCEPHALOPATHY,
			TOXIC_SHOCK_SYNDROME,
			THROMBOCYTOPENIA,
			ANAPHYLAXIS,
			FEVERISH_FEELING);

		addField(SEVERE_LOCAL_REACTION_MORE_THAN_THREE_DAYS, CheckBox.class);
		addField(SEVERE_LOCAL_REACTION_BEYOND_NEAREST_JOINT, CheckBox.class);

		Label emptyLabel = new Label("");
		emptyLabel.addStyleName(H3);
		getContent().addComponent(emptyLabel, EMPTY_LABEL_LOC);

		NullableOptionGroup seizureType = addField(SEIZURE_TYPE, NullableOptionGroup.class);
		CssStyles.style(seizureType, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_CAPTION_INLINE);

		TextArea otherAdverseEvents = addField(OTHER_ADVERSE_EVENT_DETAILS, TextArea.class);
		otherAdverseEvents.setRows(6);
		otherAdverseEvents.setDescription(
			I18nProperties.getPrefixDescription(AdverseEventsDto.I18N_PREFIX, OTHER_ADVERSE_EVENT_DETAILS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SEVERE_LOCAL_REACTION_MORE_THAN_THREE_DAYS, SEVERE_LOCAL_REACTION_BEYOND_NEAREST_JOINT),
			SEVERE_LOCAL_REACTION,
			Arrays.asList(AdverseEventState.YES),
			true);

		FieldHelper.setVisibleWhen(getFieldGroup(), SEIZURE_TYPE, SEIZURES, Arrays.asList(AdverseEventState.YES), true);
	}

	@Override
	protected <F extends Field> F addFieldToLayout(CustomLayout layout, String propertyId, F field) {
		field.addValueChangeListener(e -> fireValueChange(false));

		return super.addFieldToLayout(layout, propertyId, field);
	}
}
