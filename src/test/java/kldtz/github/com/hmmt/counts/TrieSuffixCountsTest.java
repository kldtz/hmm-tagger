package kldtz.github.com.hmmt.counts;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import kldtz.github.com.hmmt.counts.SuffixCounts;
import kldtz.github.com.hmmt.counts.TrieSuffixCounts;

public class TrieSuffixCountsTest {
private static final int MAX_SUFFIX_LENGTH = 5;
	
	private static SuffixCounts suffixCounts;
	
	@BeforeClass
	public static void initTrie() {
		Set<String> tagset = new HashSet<>();
		tagset.add("N");
		tagset.add("V");
		tagset.add("A");
		tagset.add("Punc");
		suffixCounts = new TrieSuffixCounts(tagset, MAX_SUFFIX_LENGTH);
		
		suffixCounts.incrementCounts("kleben", "V");
		suffixCounts.incrementCounts("schönen", "A");
		suffixCounts.incrementCounts("Regen", "N");
		suffixCounts.incrementCounts("Bär", "N");
		suffixCounts.incrementCounts(".", "Punc");
	}
	
	@Test
	public void testSuffixCountsForVerb() {				
		List<Integer> expectedFrequencies = new ArrayList<>(Arrays.asList(1, 1, 1, 0, 0, 0));
		List<Integer> actualFrequencies = suffixCounts.collectSuffixTagCounts("laufen", "V");
		
		testTrie(expectedFrequencies, actualFrequencies);
	}
	
	@Test
	public void testSuffixCountsForNoun() {
		List<Integer> expectedFrequencies = new ArrayList<>(Arrays.asList(2, 1, 1, 1, 0, 0));
		List<Integer> actualFrequencies = suffixCounts.collectSuffixTagCounts("Magen", "N");
		
		testTrie(expectedFrequencies, actualFrequencies);
	}
	
	@Test
	public void testCountsForShortUnkownNoun() {
		List<Integer> expectedFrequencies = new ArrayList<>(Arrays.asList(2, 0, 0, 0, 0, 0));
		List<Integer> actualFrequencies = suffixCounts.collectSuffixTagCounts("Xxx", "N");
		
		testTrie(expectedFrequencies, actualFrequencies);
	}
	
	@Test
	public void testCountsForShortKnownWord() {
		List<Integer> expectedFrequencies = new ArrayList<>(Arrays.asList(2, 1, 1, 1, 1, 1));
		List<Integer> actualFrequencies = suffixCounts.collectSuffixTagCounts("Bär", "N");
		
		testTrie(expectedFrequencies, actualFrequencies);
	}
	
	@Test
	public void testCountsForPunctuation() {
		List<Integer> expectedFrequencies = new ArrayList<>(Arrays.asList(1, 0, 0, 0, 0, 0));
		List<Integer> actualFrequencies = suffixCounts.collectSuffixTagCounts("?", "Punc");
		
		testTrie(expectedFrequencies, actualFrequencies);
	}
	
	private void testTrie(List<Integer> expected, List<Integer> actual) {
		assertEquals(6, actual.size());
		assertEquals(expected, actual);
	}
}
