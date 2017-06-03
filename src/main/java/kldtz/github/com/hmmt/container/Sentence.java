package kldtz.github.com.hmmt.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Sentence implements Iterable<WordTagTuple> {
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

	@Override
	public Iterator<WordTagTuple> iterator() {
		return new SentenceIterator();
	}
	
	private class SentenceIterator implements Iterator<WordTagTuple> {
		int currentIndex;
		
		public SentenceIterator() {
			currentIndex = 0;
		}

		@Override
		public boolean hasNext() {
			return currentIndex < words.size();
		}

		@Override
		public WordTagTuple next() {
			if (!hasNext()) {
				throw new NoSuchElementException(getClass().getName() + " has no further element");
			}
			WordTagTuple tuple = new WordTagTuple(words.get(currentIndex), tags.get(currentIndex));
			currentIndex++;
			return tuple;
		}
	}

	@Override
	public String toString() {
		return "Sentence [words=" + words + ", tags=" + tags + "]";
	}
}
