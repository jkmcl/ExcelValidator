package jkml;

import java.io.BufferedReader;
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

	private final Logger log = LogManager.getLogger(RulesManager.class);

	// workbook->(sheet->(column->rule))
	private Map<String, Map<String, Map<String, String>>> workbookMap = new HashMap<>();


	public Map<String, Map<String, String>> getSheetMap(String workbookType) {
		return workbookMap.get(workbookType);
	}

	public void addRule(String workbookType, String sheetName, String columnName, String rule) {
		var sheetMap = workbookMap.computeIfAbsent(workbookType, k -> new HashMap<>());
		var columnMap = sheetMap.computeIfAbsent(sheetName, k -> new HashMap<>());
		columnMap.put(columnName, rule);
	}

	public void printMap() {
		log.info(workbookMap);
	}

	public void loadRules(Path filePath) throws IOException {
		// Open file in UTF-8
		try (BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
			// Get file name
			String fileName = filePath.getFileName().toString();

			// Go through each line
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				// Skip blank and commented lines
				if (line.isBlank() || line.charAt(0) == '#') {
					continue;
				}

				// Split line
				String[] arr = StringUtils.split(line, "=", 2);
				String key = arr[0];
				String rule = arr[1];

				// Create entry in map
				arr = StringUtils.split(key, "/", 2);
				String sheet = arr[0];
				String col = arr[1];

				log.info("Sheet:  {}", sheet);
				log.info("Column: {}", col);
				log.info("Rule:   {}", rule);
				log.info("--");

				addRule(fileName, sheet, col, rule);
			}
		}
	}

}
