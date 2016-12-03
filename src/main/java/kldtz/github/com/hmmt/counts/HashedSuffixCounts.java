package kldtz.github.com.hmmt.counts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kldtz.github.com.hmmt.container.SctTriple;
import kldtz.github.com.hmmt.corpus.Capitalization;
import kldtz.github.com.hmmt.utils.Utils;

public class HashedSuffixCounts implements SuffixCounts, Serializable {	
	private static final long serialVersionUID = 1L;
	private Map<SctTriple, Integer> suffixCounts;
	private int maxSuffixLength;
	private Set<String> tagset;
	
	public HashedSuffixCounts(Set<String> tagset, int maxSuffixLength) {
		suffixCounts = new HashMap<>();
		this.maxSuffixLength = maxSuffixLength;
		this.tagset = tagset;
	}

	@Override
	public void incrementCounts(String word, String tag, int frequency) {
		Capitalization c = Utils.extractCapitalization(word);
		String paddedWord = Utils.padWord(word, maxSuffixLength);
		for (int i = 0; i <= maxSuffixLength; i++) {
			SctTriple sct = new SctTriple(paddedWord.substring(paddedWord.length() - i), c, tag);
			if (!suffixCounts.containsKey(sct)) {
				suffixCounts.put(sct, 0);
			}
			suffixCounts.put(sct, suffixCounts.get(sct) + frequency);
		}		
	}

	@Override
	public List<Integer> collectSuffixTagCounts(String word, String tag) {
		List<Integer> counts = new ArrayList<>();
		Capitalization c = Utils.extractCapitalization(word);
		String paddedWord = Utils.padWord(word, maxSuffixLength);
		for (int i = 0; i <= maxSuffixLength; i++) {
			SctTriple sct = new SctTriple(paddedWord.substring(paddedWord.length() - i), c, tag);
			Integer count = suffixCounts.get(sct);
			if (count == null) {
				count = 0;
			}
			counts.add(count);
		}
		return counts;
	}

	@Override
	public Set<String> collectPossibleStates(Capitalization capitalization) {
		Set<String> possibleStates = new HashSet<>();
		for (String tag : tagset) {
			SctTriple sct = new SctTriple("", capitalization, tag);
			Integer sctCount = suffixCounts.get(sct);
			if (sctCount != null && sctCount > 0) {
				possibleStates.add(tag);
			}
		}
		return possibleStates;
	}

}
