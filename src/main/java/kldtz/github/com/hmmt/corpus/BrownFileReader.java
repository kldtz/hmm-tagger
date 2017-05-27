package kldtz.github.com.hmmt.corpus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import kldtz.github.com.hmmt.container.Sentence;

public class BrownFileReader extends CorpusFileReader {

	public BrownFileReader(File corpusFile) {
		super(corpusFile);
	}

	@Override
	Sentence readSentence() throws IOException {
		String line;
		if ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty()) { // skip empty line
				return readSentence();
			}
			Sentence sentence = new Sentence();
			String[] tokens = line.split("\\s+");
			for (String token : tokens) {
				String[] wordTag = token.split("_");
				if (wordTag.length != 2) {
					logger.warn("Unexpected input format: " + Arrays.toString(wordTag));
				} else if (wordTag[1].length() > 1 && wordTag[1].charAt(1) == '|') {
					wordTag[1] = wordTag[1].substring(0, 1);
				}
				sentence.addWordTagPair(wordTag[0], wordTag[1]);
			}
			return sentence;
		}
		return null;
	}

}
