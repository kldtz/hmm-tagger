package kldtz.github.com.hmmt.counts;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import kldtz.github.com.hmmt.container.TagNgram;
import kldtz.github.com.hmmt.container.WordTagTuple;
import kldtz.github.com.hmmt.corpus.CorpusFormat;
import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.counts.CountsBuilder;

public class CountsTest {

	private static Counts counts;

	@BeforeClass
	public static void setupCounts() {
		String corpusPath = CountsTest.class.getResource("/private/brown_first_sentences.txt").getPath();
		counts = new CountsBuilder(corpusPath, CorpusFormat.BROWN).build();
	}

	@Test
	public void countedCorrectNumberOfTags() {
		assertThat(counts.getTagNgramCount(new TagNgram("NN")), is(17));
		assertThat(counts.getTagNgramCount(new TagNgram("AT")), is(10));
		assertThat(counts.getTagNgramCount(new TagNgram("JJ")), is(4));
		assertThat(counts.getTagNgramCount(new TagNgram("XY")), is(0));
		assertThat(counts.getTagNgramCount(new TagNgram(counts.getStartTag())), is(4));
	}

	@Test
	public void countedCorrectNumberOfWords() {
		assertThat(counts.getWordCount("the"), is(8));
		assertThat(counts.getWordCount("investigation"), is(1));
	}

	@Test
	public void countedCorrectNumberOfWordTagTuples() {
		WordTagTuple wtt = new WordTagTuple("the", "AT");
		assertThat(counts.getWordTagCount(wtt), is(8));
	}

	@Test
	public void countedCorrectNumberOfTagBigrams() {
		TagNgram tagNgram = new TagNgram("AT", "NN");
		assertThat(counts.getTagNgramCount(tagNgram), is(9));
		assertThat(counts.getTagNgramCount(new TagNgram(counts.getEndTag())), is(2));
	}

}
