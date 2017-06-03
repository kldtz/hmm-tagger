package kldtz.github.com.hmmt.corpus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kldtz.github.com.hmmt.container.Sentence;
import kldtz.github.com.hmmt.container.WordTagTuple;

public class GermEvalReader extends CorpusFileReader {
	private MateTagger mateTagger;

	public GermEvalReader(File corpusFile) {
		super(corpusFile);
		mateTagger = new MateTagger();
	}

	@Override
	Sentence readSentence() throws IOException {
		List<String> tokens = new ArrayList<>();
		List<String> entityTags = new ArrayList<>();
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty())
				return createSentenceWithExtendedTags(tokens, entityTags);
			if (line.charAt(0) == '#') {
				continue;
			}
			String[] tokenTag = line.split("\\s+");
			if (tokenTag.length != 4) {
				logger.warn("Unexpected input format: " + Arrays.toString(tokenTag));
			}
			tokens.add(tokenTag[1]);
			entityTags.add(tokenTag[2]);
		}
		return createSentenceWithExtendedTags(tokens, entityTags);
	}

	private Sentence createSentenceWithExtendedTags(List<String> tokens, List<String> entityTags) {
		Sentence sentence = new Sentence();
		if (tokens.isEmpty()) {
			return sentence;
		}
		List<String> posTags = mateTagger.tag(tokens);
		for (int i = 0; i < tokens.size(); i++) {
			sentence.addWordTagPair(tokens.get(i), getCombinedTag(entityTags.get(i), posTags.get(i)));
		}
		return sentence;
	}

	private String getCombinedTag(String entityTag, String posTag) {
		if (entityTag.endsWith("-PER") || entityTag.endsWith("-LOC") || entityTag.endsWith("-ORG")) {
			return entityTag;
		}
		return posTag;
	}

	public static void convertGermEvalToConll(Path germEvalInput, Path conllOutput) throws IOException {
		try (FileWriter fw = new FileWriter(conllOutput.toFile());
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bw);
				GermEvalReader germEvalReader = new GermEvalReader(germEvalInput.toFile())) {
			for (Sentence sentence : germEvalReader) {
				for (WordTagTuple wordTag : sentence) {
					pw.print(wordTag.word());
					pw.print("\t");
					pw.print(wordTag.tag());
					pw.println();
				}
				pw.println();
			}
		}
	}
}
