package kldtz.github.com.hmmt.corpus;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;

import kldtz.github.com.hmmt.container.WordTagTuple;

public class BrownFileReader extends CorpusFileReader {

	public BrownFileReader(File corpusFile) {
		super(corpusFile);
	}
	
	@Override
	protected void readSentence() {
		sentence = null;
		String line;
		try {
			if ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					readSentence();
					return;
				}
				String[] tokens = line.split("\\s+");
				sentence = new ArrayList<>(tokens.length);
				for (String token : tokens) {
					String[] wordTag = token.split("_");
					if (wordTag.length != 2) {
						logger.warn("Unexpected input format: " + Arrays.toString(wordTag));
					} else if (wordTag[1].length() > 1 && wordTag[1].charAt(1) == '|') {
						wordTag[1] = wordTag[1].substring(0, 1);
					}
					sentence.add(new WordTagTuple(wordTag[0], wordTag[1]));
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
