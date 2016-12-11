package kldtz.github.com.hmmt.evaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kldtz.github.com.hmmt.corpus.CorpusFormat;
import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.counts.CountsBuilder;
import kldtz.github.com.hmmt.tagger.TaggerBuilder;
import kldtz.github.com.hmmt.utils.Utils;

public class KFoldCrossValidation {
	static final Logger logger = LoggerFactory.getLogger(KFoldCrossValidation.class);
	private Path corpusPath;
	private CorpusFormat corpusFormat;
	private int numSentences;
	
	public KFoldCrossValidation(Path corpusPath, CorpusFormat corpusFormat) {
		this.corpusPath = corpusPath;
		this.corpusFormat = corpusFormat;
		numSentences = Utils.countSentencesInFile(corpusPath, corpusFormat);
	}
	
	private int computeSubsetSize(int k) {
		return Math.round((float) numSentences / k);
	}
	
	private BitSet selectTestInstances(int fold, int subsetSize) {
		BitSet testInstances = new BitSet();
		testInstances.set(fold * subsetSize, Math.min((fold + 1) * subsetSize, numSentences));
		return testInstances;
	}
	
	public void trainAndTagOnFolds(Path output, int k) {
		try (FileWriter fileWriter = new FileWriter(output.toFile());
				BufferedWriter writer = new BufferedWriter(fileWriter);) {
			SplitEvaluator splitEvaluator = new SplitEvaluator(corpusPath, corpusFormat, writer);
			writer.write(EvaluationData.getHeader());
			writer.newLine();
			int subsetSize = computeSubsetSize(k);
			for (int fold = 0; fold < k; fold++) {
				logger.info("Starting iteration number " + (fold + 1));
				BitSet testInstances = selectTestInstances(fold, subsetSize);
				Counts counts = new CountsBuilder(corpusPath, corpusFormat, testInstances).build();
				splitEvaluator.setLexicon(counts.getLexicon());
				splitEvaluator.setTagger(new TaggerBuilder(counts).build());
				splitEvaluator.evaluateSplit(testInstances);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		logger.info("Completed all " + k + " iterations");
	}
	
	public static void main(String[] args) {
		Path corpusPath = Paths.get("data/private/tiger.conll");
		KFoldCrossValidation mccv = new KFoldCrossValidation(corpusPath, CorpusFormat.CONLL);
		Path output = Paths.get("data/private/10-Fold_Cross_Validation_Tiger.csv");
		mccv.trainAndTagOnFolds(output, 10);
	}
}
