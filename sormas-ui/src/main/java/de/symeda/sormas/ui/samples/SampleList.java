package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class SampleList extends VerticalLayout {

	private final SampleCriteria sampleCriteria = new SampleCriteria();

	public SampleList(CaseReferenceDto caseRef) {

		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST);

		sampleCriteria.caze(caseRef);
	}

	public void reload() {

		List<SampleIndexDto> samples = FacadeProvider.getSampleFacade()
				.getIndexList(LoginHelper.getCurrentUser().getUuid(), sampleCriteria);

		removeAllComponents();

		boolean hasEditRight = LoginHelper.hasUserRight(UserRight.SAMPLE_EDIT);

		// build entries
		for (SampleIndexDto sample : samples) {
			SampleListEntry listEntry = new SampleListEntry(sample);
			if (hasEditRight) {
				listEntry.addEditListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						ControllerProvider.getSampleController().navigateToData(listEntry.getSample().getUuid());
					}
				});
			}
			addComponent(listEntry);
		}
	}
}
