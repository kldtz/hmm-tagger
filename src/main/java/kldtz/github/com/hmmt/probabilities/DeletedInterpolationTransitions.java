package kldtz.github.com.hmmt.probabilities;

import java.util.Arrays;
import java.util.stream.DoubleStream;

import kldtz.github.com.hmmt.container.TagNgram;
import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.utils.Utils;

public class DeletedInterpolationTransitions implements TransitionProbabilities {
	private Counts counts;
	private double[] parameters;

	public DeletedInterpolationTransitions(Counts counts) {
		this.counts = counts;
		learnParametersViaDeletedInterpolation(counts.getMaxNgramSize());
	}

	DeletedInterpolationTransitions(Counts counts, double[] parameters) {
		this.counts = counts;
		this.parameters = parameters;
	}

	public double getLogProbability(String... tags) {
		return Math.log(getProbability(tags));
	}

	double getProbability(String... tags) {
		return computeInterpolatedTransitionProbability(tags);
	}

	private double computeInterpolatedTransitionProbability(String[] tags) {
		int lastIndex = tags.length - 1;
		TagNgram jointEvent = new TagNgram(tags);
		TagNgram condition = jointEvent.createCopy(0, lastIndex);
		jointEvent.resize(lastIndex);
		double interpolatedProbability = computeCondProb(counts.getTagNgramCount(jointEvent),
				counts.getNumberOfTokens()) * parameters[lastIndex];
		for (int i = lastIndex - 1; i >= 0; i--) {
			jointEvent.resize(i);
			condition.resize(i);
			double partialTransitionProbability = computeCondProb(counts.getTagNgramCount(jointEvent),
					counts.getTagNgramCount(condition));
			if (partialTransitionProbability == 0) {
				return interpolatedProbability;
			}
			interpolatedProbability += partialTransitionProbability * parameters[i];
		}
		return interpolatedProbability;
	}

	private void learnParametersViaDeletedInterpolation(int maxNgramSize) {
		parameters = new double[maxNgramSize];
		for (TagNgram maxNgram : counts.getNgramSet(maxNgramSize)) {
			int trigramCount = counts.getTagNgramCount(maxNgram);
			double[] ngramValues = computeNgramValues(maxNgram);
			int indexOfLargest = Utils.findIndexOfLargestElement(ngramValues);
			parameters[indexOfLargest] += trigramCount;
		}
		normalizeParameters();
	}

	private double[] computeNgramValues(TagNgram ngram) {
		double[] ngramValues = new double[ngram.getN()];
		int lastIndex = ngram.getN() - 1;
		TagNgram jointEvent = ngram.createCopy();
		TagNgram condition = ngram.createCopy(0, lastIndex);
		jointEvent.resize(lastIndex);
		ngramValues[lastIndex] = computeCondProb(counts.getTagNgramCount(jointEvent) - 1,
				counts.getNumberOfTokens() - 1);
		for (int i = lastIndex - 1; i >= 0; i--) {
			jointEvent.resize(i);
			condition.resize(i);
			ngramValues[i] = computeCondProb(counts.getTagNgramCount(jointEvent) - 1,
					counts.getTagNgramCount(condition) - 1);
		}
		return ngramValues;
	}

	private void normalizeParameters() {
		double sum = DoubleStream.of(parameters).sum();
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = parameters[i] / sum;
		}
	}

	private double computeCondProb(long jointEventCount, long conditionCount) {
		if (conditionCount == 0) {
			return 0;
		}
		return (double) jointEventCount / conditionCount;
	}

	@Override
	public String toString() {
		return Arrays.toString(parameters);
	}
}
