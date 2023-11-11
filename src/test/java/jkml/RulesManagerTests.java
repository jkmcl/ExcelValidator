package jkml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class RulesManagerTests {

	@Test
	void testLoad() throws Exception {
		Path filePath = TestUtils.getResourceAsPath("sample.rules");

		RulesManager rmgr = new RulesManager();
		assertDoesNotThrow(() -> rmgr.loadRules(filePath));
		rmgr.printMap();
	}

}
