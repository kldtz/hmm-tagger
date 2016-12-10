package kldtz.github.com.hmmt.evaluation;

import java.util.HashMap;
import java.util.Map;

public class EvaluationData {
	private static final String SENTENCE_LENGTH = "sentenceLength";
	private static final String SENTENCE_INDEX = "sentenceIndex";
	private static final String WORD_INDEX_IN_SENTENCE = "wordIndexInSentence";
	private static final String IS_KNOWN_WORD = "isKnownWord";
	private static final String ACTUAL_TAG = "actualTag";
	private static final String EXPECTED_TAG = "expectedTag";
	private static final String WORD = "word";
	private static final String[] header = { WORD, EXPECTED_TAG, ACTUAL_TAG, IS_KNOWN_WORD, WORD_INDEX_IN_SENTENCE,
			SENTENCE_INDEX, SENTENCE_LENGTH };
	private static char delimiter = ',';

	private Map<String, String> data;

	public EvaluationData() {
		data = new HashMap<>();
	}

	public EvaluationData setWord(String word) {
		data.put(WORD, word);
		return this;
	}

	public EvaluationData setExpectedTag(String expectedTag) {
		data.put(EXPECTED_TAG, expectedTag);
		return this;
	}

	public EvaluationData setActualTag(String actualTag) {
		data.put(ACTUAL_TAG, actualTag);
		return this;
	}

	public EvaluationData setIsKnownWord(boolean isKnownWord) {
		data.put(IS_KNOWN_WORD, Boolean.toString(isKnownWord));
		return this;
	}

	public EvaluationData setWordIndexInSentence(int wordIndexInSentence) {
		data.put(WORD_INDEX_IN_SENTENCE, Integer.toString(wordIndexInSentence));
		return this;
	}

	public EvaluationData setSentenceIndex(int sentenceIndex) {
		data.put(SENTENCE_INDEX, Integer.toString(sentenceIndex));
		return this;
	}

	public EvaluationData setSentenceLength(int sentenceLength) {
		data.put(SENTENCE_LENGTH, Integer.toString(sentenceLength));
		return this;
	}
	
	public static void setDelimiter(char delim) {
		delimiter = delim;
	}

	public static String getHeader() {
		StringBuilder sb = new StringBuilder();
		for (String label : header) {
			sb.append(label);
			sb.append(delimiter);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	public String getLine() {
		StringBuilder sb = new StringBuilder();
		for (String key : header) {
			sb.append(data.get(key));
			sb.append(delimiter);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
}
