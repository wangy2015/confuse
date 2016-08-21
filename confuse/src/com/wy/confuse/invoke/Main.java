package com.wy.confuse.invoke;

import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		String[] includeClasses = null;
		String[] sources = { "G:/idea/confuse/confuse.test/src" };
		String[] excludeClasses = {};

		String[] encodings = new String[sources.length];
		Arrays.fill(encodings, "UTF-8");
		ReplaceInvoker replacer = new ReplaceInvoker(sources, encodings,
				includeClasses, excludeClasses);
		replacer.replace();
	}
}
