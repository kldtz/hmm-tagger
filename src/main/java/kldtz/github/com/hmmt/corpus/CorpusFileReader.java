package kldtz.github.com.hmmt.corpus;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kldtz.github.com.hmmt.container.WordTagTuple;

public abstract class CorpusFileReader implements Iterable<List<WordTagTuple>> {
	
	static final Logger logger = LoggerFactory.getLogger(ConllFileReader.class);

	protected File corpusFile;
	protected FileReader fileReader;
	protected BufferedReader reader;
	protected List<WordTagTuple> sentence;

	public CorpusFileReader(File corpusFile) {
		this.corpusFile = corpusFile;
	}

	abstract void readSentence();

	@Override
	public Iterator<List<WordTagTuple>> iterator() {
		return new CorpusSentenceIterator();
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

	private class CorpusSentenceIterator implements Iterator<List<WordTagTuple>> {
		
		public CorpusSentenceIterator() {
			initializeReader();
			readSentence();
		}
		
		private void initializeReader() {
			try {
				fileReader = new FileReader(corpusFile);
				reader = new BufferedReader(fileReader);
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
		public List<WordTagTuple> next() {
			List<WordTagTuple> currentSentence = sentence;
			if (hasNext()) {
				readSentence();
				return currentSentence;
			}
			throw new NoSuchElementException(getClass().getName() + " has no further element");
		}

	}
}
