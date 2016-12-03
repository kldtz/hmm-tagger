package kldtz.github.com.hmmt.container;

import java.util.ArrayList;
import java.util.List;

public class Sentence {
	private List<String> words;
	private List<String> tags;
	
	public Sentence() {
		words = new ArrayList<>();
		tags = new ArrayList<>();
	}
	
	public Sentence(List<String> words, List<String> tags) {
		this.words = words;
		this.tags = tags;
	}

	public List<String> words() {
		return words;
	}

	public List<String> tags() {
		return tags;
	}
	
	public void addWordTagPair(String word, String tag) {
		words.add(word);
		tags.add(tag);
	}
	
	public boolean isEmpty() {
		return words.isEmpty();
	}
}
