package kldtz.github.com.hmmt.counts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import kldtz.github.com.hmmt.container.SctTriple;
import kldtz.github.com.hmmt.corpus.Capitalization;
import kldtz.github.com.hmmt.utils.Utils;

public class HashedSuffixCounts implements SuffixCounts, Serializable {	
	private static final long serialVersionUID = 1L;
	private TObjectIntMap<SctTriple> suffixCounts;
	private int maxSuffixLength;
	private Set<String> tagset;
	
	public HashedSuffixCounts(Set<String> tagset, int maxSuffixLength) {
		suffixCounts = new TObjectIntHashMap<>(1000, 0.5f, 0);
		this.maxSuffixLength = maxSuffixLength;
		this.tagset = tagset;
	}

	@Override
	public void incrementCounts(String word, String tag, int frequency) {
		Capitalization c = Utils.extractCapitalization(word);
		String paddedWord = Utils.padWord(word, maxSuffixLength);
		for (int i = 0; i <= maxSuffixLength; i++) {
			SctTriple sct = new SctTriple(paddedWord.substring(paddedWord.length() - i), c, tag);
			suffixCounts.adjustOrPutValue(sct, frequency, frequency);
		}		
	}

	@Override
	public List<Integer> collectSuffixTagCounts(String word, String tag) {
		List<Integer> counts = new ArrayList<>();
		Capitalization c = Utils.extractCapitalization(word);
		String paddedWord = Utils.padWord(word, maxSuffixLength);
		for (int i = 0; i <= maxSuffixLength; i++) {
			SctTriple sct = new SctTriple(paddedWord.substring(paddedWord.length() - i), c, tag);
			counts.add(suffixCounts.get(sct));
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
