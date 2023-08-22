package io.eliud.misc.yaml;

import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker;

public class MyStringQuotingChecker extends StringQuotingChecker {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean needToQuoteName(String name) {
		return false;
	}

	@Override
	public boolean needToQuoteValue(String value) {
		return false;
	}

}
