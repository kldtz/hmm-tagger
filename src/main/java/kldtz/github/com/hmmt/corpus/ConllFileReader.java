package kldtz.github.com.hmmt.corpus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import kldtz.github.com.hmmt.container.Sentence;

public class ConllFileReader extends CorpusFileReader {

	public ConllFileReader(File corpusFile) {
		super(corpusFile);
	}

	@Override
	Sentence readSentence() throws IOException {
		Sentence sentence = new Sentence();
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty())
				return sentence;
			String[] tokenTag = line.split("\\s+");
			if (tokenTag.length != 2) {
				logger.warn("Unexpected input format: " + Arrays.toString(tokenTag));
			}
			sentence.addWordTagPair(tokenTag[0], tokenTag[1]);
		}
		return sentence;
	}
}
