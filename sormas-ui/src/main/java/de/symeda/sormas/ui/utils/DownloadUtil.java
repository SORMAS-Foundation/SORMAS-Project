package de.symeda.sormas.ui.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;

public class DownloadUtil {
	
	private DownloadUtil() {
		
	}

	@SuppressWarnings("serial")
	public static StreamResource createStreamResource(final File file, String fileName, String mimeType) {
		StreamResource streamResource = new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				try {
					return new BufferedInputStream(Files.newInputStream(file.toPath()));
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}, fileName);
		streamResource.setMIMEType(mimeType);
		streamResource.setCacheTime(0);
		return streamResource;
	}

}