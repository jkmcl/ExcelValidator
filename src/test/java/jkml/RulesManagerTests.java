package jkml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

class RulesManagerTests {

	private final Logger logger = LogManager.getLogger(RulesManagerTests.class);

	@BeforeEach
	void beforeEach(TestInfo testInfo) {
		logger.info("# Start of test: {}", testInfo.getDisplayName());
	}

	@AfterEach
	void afterEach() {
		logger.info("");
	}

	@Test
	void testLoadRules() {
		var rulesManager = new RulesManager();
		assertDoesNotThrow(() -> rulesManager.loadRules("sample", TestUtils.getResourceAsPath("sample.rules")));
	}

}
