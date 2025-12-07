package utils;

import org.apache.poi.xssf.usermodel.*;
import java.io.*;
import java.util.*;

public class ExcelWriter {

    public static void writeCartResults(String filePath, List<Map<String,String>> rows) throws Exception{

        File file = new File(filePath);

        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Failed to delete existing file: " + file.getAbsolutePath());
            }
        }

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(file)) {

            XSSFSheet sheet = workbook.createSheet("CartResults");

            // כותרות
            String[] heads = {"ProductName","UnitPrice","Quantity","RowPrice","Status","Screenshot"};
            XSSFRow header = sheet.createRow(0);
            for (int i = 0; i < heads.length; i++) {
                header.createCell(i).setCellValue(heads[i]);
            }

            // שורות עם נתונים
            int r = 1;
            for (Map<String, String> map : rows) {
                XSSFRow row = sheet.createRow(r++);
                row.createCell(0).setCellValue(map.getOrDefault("ProductName", ""));
                row.createCell(1).setCellValue(map.getOrDefault("UnitPrice", ""));
                row.createCell(2).setCellValue(map.getOrDefault("Quantity", ""));
                row.createCell(3).setCellValue(map.getOrDefault("RowPrice", ""));
                row.createCell(4).setCellValue(map.getOrDefault("Status", ""));
                row.createCell(5).setCellValue(map.getOrDefault("Screenshot", ""));
            }

            // התאמת רוחב עמודות
            for (int i = 0; i < heads.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fos);
        }
    }
}
