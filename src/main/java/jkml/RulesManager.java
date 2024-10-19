package jkml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RulesManager {

	private final Logger logger = LogManager.getLogger(RulesManager.class);

	// workbook->(sheet->(column->checker))
	private Map<String, Map<String, Map<String, String>>> sheetMap = new HashMap<>();

	// Returns sheet->(column->checker)
	public Map<String, Map<String, String>> getColumnMap(String workbookType) {
		return sheetMap.get(workbookType);
	}

	public void addRule(String workbookType, String sheetName, String columnName, String checkerName) {
		logger.debug("Sheet: {}; Column: {}; Checker: {}", sheetName, columnName, checkerName);

		var columnMap = sheetMap.computeIfAbsent(workbookType, k -> new HashMap<>());
		var checkerMap = columnMap.computeIfAbsent(sheetName, k -> new HashMap<>());
		checkerMap.put(columnName, checkerName);
	}

	public void loadRules(String workbookType, Path filePath) throws IOException {
		logger.debug("Loading rules for workbook type: {}; File path: {}", workbookType, filePath);

		// Open file in UTF-8
		try (var br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
			// Go through each line
			String line;
			while ((line = br.readLine()) != null) {
				// Skip blank and commented lines
				if (line.isBlank() || line.charAt(0) == '#') {
					continue;
				}

				// Split line
				var keyChecker = StringUtils.split(line, "=", 2);
				var key = keyChecker[0].trim();
				var checker = keyChecker[1].trim();

				// Create entry in map
				var sheetColumn = StringUtils.split(key, "/", 2);
				var sheet = sheetColumn[0].trim();
				var column = sheetColumn[1].trim();
				addRule(workbookType, sheet, column, checker);
			}
		}
	}

}
