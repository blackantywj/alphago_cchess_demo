package com.alphaele.ai;

import com.alphaele.panel.ChessPanel;

/**
 * 置换表结构
 *
 */
public class HashItem {
	public static final int HASH_SIZE = 1 << 20; // 置换表大小
	public static final int HASH_ALPHA = 1;      // ALPHA节点的置换表项
	public static final int HASH_BETA = 2;       // BETA节点的置换表项
	public static final int HASH_PV = 3;         // PV节点的置换表项
	
	byte ucDepth, ucFlag;
	short svl;
	int wmv, wReserved;//本为WORD
	long dwLock0, dwLock1;	//本为DWORD
	
	// 提取置换表项,mv需要传地址
	public static int ProbeHash(int vlAlpha, int vlBeta, int nDepth, Integer mv) {
	  boolean bMate; // 杀棋标志：如果是杀棋，那么不需要满足深度条件
	  HashItem hsh;

	  hsh = Search.HashTable[(int)(ChessPanel.pos.zobr.dwKey & (HASH_SIZE - 1))];
	  if (hsh.dwLock0 != ChessPanel.pos.zobr.dwLock0 || hsh.dwLock1 != ChessPanel.pos.zobr.dwLock1) {
	    mv = 0;
	    return -Search.MATE_VALUE;
	  }
	  mv = hsh.wmv;
	  bMate = false;
	  if (hsh.svl > Search.WIN_VALUE) {
		if (hsh.svl < Search.BAN_VALUE) {
		    return -Search.MATE_VALUE; // 可能导致搜索的不稳定性，立刻退出，但最佳着法可能拿到
		}
	    hsh.svl -= ChessPanel.pos.nDistance;
	    bMate = true;
	  } else if (hsh.svl < -Search.WIN_VALUE) {
		  if (hsh.svl > -Search.BAN_VALUE) {
		      return -Search.MATE_VALUE; // 同上
		  }
	    hsh.svl += ChessPanel.pos.nDistance;
	    bMate = true;
	  }
	  if (hsh.ucDepth >= nDepth || bMate) {
	    if (hsh.ucFlag == HASH_BETA) {
	      return (hsh.svl >= vlBeta ? hsh.svl : -Search.MATE_VALUE);
	    } else if (hsh.ucFlag == HASH_ALPHA) {
	      return (hsh.svl <= vlAlpha ? hsh.svl : -Search.MATE_VALUE);
	    }
	    return hsh.svl;
	  }
	  return -Search.MATE_VALUE;
	};

	// 保存置换表项
	public static void RecordHash(int nFlag, int vl, int nDepth, int mv) {
	  HashItem hsh;
	  hsh = Search.HashTable[(int)(ChessPanel.pos.zobr.dwKey & (HASH_SIZE - 1))];
	  if (hsh.ucDepth > nDepth) {
	    return;
	  }
	  hsh.ucFlag = (byte) nFlag;
	  hsh.ucDepth = (byte) nDepth;
	  if (vl > Search.WIN_VALUE) {
		if (mv == 0 && vl <= Search.BAN_VALUE) {
		    return; // 可能导致搜索的不稳定性，并且没有最佳着法，立刻退出
		}
	    hsh.svl = (short) (vl + ChessPanel.pos.nDistance);
	  } else if (vl < -Search.WIN_VALUE) {
		if (mv == 0 && vl >= -Search.BAN_VALUE) {
		    return; // 同上
		 }
	    hsh.svl = (short) (vl - ChessPanel.pos.nDistance);
	  } else {
	    hsh.svl = (short) vl;
	  }
	  hsh.wmv = mv;
	  hsh.dwLock0 = ChessPanel.pos.zobr.dwLock0;
	  hsh.dwLock1 = ChessPanel.pos.zobr.dwLock1;
	  Search.HashTable[(int)(ChessPanel.pos.zobr.dwKey & (HASH_SIZE - 1))] = hsh;
	};
}
