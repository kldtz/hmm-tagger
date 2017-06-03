package kldtz.gihub.com.hmmt.corpus;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

import kldtz.github.com.hmmt.container.Sentence;
import kldtz.github.com.hmmt.corpus.GermEvalReader;

public class GermEvalReaderTest {

	@Test
	public void readsAndTagsGermEvalSentence() throws URISyntaxException {
		File germEvalTestFile = new File(
				getClass().getClassLoader().getResource("private/GermEvalTwoSentences.tsv").toURI());
		try (GermEvalReader germEvalReader = new GermEvalReader(germEvalTestFile)) {
			Sentence sentence = germEvalReader.iterator().next();
			assertEquals(18, sentence.tags().size());
			assertEquals("Gleich", sentence.words().get(0));
			assertEquals(".", sentence.words().get(17));
			assertEquals("ADV-O", sentence.tags().get(0));
			assertEquals("$.-O", sentence.tags().get(17));
		}
	}
}
