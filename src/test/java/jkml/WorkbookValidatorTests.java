package jkml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

class WorkbookValidatorTests {

	private static final String RULES_FILE = "sample.rules";

	private static final String EXCEL_FILE = "sample.xlsx";

	private static final String INVALID_EXCEL_FILE = "sample-invalid.xlsx";

	private final Logger log = LogManager.getLogger(WorkbookValidatorTests.class);

	private void testValidate(String fileName, boolean valid) throws Exception {
		var filePath = TestUtils.getResourceAsPath(RULES_FILE);
		var rmgr = new RulesManager();
		rmgr.loadRules(filePath);

		var vdtr = new WorkbookValidator(rmgr);
		var errors = new ArrayList<String>();

		filePath = Path.of(this.getClass().getClassLoader().getResource(fileName).toURI());

		try (var workbook = WorkbookFactory.create(filePath.toFile())) {
			var result = vdtr.validate(workbook, RULES_FILE, errors);
			if (!errors.isEmpty()) {
				log.info("Errors:");
				for (var err : errors) {
					log.info("{}", err);
				}
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
