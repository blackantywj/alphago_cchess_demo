package com.alphaele.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alphaele.panel.ChessPanel;
import com.alphaele.panel.Rule;

/**
 * 走法顺序控制结构
 *
 */
public class Sort {
	// 走法排序阶段
	public static final int PHASE_HASH = 0;
	public static final int PHASE_KILLER_1 = 1;
	public static final int PHASE_KILLER_2 = 2;
	public static final int PHASE_GEN_MOVES = 3;
	public static final int PHASE_REST = 4;
	
	private static final List<Integer> array = new ArrayList<Integer>();//用于比较函数，排序历史表，mvv/lva 
	
	int mvHash, mvKiller1, mvKiller2; // 置换表走法和两个杀手走法
	int nPhase, nIndex, nGenMoves;    // 当前阶段，当前采用第几个走法，总共有几个走法
	int mvs[] = new int[Rule.MAX_GEN_MOVES];           // 所有的走法

	void Init(int mvHash_) { // 初始化，设定置换表走法和两个杀手走法
	  mvHash = mvHash_;
	  mvKiller1 = Search.mvKillers[ChessPanel.pos.nDistance][0];
	  mvKiller2 = Search.mvKillers[ChessPanel.pos.nDistance][1];
	  nPhase = PHASE_HASH;
	}
	
	// 得到下一个走法
	int Next() {
	  int mv;
	  switch (nPhase) {
	  // "nPhase"表示着法启发的若干阶段，依次为：

	  // 0. 置换表着法启发，完成后立即进入下一阶段；
	  case PHASE_HASH:
	    nPhase = PHASE_KILLER_1;
	    if (mvHash != 0) {
	      return mvHash;
	    }
	    // 技巧：这里没有"break"，表示"switch"的上一个"case"执行完后紧接着做下一个"case"，下同

	  // 1. 杀手着法启发(第一个杀手着法)，完成后立即进入下一阶段；
	  case PHASE_KILLER_1:
	    nPhase = PHASE_KILLER_2;
	    if (mvKiller1 != mvHash && mvKiller1 != 0 && ChessPanel.pos.LegalMove(mvKiller1)) {
	      return mvKiller1;
	    }

	  // 2. 杀手着法启发(第二个杀手着法)，完成后立即进入下一阶段；
	  case PHASE_KILLER_2:
	    nPhase = PHASE_GEN_MOVES;
	    if (mvKiller2 != mvHash && mvKiller2 != 0 && ChessPanel.pos.LegalMove(mvKiller2)) {
	      return mvKiller2;
	    }

	  // 3. 生成所有着法，完成后立即进入下一阶段；
	  case PHASE_GEN_MOVES:
	    nPhase = PHASE_REST;
	    nGenMoves = ChessPanel.pos.GenerateMoves(mvs,false);
	    
	    array.clear();//清除原走法
	    for(int item:mvs){//走法加入list
    		Integer k = new Integer(item);
    		array.add(k);
    	}
	    
	    Comparator<Integer> HistoryCmp = new HistoryComparator(); 
	    Collections.sort(array, HistoryCmp); //按Histable值从大到小排序
	    
	    for(int t=0; t<array.size(); t++){//list转int,存到mvs中
	    	mvs[t] = array.get(t);//
	    }
	    
	    nIndex = 0;

	  // 4. 对剩余着法做历史表启发；
	  case PHASE_REST:
	    while (nIndex < nGenMoves) {
	      mv = mvs[nIndex];
	      nIndex ++;
	      if (mv != mvHash && mv != mvKiller1 && mv != mvKiller2) {
	        return mv;
	      }
	    }

	  // 5. 没有着法了，返回零。
	  default:
	    return 0;
	  }
	}
}
