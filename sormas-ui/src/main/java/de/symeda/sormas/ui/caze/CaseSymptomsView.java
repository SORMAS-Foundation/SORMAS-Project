/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.caze;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.AnnotationFieldHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

public class CaseSymptomsView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/symptoms";
	public static final String COMPLICATIONS_LOC = "Complications";
	public CommitDiscardWrapperComponent<SymptomsForm> caseSymptomsComponent;

	public CaseSymptomsView() {
		super(VIEW_NAME, true);
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();
		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> caseSymptomsComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getByUuid(getCaseRef().getUuid());

		// #14119: only show the Complications side panel for diseases that actually have complications configured
		// (symptom fields annotated with @Complication). This also covers #14029 (Tuberculosis has none, so it stays empty).
		boolean showComplications =
			!AnnotationFieldHelper.getComplicatedSymptomsWithDiseases(SymptomsDto.class, caseDataDto.getDisease()).isEmpty();
		CaseSymptomSideViewComponent symptomSideViewComponent =
			showComplications ? new CaseSymptomSideViewComponent(caseDataDto.getDisease()) : null;
		caseSymptomsComponent =
			ControllerProvider.getCaseController().getSymptomsEditComponent(getCaseRef().getUuid(), getViewMode(), symptomSideViewComponent);
		LayoutWithSidePanel layout = new LayoutWithSidePanel(caseSymptomsComponent, COMPLICATIONS_LOC);
		container.addComponent(layout);
		if (showComplications) {
			layout.addSidePanelComponent(symptomSideViewComponent, COMPLICATIONS_LOC);
			symptomSideViewComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
		}

		setSubComponent(container);
		setEditPermission(caseSymptomsComponent);
	}
}
