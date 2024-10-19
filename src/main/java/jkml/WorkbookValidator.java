package jkml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

public class WorkbookValidator {

	private final Logger logger = LogManager.getLogger(WorkbookValidator.class);

	private final DataFormatter dataFormatter = new DataFormatter();

	private final RulesManager rulesManager;

	public WorkbookValidator(RulesManager rulesManager) {
		this.rulesManager = rulesManager;
	}

	public boolean validate(Workbook workbook, String workbookType, List<String> errors) {
		var columnMap = rulesManager.getColumnMap(workbookType);

		// This workbook type has no rule
		if (columnMap == null) {
			return true;
		}

		var result = true;

		// Get index of sheets that have rules in the workbook
		var sheetIndexList = new ArrayList<Integer>(columnMap.size());
		for (var sheetName : columnMap.keySet()) {
			var sheetIndex = workbook.getSheetIndex(sheetName);
			if (sheetIndex < 0) {
				errors.add("Sheet not found: " + sheetName);
				result = false;
				continue;
			}
			sheetIndexList.add(sheetIndex);
		}

		// Validate sheets that have rules in the order they appear in the workbook
		Collections.sort(sheetIndexList);
		for (var sheetIndex : sheetIndexList) {
			var sheet = workbook.getSheetAt(sheetIndex);
			var sheetName = sheet.getSheetName();
			var checkerMap = columnMap.get(sheetName);
			if (!validateSheet(sheet, checkerMap, errors)) {
				result = false;
			}
		}

		return result;
	}

	/** Given row and cell value, return the index of the column containing the cell value */
	private int getColumnIndex(Row row, String columnName) {
		for (int i = row.getFirstCellNum(), n = row.getLastCellNum(); i < n; ++i) {
			var cell = row.getCell(i);
			if (cell != null && cell.getStringCellValue().equals(columnName)) {
				return i;
			}
		}
		return -1;
	}

	/** Validate a sheet */
	private boolean validateSheet(Sheet sheet, Map<String, String> columnMap, List<String> errors) {
		var sheetName = sheet.getSheetName();
		logger.info("Validating sheet: {}", sheetName);

		var result = true;

		// Get index of columns that have rules in the sheet
		var columnIndexList = new ArrayList<Integer>(columnMap.size());
		var headerRow = sheet.getRow(0);
		for (var columnName : columnMap.keySet()) {
			var columnIndex = getColumnIndex(headerRow, columnName);
			if (columnIndex < 0) {
				errors.add(String.format("Column name (header row cell value) \"%s\" not found in sheet \"%s\"", columnName, sheetName));
				result = false;
				continue;
			}
			columnIndexList.add(columnIndex);
		}

		// Validate columns that have rules in the order they appear in the sheet
		Collections.sort(columnIndexList);
		for (int rowIndex = 1, maxRowIndex = sheet.getLastRowNum(); rowIndex <= maxRowIndex; ++rowIndex) {
			var row = sheet.getRow(rowIndex);
			for (int colIndex : columnIndexList) {
				var columnName = headerRow.getCell(colIndex).getStringCellValue();
				var cell = row.getCell(colIndex);
				if (cell == null) {
					errors.add(String.format("Cell %s in column \"%s\"is not defined", (new CellReference(rowIndex, colIndex)).formatAsString(), columnName));
					result = false;
					continue;
				}
				var checkerName = columnMap.get(columnName);
				if (!validateCell(sheetName, cell, checkerName, errors)) {
					result = false;
				}
			}
		}

		return result;
	}

	private boolean validateCell(String sheetName, Cell cell, String checkerName, List<String> errors) {
		var checker = Checkers.get(checkerName);
		if (checker == null) {
			return true;
		}

		var value = dataFormatter.formatCellValue(cell);
		if (!checker.check(value)) {
			errors.add(String.format("%s!%s contains invalid value (checker: %s): %s", sheetName, cell.getAddress(), checkerName, value));
			return false;
		}

		return true;
	}

}
