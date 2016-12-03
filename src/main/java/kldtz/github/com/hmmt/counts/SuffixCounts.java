package kldtz.github.com.hmmt.counts;

import java.util.List;
import java.util.Set;

import kldtz.github.com.hmmt.corpus.Capitalization;

public interface SuffixCounts {
	public default void incrementCounts(String word, String tag) {
		incrementCounts(word, tag, 1);
	}

	public void incrementCounts(String word, String tag, int frequency);

	public List<Integer> collectSuffixTagCounts(String word, String tag);

	public Set<String> collectPossibleStates(Capitalization capitalization);
}
