package kldtz.github.com.hmmt.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kldtz.github.com.hmmt.corpus.Capitalization;

public class Utils {
	public static final char PADDING_CHAR = ' ';
	public static final String ERROR_TAG = "??";
	private static final String TAGSET_RESOURCE = "/PennTreebankTagset.txt";
	
	private Utils() {}
	
	public static int findIndexOfLargestElement(double[] array) {
		int indexOfLargest = 0;
		double valueOfLargest = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > valueOfLargest) {
				valueOfLargest = array[i];
				indexOfLargest = i;
			}
		}
		return indexOfLargest;
	}
	
	public static <T> void swap(List<T> list, int firstIndex, int secondIndex) {
		T firstValue = list.get(firstIndex);
		list.set(firstIndex, list.get(secondIndex));
		list.set(secondIndex, firstValue);
	}
	
	public static String padWord(String word, int maxLength) {
		return padWord(word, maxLength, PADDING_CHAR);
	}
	
	public static String padWord(String word, int maxLength, char paddingChar) {
		StringBuilder sb = new StringBuilder();
		for (int i = word.length(); i < maxLength; i++) {
			sb.append(paddingChar);
		}
		sb.append(word);
		return sb.toString();
	}
	
	public static Capitalization extractCapitalization(String word) {
		char firstChar = word.charAt(0);
		Capitalization capitalization = Capitalization.OTHER;
		if (Character.isUpperCase(firstChar)) {
			capitalization = Capitalization.UPPERCASE;
		} else if (Character.isLowerCase(firstChar)) {
			capitalization = Capitalization.LOWERCASE;
		}
		return capitalization;
	}
	
	public static Set<String> readTagset() {
		return readTagset(TAGSET_RESOURCE);
	}
	
	public static Set<String> readTagset(String tagsetResource) {
		Set<String> tagset = new HashSet<>();
		try {
			Path tagsetFilePath = Paths.get(Utils.class.getResource(tagsetResource).toURI());
			Files.lines(tagsetFilePath).map(String::trim).filter(s -> !s.isEmpty()).forEach(s -> tagset.add(s));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
		tagset.add(ERROR_TAG);
		return tagset;
	}
}
