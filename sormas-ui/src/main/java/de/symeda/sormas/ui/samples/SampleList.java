package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class SampleList extends PaginationList<SampleIndexDto> {

	private final SampleCriteria sampleCriteria = new SampleCriteria();

	public SampleList(CaseReferenceDto caseRef) {
		super(5);

		sampleCriteria.caze(caseRef);
	}

	@Override
	public void reload() {
		List<SampleIndexDto> samples = FacadeProvider.getSampleFacade()
				.getIndexList(LoginHelper.getCurrentUser().getUuid(), sampleCriteria);
				
		setEntries(samples);
		if (!samples.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noSamplesLabel = new Label("There are no samples for this Case.");
			listLayout.addComponent(noSamplesLabel);
		}
	}
	
	@Override
	protected void drawDisplayedEntries() {
		for (SampleIndexDto sample : getDisplayedEntries()) {
			SampleListEntry listEntry = new SampleListEntry(sample);
			if (LoginHelper.hasUserRight(UserRight.SAMPLE_EDIT)) {
				listEntry.addEditListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						ControllerProvider.getSampleController().navigateToData(listEntry.getSample().getUuid());
					}
				});
			}
			listLayout.addComponent(listEntry);
		}
	}
	
}
