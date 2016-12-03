package kldtz.github.com.hmmt.container;

import java.io.Serializable;

public class TagNgram implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String[] tags;
	private int start;
	private int end;

	public TagNgram(String... tags) {
		if (tags == null) {
			throw new IllegalArgumentException("Tags must not be null");
		}
		this.tags = tags;
		start = 0;
		end = tags.length;
	}
	
	public int getN() {
		return end - start;
	}
	
	public String[] getTags() {
		String[] visibleTags = new String[tags.length - start];
		for (int i = start; i < end; i++) {
			visibleTags[i - start] = tags[i];
		}
		return visibleTags;
	}
	
	public String getFirst() {
		if (tags.length <= start) {
			throw new IllegalStateException("No first element");
		}
		return tags[start];
	}
	
	public TagNgram createCopy() {
		TagNgram clone = new TagNgram(tags);
		clone.start = start;
		clone.end = end;
		return clone;
	}
	
	public TagNgram createCopy(int start, int end) {
		TagNgram clone = new TagNgram(tags);
		clone.resize(start, end);
		return clone;
	}
	
	private void setStart(int start) {
		if (start < 0) {
			throw new IllegalArgumentException("Start index must not be greater than length of backing array");
		}
		this.start = start;
	}
	
	private void setEnd(int end) {
		if (end > tags.length) {
			throw new IllegalArgumentException("End index must not be greater than length of backing array");
		}
		this.end = end;
	}
	
	public void resize(int start, int end) {
		if (start > end) {
			throw new IllegalArgumentException("Start index must not be greater than end index");
		}
		setStart(start);
		setEnd(end);
	}
	
	public void resize(int start) {
		resize(start, end);
	}
	
	/**
	 * Ignore elements before start and after end index.
	 * @return
	 */
	@Override
	public int hashCode() {		
		if (tags == null)
            return 0;
        int result = 1;
        for (int i = start; i < end; i++) {
        	String tag = tags[i];
            result = 31 * result + (tag == null ? 0 : tag.hashCode());
        }
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
		TagNgram other = (TagNgram) obj;
        if (getN() != other.getN())
            return false;
        for (int i = 0; i < getN(); i++) {
            String tag1 = tags[i + start];
            String tag2 = other.tags[i + other.start];
            if (!(tag1==null ? tag2==null : tag1.equals(tag2)))
                return false;
        }
        return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = start; i < end; i++) {
			String tag = tags[i];
			sb.append(tag);
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");
		return sb.toString();
	}
}
