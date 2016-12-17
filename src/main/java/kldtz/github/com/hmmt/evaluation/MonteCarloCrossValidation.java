package kldtz.github.com.hmmt.evaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kldtz.github.com.hmmt.corpus.CorpusFormat;
import kldtz.github.com.hmmt.counts.Counts;
import kldtz.github.com.hmmt.counts.CountsBuilder;
import kldtz.github.com.hmmt.tagger.Tagger;
import kldtz.github.com.hmmt.tagger.TaggerBuilder;
import kldtz.github.com.hmmt.utils.Utils;

public class MonteCarloCrossValidation {
	static final Logger logger = LoggerFactory.getLogger(MonteCarloCrossValidation.class);
	private Path corpusPath;
	private CorpusFormat corpusFormat;
	private int numSentences;
	private Random rand;

	public MonteCarloCrossValidation(Path corpusPath, CorpusFormat corpusFormat, long seed) {
		this.corpusPath = corpusPath;
		this.corpusFormat = corpusFormat;
		numSentences = Utils.countSentencesInFile(corpusPath, corpusFormat);
		rand = new Random(seed);
	}

	private BitSet selectRandomTestInstances(double fraction) {
		BitSet bitset = new BitSet();
		long numTestInstances = Math.round(fraction * numSentences);
		for (long i = numTestInstances; i >= 0; i--) {
			bitset.set(rand.nextInt(numSentences));
		}
		return bitset;
	}

	public void trainAndTagOnRandomSplits(Path output, int iterations, double fraction) {
		try (FileWriter fileWriter = new FileWriter(output.toFile());
				BufferedWriter writer = new BufferedWriter(fileWriter);) {
			SplitEvaluator splitEvaluator = new SplitEvaluator(corpusPath, corpusFormat, writer);
			writer.write(EvaluationData.getHeader());
			writer.newLine();
			for (int i = 0; i < iterations; i++) {
				logger.info("Starting iteration number " + (i + 1));
				BitSet testInstances = selectRandomTestInstances(fraction);
				Counts counts = new CountsBuilder(corpusPath, corpusFormat, testInstances).build();
				Set<String> lexicon = counts.getLexicon();
				Tagger tagger = new TaggerBuilder(counts).build();
				splitEvaluator.setLexicon(lexicon);
				splitEvaluator.setTagger(tagger);
				splitEvaluator.evaluateSplit(testInstances, i);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		logger.info("Completed all " + iterations + " iterations");
	}

	public static void main(String[] args) {
		Path corpusPath = Paths.get("data/private/tiger.conll");
		MonteCarloCrossValidation mccv = new MonteCarloCrossValidation(corpusPath, CorpusFormat.CONLL, 1L);
		Path output = Paths.get("data/private/MonteCarloCrossValidation_Tiger_90-10_1L.csv");
		mccv.trainAndTagOnRandomSplits(output, 10, 0.1);
	}
}
