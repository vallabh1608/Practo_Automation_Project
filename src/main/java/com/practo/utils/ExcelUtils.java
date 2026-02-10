package com.practo.utils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;

import java.io.*;
import java.util.List;

public class ExcelUtils {
	
	public static void appendAnchors(List<String> names, List<String> hrefs, String filePath, String sheetName)
			throws IOException {

		if (names == null || hrefs == null || names.size() != hrefs.size()) {
			throw new IllegalArgumentException("names and hrefs must be non-null and same size.");
			
		}

		String safeSheet = WorkbookUtil
				.createSafeSheetName((sheetName == null || sheetName.isBlank()) ? "Anchors" : sheetName.trim());
		if (safeSheet.length() > 32)
			safeSheet = safeSheet.substring(0, 32);

		File outFile = new File(filePath);
		File parent = outFile.getParentFile();
		if (parent != null && !parent.exists())
			parent.mkdirs();

		Workbook wb = null;
		boolean newBook = !outFile.exists();

		if (newBook) {
			wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
		} else {
			try (FileInputStream fis = new FileInputStream(outFile)) {
				wb = WorkbookFactory.create(fis);
			} catch (Exception e) {

				wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
			}
		}

		try {
			Sheet sheet = wb.getSheet(safeSheet);
			if (sheet == null) {
				sheet = wb.createSheet(safeSheet);

				Row header = sheet.createRow(0);
				header.createCell(0).setCellValue("Name");
				header.createCell(1).setCellValue("Link");
			}
			
			int lastRow = sheet.getLastRowNum();
			int startRow = (lastRow == 0 && sheet.getRow(0) == null) ? 0 : lastRow + 1;
			if (startRow == 0)
				startRow = 1;
			for (int i = 0; i < names.size(); i++) {
				Row row = sheet.createRow(startRow + i);
				row.createCell(0).setCellValue(safeText(names.get(i)));
				row.createCell(1).setCellValue(safeText(hrefs.get(i)));
			}

			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);

			try (FileOutputStream fos = new FileOutputStream(outFile)) {
				wb.write(fos);
			}
		} finally {
			wb.close();
		}
	}
	private static String safeText(String s) {
		return (s == null) ? "" : s.trim();
	}
}
