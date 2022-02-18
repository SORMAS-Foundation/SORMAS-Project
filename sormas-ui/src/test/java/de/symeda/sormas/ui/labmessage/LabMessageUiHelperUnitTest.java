/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.labmessage;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserReferenceDto;

import de.symeda.sormas.ui.samples.SampleCreateForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LabMessageUiHelperUnitTest {

	@Test
	public void testEstablishFinalCommitButtons() {

		Mockito.mockConstruction(SampleCreateForm.class);

		UserReferenceDto user = new UserReferenceDto();
		CaseReferenceDto caze = new CaseReferenceDto();
		SampleDto sample = SampleDto.build(user, caze);

		SampleCreateForm createForm = new SampleCreateForm(Disease.CORONAVIRUS);
		when(createForm.getFieldGroup()).thenReturn(new BeanFieldGroup(SampleDto.class));

		CommitDiscardWrapperComponent<SampleCreateForm> sampleComponent =
			new CommitDiscardWrapperComponent<>(createForm, true, createForm.getFieldGroup());
		when(sampleComponent.getWrappedComponent().getValue()).thenReturn(sample);

		LabMessageUiHelper.establishFinalCommitButtons(sampleComponent);

		HorizontalLayout buttonsPanel = sampleComponent.getButtonsPanel();
		Button saveAndOpenEntryButton = (Button) buttonsPanel.getComponent(buttonsPanel.getComponentCount() - 2);

		assertThat(saveAndOpenEntryButton.getStyleName(), equalTo(sampleComponent.getCommitButton().getStyleName()));
		assertThat(saveAndOpenEntryButton.getId(), equalTo("saveAndOpenEntryButton"));

		// Both commit buttons should do the same, except each one should trigger a different navigation 
		assertThat(
			saveAndOpenEntryButton.getListeners(Button.ClickEvent.class).size(),
			equalTo(sampleComponent.getCommitButton().getListeners(Button.ClickEvent.class).size()));
	}

}
