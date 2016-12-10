package kldtz.github.com.hmmt.counts;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import kldtz.github.com.hmmt.container.WordTagTuple;
import kldtz.github.com.hmmt.corpus.CorpusFileReader;
import kldtz.github.com.hmmt.corpus.CorpusFormat;

public class CountsBuilder {
	private final String corpusPath;
	private final CorpusFormat corpusFormat;
	private int maxNgramSize;
	private int maxSuffixLength;
	private SuffixCountsType suffixCountsType;
	private int maxWordFrequency;
	private BitSet testInstances;

	public CountsBuilder(String corpusPath, CorpusFormat corpusFormat) {
		this.corpusPath = corpusPath;
		this.corpusFormat = corpusFormat;
		maxNgramSize = 3;
		maxSuffixLength = 4;
		suffixCountsType = SuffixCountsType.HASH;
		maxWordFrequency = 10;
		testInstances = null;
	}

	// for evaluation purposes
	public CountsBuilder(Path corpusFilePath, CorpusFormat corpusFormat, BitSet isTestInstance) {
		if (Files.isDirectory(corpusFilePath)) {
			throw new IllegalArgumentException("Corpus must be provided in one file");
		}
		this.corpusPath = corpusFilePath.toString();
		this.corpusFormat = corpusFormat;
		maxNgramSize = 3;
		maxSuffixLength = 4;
		suffixCountsType = SuffixCountsType.HASH;
		maxWordFrequency = 10;
		this.testInstances = isTestInstance;
	}

	public CountsBuilder maxNgramSize(int maxNgramSize) {
		this.maxNgramSize = maxNgramSize;
		return this;
	}

	public CountsBuilder maxSuffixLength(int maxSuffixLength) {
		this.maxSuffixLength = maxSuffixLength;
		return this;
	}

	public CountsBuilder suffixCounts(SuffixCountsType suffixCountsType) {
		this.suffixCountsType = suffixCountsType;
		return this;
	}

	public CountsBuilder maxWordFrequencyForSuffixCounts(int maxWordFrequency) {
		this.maxWordFrequency = maxWordFrequency;
		return this;
	}

	public Counts build() {
		WordCounts wordCounts = testInstances == null ? collectWordLevelCounts()
				: collectSelectedWordLevelCountsInFile();
		SuffixCounts suffixCounts = collectSuffixLevelCounts(wordCounts);
		return new Counts(wordCounts, suffixCounts);
	}

	private WordCounts collectWordLevelCounts() {
		Queue<File> queue = new LinkedList<>();
		queue.offer(new File(corpusPath));
		WordCounts wordCounts = new HashedWordCounts(maxNgramSize);
		while (!queue.isEmpty()) {
			File element = queue.poll();
			if (element.isFile()) {
				collectWordLevelCountsInFile(wordCounts, element);
			} else {
				queue.addAll(Arrays.asList(element.listFiles()));
			}
		}
		return wordCounts;
	}

	private WordCounts collectSelectedWordLevelCountsInFile() {
		WordCounts wordCounts = new HashedWordCounts(maxNgramSize);
		CorpusFileReader corpusReader = corpusFormat.createCorpusFileReader(new File(corpusPath));
		int sentenceIndex = 0;
		for (List<WordTagTuple> sentence : corpusReader) {
			if (!testInstances.get(sentenceIndex)) {
				List<WordTagTuple> input = prepareInput(sentence, wordCounts.getStartTag(), wordCounts.getEndTag());
				countSentence(wordCounts, input);
			}
			sentenceIndex++;
		}
		corpusReader.close();
		return wordCounts;
	}

	private void collectWordLevelCountsInFile(WordCounts wordCounts, File file) {
		CorpusFileReader corpusReader = corpusFormat.createCorpusFileReader(file);
		for (List<WordTagTuple> sentence : corpusReader) {
			List<WordTagTuple> input = prepareInput(sentence, wordCounts.getStartTag(), wordCounts.getEndTag());
			countSentence(wordCounts, input);
		}
		corpusReader.close();
	}

	private List<WordTagTuple> prepareInput(List<WordTagTuple> sentence, String startTag, String endTag) {
		List<WordTagTuple> input = new ArrayList<>();
		WordTagTuple startTuple = new WordTagTuple(startTag, startTag);
		for (int i = 0; i < maxNgramSize - 1; i++) {
			input.add(startTuple);
		}
		input.addAll(sentence);
		WordTagTuple endTuple = new WordTagTuple(endTag, endTag);
		input.add(endTuple);
		return input;
	}

	private void countSentence(WordCounts wordCounts, List<WordTagTuple> sentence) {
		for (int i = 1; i <= maxNgramSize; i++) {
			for (int j = 0; j <= sentence.size() - i; j++) {
				List<WordTagTuple> ngram = sentence.subList(j, j + i);
				countNgram(wordCounts, ngram);
			}
		}
	}

	private void countNgram(WordCounts wordCounts, List<WordTagTuple> ngram) {
		if (ngram.size() == 1) {
			wordCounts.incrementCounts(ngram.get(0).word(), ngram.get(0).tag());
		}
		String[] tags = new String[ngram.size()];
		for (int k = 0; k < ngram.size(); k++) {
			tags[k] = ngram.get(k).tag();
		}
		wordCounts.incrementCount(tags);
	}

	private SuffixCounts collectSuffixLevelCounts(WordCounts wordCounts) {
		SuffixCounts suffixCounts = suffixCountsType.constructInstance(wordCounts.collectTagset(), maxSuffixLength);
		for (WordTagTuple wordTag : wordCounts.getWordTagTuples()) {
			if (wordCounts.getWordCount(wordTag.word()) <= maxWordFrequency) {
				suffixCounts.incrementCounts(wordTag.word(), wordTag.tag(), wordCounts.getWordTagCount(wordTag));
			}
		}
		return suffixCounts;
	}
}
