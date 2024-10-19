package jkml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

class WorkbookValidatorTests {

	private static final String RULES_FILE = "sample.rules";

	private static final String EXCEL_FILE = "sample.xlsx";

	private static final String INVALID_EXCEL_FILE = "sample-invalid.xlsx";

	private final Logger logger = LogManager.getLogger(WorkbookValidatorTests.class);

	@BeforeEach
	void beforeEach(TestInfo testInfo) {
		logger.info("# Start of test: {}", testInfo.getDisplayName());
	}

	@AfterEach
	void afterEach() {
		logger.info("");
	}

	private void testValidate(String xlsxFileName, boolean valid) throws Exception {
		var xlsxFilePath = TestUtils.getResourceAsPath(xlsxFileName);
		logger.info("Validating file: {}", xlsxFilePath);

		var rulesManager = new RulesManager();
		rulesManager.loadRules("test", TestUtils.getResourceAsPath(RULES_FILE));

		var validator = new WorkbookValidator(rulesManager);
		var errors = new ArrayList<String>();

		try (var workbook = WorkbookFactory.create(xlsxFilePath.toFile())) {
			var result = validator.validate(workbook, "test", errors);
			if (errors.isEmpty()) {
				logger.info("No validation error");
			} else {
				logger.info("Validation errors:");
				errors.forEach(err -> logger.info("{}", err));
			}
			assertEquals(valid, result);
			assertEquals(valid, errors.isEmpty());
		}
	}

	@Test
	void testValidate_valid() throws Exception {
		testValidate(EXCEL_FILE, true);
	}

	@Test
	void testValidate_invalid() throws Exception {
		testValidate(INVALID_EXCEL_FILE, false);
	}

}
