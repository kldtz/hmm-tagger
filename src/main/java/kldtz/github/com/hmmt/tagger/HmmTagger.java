package kldtz.github.com.hmmt.tagger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kldtz.github.com.hmmt.probabilities.EmissionProbabilities;
import kldtz.github.com.hmmt.probabilities.TransitionProbabilities;

public class HmmTagger implements Tagger {
	private TransitionProbabilities transition;
	private EmissionProbabilities emission;
	private States states;
	private int maxNgramSize;

	private List<ViterbiCell> previousViterbiCells;
	private List<ViterbiCell> currentViterbiCells;
	
	HmmTagger(States states, TransitionProbabilities transitionProbabilities,
			EmissionProbabilities emissionProbabilities, int maxNgramSize) {
		this.states = states;
		this.transition = transitionProbabilities;
		this.emission = emissionProbabilities;
		this.maxNgramSize = maxNgramSize;
	}

	public List<String> tag(List<String> tokens) {
		initialize();
		recurse(tokens);
		terminate();
		return backtrace();
	}

	private void initialize() {
		previousViterbiCells = new ArrayList<>();
		ViterbiCell previousCell = null;
		for (int i = 0; i < maxNgramSize - 1; i++) {
			ViterbiCell currentCell = new ViterbiCell(0, states.getStartState(), previousCell);
			previousCell = currentCell;
		}
		previousViterbiCells.add(previousCell);
		currentViterbiCells = new ArrayList<>();
	}

	private void recurse(List<String> observations) {
		for (String observation : observations) {
			for (String state : states.getPossibleStates(observation)) {
				ViterbiCell currentCell = computeViterbiCell(observation, state);
				if (currentCell != null) {
					currentViterbiCells.add(currentCell);
				}
			}
			previousViterbiCells = currentViterbiCells;
			currentViterbiCells = new ArrayList<>();
		}
	}

	private ViterbiCell computeViterbiCell(String observation, String state) {
		double emissionProbability = emission.getLogProbability(observation, state);
		if (!Double.isFinite(emissionProbability)) {
			return null;
		}
		ViterbiCell maxCell = computeMaxCell(state, emissionProbability);
		if (!Double.isFinite(maxCell.getValue())) {
			return null;
		}
		return maxCell;
	}

	private ViterbiCell computeMaxCell(String state, double emissionProbability) {
		ViterbiCell maxCell = new ViterbiCell(Double.NEGATIVE_INFINITY, state, null);
		for (ViterbiCell previousCell : previousViterbiCells) {
			double value = computeViterbiValue(previousCell, state, emissionProbability);
			if (value > maxCell.getValue()) {
				maxCell.setValue(value);
				maxCell.setPreviousCell(previousCell);
			}
		}
		return maxCell;
	}

	private double computeViterbiValue(ViterbiCell previousCell, String state, double emissionProbability) {
		String[] ngram = new String[maxNgramSize];
		ngram[ngram.length - 1] = state;
		ViterbiCell currentCell = previousCell;
		for (int i = ngram.length - 2; i >= 0; i--) {
			ngram[i] = currentCell.getState();
			currentCell = currentCell.getPreviousCell();
		}
		return previousCell.getValue() + transition.getLogProbability(ngram) + emissionProbability;
	}

	private void terminate() {
		ViterbiCell finalCell = computeMaxCell(states.getEndState(), 0);
		if (Double.isFinite(finalCell.getValue())) {
			currentViterbiCells.add(finalCell);
		}
	}

	private List<String> backtrace() {
		LinkedList<String> tags = new LinkedList<>();
		if (!hasTerminatedSuccessfully()) {
			return tags;
		}
		ViterbiCell currentCell = currentViterbiCells.get(0).getPreviousCell();
		do {
			tags.add(0, currentCell.getState());
			currentCell = currentCell.getPreviousCell();
		} while (!currentCell.getState().equals(states.getStartState()));
		return tags;
	}
	
	private boolean hasTerminatedSuccessfully() {
		return !currentViterbiCells.isEmpty();
	}
}
