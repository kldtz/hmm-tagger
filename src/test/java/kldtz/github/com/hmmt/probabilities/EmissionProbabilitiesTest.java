package kldtz.github.com.hmmt.probabilities;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import kldtz.github.com.hmmt.corpus.CorpusFormat;
import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.counts.CountsBuilder;
import kldtz.github.com.hmmt.probabilities.InterpolatedEmissions;

public class EmissionProbabilitiesTest {
	private static final String START_TAG = "<S>";
	private static final String END_TAG = "</S>";

	private static InterpolatedEmissions emissionProbabilities;

	@BeforeClass
	public static void setupCounts() {
		String corpusPath = EmissionProbabilitiesTest.class.getResource("/private/brown_first_sentences.txt")
				.getPath();
		Counts counts = new CountsBuilder(corpusPath, CorpusFormat.BROWN).build();
		emissionProbabilities = new InterpolatedEmissions(counts);
	}

	@Test
	public void testUnsmoothedEmissionProbability() {
		assertThat(emissionProbabilities.computeUnsmoothedProbability("the", "AT"), is(0.8));
		assertThat(emissionProbabilities.computeUnsmoothedProbability(START_TAG, START_TAG), is(1.0));
		assertThat(emissionProbabilities.computeUnsmoothedProbability(END_TAG, END_TAG), is(1.0));
	}
}
