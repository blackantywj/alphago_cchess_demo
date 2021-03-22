package com.alphaele.ai;
import java.util.Comparator; 

import com.alphaele.panel.BoardOperation;
import com.alphaele.panel.ChessPanel;

/**
 * ����mvv/lva��ֵ�ɴ�С����mvs
 *
 */
public class MvvLvaComparator implements Comparator<Integer>{ 

	@Override
	public int compare(Integer o1, Integer o2) {//�Ӵ�С����
		return MvvLva(o2)-MvvLva(o1);
	} 
	
	// MVV/LVAÿ�������ļ�ֵ
	public static byte cucMvvLva[] = {
	  0, 0, 0, 0, 0, 0, 0, 0,
	  5, 1, 1, 3, 4, 3, 2, 0,
	  5, 1, 1, 3, 4, 3, 2, 0
	};
	
	// ��MVV/LVAֵ
	public int MvvLva(int mv) {
	  return (cucMvvLva[ChessPanel.pos.ucpcSquares[BoardOperation.DST(mv)]] << 3) - cucMvvLva[ChessPanel.pos.ucpcSquares[BoardOperation.SRC(mv)]];
	}
    
}
