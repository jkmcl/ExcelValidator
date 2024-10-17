package jkml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class RulesManagerTests {

	@Test
	void testLoad() {
		var filePath = TestUtils.getResourceAsPath("sample.rules");

		var rmgr = new RulesManager();
		assertDoesNotThrow(() -> rmgr.loadRules(filePath));
		rmgr.printMap();
	}

}
