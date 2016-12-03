package kldtz.github.com.hmmt.counts;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import kldtz.github.com.hmmt.counts.ReverseTrie;

public class ReverseTrieTest {
	
	private static final int MAX_SUFFIX_LENGTH = 5;
	
	private static ReverseTrie trie;
	
	@BeforeClass
	public static void initTrie() {
		trie = new ReverseTrie(3, MAX_SUFFIX_LENGTH);
		
		trie.insert("kleben", 0);
		trie.insert("schönen", 1);
		trie.insert("Regen", 2);
		trie.insert("Bär", 2);
	}
	
	@Test
	public void trieShouldHaveCorrectNumberOfNodes() {
		assertEquals(15, trie.getNumberOfNodes());
	}
	
	@Test
	public void trieShouldReturnCorrectSuffixCountsForVerb() {				
		List<Integer> expectedFrequencies = new ArrayList<>(Arrays.asList(1, 1, 1, 0, 0, 0));
		List<Integer> actualFrequencies = trie.collectSuffixTagFrequencies("laufen", 0);
		
		testTrie(expectedFrequencies, actualFrequencies);
	}
	
	@Test
	public void trieShouldReturnCorrectSuffixCountsForNoun() {
		List<Integer> expectedFrequencies = new ArrayList<>(Arrays.asList(2, 1, 1, 1, 0, 0));
		List<Integer> actualFrequencies = trie.collectSuffixTagFrequencies("Magen", 2);
		
		testTrie(expectedFrequencies, actualFrequencies);
	}
	
	@Test
	public void trieShouldReturnCorrectCountsForShortUnkownNoun() {
		List<Integer> expectedFrequencies = new ArrayList<>(Arrays.asList(2, 0, 0, 0, 0, 0));
		List<Integer> actualFrequencies = trie.collectSuffixTagFrequencies("Xxx", 2);
		
		testTrie(expectedFrequencies, actualFrequencies);
	}
	
	@Test
	public void trieShouldReturnCorrectCountsForShortKnownWord() {
		List<Integer> expectedFrequencies = new ArrayList<>(Arrays.asList(2, 1, 1, 1, 1, 1));
		List<Integer> actualFrequencies = trie.collectSuffixTagFrequencies("Bär", 2);
		
		testTrie(expectedFrequencies, actualFrequencies);
	}
	
	private void testTrie(List<Integer> expected, List<Integer> actual) {
		assertEquals(6, actual.size());
		assertEquals(expected, actual);
	}
	

}
