package kldtz.github.com.hmmt.tagger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kldtz.github.com.hmmt.container.TagNgram;
import kldtz.github.com.hmmt.corpus.Capitalization;
import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.utils.Utils;

public class States {
	private Set<String> states;
	private String startState;
	private String endState;

	private Map<String, Set<String>> possibleStatesWord;
	private Map<Capitalization, Set<String>> possibleStateCapitalization;

	States(Counts counts) {
		states = new HashSet<>();
		for (TagNgram tagUnigram : counts.getNgramSet(1)) {
			states.add(tagUnigram.getFirst());
		}
		startState = counts.getStartTag();
		endState = counts.getEndTag();
		collectPossibleStates(counts);
	}

	private void collectPossibleStates(Counts counts) {
		possibleStatesWord = counts.collectPossibleStates();
		possibleStateCapitalization = new HashMap<>();
		for (Capitalization c : Capitalization.values()) {
			possibleStateCapitalization.put(c, counts.collectPossibleStates(c));
		}
	}

	public Set<String> getStates() {
		return states;
	}

	public String getStartState() {
		return startState;
	}

	public String getEndState() {
		return endState;
	}

	public Set<String> getPossibleStates(String observation) {
		Set<String> candidateStates = possibleStatesWord.get(observation);
		if (candidateStates == null || candidateStates.isEmpty()) {
			candidateStates = possibleStateCapitalization.get(Utils.extractCapitalization(observation));
		}
		if (candidateStates == null || candidateStates.isEmpty()) {
			return states;
		}
		return candidateStates;
	}
}
