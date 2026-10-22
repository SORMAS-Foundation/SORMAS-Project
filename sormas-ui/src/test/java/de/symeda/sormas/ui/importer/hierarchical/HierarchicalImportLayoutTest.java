package de.symeda.sormas.ui.importer.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
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
	private static final ImportPreviewDto ONE_READY = new ImportPreviewDto(
		List.of(),
		List.of(),
		List.of(new BlockPreviewDto(0, 3, 3, "Jean Muller", BlockStatus.READY, "fp", List.of(), List.of())));

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

	@Test
	void aFinishedImportOffersCloseInsteadOfAFullProgressBar() {
		HierarchicalImportLayout layout = layout();
		AtomicBoolean closed = new AtomicBoolean();
		Window window = new Window() {

			@Override
			public void close() {
				closed.set(true);
			}
		};
		window.setContent(layout);
		layout.previewArrived(SHEET, ONE_READY);
		button(layout, "Import (1)").click();

		layout.commitFinished(Optional.empty());

		assertFalse(progressBar(layout).isVisible());
		Button close = button(layout, I18nProperties.getCaption(Captions.actionClose));
		assertTrue(close.isVisible());
		close.click();
		assertTrue(closed.get());
	}

	@Test
	void closeIsOnlyOfferedOnceTheImportIsDone() {
		HierarchicalImportLayout layout = layout();

		layout.previewArrived(SHEET, ONE_READY);

		assertFalse(button(layout, I18nProperties.getCaption(Captions.actionClose)).isVisible());
	}

	private static Button button(HasComponents root, String caption) {
		return components(root).filter(Button.class::isInstance)
			.map(Button.class::cast)
			.filter(button -> caption.equals(button.getCaption()))
			.findFirst()
			.orElseThrow(() -> new AssertionError("No button " + caption));
	}

	private static ProgressBar progressBar(HasComponents root) {
		return components(root).filter(ProgressBar.class::isInstance).map(ProgressBar.class::cast).findFirst().orElseThrow();
	}

	private static Stream<Component> components(HasComponents root) {
		return StreamSupport.stream(root.spliterator(), false)
			.flatMap(child -> child instanceof HasComponents ? Stream.concat(Stream.of(child), components((HasComponents) child)) : Stream.of(child));
	}

	private static HierarchicalImportLayout layout() {
		try (MockedStatic<UI> current = Mockito.mockStatic(UI.class)) {
			current.when(UI::getCurrent).thenReturn(new SormasUI());
			return new HierarchicalImportLayout(HierarchicalImportFacade.CASE_TEMPLATE);
		}
	}
}
