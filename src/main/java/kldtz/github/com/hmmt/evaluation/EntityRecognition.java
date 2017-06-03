package kldtz.github.com.hmmt.evaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;

import kldtz.github.com.hmmt.corpus.CorpusFormat;
import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.counts.CountsBuilder;
import kldtz.github.com.hmmt.tagger.Tagger;
import kldtz.github.com.hmmt.tagger.TaggerBuilder;
import kldtz.github.com.hmmt.utils.Utils;

public class EntityRecognition {

	public static void main(String[] args) throws IOException {
		Path trainSet = Paths.get("data/private/GermEval2014/train.conll");
		Path devSet = Paths.get("data/private/GermEval2014/dev.conll");
		Path results = Paths.get("data/private/GermEval2014-dev-results-n4.csv");
		tagAllSentences(trainSet, devSet, results);
	}

	private static void tagAllSentences(Path trainSet, Path evalSet, Path results) throws IOException {
		Counts counts = new CountsBuilder(trainSet.toString(), CorpusFormat.CONLL).maxWordFrequencyForSuffixCounts(10).maxNgramSize(4).build();
		Tagger tagger = new TaggerBuilder(counts).build();
		try (FileWriter fw = new FileWriter(results.toFile()); BufferedWriter bw = new BufferedWriter(fw);) {
			bw.write(EvaluationData.getHeader());
			bw.newLine();
			SplitEvaluator splitEvaluator = new SplitEvaluator(evalSet, CorpusFormat.CONLL, bw);
			splitEvaluator.setLexicon(counts.getLexicon());
			splitEvaluator.setTagger(tagger);
			BitSet testInstances = selectAllSentences(evalSet);
			splitEvaluator.evaluateSplit(testInstances, 0);
		}
	}

	private static BitSet selectAllSentences(Path input) {
		int numSentences = Utils.countSentencesInFile(input, CorpusFormat.CONLL);
		BitSet testInstances = new BitSet();
		testInstances.set(0, numSentences);
		return testInstances;
	}
}
