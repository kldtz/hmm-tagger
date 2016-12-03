package kldtz.github.com.hmmt.tagger;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import kldtz.github.com.hmmt.corpus.CorpusFormat;
import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.counts.CountsBuilder;
import kldtz.github.com.hmmt.counts.CountsSerializationTest;
import kldtz.github.com.hmmt.counts.SuffixCountsType;

public class PlainHmmTaggerTest {
	
	private static Tagger tagger;
	
	@BeforeClass
	public static void loadTagger() {
		String corpusPath = CountsSerializationTest.class.getResource("/private/pos.train.txt").getPath();
		CorpusFormat corpusFormat = CorpusFormat.CONLL;
		Counts counts = new CountsBuilder(corpusPath, corpusFormat).suffixCounts(SuffixCountsType.TRIE).build();
		tagger = new TaggerBuilder(counts).build();
	}
	
	@Test 
	public void tagSentenceWithKnownWordsIncludingPersonalPronounsCorrectly() {
		testTagger("It feels like we always go backwards .",
				 "PRP VBZ IN PRP RB VBP RB .");
	}
	
	@Test
	public void tagSentenceWithKnownWordsIncludingPastTenseVerbCorrectly() {
		testTagger("A man walked along the street .",
				 "DT NN VBD IN DT NN .");
	}
	
	@Test
	public void tagSentenceWithUnknownVerbCorrectly() {
		testTagger("The man wxyzed along the street .", 
				"DT NN VBN IN DT NN .");
	}
	
	private void testTagger(String input, String expectedOutput) {
		List<String> sentence = Arrays.asList(input.split(" "));
		List<String> expectedTags = Arrays.asList(expectedOutput.split(" "));
		testTagger(sentence, expectedTags);
	}
	
	private void testTagger(List<String> input, List<String> expectedTags) {
		List<String> actualTags = tagger.tag(input);

		int expectedSize = expectedTags.size();
		int actualSize = actualTags.size();
		assertEquals("Number of tags", expectedSize, actualSize);
		
		assertEquals(expectedTags, actualTags);
	}
}
