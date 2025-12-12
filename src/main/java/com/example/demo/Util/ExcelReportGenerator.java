package com.example.demo.Util;

import com.example.demo.DTO.StudentReportRowDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelReportGenerator {

    public static byte[] generateStudentReport(List<StudentReportRowDTO> rows, String scopeLabel) throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Students");

            // Header style
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Percentage style
            CellStyle percentStyle = wb.createCellStyle();
            DataFormat fmt = wb.createDataFormat();
            percentStyle.setDataFormat(fmt.getFormat("0.00"));

            int rowIdx = 0;

            // Title row
            Row title = sheet.createRow(rowIdx++);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("Student Report (" + scopeLabel + ")");
            titleCell.setCellStyle(headerStyle);

            rowIdx++; // spacer

            // Header row
            Row header = sheet.createRow(rowIdx++);
            String[] cols = {"ID","Name","Department","Email","DOB","Courses","Subjects","Total Marks","Percentage"};
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }

            // Data rows
            for (StudentReportRowDTO r : rows) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;
                row.createCell(col++).setCellValue(r.getId());
                row.createCell(col++).setCellValue(r.getName());
                row.createCell(col++).setCellValue(r.getDepartmentName());  // ✅ FIXED
                row.createCell(col++).setCellValue(r.getEmail());
                row.createCell(col++).setCellValue(r.getDob() != null ? r.getDob().toString() : "");
                row.createCell(col++).setCellValue(String.join(", ", r.getCourseNames()));
                row.createCell(col++).setCellValue(r.getSubjectsCount());
                row.createCell(col++).setCellValue(r.getTotalMarks());
                Cell pCell = row.createCell(col++);
                pCell.setCellValue(r.getPercentage());
                pCell.setCellStyle(percentStyle);
            }

            // Autosize columns
            for (int i = 0; i < cols.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }
}
