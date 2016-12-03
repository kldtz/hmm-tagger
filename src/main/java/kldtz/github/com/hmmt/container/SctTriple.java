package kldtz.github.com.hmmt.container;

import java.io.Serializable;

import kldtz.github.com.hmmt.corpus.Capitalization;

public class SctTriple implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String suffix;
	private Capitalization capitalization;
	private String tag;
	
	public SctTriple(String suffix, Capitalization capitalization, String tag) {
		super();
		this.suffix = suffix;
		this.capitalization = capitalization;
		this.tag = tag;
	}

	public String getSuffix() {
		return suffix;
	}

	public Capitalization getCapitalization() {
		return capitalization;
	}

	public String getTag() {
		return tag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((capitalization == null) ? 0 : capitalization.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
		SctTriple other = (SctTriple) obj;
		if (capitalization != other.capitalization)
			return false;
		if (suffix == null) {
			if (other.suffix != null)
				return false;
		} else if (!suffix.equals(other.suffix))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

	
}
