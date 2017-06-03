package kldtz.github.com.hmmt.corpus;

import java.util.ArrayList;
import java.util.List;

import is2.data.SentenceData09;
import is2.tag.Options;
import is2.tag.Tagger;

public class MateTagger {
	public static final String MODEL_PATH = "data/private/tag-ger-3.6.model";

	private Tagger tagger;

	public MateTagger() {
		Options taggerOptions = new Options(new String[] { "-model", MODEL_PATH });
		tagger = new Tagger(taggerOptions);
	}

	public List<String> tag(List<String> tokens) {
		SentenceData09 sentenceData = createSentenceDataFromTokens(tokens);
		sentenceData = tagger.apply(sentenceData);
		List<String> tags = new ArrayList<>();
		for (int i = 0; i < sentenceData.ppos.length; i++) {
			tags.add(sentenceData.ppos[i]);
		}
		return tags;
	}

	public static SentenceData09 createSentenceDataFromTokens(List<String> tokens) {
		String[] mateTokens = createMateTokens(tokens);
		SentenceData09 sentenceData = new SentenceData09();
		sentenceData.init(mateTokens);
		return sentenceData;
	}

	private static String[] createMateTokens(List<String> tokens) {
		String[] mateTokens = new String[tokens.size() + 1];
		mateTokens[0] = "<root>";
		for (int i = 0; i < tokens.size(); i++) {
			mateTokens[i + 1] = tokens.get(i);
		}
		return mateTokens;
	}
}
