package jkml;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class RulesManagerTests {

	@Test
	void testLoad() throws Exception {
		Path filePath = Path.of(this.getClass().getClassLoader().getResource("sample.rules").toURI());

		RulesManager rmgr = new RulesManager();
		rmgr.loadRules(filePath);
		rmgr.printMap();
	}

}
