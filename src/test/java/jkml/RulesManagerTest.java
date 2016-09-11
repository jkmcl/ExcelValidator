package jkml;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;


public class RulesManagerTest {
	
	@Test
	public void testLoad() throws Exception {
		Path filePath = Paths.get(this.getClass().getClassLoader().getResource("sample.rules").toURI());
		
		RulesManager rmgr = new RulesManager();
		rmgr.loadRules(filePath);
		rmgr.printMap();
	}

}
