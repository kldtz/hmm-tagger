package kldtz.github.com.hmmt.counts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.hash.TObjectIntHashMap;
import kldtz.github.com.hmmt.container.TagNgram;
import kldtz.github.com.hmmt.container.WordTagTuple;

public class HashedWordCounts implements WordCounts, Serializable {
	private static final long serialVersionUID = 1L;
	private TObjectIntHashMap<String> wordCounts;
	private TObjectIntHashMap<WordTagTuple> wordTagCounts;
	private long numberOfTokens;
	private int maxNgramSize;

	private List<TObjectIntHashMap<TagNgram>> tagNgramCounts;
	
	public HashedWordCounts(int maxNgramSize) {
		this.wordCounts = new TObjectIntHashMap<>();
		this.wordTagCounts = new TObjectIntHashMap<>();
		this.tagNgramCounts = new ArrayList<>();
		for (int i = 0; i < maxNgramSize; i++) {
			tagNgramCounts.add(new TObjectIntHashMap<>());
		}
		this.numberOfTokens = 0;
		this.maxNgramSize = maxNgramSize;
	}
	
	@Override
	public void incrementCounts(String word, String tag) {
		wordCounts.adjustOrPutValue(word, 1, 1);
		WordTagTuple wordTag = new WordTagTuple(word, tag);
		wordTagCounts.adjustOrPutValue(wordTag, 1, 1);
		numberOfTokens++;
	}
	
	@Override
	public void incrementCount(String... tags) {
		TagNgram tagNgram = new TagNgram(tags);
		tagNgramCounts.get(tagNgram.getN() - 1).adjustOrPutValue(tagNgram, 1, 1);
	}
	
	@Override
	public int getWordCount(String word) {
		if (!wordCounts.contains(word)) {
			return 0;
		}
		return wordCounts.get(word);
	}

	@Override
	public int getWordTagCount(WordTagTuple wordTag) {
		if (!wordTagCounts.contains(wordTag)) {
			return 0;
		}
		return wordTagCounts.get(wordTag);
	}
	
	@Override
	public int getTagNgramCount(TagNgram tagNgram) {
		if (!tagNgramCounts.get(tagNgram.getN() - 1).contains(tagNgram)) {
			return 0;
		}
		return tagNgramCounts.get(tagNgram.getN() - 1).get(tagNgram);
	}

	@Override
	public long getNumberOfTokens() {
		return numberOfTokens;
	}
	
	@Override
	public Set<TagNgram> getNgramSet(int n) {
		int index = n - 1;
		if (index >= tagNgramCounts.size()) {
			throw new IllegalArgumentException(index + " >= " + tagNgramCounts.size());
		}
		return tagNgramCounts.get(index).keySet();
	}
	
	@Override
	public Map<String, Set<String>> collectPossibleStates() {
		HashMap<String, Set<String>> possibleStates = new HashMap<>();
		for (WordTagTuple wordTag : wordTagCounts.keySet()) {
			if (!possibleStates.containsKey(wordTag.word())) {
				possibleStates.put(wordTag.word(), new HashSet<String>());
			}
			possibleStates.get(wordTag.word()).add(wordTag.tag());
		}
		return possibleStates;
	}

	@Override
	public int getMaxNgramSize() {
		return maxNgramSize;
	}	
	
	@Override
	public Set<String> getLexikon() {
		return wordCounts.keySet();
	}
	
	@Override
	public Set<WordTagTuple> getWordTagTuples() {
		return wordTagCounts.keySet();
	}
}
