package kldtz.github.com.hmmt.probabilities;

public interface EmissionProbabilities {
	public double getProbability(String word, String tag);
	public double getLogProbability(String word, String tag);
}
