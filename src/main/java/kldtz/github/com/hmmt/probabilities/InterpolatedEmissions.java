package kldtz.github.com.hmmt.probabilities;

import java.util.List;
import java.util.Set;

import gnu.trove.map.hash.TObjectDoubleHashMap;
import kldtz.github.com.hmmt.container.TagNgram;
import kldtz.github.com.hmmt.container.WordTagTuple;
import kldtz.github.com.hmmt.counts.Counts;

public class InterpolatedEmissions implements EmissionProbabilities {
	public static final int MAX_SUFFIX_LENGTH = 5;

	private Counts counts;
	private double weight;
	
	private TObjectDoubleHashMap<WordTagTuple> wordTagProbabilities;

	public InterpolatedEmissions(Counts wordCounts) {
		this.counts = wordCounts;
		wordTagProbabilities = new TObjectDoubleHashMap<>();
		weight = computeStandardDeviationOfTagProbabilities();
	}

	public double computeUnsmoothedProbability(String word, String tag) {
		WordTagTuple wordTag = new WordTagTuple(word, tag);
		TagNgram unigram = new TagNgram(tag);
		int tagCount = counts.getTagNgramCount(unigram);
		if (tagCount == 0) {
			return 0;
		}
		return (double) counts.getWordTagCount(wordTag) / tagCount;
	}

	public double computeUnsmoothedAbsLogProbability(String word, String tag) {
		return Math.abs(Math.log(computeUnsmoothedProbability(word, tag)));
	}
	
	private double computeStandardDeviationOfTagProbabilities() {
		Set<TagNgram> tagSet = counts.getNgramSet(1);
		double averageTagProb = computeAverageTagProbability(tagSet);
		double squaredDifference = 0;
		for (TagNgram tag : tagSet) {
			squaredDifference += Math.pow(computeTagProbability(tag) - averageTagProb, 2);
		}
		return squaredDifference / (tagSet.size() - 1);
	}

	private double computeAverageTagProbability(Set<TagNgram> tagSet) {
		double tagCount = 0;
		for (TagNgram tag : tagSet) {
			tagCount += counts.getTagNgramCount(tag);
		}
		return tagCount / (counts.getNumberOfTokens() * tagSet.size());
	}
	
	private double computeTagProbability(TagNgram tag) {
		return (double) counts.getTagNgramCount(tag) / counts.getNumberOfTokens();
	}

	public double getLogProbability(String word, String tag) {
		return Math.log(getProbability(word, tag));
	}

	public double getProbability(String word, String tag) {
		WordTagTuple wordTag = new WordTagTuple(word, tag);
		if (wordTagProbabilities.contains(wordTag)) {
			return wordTagProbabilities.get(wordTag);
		}
		double wordProb = computeUnsmoothedProbability(word, tag);
		double suffixProb = computeInterpolatedSuffixProbability(word, tag);
		double interpolatedWordProb = (1 - weight) * wordProb + weight * suffixProb;
		wordTagProbabilities.put(wordTag, interpolatedWordProb);
		return interpolatedWordProb;
	}

	private double computeInterpolatedSuffixProbability(String word, String tag) {
		int tagCount = counts.getTagNgramCount(new TagNgram(tag));
		List<Integer> suffixFrequencies = counts.collectSuffixTagCounts(word, tag);
		double prob = computeProb(suffixFrequencies.get(0), tagCount);
		for (int i = 1; i < suffixFrequencies.size(); i++) {
			prob = (computeProb(suffixFrequencies.get(i), tagCount) + weight
					* prob) / (1 + weight);
		}
		return prob;
	}
	
	private double computeProb(int suffixCount, int tagCount) {
		if (tagCount == 0) {
			return 0;
		}
		return (double) suffixCount / tagCount;
	}
}
