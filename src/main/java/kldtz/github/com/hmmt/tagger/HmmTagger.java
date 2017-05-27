package kldtz.github.com.hmmt.tagger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import kldtz.github.com.hmmt.probabilities.EmissionProbabilities;
import kldtz.github.com.hmmt.probabilities.TransitionProbabilities;

public class HmmTagger implements Tagger {
	private TransitionProbabilities transition;
	private EmissionProbabilities emission;
	private States states;
	private int maxNgramSize;

	HmmTagger(States states, TransitionProbabilities transitionProbabilities,
			EmissionProbabilities emissionProbabilities, int maxNgramSize) {
		this.states = states;
		this.transition = transitionProbabilities;
		this.emission = emissionProbabilities;
		this.maxNgramSize = maxNgramSize;
	}

	public List<String> tag(List<String> tokens) {
		List<ViterbiCell> previousViterbiCells = initialize();
		previousViterbiCells = recurse(tokens, previousViterbiCells);
		Optional<ViterbiCell> finalCell = terminate(previousViterbiCells);
		return backtrace(finalCell);
	}

	private List<ViterbiCell> initialize() {
		List<ViterbiCell> previousViterbiCells = new ArrayList<>();
		ViterbiCell previousCell = null;
		for (int i = 0; i < maxNgramSize - 1; i++) {
			ViterbiCell currentCell = new ViterbiCell(0, states.getStartState(), previousCell);
			previousCell = currentCell;
		}
		previousViterbiCells.add(previousCell);
		return previousViterbiCells;
	}

	private  List<ViterbiCell> recurse(List<String> observations, List<ViterbiCell> previousViterbiCells) {
		List<ViterbiCell> currentViterbiCells = new ArrayList<>();
		for (String observation : observations) {
			for (String state : states.getPossibleStates(observation)) {
				ViterbiCell currentCell = computeViterbiCell(observation, state, previousViterbiCells);
				if (currentCell != null) {
					currentViterbiCells.add(currentCell);
				}
			}
			previousViterbiCells = currentViterbiCells;
			currentViterbiCells = new ArrayList<>();
		}
		return previousViterbiCells;
	}

	private ViterbiCell computeViterbiCell(String observation, String state, List<ViterbiCell> previousViterbiCells) {
		double emissionProbability = emission.getLogProbability(observation, state);
		if (!Double.isFinite(emissionProbability)) {
			return null;
		}
		ViterbiCell maxCell = computeMaxCell(state, emissionProbability, previousViterbiCells);
		if (!Double.isFinite(maxCell.getValue())) {
			return null;
		}
		return maxCell;
	}

	private ViterbiCell computeMaxCell(String state, double emissionProbability,  List<ViterbiCell> previousViterbiCells) {
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

	private Optional<ViterbiCell> terminate(List<ViterbiCell> previousViterbiCells) {
		ViterbiCell finalCell = computeMaxCell(states.getEndState(), 0, previousViterbiCells);
		if (Double.isFinite(finalCell.getValue())) {
			return Optional.of(finalCell);
		}
		return Optional.empty();
	}

	private List<String> backtrace(Optional<ViterbiCell> finalCell) {
		LinkedList<String> tags = new LinkedList<>();
		if (!finalCell.isPresent()) {
			return tags;
		}
		ViterbiCell currentCell = finalCell.get().getPreviousCell();
		do {
			tags.add(0, currentCell.getState());
			currentCell = currentCell.getPreviousCell();
		} while (!currentCell.getState().equals(states.getStartState()));
		return tags;
	}
}
