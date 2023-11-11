package jkml;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class TestUtils {

	public static Path getResourceAsPath(String name) {
		try {
			return Path.of(Thread.currentThread().getContextClassLoader().getResource(name).toURI());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
