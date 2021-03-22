package com.alphaele.ai;

import java.util.Comparator;

/**
 * 按历史表从大到小排列mvs
 *
 */
public class HistoryComparator implements Comparator<Integer>{

	@Override
	public int compare(Integer o1, Integer o2) {
		return Search.nHistoryTable[o2] - Search.nHistoryTable[o1];
	}

}
