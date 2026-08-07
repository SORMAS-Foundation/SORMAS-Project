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

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.document.DocumentStorageService;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@LocalBean
public class AttachmentService {

    public static final String DOCX_FILE_EXTENSION = ".docx";
    public static final String PDF_FILE_EXTENSION = ".pdf";
    public static final String IMAGE_FILE_EXTENSTIONS = ".jpg,.jpeg,.png,.gif";
    private static final String TEMP_FILE_PREFIX = "sormas_temp";
    private static final Random RANDOM = new Random();

    @EJB
    private DocumentService documentService;
    @EJB
    private DocumentStorageService documentStorageService;
    @EJB
    private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

    // @formatter:off
    private final Map<String, PdfConverter> converters = Map.of(
            DOCX_FILE_EXTENSION, new Docx4jFopDocXConverter(),
            IMAGE_FILE_EXTENSTIONS, new ImageConverter()
    );
    // @formatter:on

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

    File encryptPdf(File pdf, String password) throws IOException {
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

        return converters.get(converterKey.get());
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

    private class XdocReportDocXConverter implements PdfConverter {

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

    private class Docx4jFopDocXConverter implements PdfConverter {

        @Override
        public File convert(File file) throws IOException {
            String convertedFilePath = getTmpFilePathForConversion() + PDF_FILE_EXTENSION;

            try (InputStream is = new FileInputStream(file); OutputStream os = new FileOutputStream(convertedFilePath)) {

                WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);

                Docx4J.toPDF(wordMLPackage, os);

            } catch (Exception e) {
                throw new IOException("Failed to convert DOCX to PDF", e);
            }

            return new File(convertedFilePath);
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


    public File replaceText(File pdfFile, Map<String, String> replacementContext) throws IOException {
        File outputFile = new File(getTmpFilePathForConversion() + "_edited" + PDF_FILE_EXTENSION);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            replaceText(document, replacementContext);
            document.save(outputFile);
        }

        return outputFile;
    }

    public static PDDocument replaceText(PDDocument document, Map<String, String> replacementContext) throws IOException {
        Map<String, String> actualReplacementContext = replacementContext.entrySet().stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getKey()) && StringUtils.isNotBlank(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (MapUtils.isEmpty(actualReplacementContext)) {
            return document;
        }

        for (PDPage page : document.getPages()) {
            PDFStreamParser parser = new PDFStreamParser(page);
            List<Object> tokens = parser.parse();

            PDFont currentFont = null;

            for (int j = 0; j < tokens.size(); j++) {
                Object token = tokens.get(j);
                if (!(token instanceof Operator)) {
                    continue;
                }

                Operator op = (Operator) token;

                switch (op.getName()) {
                    case "Tf":
                        currentFont = resolveFont(tokens, j, page);
                        break;
                    case "Tj":
                        replaceInTj(tokens, j, actualReplacementContext, currentFont);
                        break;
                    case "TJ":
                        replaceInTJ(tokens, j, actualReplacementContext, currentFont);
                        break;
                    default:
                        break;
                }
            }

            writeTokensBack(document, page, tokens);
        }

        return document;
    }

    private static PDFont resolveFont(List<Object> tokens, int tfIndex, PDPage page) {
        // operands for "Tf" are: /FontName size Tf  -> font name is 2 tokens back
        if (tfIndex >= 2 && tokens.get(tfIndex - 2) instanceof COSName) {
            COSName fontName = (COSName) tokens.get(tfIndex - 2);
            try {
                return page.getResources().getFont(fontName);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private static void replaceInTj(List<Object> tokens, int operatorIndex,
                                    Map<String, String> replacementContext, PDFont font) {
        if (font == null) return;

        COSString previous = (COSString) tokens.get(operatorIndex - 1);
        String decoded = decodeWithFont(previous, font);
        if (decoded == null) return;

        String updated = decoded;
        for (Map.Entry<String, String> entry : replacementContext.entrySet()) {
            updated = updated.replace(entry.getKey(), entry.getValue());
        }

        if (!updated.equals(decoded)) {
            encodeInto(previous, updated, font);
        }
    }

    private static void replaceInTJ(List<Object> tokens, int operatorIndex,
                                    Map<String, String> replacementContext, PDFont font) {
        if (font == null) return;

        COSArray array = (COSArray) tokens.get(operatorIndex - 1);

        // Decode each string element individually via the font, keep numbers untouched
        StringBuilder combined = new StringBuilder();
        List<Integer> stringElementIndices = new ArrayList<>();
        for (int k = 0; k < array.size(); k++) {
            COSBase element = array.getObject(k);
            if (element instanceof COSString) {
                String decoded = decodeWithFont((COSString) element, font);
                if (decoded != null) {
                    combined.append(decoded);
                    stringElementIndices.add(k);
                }
            }
        }

        String key = combined.toString().trim();
        String replacement = replacementContext.get(key);
        if (replacement == null || stringElementIndices.isEmpty()) {
            return; // no whole-run match; leave array untouched to preserve spacing
        }

        // Put the full replacement into the FIRST string element (preserves its position),
        // blank out the other string elements but KEEP the numbers between them so
        // kerning/justification spacing for the rest of the line is unaffected.
        int firstIdx = stringElementIndices.get(0);
        encodeInto((COSString) array.getObject(firstIdx), replacement, font);

        for (int i = 1; i < stringElementIndices.size(); i++) {
            int idx = stringElementIndices.get(i);
            ((COSString) array.getObject(idx)).setValue(new byte[0]); // empty, not removed
        }
    }

    private static String decodeWithFont(COSString cosString, PDFont font) {
        byte[] bytes = cosString.getBytes();
        StringBuilder sb = new StringBuilder();
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            while (in.available() > 0) {
                int code = font.readCode(in);
                String unicode = font.toUnicode(code);
                if (unicode != null) {
                    sb.append(unicode);
                }
            }
        } catch (IOException e) {
            return null;
        }
        return sb.toString();
    }

    private static void encodeInto(COSString cosString, String text, PDFont font) {
        try {
            cosString.setValue(font.encode(text));
        } catch (IllegalArgumentException | IOException e) {
            // replacement text has a character not in this font's embedded subset
            System.err.println("Cannot encode '" + text + "' with font " + font.getName() + ": " + e.getMessage());
        }
    }

    private static void writeTokensBack(PDDocument document, PDPage page, List<Object> tokens) throws IOException {
        PDStream updatedStream = new PDStream(document);
        try (OutputStream out = updatedStream.createOutputStream(COSName.FLATE_DECODE)) {
            new ContentStreamWriter(out).writeTokens(tokens);
        }
        page.setContents(updatedStream);
    }
}
