package kldtz.github.com.hmmt.counts;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import kldtz.github.com.hmmt.corpus.CorpusFormat;
import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.counts.CountsBuilder;
import kldtz.github.com.hmmt.counts.SuffixCountsType;
import kldtz.github.com.hmmt.tagger.Tagger;
import kldtz.github.com.hmmt.tagger.TaggerBuilder;

public class CountsSerializationTest {
	private static CountsBuilder countsBuilder;
	
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();
	private String countsSerPath;

	@BeforeClass
	public static void buildCounts() {
		String corpusPath = CountsSerializationTest.class.getResource("/private/pos.train.txt").getPath();
		CorpusFormat corpusFormat = CorpusFormat.CONLL;
		countsBuilder = new CountsBuilder(corpusPath, corpusFormat);
	}
	
	@Before
	public void createTemporaryFile() throws IOException {
		countsSerPath = tmpFolder.newFile("counts.ser").getAbsolutePath();
	}

	@Test
	public void testHashedSuffixCountsSerialization() throws IOException {
		Counts counts = countsBuilder.build();
		testSerialization(counts);
	}

	private void testSerialization(Counts counts) throws IOException {
		counts.serialize(countsSerPath);
		Counts deserializedCounts = Counts.deserialize(countsSerPath);
		Tagger tagger = new TaggerBuilder(deserializedCounts).build();
		List<String> actualTags = tagger.tag(Arrays.asList("This is a test sentence .".split(" ")));
		List<String> expectedTags = Arrays.asList("DT VBZ DT NN NN .".split(" "));
		assertEquals(expectedTags, actualTags);
	}
	
	@Test
	public void testTrieSuffixCountsSerialization() throws IOException {
		Counts counts = countsBuilder.suffixCounts(SuffixCountsType.TRIE).build();
		testSerialization(counts);
	}
}
