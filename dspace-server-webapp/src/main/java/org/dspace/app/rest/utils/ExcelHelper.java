/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.app.rest.utils;

import java.io.*;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dspace.app.rest.model.ExcelDTO;
import org.dspace.content.Item;
import org.hibernate.internal.build.AllowSysOut;

public class ExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {
            "Sr No",
            "Case Detail",
            "Hierarchy(Community & Collection)",
            "Upload date",
            "Uploaded by",
    };
    static String SHEET = "items";

    public static ByteArrayInputStream tutorialsToExcel(List<ExcelDTO> tutorials) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }
            int rowIdx=1;
            int i = 1;
            for (ExcelDTO item : tutorials) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(i);
                row.createCell(1).setCellValue(item.getCaseDetail());
                row.createCell(2).setCellValue(item.getHierarchy());
                row.createCell(3).setCellValue(item.getUploaddate());
                row.createCell(4).setCellValue(item.getUploadedby());
                i++;
            }
            FileOutputStream out1 = new FileOutputStream( new File("D:\\item.xlsx"));
            workbook.write(out1);
            out1.flush();
            out1.close();
            workbook.write(out);
            out.close();
            workbook.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}