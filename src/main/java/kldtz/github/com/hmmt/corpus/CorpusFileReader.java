package kldtz.github.com.hmmt.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kldtz.github.com.hmmt.container.Sentence;

public abstract class CorpusFileReader implements Iterable<Sentence> {
	static final Logger logger = LoggerFactory.getLogger(CorpusFileReader.class);

	protected File corpusFile;
	protected FileReader fileReader;
	protected BufferedReader reader;
	protected Sentence sentence;

	public CorpusFileReader(File corpusFile) {
		this.corpusFile = corpusFile;
	}

	abstract void readSentence();

	@Override
	public Iterator<Sentence> iterator() {
		return new TestCorpusIterator();
	}
	
	public void close() {
		close(reader);
		close(fileReader);
		sentence = null;
	}
	
	private void close(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error("Could not close " + reader.getClass().getSimpleName(), e);
			}
		}
	}

	private class TestCorpusIterator implements Iterator<Sentence> {

		public TestCorpusIterator() {
			try {
				fileReader = new FileReader(corpusFile);
				reader = new BufferedReader(fileReader);
				readSentence();
			} catch (FileNotFoundException e) {
				String message = String.format("Corpus file %s not found", corpusFile.getAbsolutePath());
				throw new UncheckedIOException(message, e);
			}
		}

		@Override
		public boolean hasNext() {
			return sentence != null && !sentence.isEmpty();
		}

		@Override
		public Sentence next() {
			Sentence currentSentence = sentence;
			if (hasNext()) {
				readSentence();
				return currentSentence;
			}
			throw new NoSuchElementException(getClass().getName() + " has no further element");
		}

	}
}
