package kldtz.github.com.hmmt.counts;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kldtz.github.com.hmmt.container.TagNgram;
import kldtz.github.com.hmmt.container.WordTagTuple;
import kldtz.github.com.hmmt.corpus.Capitalization;

public class Counts implements Serializable {
	private static final long serialVersionUID = 1L;

	private WordCounts wordCounts;
	private SuffixCounts suffixCounts;

	Counts(WordCounts wordCounts, SuffixCounts suffixCounts) {
		this.wordCounts = wordCounts;
		this.suffixCounts = suffixCounts;
	}

	public Set<String> collectTagset() {
		return wordCounts.collectTagset();
	}

	public int getMaxNgramSize() {
		return wordCounts.getMaxNgramSize();
	}

	public int getWordCount(String word) {
		return wordCounts.getWordCount(word);
	}

	public int getWordTagCount(WordTagTuple wordTag) {
		return wordCounts.getWordTagCount(wordTag);
	}

	public int getTagNgramCount(TagNgram tagNgram) {
		return wordCounts.getTagNgramCount(tagNgram);
	}

	public long getNumberOfTokens() {
		return wordCounts.getNumberOfTokens();
	}

	public Set<TagNgram> getNgramSet(int n) {
		return wordCounts.getNgramSet(n);
	}

	public Map<String, Set<String>> collectPossibleStates() {
		return wordCounts.collectPossibleStates();
	}

	public String getStartTag() {
		return wordCounts.getStartTag();
	}

	public String getEndTag() {
		return wordCounts.getEndTag();
	}

	public List<Integer> collectSuffixTagCounts(String word, String tag) {
		return suffixCounts.collectSuffixTagCounts(word, tag);
	}

	public Set<String> collectPossibleStates(Capitalization capitalization) {
		return suffixCounts.collectPossibleStates(capitalization);
	}

	public void serialize(String path) throws IOException {
		try (FileOutputStream fileOut = new FileOutputStream(path);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);) {
			out.writeObject(this);
		}
	}

	public static Counts deserialize(String path) throws IOException {
		try (FileInputStream fileIn = new FileInputStream(path);
				ObjectInputStream in = new ObjectInputStream(fileIn);) {
			return (Counts) in.readObject();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
