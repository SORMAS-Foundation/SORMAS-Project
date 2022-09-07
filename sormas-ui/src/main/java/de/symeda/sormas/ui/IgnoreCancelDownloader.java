package de.symeda.sormas.ui;

import java.io.IOException;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;

public class IgnoreCancelDownloader extends FileDownloader {

	/**
	 * Creates a new file downloader for the given resource. To use the
	 * downloader, you should also {@link #extend(AbstractClientConnector)} the
	 * component.
	 * 
	 * Source: https://vaadin.com/docs/v8/framework/articles/LettingTheUserDownloadAFile
	 *
	 * @param resource
	 *            the resource to download when the user clicks the extended
	 *            component.
	 */
	public IgnoreCancelDownloader(Resource resource) {
		super(resource);
	}

	@Override
	public boolean handleConnectorRequest(final VaadinRequest request, final VaadinResponse response, final String path) {
		try {
			return super.handleConnectorRequest(request, response, path);
		} catch (final IOException ignored) {
			return true;
		}
	}
}
