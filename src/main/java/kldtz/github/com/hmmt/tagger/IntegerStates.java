package kldtz.github.com.hmmt.tagger;

import java.util.ArrayList;
import java.util.List;

import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.utils.Utils;

public class IntegerStates {
	private List<String> states;
	private String startState;
	private String endState;

	IntegerStates(Counts counts) {
		states = new ArrayList<>(counts.collectTagset());
		startState = counts.getStartTag();
		endState = counts.getEndTag();
		int startIndex = states.indexOf(startState);
		int endIndex = states.indexOf(endState);
		Utils.swap(states, startIndex, states.size() - 2);
		Utils.swap(states, endIndex, states.size() - 1);
	}

	public String getState(int index) {
		return states.get(index);
	}

	/**
	 * Number of states excluding start and end state.
	 * 
	 * @return
	 */
	public int getNumberOfStates() {
		return states.size() - 2;
	}
	
	public int getStartStateIndex() {
		return states.size() - 2;
	}
	
	public int getEndStateIndex() {
		return states.size() - 1;
	}
	
	public String getStartState() {
		return startState;
	}
	
	public String getEndState() {
		return endState;
	}
}
