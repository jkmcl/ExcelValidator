package jkml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;


public class WorkbookValidator {

	private final Logger log = LogManager.getLogger(WorkbookValidator.class);

	private RulesManager rmgr = new RulesManager();

	public WorkbookValidator(RulesManager rmgr) {
		this.rmgr = rmgr;
	}

	public boolean validate(Workbook workbook, String workbookType, List<String> errors) {
		Map<String, Map<String, String>> sheetMap = rmgr.getSheetMap(workbookType);

		// This workbook type has no rule
		if (sheetMap == null) {
			return true;
		}

		boolean ret = true;

		// Get index of sheets that have rules in the workbook
		List<Integer> sheetIndexList = new ArrayList<>(sheetMap.size());
		for (String sheetName : sheetMap.keySet()) {
			int sheetIndex = workbook.getSheetIndex(sheetName);
			if (sheetIndex < 0) {
				errors.add("Sheet not found: " + sheetName);
				ret = false;
				continue;
			}
			sheetIndexList.add(sheetIndex);
		}

		// Validate sheets that have rules in the order they appear in the workbook
		Collections.sort(sheetIndexList);
		log.debug("Sheet index list:" + sheetIndexList);
		for (int sheetIndex : sheetIndexList) {
			Sheet sheet = workbook.getSheetAt(sheetIndex);
			String sheetName = sheet.getSheetName();
			Map<String, String> columnMap = sheetMap.get(sheetName);
			if (!validateSheet(sheet, columnMap, errors)) {
				ret = false;
			}
		}

		return ret;
	}

	/** Given row and cell value, return the index of the column containing the cell value */
	private int getColumnIndex(Row row, String columnName) {
		for (int i = row.getFirstCellNum(), n = row.getLastCellNum(); i < n; ++i) {
			Cell cell = row.getCell(i);
			if (cell != null && cell.getStringCellValue().equals(columnName)) {
				return i;
			}
		}
		return -1;
	}

	/** Validate a sheet */
	private boolean validateSheet(Sheet sheet, Map<String, String> columnMap, List<String> errors) {
		boolean ret = true;

		// Get index of columns that have rules in the sheet
		List<Integer> columnIndexList = new ArrayList<>(columnMap.size());
		Row headerRow = sheet.getRow(0);
		for (String columnName : columnMap.keySet()) {
			int columnIndex = getColumnIndex(headerRow, columnName);
			if (columnIndex < 0) {
				errors.add(String.format("Column name (header row cell value) \"%s\" not found in sheet \"%s\"", columnName, sheet.getSheetName()));
				ret = false;
				continue;
			}
			columnIndexList.add(columnIndex);
		}

		// Validate columns that have rules in the order they appear in the sheet
		Collections.sort(columnIndexList);
		log.debug("Column index list:" + columnIndexList);
		for (int rowIndex = 1, maxRowIndex = sheet.getLastRowNum(); rowIndex <= maxRowIndex; ++rowIndex) {
			Row row = sheet.getRow(rowIndex);
			for (int colIndex : columnIndexList) {
				String columnName = headerRow.getCell(colIndex).getStringCellValue();
				Cell cell = row.getCell(colIndex);
				if (cell == null) {
					errors.add(String.format("Cell %s in column \"%s\"is not defined", (new CellReference(rowIndex, colIndex)).formatAsString(), columnName));
					ret = false;
					continue;
				}
				String rule = columnMap.get(columnName);
				if (!validateCell(cell, rule, errors)) {
					ret = false;
				}
			}
		}

		return ret;
	}

	private boolean validateCell(Cell cell, String rule, List<String> errors) {
		CellReference cellRef = new CellReference(cell);
		log.debug(String.format("Validating sheet \"%s\" and cell \"%s\"", cell.getSheet().getSheetName(), cellRef.formatAsString()));
		return true;
	}

}
