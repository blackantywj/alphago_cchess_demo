package com.alphaele.ai;

import java.util.Comparator;

/**
 * ����ʷ��Ӵ�С����mvs
 *
 */
public class HistoryComparator implements Comparator<Integer>{

	@Override
	public int compare(Integer o1, Integer o2) {
		return Search.nHistoryTable[o2] - Search.nHistoryTable[o1];
	}

}
