package com.alphaele.ai;
import java.util.Comparator; 

import com.alphaele.panel.BoardOperation;
import com.alphaele.panel.ChessPanel;

/**
 * 按照mvv/lva的值由大到小排序mvs
 *
 */
public class MvvLvaComparator implements Comparator<Integer>{ 

	@Override
	public int compare(Integer o1, Integer o2) {//从大到小排列
		return MvvLva(o2)-MvvLva(o1);
	} 
	
	// MVV/LVA每种子力的价值
	public static byte cucMvvLva[] = {
	  0, 0, 0, 0, 0, 0, 0, 0,
	  5, 1, 1, 3, 4, 3, 2, 0,
	  5, 1, 1, 3, 4, 3, 2, 0
	};
	
	// 求MVV/LVA值
	public int MvvLva(int mv) {
	  return (cucMvvLva[ChessPanel.pos.ucpcSquares[BoardOperation.DST(mv)]] << 3) - cucMvvLva[ChessPanel.pos.ucpcSquares[BoardOperation.SRC(mv)]];
	}
    
}
