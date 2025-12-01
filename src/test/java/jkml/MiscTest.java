package jkml;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

class MiscTest {

	@Test
	void test() throws IOException {

		var text = StringUtils.repeat("x", 32_768);

		var outFile = Path.of("target", "test-output", "output.xlsx");

		PathUtils.createParentDirectories(outFile);
		Files.deleteIfExists(outFile);

		try (var wb = WorkbookFactory.create(true); var os = Files.newOutputStream(outFile)) {
			var cell = wb.createSheet("Sheet1").createRow(0).createCell(0);
			cell.setCellValue(StringUtils.truncate(text, SpreadsheetVersion.EXCEL2007.getMaxTextLength()));
			wb.write(os);
		}

		assertTrue(Files.exists(outFile));
	}

}
