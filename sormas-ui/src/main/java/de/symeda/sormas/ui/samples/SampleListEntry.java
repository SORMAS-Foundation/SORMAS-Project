package de.symeda.sormas.ui.samples;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleListEntry extends HorizontalLayout {

	private final SampleIndexDto sample;

	public SampleListEntry(SampleIndexDto sample) {

		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		this.sample = sample;
		String htmlLeft = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE,
				sample.getLab().getCaption().toString());
//						LayoutUtil.div(I18nProperties.getPrefixFieldCaption(TaskDto.I18N_PREFIX, TaskDto.SUGGESTED_START) + ": " + DateHelper.formatLocalShortDate(task.getSuggestedStart())) +
//						LayoutUtil.div(I18nProperties.getPrefixFieldCaption(TaskDto.I18N_PREFIX, TaskDto.DUE_DATE) + ": " + DateHelper.formatLocalShortDate(task.getDueDate()));
		Label labelLeft = new Label(htmlLeft, ContentMode.HTML);
		addComponent(labelLeft);

		String htmlRight = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE,
				sample.getSampleTestResult().toString());
//						LayoutUtil.div(I18nProperties.getPrefixFieldCaption(TaskDto.I18N_PREFIX, TaskDto.PRIORITY) + ": " + task.getPriority().toString()) +
//						LayoutUtil.div(I18nProperties.getPrefixFieldCaption(TaskDto.I18N_PREFIX, TaskDto.ASSIGNEE_USER) + ": " + task.getAssigneeUser().getCaption());
		Label labelRight = new Label(htmlRight, ContentMode.HTML);
		labelRight.addStyleName(CssStyles.ALIGN_RIGHT);
		addComponent(labelRight);
		setComponentAlignment(labelRight, Alignment.TOP_RIGHT);
	}

	public SampleIndexDto getSample() {
		return sample;
	}
}
