package de.symeda.sormas.ui.importer.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.importexport.hierarchical.BlockPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.SormasUI;

class HierarchicalImportLayoutTest extends AbstractUiBeanTest {

	private static final ImportSheet SHEET = new ImportSheet(List.of("CaseData.disease"), List.of());
	private static final ImportPreviewDto ALL_UNCHANGED = new ImportPreviewDto(
		List.of(),
		List.of(),
		List.of(new BlockPreviewDto(0, 3, 3, "Jean Muller", BlockStatus.UNCHANGED, "fp", List.of(), List.of())));

	@Test
	void aReuploadWhereEveryBlockIsUnchangedShowsItsPreviewAgain() {
		HierarchicalImportLayout layout = layout();

		layout.previewArrived(SHEET, ALL_UNCHANGED);
		layout.previewArrived(SHEET, ALL_UNCHANGED);

		assertFalse(layout.isBusy());
	}

	@Test
	void theWindowCannotBeClosedWhileThePanelIsBusy() {
		HierarchicalImportLayout layout = layout();
		Window window = new Window();
		window.setContent(layout);

		layout.preview(SHEET);
		assertFalse(window.isClosable());

		layout.previewArrived(SHEET, ALL_UNCHANGED);
		assertTrue(window.isClosable());
	}

	@Test
	void theErrorFileIsNeverServedFromTheBrowserCache() {
		HierarchicalImportLayout layout = layout();

		layout.previewArrived(SHEET, ALL_UNCHANGED);

		StreamResource resource = (StreamResource) layout.errorFileResource();
		assertEquals(0, resource.getCacheTime());
		assertEquals("text/csv", resource.getMIMEType());
	}

	private static HierarchicalImportLayout layout() {
		try (MockedStatic<UI> current = Mockito.mockStatic(UI.class)) {
			current.when(UI::getCurrent).thenReturn(new SormasUI());
			return new HierarchicalImportLayout(HierarchicalImportFacade.CASE_TEMPLATE);
		}
	}
}
