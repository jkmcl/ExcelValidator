package jkml;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Checkers {

	public static final String ALPHA = "isAlpha";

	public static final String ALPHA_SPACE = "isAlphaSpace";

	public static final String ALPHANUMERIC = "isAlphanumeric";

	public static final String ALPHANUMERIC_SPACE = "isAlphanumericSpace";

	public static final String NUMBERIC = "isNumeric";

	public static final String NUMBERIC_SPACE = "isNumericSpace";

	private static final Map<String, Checker> CHECKER_MAP = Map.of(
			ALPHA, StringUtils::isAlpha,
			ALPHA_SPACE, StringUtils::isAlphaSpace,
			ALPHANUMERIC, StringUtils::isAlphanumeric,
			ALPHANUMERIC_SPACE, StringUtils::isAlphanumericSpace,
			NUMBERIC, StringUtils::isNumeric,
			NUMBERIC_SPACE, StringUtils::isNumericSpace);

	public static Checker get(String name) {
		return CHECKER_MAP.get(name);
	}

	private Checkers() {
	}

}
