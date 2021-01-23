package de.symeda.sormas.ui.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import com.vaadin.v7.data.util.converter.ConverterFactory;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.SormasUI;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.DownloadStream;
import com.vaadin.server.StreamResource;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.CaseFollowUpGrid;
import de.symeda.sormas.ui.caze.CasesView;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Date;

public class GridExportStreamResourceXLSXTest extends AbstractBeanTest {

    private SormasUI ui;

    @Before
    public void initUI() throws Exception {

        creator.createUser(null, null, null, "ad", "min", UserRole.ADMIN, UserRole.NATIONAL_USER);

        VaadinRequest request = Mockito.mock(VaadinServletRequest.class);
        when(request.getUserPrincipal()).thenReturn((Principal) () -> "admin");

        CurrentInstance.set(VaadinRequest.class, request);

        VaadinService service = Mockito.mock(VaadinService.class);
        CurrentInstance.set(VaadinService.class, service);

        VaadinSession session = Mockito.mock(VaadinSession.class);
        ConverterFactory converterFactory = new SormasDefaultConverterFactory();
        when(session.getConverterFactory()).thenReturn(converterFactory);
        when(session.getService()).thenReturn(service);
        CurrentInstance.set(VaadinSession.class, session);

        ui = new SormasUI();
        CurrentInstance.set(UI.class, ui);

        java.lang.reflect.Field pageField = UI.class.getDeclaredField("page");
        pageField.setAccessible(true);
        pageField.set(ui, Mockito.mock(Page.class));
    }

    @Test
    public void testCaseViewXSLX() throws IOException {
        int followUpRangeInterval = 14;
        final CaseCriteria criteria = ViewModelProviders.of(CasesView.class).get(CaseCriteria.class);
        final FilteredGrid<?, CaseCriteria> grid =
                new CaseFollowUpGrid(criteria, new Date(), followUpRangeInterval, CasesView.class);
        StreamResource streamResource =
                new GridExportStreamResourceXLSX(grid,
                        "sormas_cases",
                        "sormas_cases.xlsx");
        assertEquals(MimeTypes.XSLX.mimeType, streamResource.getMIMEType());
        DownloadStream downloadStream = streamResource.getStream();
        InputStream inputStream = downloadStream.getStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow xssfRow = sheet.getRow(0);
        XSSFCell xssfCell = xssfRow.getCell(0);
        String value = xssfCell.getStringCellValue();
        assertEquals("Follow-up ID", value);
    }
}
