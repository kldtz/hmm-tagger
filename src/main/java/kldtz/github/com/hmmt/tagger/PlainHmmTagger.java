package kldtz.github.com.hmmt.tagger;

import java.util.LinkedList;
import java.util.List;

import kldtz.github.com.hmmt.probabilities.EmissionProbabilities;
import kldtz.github.com.hmmt.probabilities.TransitionProbabilities;

public class PlainHmmTagger implements Tagger {

	private double[][] viterbi;
	private int[][] backpointer;
	private double terminalValue;
	private int terminalBackpointer;

	private IntegerStates states;
	private TransitionProbabilities transition;
	private EmissionProbabilities emission;
	private List<String> observations;
	
	private int maxNgramSize;

	PlainHmmTagger(IntegerStates states, TransitionProbabilities transitionProbabilities,
			EmissionProbabilities emissionProbabilities, int maxNgramSize) {
		this.states = states;
		this.transition = transitionProbabilities;
		this.emission = emissionProbabilities;
		this.maxNgramSize = maxNgramSize;
	}

	public List<String> tag(List<String> tokens) {
		this.observations = tokens;
		return viterbi();
	}

	private List<String> viterbi() {
		initialize();
		recurse();
		terminate();
		return backtrace();
	}

	private void initialize() {
		viterbi = new double[states.getNumberOfStates()][observations.size()];
		backpointer = new int[states.getNumberOfStates()][observations.size()];
		for (int s = 0; s < states.getNumberOfStates(); s++) {
			String[] transitionTags = prepareTransitionTags(states.getStartStateIndex(), s, 0);
			viterbi[s][0] = transition.getLogProbability(transitionTags)
					+ emission.getLogProbability(observations.get(0), states.getState(s));
			backpointer[s][0] = states.getStartStateIndex();
		}
	}
	
	private String[] prepareTransitionTags(int ps, int s, int t) {
		String[] transitionTags = new String[maxNgramSize];
		transitionTags[maxNgramSize - 1] = states.getState(s);
		transitionTags[maxNgramSize - 2] = states.getState(ps);
		int currentState = ps;
		int currentTime = t - 1;
		for (int i = maxNgramSize - 3; i >= 0; i--) {
			if (currentTime < 0) {
				transitionTags[i] = states.getStartState();
				continue;
			}
			currentState = backpointer[currentState][currentTime];
			currentTime--;
			transitionTags[i] = states.getState(currentState);
		}
		return transitionTags;
	}

	private void recurse() {
		for (int t = 1; t < observations.size(); t++) {
			for (int s = 0; s < states.getNumberOfStates(); s++) {
				computeCell(s, t);
			}
		}
	}

	private void computeCell(int s, int t) {
		double emissionLogProbability = emission.getLogProbability(observations.get(t), states.getState(s));
		if (!Double.isFinite(emissionLogProbability)) {
			viterbi[s][t] = Double.NEGATIVE_INFINITY;
			backpointer[s][t] = 0;
			return;
		}
		double maxValue = computeValue(0, s, t, emissionLogProbability);
		int maxPs = 0;
		for (int ps = 1; ps < states.getNumberOfStates(); ps++) {
			double value = computeValue(ps, s, t, emissionLogProbability);
			if (value > maxValue) {
				maxValue = value;
				maxPs = ps;
			}
		}
		viterbi[s][t] = maxValue;
		backpointer[s][t] = maxPs;
	}

	private double computeValue(int ps, int s, int t, double emissionLogProbability) {
		double lastViterbiValue = viterbi[ps][t - 1];
		if (!Double.isFinite(lastViterbiValue)) {
			return Double.NEGATIVE_INFINITY;
		}
		String[] transitionTags = prepareTransitionTags(ps, s, t);
		return lastViterbiValue + transition.getLogProbability(transitionTags) + emissionLogProbability;
	}
	
	private void terminate() {
		double maxValue = computeTerminalValue(0);
		int maxPs = 0;
		for (int ps = 1; ps < states.getNumberOfStates(); ps++) {
			double value = computeTerminalValue(ps);
			if (value > maxValue) {
				maxValue = value;
				maxPs = ps;
			}
		}
		terminalValue = maxValue;
		terminalBackpointer = maxPs;
	}

	private double computeTerminalValue(int ps) {
		int lastObs = observations.size() - 1;
		String[] transitionTags = prepareTransitionTags(ps, states.getEndStateIndex(), lastObs);
		return viterbi[ps][lastObs] + transition.getLogProbability(transitionTags);
	}

	private List<String> backtrace() {
		LinkedList<String> tags = new LinkedList<>();
		if (!hasTerminatedSuccessfully()) {
			return tags;
		}
		int lastState = terminalBackpointer;
		for (int time = observations.size() - 1; time >= 0; time--) {
			tags.addFirst(states.getState(lastState));
			lastState = backpointer[lastState][time];
		}
		return tags;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int state = 0; state < states.getNumberOfStates(); state++) {
			for (int time = 0; time < observations.size(); time++) {
				if (Double.isFinite(viterbi[state][time])) {
					sb.append(states.getState(state) + ": " + arrayToStringWithIndices(viterbi[state]));
					sb.append("\n");
					break;
				}
			}
		}
		sb.append("Terminal Value: " + terminalValue);
		return sb.toString();
	}

	private String arrayToStringWithIndices(double[] array) {
		StringBuilder sb = new StringBuilder();
		if (array.length > 0) {
			sb.append("0:").append(array[0]);
		}
		for (int i = 1; i < array.length; i++) {
			sb.append(", ");
			sb.append(i).append(":");
			sb.append(array[i]);
		}
		return sb.toString();
	}

	private boolean hasTerminatedSuccessfully() {
		return Double.isFinite(terminalValue);
	}

}
