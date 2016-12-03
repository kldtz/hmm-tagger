package kldtz.github.com.hmmt.corpus;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;

import kldtz.github.com.hmmt.container.Sentence;

public class ConllTestReader extends TestCorpusReader {

	public ConllTestReader(File corpusFile) {
		super(corpusFile);
	}

	@Override
	void readSentence() {
		sentence = new Sentence();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					return;
				String[] tokenTag = line.split("\\s+");
				if (tokenTag.length != 2) {
					logger.warn("Unexpected input format: " + Arrays.toString(tokenTag));
				}
				sentence.addWordTagPair(tokenTag[0], tokenTag[1]);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
