package kldtz.github.com.hmmt.counts;

import java.util.Set;

public enum SuffixCountsType {
	HASH {
		@Override
		public SuffixCounts constructInstance(Set<String> tagset, int maxSuffixLength) {
			return new HashedSuffixCounts(tagset, maxSuffixLength);
		}
	},
	TRIE {
		@Override
		public SuffixCounts constructInstance(Set<String> tagset, int maxSuffixLength) {
			return new TrieSuffixCounts(tagset, maxSuffixLength);
		}
	};
	
	public abstract SuffixCounts constructInstance(Set<String> tagset, int maxSuffixLength);
}
