package jkml;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

class WorkbookValidatorTests {

	private static final String RULES_FILE = "sample.rules";

	private static final String EXCEL_FILE = "sample.xlsx";

	private final Logger log = LogManager.getLogger(WorkbookValidatorTests.class);

	@Test
	void testValidate() throws Exception {
		Path filePath = TestUtils.getResourceAsPath(RULES_FILE);
		RulesManager rmgr = new RulesManager();
		rmgr.loadRules(filePath);

		WorkbookValidator vdtr = new WorkbookValidator(rmgr);
		List<String> errors = new ArrayList<>();

		filePath = Path.of(this.getClass().getClassLoader().getResource(EXCEL_FILE).toURI());


		try (Workbook workbook = WorkbookFactory.create(filePath.toFile())) {
			var result = vdtr.validate(workbook, RULES_FILE, errors);
			log.info("Errors:");
			for (var err : errors) {
				log.info("{}", err);
			}
			assertTrue(result);
			assertTrue(errors.isEmpty());
		}
	}

}
