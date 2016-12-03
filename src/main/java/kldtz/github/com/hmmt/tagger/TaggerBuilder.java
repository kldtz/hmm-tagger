package kldtz.github.com.hmmt.tagger;

import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.probabilities.DeletedInterpolationTransitions;
import kldtz.github.com.hmmt.probabilities.EmissionProbabilities;
import kldtz.github.com.hmmt.probabilities.InterpolatedEmissions;
import kldtz.github.com.hmmt.probabilities.TransitionProbabilities;

public class TaggerBuilder {
	private boolean unoptimized;
	private Counts counts;

	public TaggerBuilder(Counts counts) {
		this.counts = counts;
		unoptimized = false;
	}

	public TaggerBuilder unoptimized() {
		this.unoptimized = true;
		return this;
	}

	public Tagger build() {
		TransitionProbabilities transitions = new DeletedInterpolationTransitions(counts);
		EmissionProbabilities emissions = new InterpolatedEmissions(counts);
		if (unoptimized) {
			IntegerStates integerStates = new IntegerStates(counts);
			return new PlainHmmTagger(integerStates, transitions, emissions, counts.getMaxNgramSize());
		}
		States states = new States(counts);
		return new HmmTagger(states, transitions, emissions, counts.getMaxNgramSize());
	}
}
