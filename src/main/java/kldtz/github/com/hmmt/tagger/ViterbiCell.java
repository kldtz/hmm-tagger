package kldtz.github.com.hmmt.tagger;

public class ViterbiCell {
	private double value;
	private String state;
	private ViterbiCell previousCell;
	
	public ViterbiCell(double value, String state, ViterbiCell previousCell) {
		super();
		this.value = value;
		this.state = state;
		this.previousCell = previousCell;
	}
	
	public double getValue() {
		return value;
	}
	
	public String getState() {
		return state;
	}
	
	public ViterbiCell getPreviousCell() {
		return previousCell;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setPreviousCell(ViterbiCell previousCell) {
		this.previousCell = previousCell;
	}
}
