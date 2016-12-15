package kldtz.github.com.hmmt.evaluation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import kldtz.github.com.hmmt.container.Sentence;
import kldtz.github.com.hmmt.corpus.CorpusFileReader;
import kldtz.github.com.hmmt.corpus.CorpusFormat;
import kldtz.github.com.hmmt.tagger.Tagger;
import kldtz.github.com.hmmt.utils.Utils;

public class SplitEvaluator {
	private Path corpusPath;
	private CorpusFormat corpusFormat;
	private Set<String> lexicon;
	private BufferedWriter writer;
	private Tagger tagger;
	
	public SplitEvaluator(Path corpusPath, CorpusFormat corpusFormat, BufferedWriter writer) {
		this.corpusPath = corpusPath;
		this.corpusFormat = corpusFormat;
		this.writer = writer;
	}
	
	public void setLexicon(Set<String> lexicon) {
		this.lexicon = lexicon;
	}
	
	public void setTagger(Tagger tagger) {
		this.tagger = tagger;
	}

	public void evaluateSplit(BitSet testInstances) throws IOException {
		if (lexicon == null || tagger == null) {
			throw new IllegalStateException("The SplitEvaluator needs a tagger and a lexicon");
		}
		CorpusFileReader corpusReader = corpusFormat.createCorpusFileReader(corpusPath.toFile());
		int sentenceIndex = 0;
		for (Sentence sentence : corpusReader) {
			if (testInstances.get(sentenceIndex)) {
				evaluateSentence(sentence, sentenceIndex);
			}
			sentenceIndex++;
		}
		corpusReader.close();
	}

	private void evaluateSentence(Sentence sentence, int sentenceIndex) throws IOException {
		List<String> words = sentence.words();
		List<String> expectedTags = sentence.tags();
		List<String> actualTags = tagger.tag(sentence.words());
		for (int j = 0; j < words.size(); j++) {
			EvaluationData data = new EvaluationData();
			data.setWord(words.get(j))
				.setExpectedTag(expectedTags.get(j))
				.setActualTag(actualTags.size() > j ? actualTags.get(j) : Utils.ERROR_TAG)
				.setIsKnownWord(lexicon.contains(words.get(j)))
				.setSentenceIndex(sentenceIndex)
				.setWordIndexInSentence(j)
				.setSentenceLength(words.size());
			writer.write(data.getLine());
			writer.newLine();
		}
	}
}
