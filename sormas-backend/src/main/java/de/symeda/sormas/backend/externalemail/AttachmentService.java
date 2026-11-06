/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.externalemail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.google.common.base.Suppliers;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import de.symeda.sormas.api.conversion.DocxToPdfConversionAdapterFacade;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.document.DocumentStorageService;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

@Stateless
@LocalBean
public class AttachmentService {

	public static final String DOCX_FILE_EXTENSION = ".docx";
	public static final String PDF_FILE_EXTENSION = ".pdf";
	public static final String IMAGE_FILE_EXTENSTIONS = ".jpg,.jpeg,.png,.gif";

	private static final String DOCX_PDF_CONVERTER_CONFIG_KEY = "DOCX_PDF_CONVERTER";
	private static final String XDOC_DEFAULT_DOCX_TO_PDF_CONVERTER = "XDOC";
	private static final String DOCX_TO_PDF_CONVERSION_FACADE_JNDI_CONFIG_KEY = "DOCX_TO_PDF_CONVERSION_FACADE_JNDI";

	private static final String TEMP_FILE_PREFIX = "sormas_temp";
	private static final Random RANDOM = new Random();

	@EJB
	private DocumentService documentService;
	@EJB
	private DocumentStorageService documentStorageService;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@EJB
	private SystemConfigurationValueFacade systemConfigurationValueEjb;

	private final Supplier<PdfConverter> IMAGE_CONVERTER_SUPPLIER = Suppliers.memoize(ImageConverter::new);

	private final Map<String, DocxToPdfConverter> docxToPdfConverters =
		Map.of(XDOC_DEFAULT_DOCX_TO_PDF_CONVERTER, new XdocReportDocXConverter(), "ADAPTER_DELEGATE", new AdapterDelegateConverter());

	private @NotNull Supplier<PdfConverter> getAppropriateDocxToPdfConverter() {
		return () -> Optional.ofNullable(systemConfigurationValueEjb.getValue(DOCX_PDF_CONVERTER_CONFIG_KEY))
			.map(docxToPdfConverters::get)
			.orElseGet(() -> docxToPdfConverters.get(XDOC_DEFAULT_DOCX_TO_PDF_CONVERTER));
	}

	private final Map<String, Supplier<PdfConverter>> converters =
		Map.of(DOCX_FILE_EXTENSION, getAppropriateDocxToPdfConverter(), IMAGE_FILE_EXTENSTIONS, IMAGE_CONVERTER_SUPPLIER);

	public Set<String> getAttachableFileExtensions() {
		return Set.of((PDF_FILE_EXTENSION + "," + DOCX_FILE_EXTENSION + "," + IMAGE_FILE_EXTENSTIONS).split(","));
	}

	public Map<File, String> createEncryptedPdfs(Map<File, String> sormasDocuments, String password) {
		Map<File, String> encryptedFiles = new HashMap<>(Collections.emptyMap());

		sormasDocuments.forEach((document, fileName) -> {
			String fileExtension = fileName.substring(fileName.lastIndexOf("."));

			try {
				final File encryptedPdf;
				if (fileExtension.equals(PDF_FILE_EXTENSION)) {
					encryptedPdf = encryptPdf(document, password);
				} else {
					PdfConverter converter = getConverter(fileExtension);
					fileName = converter.getConvertedFileName(fileName);
					File converted = converter.convert(document);
					encryptedPdf = encryptPdf(converted, password);
					converted.delete();
				}
				encryptedFiles.put(encryptedPdf, fileName);
			} catch (IOException e) {
				// not really expected to happen
				throw new RuntimeException(e);
			}
		});

		return encryptedFiles;
	}

	private File encryptPdf(File pdf, String password) throws IOException {
		PDDocument pdd = Loader.loadPDF(pdf);

		AccessPermission ap = new AccessPermission();
		StandardProtectionPolicy stpp = new StandardProtectionPolicy(password, password, ap);
		stpp.setEncryptionKeyLength(128);
		stpp.setPermissions(ap);
		pdd.protect(stpp);

		String encryptedFile = getTmpFilePathForConversion() + "_encrypted" + PDF_FILE_EXTENSION;
		pdd.save(encryptedFile);
		pdd.close();

		return new File(encryptedFile);
	}

	private PdfConverter getConverter(String fileExtension) {
		Optional<String> converterKey = converters.keySet().stream().filter(k -> k.contains(fileExtension)).findFirst();
		if (converterKey.isEmpty()) {
			throw new IllegalArgumentException("No converter found for file extension " + fileExtension);
		}

		return converters.get(converterKey.get()).get();
	}

	private Path getTmpFilePathForConversion() {
		Path path = Paths.get(configFacade.getTempFilesPath());
		String fileName = TEMP_FILE_PREFIX + "_converted_" + DateHelper.formatDateForExport(new Date()) + "_" + RANDOM.nextInt(Integer.MAX_VALUE);

		return path.resolve(fileName);

	}

	private interface PdfConverter {

		File convert(File file) throws IOException;

		default String getConvertedFileName(String fileName) {
			return fileName.substring(0, fileName.lastIndexOf(".")) + PDF_FILE_EXTENSION;
		}
	}

	private interface DocxToPdfConverter extends PdfConverter {

	}

	private class XdocReportDocXConverter implements DocxToPdfConverter {

		@Override
		public File convert(File file) throws IOException {
			String convertedFilePath = getTmpFilePathForConversion() + PDF_FILE_EXTENSION;
			try (InputStream inputStream = new FileInputStream(file); OutputStream outputStream = new FileOutputStream(convertedFilePath)) {
				XWPFDocument document = new XWPFDocument(inputStream);
				PdfOptions options = PdfOptions.create();
				// Convert .docx file to .pdf file
				fr.opensagres.poi.xwpf.converter.pdf.PdfConverter.getInstance().convert(document, outputStream, options);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}

			return new File(convertedFilePath);
		}
	}

	private class AdapterDelegateConverter implements DocxToPdfConverter {

		@Override
		public File convert(File file) {
			String targetPdfFilePath = getTmpFilePathForConversion() + PDF_FILE_EXTENSION;
			getDocxToPdfConversionFacade().convertToPdf(file.getPath(), targetPdfFilePath);
			return new File(targetPdfFilePath);
		}

		private DocxToPdfConversionAdapterFacade getDocxToPdfConversionFacade() {
			String jndiName = systemConfigurationValueEjb.getValue(DOCX_TO_PDF_CONVERSION_FACADE_JNDI_CONFIG_KEY);

			if (StringUtils.isBlank(jndiName)) {
				throw new RuntimeException(
					String.format(
						"[%s] was not specified, but is required to call the adapter for the conversion",
						DOCX_TO_PDF_CONVERSION_FACADE_JNDI_CONFIG_KEY));
			}
			try {
				return (DocxToPdfConversionAdapterFacade) new InitialContext().lookup(jndiName);
			} catch (NamingException e) {
				throw new RuntimeException("Could not look up DocxToPdfConversionFacade via JNDI: " + jndiName, e);
			}
		}

	}

	private class ImageConverter implements PdfConverter {

		@Override
		public File convert(File file) throws IOException {
			String convertedFilePath = getTmpFilePathForConversion() + PDF_FILE_EXTENSION;

			try (FileOutputStream fos = new FileOutputStream(convertedFilePath);) {
				Document document = new Document();
				PdfWriter writer = PdfWriter.getInstance(document, fos);

				writer.open();
				document.open();

				Image image = Image.getInstance(file.getPath());
				float scale = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / image.getWidth()) * 100;
				image.scalePercent(scale);
				document.add(image);

				document.close();
				writer.close();
			} catch (DocumentException e) {
				throw new RuntimeException(e);
			}

			return new File(convertedFilePath);
		}
	}

}
