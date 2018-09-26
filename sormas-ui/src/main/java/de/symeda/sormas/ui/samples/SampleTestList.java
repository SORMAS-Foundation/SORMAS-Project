package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class SampleTestList extends PaginationList<SampleTestDto> {

	private SampleReferenceDto sampleRef;
	private int caseSampleCount;

	public SampleTestList(SampleReferenceDto sampleRef) {
		super(5);

		this.sampleRef = sampleRef;
	}

	@Override
	public void reload() {
		List<SampleTestDto> sampleTests = ControllerProvider.getSampleTestController()
				.getSampleTestsBySample(sampleRef);
		

		setEntries(sampleTests);
		if (!sampleTests.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noSampleTestsLabel = new Label("There are no tests for this Sample.");
			listLayout.addComponent(noSampleTestsLabel);
		}
	}
	
	@Override
	protected void drawDisplayedEntries() {
		for (SampleTestDto sampleTest : getDisplayedEntries()) {
			SampleTestListEntry listEntry = new SampleTestListEntry(sampleTest);
			if (LoginHelper.hasUserRight(UserRight.SAMPLE_EDIT)) {
				listEntry.addEditListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						ControllerProvider.getSampleTestController().edit(sampleTest, caseSampleCount,
								SampleTestList.this::reload);
					}
				});
			}
			listLayout.addComponent(listEntry);
		}
	}
}
