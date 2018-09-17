package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class SampleTestList extends VerticalLayout {

	private SampleReferenceDto sampleRef;
	private int caseSampleCount;

	public SampleTestList(SampleReferenceDto sampleRef) {

		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST);

		this.sampleRef = sampleRef;
	}

	public void reload() {

		List<SampleTestDto> sampleTests = ControllerProvider.getSampleTestController()
				.getSampleTestsBySample(sampleRef);

		removeAllComponents();

		boolean hasEditRight = LoginHelper.hasUserRight(UserRight.SAMPLE_EDIT);

		// build entries
		for (SampleTestDto sampleTest : sampleTests) {
			SampleTestListEntry listEntry = new SampleTestListEntry(sampleTest);
			if (hasEditRight) {
				listEntry.addEditListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						ControllerProvider.getSampleTestController().edit(sampleTest, caseSampleCount,
								SampleTestList.this::reload);
					}
				});
			}
			addComponent(listEntry);
		}
	}
}
