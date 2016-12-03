package kldtz.github.com.hmmt.corpus;

import java.io.File;

public enum CorpusFormat {
	CONLL {
		@Override
		public CorpusFileReader createCorpusFileReader(File corpusFile) {
			return new ConllFileReader(corpusFile);
		}

		@Override
		public TestCorpusReader createTestCorpusReader(File corpusFile) {
			return new ConllTestReader(corpusFile);
		}
	},
	BROWN {
		@Override
		public CorpusFileReader createCorpusFileReader(File corpusFile) {
			return new BrownFileReader(corpusFile);
		}

		@Override
		public TestCorpusReader createTestCorpusReader(File corpusFile) {
			throw new UnsupportedOperationException();
		}
	};
	
	public abstract CorpusFileReader createCorpusFileReader(File corpusFile);
	
	public abstract TestCorpusReader createTestCorpusReader(File corpusFile);
}