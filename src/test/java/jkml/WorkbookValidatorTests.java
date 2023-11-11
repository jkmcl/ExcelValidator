package jkml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

class WorkbookValidatorTests {

	private static final String RULES_FILE = "sample.rules";

	private static final String EXCEL_FILE = "sample.xlsx";

	@Test
	void test() throws Exception {
		Path filePath = TestUtils.getResourceAsPath(RULES_FILE);
		RulesManager rmgr = new RulesManager();
		rmgr.loadRules(filePath);

		WorkbookValidator vdtr = new WorkbookValidator(rmgr);
		List<String> errors = new ArrayList<>();

		filePath = Path.of(this.getClass().getClassLoader().getResource(EXCEL_FILE).toURI());

		try (Workbook workbook = WorkbookFactory.create(filePath.toFile())) {
			vdtr.validate(workbook, RULES_FILE, errors);
		}
	}

}
