package vpn.mailSender.excels;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Number;
import jxl.write.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vpn.Main;
import vpn.mailSender.respondents.NotifiedEmployee;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PrintInExcel {

    //private static DateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");

    private static final Logger log  = LogManager.getLogger(PrintInExcel.class);
    private static final String EXCEL_GENERATED_FOLDER = "reports";

    public String createExcelFile(Set<NotifiedEmployee> notifiedEmployees, String folderPath) {

        WritableWorkbook workbook = null;

        //		Calendar c = Calendar.getInstance();
        //		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        //		String time = s.format(c.getTime());
        //		String fileName = reportFileLocationPath + "Performance_" + time + ".xls";
        String fileName = folderPath +File.separator + "wfhReport" + ".xls";

        WorkbookSettings workbookSettings = new WorkbookSettings();
        workbookSettings.setLocale(new Locale("en", "EN"));
        workbookSettings.setEncoding("UTF-8");

        // Create new CSV (Comma Separated Value) file on local file system
        File file = new File(fileName);
        log.info("Performance Report file is located in: " + file.getAbsolutePath());
        log.info("Report file is located in: " + file.getAbsolutePath());

        // WritableWorkbook workbook = null;
        // Create Excel workbook
        try {
            workbook = Workbook.createWorkbook(file, workbookSettings);

            workbook.createSheet("Global", 0);
            processGlobalSheet(0, workbook, notifiedEmployees);


            workbook.write();
            workbook.close();

        } catch (IOException | WriteException e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
        return fileName;

    }

    private static void processGlobalSheet(int currentSheet, WritableWorkbook workbook, Set<NotifiedEmployee> notifiedEmployees) {
        try {
            // Get the sheet to save the content in it
            WritableSheet reportSheet = workbook.getSheet(currentSheet);

            int j = 0;
            Colour BLUE = Colour.ICE_BLUE;
            Colour GREEN = Colour.GREEN;
            Colour LIGHT_GREEN = Colour.LIGHT_GREEN;
            Colour ORANGE = Colour.ORANGE;
            Colour WHITE = Colour.WHITE;

            WritableCellFormat blueHeaderFormat = getFormat(BLUE, true);
            WritableCellFormat greenHeaderFormat = getFormat(GREEN, true);
            WritableCellFormat lightGreenHeaderFormat = getFormat(LIGHT_GREEN, true);
            WritableCellFormat orangeHeaderFormat = getFormat(ORANGE, true);

            WritableCellFormat normalFormat = getFormat(WHITE, false);

            reportSheet.setColumnView(0, 14); // Employee Initials
            reportSheet.setColumnView(1, 14); // Employee Name
            reportSheet.setColumnView(2, 21); // Employee Email
            reportSheet.setColumnView(3, 18); // Employee Team
            reportSheet.setColumnView(4, 10); // TeamLeader Email
            reportSheet.setColumnView(5, 10); // date



            reportSheet.setRowView(0, 1200);

            reportSheet.addCell(new Label(j++, 0, "Employee Initials", blueHeaderFormat));
            reportSheet.addCell(new Label(j++, 0, "Employee Name", blueHeaderFormat));
            reportSheet.addCell(new Label(j++, 0, "Employee Email", blueHeaderFormat));
            reportSheet.addCell(new Label(j++, 0, "Employee Team", blueHeaderFormat));
            reportSheet.addCell(new Label(j++, 0, "TeamLeader Email", blueHeaderFormat));
            reportSheet.addCell(new Label(j++, 0, "Date", blueHeaderFormat));



            // Iterate over content and fill data in "Global" sheet
            int contSize = notifiedEmployees.size();
            int cur = 0;

//            for (int i = 1; i <= contSize; i++) {
            for (NotifiedEmployee notifiedEmployee:notifiedEmployees) {
                cur++;

                j = 0;

                reportSheet.addCell(new Label(j++, cur, notifiedEmployee.getEmployeeInitials(), normalFormat));
                reportSheet.addCell(new Label(j++, cur, notifiedEmployee.getEmployeeName(), normalFormat));
                reportSheet.addCell(new Label(j++, cur, notifiedEmployee.getEmployeeEmail(), normalFormat));
                reportSheet.addCell(new Label(j++, cur, notifiedEmployee.getEmployeeTeam(), normalFormat));
                reportSheet.addCell(new Label(j++, cur, notifiedEmployee.getTeamLeaderEmail(), normalFormat));
                reportSheet.addCell(new Label(j++, cur, notifiedEmployee.getDate().toString(), normalFormat));


            }
        } catch (WriteException e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
    }


    private static WritableCellFormat getFormat(Colour color, boolean bold) {
        try {
            WritableFont font;
            if (bold)
                font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD, false);
            else
                font = new WritableFont(WritableFont.TIMES, 11, WritableFont.NO_BOLD, false);
            WritableCellFormat trzyFormat = new WritableCellFormat(font);
            trzyFormat.setBackground(color);
            trzyFormat.setWrap(true);
            trzyFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
            trzyFormat.setAlignment(Alignment.CENTRE);

            return trzyFormat;
        } catch (WriteException e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
        return null;
    }

    /**
     * Creates folder in default path.
     *
     * @return absolute path of the folder.
     */
    public String createFolder() {
        File directory = new File(EXCEL_GENERATED_FOLDER);
        log.error( "Full path of excel-generated directory: " + directory.getAbsolutePath());
        directory.mkdir();
        return directory.getAbsolutePath();
    }

    /**
     * Deletes folder.
     *
     * @param folder for deleting.
     */
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}
