package kldtz.github.com.hmmt.counts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kldtz.github.com.hmmt.corpus.Capitalization;
import kldtz.github.com.hmmt.utils.TagIndexMapping;
import kldtz.github.com.hmmt.utils.Utils;

public class TrieSuffixCounts implements SuffixCounts, Serializable {
	private static final long serialVersionUID = 1L;
	private TagIndexMapping tagIndexMapping;
	private Map<Capitalization, ReverseTrie> tries;
	
	public TrieSuffixCounts(Set<String> tagset, int maxSuffixLength) {
		tagIndexMapping = new TagIndexMapping(tagset);
		
		tries = new HashMap<>();
		for (Capitalization capitalization : Capitalization.values()) {
			tries.put(capitalization, new ReverseTrie(tagset.size(), maxSuffixLength));
		}
	}
	
	@Override
	public void incrementCounts(String word, String tag, int frequency) {
		int tagIndex = tagIndexMapping.getIndex(tag);
		Capitalization capitalization = Utils.extractCapitalization(word);
		tries.get(capitalization).insert(word, tagIndex, frequency);		
	}

	@Override
	public List<Integer> collectSuffixTagCounts(String word, String tag) {
		int tagIndex = tagIndexMapping.getIndex(tag);
		Capitalization capitalization = Utils.extractCapitalization(word);
		return tries.get(capitalization).collectSuffixTagFrequencies(word, tagIndex);
	}
	
	@Override
	public Set<String> collectPossibleStates(Capitalization capitalization) {
		Set<String> possibleStates = new HashSet<>();
		int[] suffixTagCountsRoot = tries.get(capitalization).getPossibleStates();
		for (int i = 0; i < suffixTagCountsRoot.length; i++) {
			if (suffixTagCountsRoot[i] > 0) {
				possibleStates.add(tagIndexMapping.getTag(i));
			}
		}
		return possibleStates;
	}
}
