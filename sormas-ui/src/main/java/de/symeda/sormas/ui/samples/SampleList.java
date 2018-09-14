package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class SampleList extends VerticalLayout implements LayoutClickListener {

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
		// build entries
		for (SampleIndexDto sample : samples) {
			SampleListEntry listEntry = new SampleListEntry(sample);
			listEntry.addLayoutClickListener(this);
			addComponent(listEntry);
		}
	}

	@Override
	public void layoutClick(LayoutClickEvent event) {
		SampleListEntry listEntry = (SampleListEntry) event.getComponent();
		ControllerProvider.getSampleController().navigateToData(listEntry.getSample().getUuid());
	}
}
