package kldtz.github.com.hmmt.tagger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kldtz.github.com.hmmt.container.Sentence;
import kldtz.github.com.hmmt.corpus.CorpusFormat;
import kldtz.github.com.hmmt.corpus.TestCorpusReader;
import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.counts.CountsBuilder;
import kldtz.github.com.hmmt.counts.SuffixCountsType;
import kldtz.github.com.hmmt.evaluation.ConfusionMatrix;
import kldtz.github.com.hmmt.utils.Utils;

public class HmmTaggerRegressionTest {
	private static final double PRECISION = 0.0001;
	private static final String TEST_CORPUS = HmmTaggerRegressionTest.class.getResource("/private/pos.test.txt").getPath();
	private static final CorpusFormat TEST_CORPUS_FORMAT = CorpusFormat.CONLL;
	private static CountsBuilder countsBuilder;

	private ConfusionMatrix confusionMatrix;

	@BeforeClass
	public static void setUpTaggerBuilder() {
		String corpusPath = HmmTaggerRegressionTest.class.getResource("/private/pos.train.txt").getPath();
		CorpusFormat corpusFormat = CorpusFormat.CONLL;
		countsBuilder = new CountsBuilder(corpusPath, corpusFormat);
	}

	@Before
	public void initializeConfusionMatrix() {
		confusionMatrix = new ConfusionMatrix(Utils.readTagset());
	}

	@Test
	public void testTrigramTaggerWithHashedSuffixCountsOfLengthFourFromWordsWithFrequencyLessThanTen() {
		Counts counts = countsBuilder.suffixCounts(SuffixCountsType.HASH).maxNgramSize(3).maxSuffixLength(4)
				.maxWordFrequencyForSuffixCounts(10).build();
		Tagger tagger = new TaggerBuilder(counts).build();

		collectTrainingCorpusStats(tagger);

		assertThat(confusionMatrix.accuracy(), closeTo(0.97186, PRECISION));
		assertThat(confusionMatrix.macroAveragedPrecision(), closeTo(0.94977, PRECISION));
		assertThat(confusionMatrix.macroAveragedRecall(), closeTo(0.93964, PRECISION));
	}
	
	@Test
	public void testTrigramTaggerWithTrieSuffixCountsOfLengthFourFromWordsWithFrequencyLessThanTen() {
		Counts counts = countsBuilder.suffixCounts(SuffixCountsType.TRIE).maxNgramSize(3).maxSuffixLength(4)
				.maxWordFrequencyForSuffixCounts(10).build();
		Tagger tagger = new TaggerBuilder(counts).build();

		collectTrainingCorpusStats(tagger);

		assertThat(confusionMatrix.accuracy(), closeTo(0.97186, PRECISION));
		assertThat(confusionMatrix.macroAveragedPrecision(), closeTo(0.94977, PRECISION));
		assertThat(confusionMatrix.macroAveragedRecall(), closeTo(0.93964, PRECISION));
	}
	
	@Test
	public void testBigramTaggerWithHashedSuffixCountsOfLengthFourFromWordsWithFrequencyLessThanTen() {
		Counts counts = countsBuilder.suffixCounts(SuffixCountsType.HASH).maxNgramSize(2).maxSuffixLength(4)
				.maxWordFrequencyForSuffixCounts(10).build();
		Tagger tagger = new TaggerBuilder(counts).build();
		
		collectTrainingCorpusStats(tagger);
		
		assertThat(confusionMatrix.accuracy(), closeTo(0.96952, PRECISION));
		assertThat(confusionMatrix.macroAveragedPrecision(), closeTo(0.94605, PRECISION));
		assertThat(confusionMatrix.macroAveragedRecall(), closeTo(0.94398, PRECISION));
	}
	
	@Test
	public void testBigramTaggerWithTrieSuffixCountsOfLengthFourFromWordsWithFrequencyLessThanTen() {
		Counts counts = countsBuilder.suffixCounts(SuffixCountsType.TRIE).maxNgramSize(2).maxSuffixLength(4)
				.maxWordFrequencyForSuffixCounts(10).build();
		Tagger tagger = new TaggerBuilder(counts).build();
		
		collectTrainingCorpusStats(tagger);
		
		assertThat(confusionMatrix.accuracy(), closeTo(0.96952, PRECISION));
		assertThat(confusionMatrix.macroAveragedPrecision(), closeTo(0.94605, PRECISION));
		assertThat(confusionMatrix.macroAveragedRecall(), closeTo(0.94398, PRECISION));
	}

	private void collectTrainingCorpusStats(Tagger tagger) {
		TestCorpusReader corpusReader = TEST_CORPUS_FORMAT.createTestCorpusReader(new File(TEST_CORPUS));
		for (Sentence sentence : corpusReader) {
			List<String> actualTags = tagger.tag(sentence.words());
			collectSentenceStats(sentence.tags(), actualTags);
		}
		corpusReader.close();
	}

	private void collectSentenceStats(List<String> expectedTags, List<String> actualTags) {
		for (int j = 0; j < expectedTags.size(); j++) {
			String expectedTag = expectedTags.get(j);
			String actualTag = actualTags.size() > j ? actualTags.get(j) : Utils.ERROR_TAG;
			confusionMatrix.increment(expectedTag, actualTag);
		}
	}
}
