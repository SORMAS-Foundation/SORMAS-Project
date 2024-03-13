package de.symeda.sormas.ui.samples.sampleLink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.MultilineLabel;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;

public class SampleListComponentLayout extends SideComponentLayout {

	public SampleListComponentLayout(SampleListComponent sampleListComponent, String infoText) {
		super(sampleListComponent);

		if (UiUtil.permitted(UserRight.SAMPLE_CREATE)) {
			sampleListComponent.addStyleName(CssStyles.VSPACE_NONE);
			MultilineLabel sampleInfo =
				new MultilineLabel(infoText == null ? "" : VaadinIcons.INFO_CIRCLE.getHtml() + " " + infoText, ContentMode.HTML);
			sampleInfo.addStyleNames(CssStyles.VSPACE_2, CssStyles.VSPACE_TOP_4);
			addComponent(sampleInfo);
		}
	}

	public SampleListComponentLayout(SampleListComponent sampleListComponent, String infoText, boolean isEditAllowed) {
		super(sampleListComponent);

		if (UiUtil.permitted(UserRight.SAMPLE_CREATE) && infoText != null) {
			sampleListComponent.addStyleName(CssStyles.VSPACE_NONE);
			MultilineLabel sampleInfo = new MultilineLabel(VaadinIcons.INFO_CIRCLE.getHtml() + " " + infoText, ContentMode.HTML);
			sampleInfo.addStyleNames(CssStyles.VSPACE_2, CssStyles.VSPACE_TOP_4);
			sampleInfo.setEnabled(isEditAllowed);
			addComponent(sampleInfo);
		}

	}
}
