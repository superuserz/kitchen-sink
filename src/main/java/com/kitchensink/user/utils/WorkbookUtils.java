package com.kitchensink.user.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public class WorkbookUtils {
	/**
	 * Sets the row.
	 *
	 * @param row         the row
	 * @param workbook    the workbook
	 * @param firstColumn the first column
	 * @param vars        the vars
	 * @param bold        the bold
	 * @param indexColor  the index color
	 * @param fontColor   the font color
	 */

	private WorkbookUtils() {

	}

	/**
	 * Sets the row.
	 *
	 * @param row         the row
	 * @param workbook    the workbook
	 * @param firstColumn the first column
	 * @param vars        the vars
	 * @param bold        the bold
	 * @param indexColor  the index color
	 * @param fontColor   the font color
	 */

	public static void setRow(final Row row, final Workbook workbook, int firstColumn, final String[] vars,
			final boolean bold, final short indexColor, final short fontColor) {

		Font font = workbook.createFont();

		font.setColor(fontColor);

		CellStyle cellStyle = workbook.createCellStyle();

		if (bold) {
			font.setBold(bold);
		}
		if (indexColor != -1) {
			cellStyle.setFillForegroundColor(indexColor);
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		font.setFontHeightInPoints((short) 10);
		cellStyle.setFont(font);
		cellStyle.setWrapText(true);

		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);

		for (String heading : vars) {
			Cell cell = row.createCell(firstColumn);
			cell.setCellValue(heading);
			cell.setCellStyle(cellStyle);
			firstColumn += 1;
		}
	}
}
