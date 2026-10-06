package de.symeda.sormas.ui.importer.hierarchical;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.BiConsumer;

import com.vaadin.ui.Upload;

import de.symeda.sormas.api.FacadeProvider;

public class SheetUploadReceiver implements Upload.Receiver, Upload.SucceededListener {

	private static final long serialVersionUID = 1L;

	private final BiConsumer<File, String> onUploaded;
	private File file;
	private String fileName;

	public SheetUploadReceiver(BiConsumer<File, String> onUploaded) {
		this.onUploaded = onUploaded;
	}

	@Override
	public OutputStream receiveUpload(String fileName, String mimeType) {
		String lowerName = fileName.toLowerCase(Locale.ROOT);
		if (!lowerName.endsWith(".csv") && !lowerName.endsWith(".xlsx")) {
			throw new IllegalArgumentException(fileName);
		}
		try {
			this.fileName = fileName;
			this.file = Files.createTempFile(Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath()), "sormas_nested_import_", "_" + fileName)
				.toFile();
			return new FileOutputStream(file);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void uploadSucceeded(Upload.SucceededEvent event) {
		onUploaded.accept(file, fileName);
	}
}
