package com.alphaele.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alphaele.panel.ChessPanel;
import com.alphaele.panel.Rule;

/**
 * �߷�˳����ƽṹ
 *
 */
public class Sort {
	// �߷�����׶�
	public static final int PHASE_HASH = 0;
	public static final int PHASE_KILLER_1 = 1;
	public static final int PHASE_KILLER_2 = 2;
	public static final int PHASE_GEN_MOVES = 3;
	public static final int PHASE_REST = 4;
	
	private static final List<Integer> array = new ArrayList<Integer>();//���ڱȽϺ�����������ʷ��mvv/lva 
	
	int mvHash, mvKiller1, mvKiller2; // �û����߷�������ɱ���߷�
	int nPhase, nIndex, nGenMoves;    // ��ǰ�׶Σ���ǰ���õڼ����߷����ܹ��м����߷�
	int mvs[] = new int[Rule.MAX_GEN_MOVES];           // ���е��߷�

	void Init(int mvHash_) { // ��ʼ�����趨�û����߷�������ɱ���߷�
	  mvHash = mvHash_;
	  mvKiller1 = Search.mvKillers[ChessPanel.pos.nDistance][0];
	  mvKiller2 = Search.mvKillers[ChessPanel.pos.nDistance][1];
	  nPhase = PHASE_HASH;
	}
	
	// �õ���һ���߷�
	int Next() {
	  int mv;
	  switch (nPhase) {
	  // "nPhase"��ʾ�ŷ����������ɽ׶Σ�����Ϊ��

	  // 0. �û����ŷ���������ɺ�����������һ�׶Σ�
	  case PHASE_HASH:
	    nPhase = PHASE_KILLER_1;
	    if (mvHash != 0) {
	      return mvHash;
	    }
	    // ���ɣ�����û��"break"����ʾ"switch"����һ��"case"ִ��������������һ��"case"����ͬ

	  // 1. ɱ���ŷ�����(��һ��ɱ���ŷ�)����ɺ�����������һ�׶Σ�
	  case PHASE_KILLER_1:
	    nPhase = PHASE_KILLER_2;
	    if (mvKiller1 != mvHash && mvKiller1 != 0 && ChessPanel.pos.LegalMove(mvKiller1)) {
	      return mvKiller1;
	    }

	  // 2. ɱ���ŷ�����(�ڶ���ɱ���ŷ�)����ɺ�����������һ�׶Σ�
	  case PHASE_KILLER_2:
	    nPhase = PHASE_GEN_MOVES;
	    if (mvKiller2 != mvHash && mvKiller2 != 0 && ChessPanel.pos.LegalMove(mvKiller2)) {
	      return mvKiller2;
	    }

	  // 3. ���������ŷ�����ɺ�����������һ�׶Σ�
	  case PHASE_GEN_MOVES:
	    nPhase = PHASE_REST;
	    nGenMoves = ChessPanel.pos.GenerateMoves(mvs,false);
	    
	    array.clear();//���ԭ�߷�
	    for(int item:mvs){//�߷�����list
    		Integer k = new Integer(item);
    		array.add(k);
    	}
	    
	    Comparator<Integer> HistoryCmp = new HistoryComparator(); 
	    Collections.sort(array, HistoryCmp); //��Histableֵ�Ӵ�С����
	    
	    for(int t=0; t<array.size(); t++){//listתint,�浽mvs��
	    	mvs[t] = array.get(t);//
	    }
	    
	    nIndex = 0;

	  // 4. ��ʣ���ŷ�����ʷ��������
	  case PHASE_REST:
	    while (nIndex < nGenMoves) {
	      mv = mvs[nIndex];
	      nIndex ++;
	      if (mv != mvHash && mv != mvKiller1 && mv != mvKiller2) {
	        return mv;
	      }
	    }

	  // 5. û���ŷ��ˣ������㡣
	  default:
	    return 0;
	  }
	}
}
