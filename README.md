# Hidden Markov Model Tagger
Java implementation of a hidden Markov model tagger. This implementation can be parameterized for different ngram sizes (n > 1) and deals with unknown words by smoothing word emission probabilities via suffixes as described in [Samuelsson (1993)](#samuelsson-1993) and [Brants (2000)](#brants-2000).

## Usage

To use the tagger, you first need to train a model on a tagged corpus.

### Training a model

Provide the `CountsBuilder` with a path to your corpus and the [corpus format](#corpus-formats):
```java
Counts counts = new CountsBuilder("/path/to/corpus/in/conll/format", CorpusFormat.CONLL).build();
```

This gives you a model trained on your corpus with default paramters (e.g., an ngram size of three and a maximum suffix length of four). This model is simply a collection of word and tag frequencies which later can be used to compute emission and transition probabilities for the hidden Markov model. To train a model with different parameters, pass them to the builder:

```java
Counts counts = new CountsBuilder("/path/to/corpus/in/conll/format", CorpusFormat.CONLL)
                        .maxNgramSize(2)
                        .maxSuffixLength(6)
                        .maxWordFrequencyForSuffixCounts(8)
                        .build();
```

In this case, we get a bigram model that counts suffixes up to a length of six characters of words that occur at most eight times in the corpus (to enable the computation of smoothed emission probabilities). 

### Model persistence

Models can be serialized and deserialized like this:

```java
String path = "/path/to/serialized/model.ser";
try {
    counts.serialize(path);
    Counts deserializedCounts = Counts.deserialize(path);
} catch(IOException e) {
    // handle exception
}
```


### Tagging

Once you have the counts, you can use them to obtain a tagger.

```java
Tagger tagger = new TaggerBuilder(counts).build();
```

To tag a sentence, pass it to the tagger as a list of strings:

```java
List<String> sentence = Arrays.asList("A man walked along the street .".split(" "));
List<String> tags = tagger.tag(sentence);
```

## <a name="corpus-formats"></a>Supported Corpus Formats
Currently there are only two valid input formats:

### Underline format
Each sentence is on a separate line. Tokens and tags appear in pairs connected with an underscore. Token-tag pairs are delimited by spaces. 

```
This_DT is_VBZ a_DT sentence_NN ._.
This_DT is_VBZ another_DT sentence_NN ._.
```

### CoNLL format
Each token-tag pair is on a separate line. Tokens and tags are delimited by a tab. The end of a sentence is indicated by an empty line.

```
This    DT
is  VBZ
a   DT
sentence    NN
.   .

This    DT
is  VBZ
another DT
sentence    NN
.   .
```

## Evaluation

I evaluated the tagger on the German TIGER corpus ([Albert et al. (2003)](#albert-et-al-2003), [Brants et al. (2004)](#brants-et-al-2004)) using 10-fold cross validation: The corpus was partitioned into 10 parts with an equal number of sentences (5047). Each subsample was used to validate the model trained on the remaining 9 subsamples. The following table reports mean and standard deviation (in parantheses) of the per-tag accuracy across all ten folds.

| Ngram size| Mean total accuracy| Mean acc. for known words| Mean acc. for unknown words| 
|----------:|-------------------:|-------------------------:|---------------------------:|
|3|96.86 (0.30)|97.65 (0.18)|87.46 (1.00)|
|4|96.94 (0.31)|97.71 (0.18)|87.92 (1.12)|

(All values are percentages.)

## Named-Entity Recognition

The tagger was developed with part-of-speech tagging in mind, however, I was interested in how well it would do on the task of named-entity recognition (without any extension or parameter tweaking). I trained a 4-gram model on the outer spans of the *GermEval 2014 NER* training set ([Benikova et al., 2014](#benikova-et-al-2014)), where I added POS tags to all tokens that are not part of a named entity using the [Mate tagger](https://code.google.com/archive/p/mate-tools/).

An exact-match evaluation on the outer spans of the development set yielded the following results:

| Label | Precision | Recall | F1 |
|------:|----------:|-------:|---:|
| LOC | 0.683 | 0.672 | 0.677 |
| ORG | 0.492 | 0.488 | 0.490 |
| PER | 0.648 | 0.616 | 0.631 |

## References

* <a name="benikova-et-al-2014"></a> Benikova, D., Biemann, C., & Reznicek, M. (2014, May). NoSta-D Named Entity Annotation for German: Guidelines and Dataset. In LREC (pp. 2524-2531).
* <a name="brants-2000"></a>Brants, T. (2000). TnT: a statistical part-of-speech tagger. In Proceedings of the 6th Conference on Applied Natural Language Processing (pp. 224-231). Association for Computational Linguistics.
* <a name="brants-et-al-2004"></a>Brants, Sabine, Stefanie Dipper, Peter Eisenberg, Silvia Hansen, Esther König, Wolfgang Lezius, Christian Rohrer, George Smith, and Hans Uszkoreit (2004). TIGER: Linguistic Interpretation of a German Corpus. Journal of Language and Computation, 2004 (2), 597-620.
* <a name="samuelsson-1993"></a>Samuelsson, C. (1993). Morphological tagging based entirely on Bayesian inference. In Proceedings of the 9th Nordic Conference on Computational Linguistics.
* <a name="albert-et-al-2003"></a>Albert, S., Anderssen, J., Bader, R., Becker, S., Bracht, T., Brants, S., ... , Uszkoreit, H. (2003). TIGER Annotationsschema. Manuscript. Universität des Saarlands, Universität Stuttgart, Universität Potsdam. July 2003.

## License

Copyright 2016 Tobias Kolditz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This software uses third-party components, whose licenses are listed in the [third-party software notices](LICENSE-3RD-PARTY/3RD-PARTY_SOFTWARE_NOTICES.md).

