package kldtz.github.com.hmmt.evaluation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import kldtz.github.com.hmmt.utils.TagIndexMapping;

public class ConfusionMatrix {
	TagIndexMapping mapping;
	int[][] frequencies;
	int[] columnSize;
	Map<String, Double> stats;

	int tagsetSize;

	public ConfusionMatrix(Set<String> tagset) {
		mapping = new TagIndexMapping(tagset);
		frequencies = new int[tagset.size()][tagset.size()];
		initializeColumnSize();
		stats = new HashMap<>();
		tagsetSize = tagset.size();
	}

	public void increment(String expected, String actual) {
		frequencies[mapping.getIndex(expected)][mapping.getIndex(actual)] += 1;
	}

	public void incrementBy(String expected, String acctual, int value) {
		frequencies[mapping.getIndex(expected)][mapping.getIndex(acctual)] += value;
	}

	private double numberOfExpectedTags(int tagIndex) {
		String key = "numberOfExpectedTags_" + tagIndex;
		if (!stats.containsKey(key)) {
			stats.put(key, (double) Arrays.stream(frequencies[tagIndex]).sum());
		}
		return stats.get(key);
	}

	private double numberOfActualTags(int tagIndex) {
		String key = "numberOfActualTags_" + tagIndex;
		if (!stats.containsKey(key)) {
			stats.put(key, (double) IntStream.range(0, tagsetSize).map(i -> frequencies[i][tagIndex]).sum());
		}
		return stats.get(key);
	}

	public double macroAveragedPrecision() {
		String key = "macroAveragePrecision";
		if (!stats.containsKey(key)) {
			stats.put(key, IntStream.range(0, tagsetSize).mapToDouble(this::precision).filter(Double::isFinite)
					.average().getAsDouble());
		}
		return stats.get(key);
	}

	public List<Pair<String, Double>> precisionPerTag() {
		List<Pair<String, Double>> precisionPerTag = new ArrayList<>();
		for (String tag : mapping.getTagset()) {
			double precision = precision(tag);
			if (!Double.isFinite(precision)) {
				continue;
			}
			precisionPerTag.add(new ImmutablePair<>(tag, precision));
		}
		precisionPerTag.sort(new PairComparator<Double>());
		return precisionPerTag;
	}

	public double precision(String tag) {
		return precision(mapping.getIndex(tag));
	}

	private double precision(int tagIndex) {
		return numberOfHits(tagIndex) / numberOfActualTags(tagIndex);
	}

	public double macroAveragedRecall() {
		String key = "macroAverageRecall";
		if (!stats.containsKey(key)) {
			stats.put(key, IntStream.range(0, tagsetSize).mapToDouble(this::recall).filter(Double::isFinite).average()
					.getAsDouble());
		}
		return stats.get(key);
	}

	public List<Pair<String, Double>> recallPerTag() {
		List<Pair<String, Double>> recallPerTag = new ArrayList<>();
		for (String tag : mapping.getTagset()) {
			double recall = recall(tag);
			if (!Double.isFinite(recall)) {
				continue;
			}
			recallPerTag.add(new ImmutablePair<>(tag, recall));
		}
		recallPerTag.sort(new PairComparator<Double>());
		return recallPerTag;
	}

	public double recall(String tag) {
		return recall(mapping.getIndex(tag));
	}

	private double recall(int tagIndex) {
		return numberOfHits(tagIndex) / numberOfExpectedTags(tagIndex);
	}

	public double accuracy() {
		return numberOfHits() / total();
	}

	private double numberOfHits(int tagIndex) {
		return frequencies[tagIndex][tagIndex];
	}

	private double numberOfHits() {
		String key = "hits";
		if (!stats.containsKey(key)) {
			int tps = IntStream.range(0, tagsetSize).map(i -> frequencies[i][i]).sum();
			stats.put(key, (double) tps);
		}
		return stats.get(key);
	}

	private double total() {
		String key = "total";
		if (!stats.containsKey(key)) {
			int total = Arrays.stream(frequencies).flatMapToInt(Arrays::stream).sum();
			stats.put(key, (double) total);
		}
		return stats.get(key);
	}

	private class PairComparator<V extends Comparable<V>> implements Comparator<Pair<?, V>> {
		@Override
		public int compare(Pair<?, V> o1, Pair<?, V> o2) {
			return o1.getValue().compareTo(o2.getValue());
		}
	}

	// Layout ------------------------------------------------

	public String getSummaryStatistics() {
		List<Pair<String, Double>> precisionPerTag = precisionPerTag();
		List<Pair<String, Double>> recallPerTag = recallPerTag();
		StringBuilder sb = new StringBuilder();
		sb.append("Precision(Tag)").append("\t").append("Recall(Tag)").append("\n");
		String formatString = "#.###";
		int keyLength = columnSize[0] + 1;
		int valueSize = formatString.length();
		DecimalFormat df = new DecimalFormat(formatString);
		for (int i = 0; i < Math.max(precisionPerTag.size(), recallPerTag.size()); i++) {
			if (i < precisionPerTag.size()) {
				sb.append(padLeft(precisionPerTag.get(i).getKey()));
				sb.append(": ");
				sb.append(padRight(df.format(precisionPerTag.get(i).getValue()), valueSize));
			} else {
				sb.append(padLeft("", keyLength)).append(padRight("", valueSize));
			}
			if (i < recallPerTag.size()) {
				sb.append("\t");
				sb.append(padLeft(recallPerTag.get(i).getKey()));
				sb.append(": ");
				sb.append(padRight(df.format(recallPerTag.get(i).getValue()), valueSize));
				sb.append("\n");
			}
		}
		String line = padRight("", columnSize[0] - 1, '-') + "--" + padRight("", valueSize, '-');
		sb.append(line);
		sb.append("\t");
		sb.append(line);
		return sb.toString();
	}

	private String padLeft(String value) {
		int size = columnSize[0] - 1;
		return padLeft(value, size);
	}
	
	private String padLeft(String value, int size) {
		return padLeft(value, size, ' ');
	}

	private String padLeft(String value, int size, char paddingChar) {
		if (value.length() > size) {
			throw new IllegalArgumentException("Value is bigger than given size!");
		}
		String paddedValue = "";
		while (paddedValue.length() < size - value.length()) {
			paddedValue += paddingChar;
		}
		paddedValue += value;
		return paddedValue;
	}

	public String getConfusionMatrix() {
		updateMinColumnSizes();
		StringBuilder sb = new StringBuilder();
		sb.append(padLeft("T\\P")).append(" ");
		for (int expectedTagIndex = 0; expectedTagIndex < tagsetSize; expectedTagIndex++) {
			sb.append(padField(mapping.getTag(expectedTagIndex), expectedTagIndex + 1));
		}
		sb.append("\n");
		for (int actualTagIndex = 0; actualTagIndex < tagsetSize; actualTagIndex++) {
			sb.append(padLeft(mapping.getTag(actualTagIndex))).append(" ");
			for (int expectedTagIndex = 0; expectedTagIndex < tagsetSize; expectedTagIndex++) {
				sb.append(padField(frequencies[actualTagIndex][expectedTagIndex], expectedTagIndex + 1));
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private void initializeColumnSize() {
		String[] tagArray = mapping.getTagArray();
		columnSize = new int[tagArray.length + 1];
		int column = 1;
		int maxTagLength = 0;
		for (String tag : tagArray) {
			columnSize[column] = tag.length() + 1;
			if (tag.length() > maxTagLength) {
				maxTagLength = tag.length();
			}
			column++;
		}
		columnSize[0] = maxTagLength + 1;
	}

	private void updateMinColumnSizes() {
		for (int column = 1; column < columnSize.length; column++) {
			int maxValueLength = computeMaxValueLength(column);
			if (maxValueLength + 1 > columnSize[column]) {
				columnSize[column] = maxValueLength + 1;
			}
		}
	}

	private int computeMaxValueLength(int column) {
		int expectedTagIndex = column - 1;
		return IntStream.range(0, tagsetSize).map(i -> Integer.toString(frequencies[i][expectedTagIndex]).length())
				.max().getAsInt();
	}

	private String padField(int field, int column) {
		return padField(Integer.toString(field), column);
	}

	private String padField(String field, int column) {
		int cellSize = columnSize[column];
		return padRight(field, cellSize - 1) + " ";
	}
	
	private String padRight(String value, int size) {
		return padRight(value, size, ' ');
	}

	private String padRight(String value, int size, char paddingChar) {
		if (value.length() > size) {
			throw new IllegalArgumentException("Field is bigger than given size!");
		}
		String formattedValue = value;
		while (formattedValue.length() < size) {
			formattedValue += paddingChar;
		}
		return formattedValue;
	}

}
