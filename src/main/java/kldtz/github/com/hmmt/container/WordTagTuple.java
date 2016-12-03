package kldtz.github.com.hmmt.container;

import java.io.Serializable;

public class WordTagTuple implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String word;
	private String tag;
	
	public WordTagTuple(String word, String tag) {
		super();
		this.word = word;
		this.tag = tag;
	}
	
	public String word() {
		return word;
	}
	
	public String tag() {
		return tag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordTagTuple other = (WordTagTuple) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}
	
}
