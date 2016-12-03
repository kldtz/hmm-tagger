package kldtz.github.com.hmmt.probabilities;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.BeforeClass;
import org.junit.Test;

import kldtz.github.com.hmmt.container.TagNgram;
import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.probabilities.DeletedInterpolationTransitions;

public class TransitionProbabilitiesTest {

	private static Counts counts;

	@BeforeClass
	public static void mockCounts() {
		counts = mock(Counts.class);
		when(counts.getTagNgramCount(new TagNgram("N"))).thenReturn(12);
		when(counts.getTagNgramCount(new TagNgram("A"))).thenReturn(12);
		when(counts.getTagNgramCount(new TagNgram("A", "N"))).thenReturn(6);
		when(counts.getTagNgramCount(new TagNgram("D", "A"))).thenReturn(3);
		when(counts.getTagNgramCount(new TagNgram("D", "A", "N"))).thenReturn(3);
		when(counts.getNumberOfTokens()).thenReturn(100L);
	}

	@Test
	public void testInterpolatedTransitionProbability() {
		DeletedInterpolationTransitions probabilities = new DeletedInterpolationTransitions(counts, new double[] { 0.1, 0.2, 0.5 });
		String[] tags = new String[] { "D", "A", "N" };
		double interpolatedProb = probabilities.getProbability(tags);
		assertThat(interpolatedProb, is(0.26));
	}

	@Test
	public void testTrigramProbability() {
		DeletedInterpolationTransitions probabilities = new DeletedInterpolationTransitions(counts, new double[] { 1, 0, 0 });
		String[] tags = new String[] { "D", "A", "N" };
		double interpolatedProb = probabilities.getProbability(tags);
		assertThat(interpolatedProb, is(1.0));
	}
	
	@Test
	public void testBigramProbability() {
		DeletedInterpolationTransitions probabilities = new DeletedInterpolationTransitions(counts, new double[] { 0, 1, 0 });
		String[] tags = new String[] { "D", "A", "N" };
		double interpolatedProb = probabilities.getProbability(tags);
		assertThat(interpolatedProb, is(0.5));
	}

	@Test
	public void testUnigramProbability() {
		DeletedInterpolationTransitions probabilities = new DeletedInterpolationTransitions(counts, new double[] { 0, 0, 1 });
		String[] tags = new String[] { "D", "A", "N" };
		double interpolatedProb = probabilities.getProbability(tags);
		assertThat(interpolatedProb, is(0.12));
	}
}
