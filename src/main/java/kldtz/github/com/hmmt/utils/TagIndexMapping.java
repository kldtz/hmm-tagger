package kldtz.github.com.hmmt.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import gnu.trove.map.hash.TObjectIntHashMap;

public class TagIndexMapping implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private TObjectIntHashMap<String> tagToIndex;
	private String[] indexToTag;
	
	public TagIndexMapping(Set<String> tagset) {
		List<String> sortedTagset = new ArrayList<>(tagset);
		Collections.sort(sortedTagset);
		setupTagIndexMapping(sortedTagset);
	}
	
	public TagIndexMapping(Set<String> tagset, Comparator<String> comparator) {
		List<String> sortedTagset = new ArrayList<>(tagset);
		sortedTagset.sort(comparator);
		setupTagIndexMapping(sortedTagset);
	}
	
	private void setupTagIndexMapping(List<String> tagset) {
		tagToIndex = new TObjectIntHashMap<>();
		indexToTag = new String[tagset.size()];
		int index = 0;
		for (String tag : tagset) {
			tag = tag.trim();
			tagToIndex.put(tag, index);
			indexToTag[index] = tag;
			index++;
		}
	}

	public int getIndex(String tag) {
		if (!tagToIndex.contains(tag)) {
			throw new IllegalArgumentException("Unknown tag: " + tag);
		}
		return tagToIndex.get(tag);
	}
	
	public String getTag(int index) {
		if (index >= indexToTag.length ||  index < 0) {
			throw new IllegalArgumentException("Invalid tag index:" + index);
		}
		return indexToTag[index];
	}
	
	public Set<String> getTagset() {
		return tagToIndex.keySet();
	}
	
	public String[] getTagArray() {
		return indexToTag;
	}
}
