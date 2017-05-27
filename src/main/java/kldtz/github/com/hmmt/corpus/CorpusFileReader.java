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

public abstract class CorpusFileReader implements Iterable<Sentence>, AutoCloseable {
	static final Logger logger = LoggerFactory.getLogger(CorpusFileReader.class);

	protected File corpusFile;
	protected FileReader fileReader;
	protected BufferedReader reader;

	public CorpusFileReader(File corpusFile) {
		this.corpusFile = corpusFile;
	}

	abstract Sentence readSentence() throws IOException;

	@Override
	public Iterator<Sentence> iterator() {
		try {
			return new TestCorpusIterator();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public void close() {
		close(reader);
		close(fileReader);
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
		
		private Sentence sentence;

		public TestCorpusIterator() throws IOException {
			try {
				fileReader = new FileReader(corpusFile);
				reader = new BufferedReader(fileReader);
				sentence = readSentence();
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
				try {
					sentence = readSentence();
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
				return currentSentence;
			}
			throw new NoSuchElementException(getClass().getName() + " has no further element");
		}

	}
}
