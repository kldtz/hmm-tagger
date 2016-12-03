package kldtz.github.com.hmmt.counts;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kldtz.github.com.hmmt.container.TagNgram;
import kldtz.github.com.hmmt.container.WordTagTuple;

public interface WordCounts {
	void incrementCounts(String word, String tag);

	void incrementCount(String... tags);

	int getMaxNgramSize();
	
	int getWordCount(String word);
	
	int getWordTagCount(WordTagTuple wordTag);
	
	int getTagNgramCount(TagNgram tagNgram);
	
	long getNumberOfTokens();
	
	Set<TagNgram> getNgramSet(int n);
	
	Map<String, Set<String>> collectPossibleStates();
	
	default String getStartTag() {
		return "<S>";
	}
	
	default String getEndTag() {
		return "</S>";
	}
	
	default Set<String> collectTagset() {
		Set<String> tagset = new HashSet<>();
		for (TagNgram tagUnigram : getNgramSet(1)) {
			tagset.add(tagUnigram.getFirst());
		}
		return tagset;
	}

	Set<String> getLexikon();

	Set<WordTagTuple> getWordTagTuples();
}
