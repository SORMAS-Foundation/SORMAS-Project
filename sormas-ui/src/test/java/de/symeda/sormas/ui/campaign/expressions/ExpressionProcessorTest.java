package de.symeda.sormas.ui.campaign.expressions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

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

@RunWith(MockitoJUnitRunner.class)
public class ExpressionProcessorTest {

	private ExpressionProcessor expressionProcessor;
	private CampaignFormBuilder campaignFormBuilder;

	@BeforeClass
	public static void setupClass() {
		UI ui = new SormasUI();
		final MockedStatic<UI> uiMockedStatic = Mockito.mockStatic(UI.class);
		uiMockedStatic.when(UI::getCurrent).thenReturn(ui);

		Page page = new Page(UI.getCurrent(), new PageState());
		MockedStatic<Page> pageMockedStatic = Mockito.mockStatic(Page.class);
		pageMockedStatic.when(Page::getCurrent).thenReturn(page);
	}

	@Before
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

	private <D> List<D> createData(final ObjectMapper objectMapper, final InputStream inputStream, Class<D> dataType) throws IOException {
		JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, dataType);
		return objectMapper.readValue(inputStream, type);
	}
}
