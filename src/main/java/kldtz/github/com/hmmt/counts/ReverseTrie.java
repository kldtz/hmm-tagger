package kldtz.github.com.hmmt.counts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.hash.TCharObjectHashMap;

public class ReverseTrie implements Serializable {
	private static final long serialVersionUID = 1L;
	private int numberOfTags;
	private int maxSuffixLength;
	private ReverseTrieNode root;
	private int numberOfNodes;

	public ReverseTrie(int numberOfTags, int maxSuffixLength) {
		this.numberOfTags = numberOfTags;
		this.maxSuffixLength = maxSuffixLength;
		root = createNodeInstance('$');
		numberOfNodes = 1;
	}
	
	public void insert(String word, int tagIndex) {
		insert(word, tagIndex, 1);
	}
	
	public void insert(String word, int tagIndex, int frequency) {
		ReverseTrieNode node = root;
		node.incrementCounts(tagIndex, frequency);
		int lastIndex = Math.max(word.length() - maxSuffixLength, 0);
		for (int i = word.length() - 1; i >= lastIndex; i--) {
			char c = word.charAt(i);
			if (!node.hasChild(c)) {
				node.addChild(createNodeInstance(c));
			}
			node = node.getChild(word.charAt(i));
			node.incrementCounts(tagIndex, frequency);			
		}
	}
	
	public List<Integer> collectSuffixTagFrequencies(String word, int tagIndex) {
		List<Integer> suffixTagFreqs = new ArrayList<>(maxSuffixLength + 1);
		ReverseTrieNode node = root;
		suffixTagFreqs.add(node.getSuffixTagCount(tagIndex));
		int lastSuffixTagCount = 0;
		int index;
		for (index = word.length() - 1; index >= 0; index--) {
			node = node.getChild(word.charAt(index));
			if (node == null) {
				break;
			}			
			lastSuffixTagCount = node.getSuffixTagCount(tagIndex);
			suffixTagFreqs.add(lastSuffixTagCount);
		}
		fillRemainingCounts(word, suffixTagFreqs, lastSuffixTagCount);
		return suffixTagFreqs;
	}

	private void fillRemainingCounts(String word, List<Integer> suffixTagFreqs, int lastSuffixTagCount) {
		if (word.length() + 1 == suffixTagFreqs.size()) {
			while (suffixTagFreqs.size() < maxSuffixLength + 1) {
				suffixTagFreqs.add(lastSuffixTagCount);
			}	
		} else {
			while (suffixTagFreqs.size() < maxSuffixLength + 1) {
				suffixTagFreqs.add(0);
			}
		}
	}

	private ReverseTrieNode createNodeInstance(char c) {
		return new ReverseTrieNode(c, numberOfTags);
	}
	
	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	private class ReverseTrieNode implements Serializable {
		private static final long serialVersionUID = 1L;
		private char character;
		private int count;
		private int[] suffixTagCounts;
		private TCharObjectMap<ReverseTrieNode> children;

		private ReverseTrieNode(char character, int numberOfTags) {
			this.character = character;
			count = 0;
			suffixTagCounts = new int[numberOfTags];
			children = new TCharObjectHashMap<>();
		}

		private char getCharacter() {
			return character;
		}

		private int getSuffixCount() {
			return count;
		}

		private int getSuffixTagCount(int tagIndex) {
			return suffixTagCounts[tagIndex];
		}

		private ReverseTrieNode getChild(char c) {
			return children.get(c);
		}

		private boolean hasChild(char c) {
			return children.containsKey(c);
		}

		private void addChild(ReverseTrieNode node) {
			children.put(node.getCharacter(), node);
			numberOfNodes++;
		}

		private void incrementCounts(int tagIndex) {
			incrementCounts(tagIndex, 1);
		}
		
		private void incrementCounts(int tagIndex, int frequency) {
			count += frequency;
			suffixTagCounts[tagIndex] += frequency;
		}
		
		private int[] getSuffixTagCounts() {
			return suffixTagCounts;
		}
	}

	public int[] getPossibleStates() {
		return root.getSuffixTagCounts();		
	}
}
