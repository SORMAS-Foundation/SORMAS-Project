/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.campaign.expressions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ui.PageState;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignFormBuilder;

public class ExpressionProcessorTest {

	// static mocks are registered per thread; they must be closed after the class, otherwise every later test class on the
	// same thread that mocks UI or Page fails with "static mocking is already registered in the current thread"
	private static MockedStatic<UI> uiMockedStatic;
	private static MockedStatic<Page> pageMockedStatic;

	private ExpressionProcessor expressionProcessor;
	private CampaignFormBuilder campaignFormBuilder;

	@BeforeAll
	public static void setupClass() {
		UI ui = new SormasUI();
		uiMockedStatic = Mockito.mockStatic(UI.class);
		uiMockedStatic.when(UI::getCurrent).thenReturn(ui);

		Page page = new Page(UI.getCurrent(), new PageState());
		pageMockedStatic = Mockito.mockStatic(Page.class);
		pageMockedStatic.when(Page::getCurrent).thenReturn(page);
	}

	@AfterAll
	public static void tearDownClass() {

		// close both, even if one of them was never created or closing the first one fails
		try {
			if (pageMockedStatic != null) {
				pageMockedStatic.close();
			}
		} finally {
			pageMockedStatic = null;
			if (uiMockedStatic != null) {
				uiMockedStatic.close();
			}
			uiMockedStatic = null;
		}
	}

	@BeforeEach
	public void setup() throws IOException {
		GridLayout campaignFormLayout = new GridLayout(12, 1);
		ObjectMapper objectMapper = new ObjectMapper();

		List<CampaignFormElement> campaignFormElements =
			createData(objectMapper, this.getClass().getResourceAsStream("/campaign/expressions/formelements.json"), CampaignFormElement.class);
		List<CampaignFormDataEntry> campaignFormDataEntries =
			createData(objectMapper, this.getClass().getResourceAsStream("/campaign/expressions/formvalues.json"), CampaignFormDataEntry.class);

		campaignFormBuilder = new CampaignFormBuilder(campaignFormElements, campaignFormDataEntries, campaignFormLayout, Collections.emptyList());
		campaignFormBuilder.buildForm();
		expressionProcessor = new ExpressionProcessor(campaignFormBuilder);
	}

	@Test
	public void testDisableExpressionFieldsForEditing() {
		expressionProcessor.disableExpressionFieldsForEditing();
		assertThat(campaignFormBuilder.getFields(), not(equalTo(Collections.emptyMap())));
		assertThat(campaignFormBuilder.getFields().entrySet().stream().filter(entry -> !entry.getValue().isEnabled()).count(), is(2L));
	}

	@Test
	public void testAddExpressionListener() {
		expressionProcessor.addExpressionListener();
		final TextField field = (TextField) campaignFormBuilder.getFields().get("childrenVaccinatedRecall");
		field.setValue("xyz");
		field.setValue("42");
	}

	@Test
	public void testConfigureExpressionFieldsWithTooltip() {
		expressionProcessor.configureExpressionFieldsWithTooltip();
		final TextField field = (TextField) campaignFormBuilder.getFields().get("childrenLivingInHouses");
		assertThat(field.getDescription(), is("Calculated based on: Number of < 5 years children vaccinated based on recall"));
		final TextField fieldRecall = (TextField) campaignFormBuilder.getFields().get("childrenVaccinatedRecall");
		assertThat(fieldRecall.getDescription(), isEmptyOrNullString());
	}

	private <D> List<D> createData(final ObjectMapper objectMapper, final InputStream inputStream, Class<D> dataType) throws IOException {
		JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, dataType);
		return objectMapper.readValue(inputStream, type);
	}
}
