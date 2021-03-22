package com.alphaele.ai;

import com.alphaele.panel.ChessPanel;

/**
 * �û���ṹ
 *
 */
public class HashItem {
	public static final int HASH_SIZE = 1 << 20; // �û����С
	public static final int HASH_ALPHA = 1;      // ALPHA�ڵ���û�����
	public static final int HASH_BETA = 2;       // BETA�ڵ���û�����
	public static final int HASH_PV = 3;         // PV�ڵ���û�����
	
	byte ucDepth, ucFlag;
	short svl;
	int wmv, wReserved;//��ΪWORD
	long dwLock0, dwLock1;	//��ΪDWORD
	
	// ��ȡ�û�����,mv��Ҫ����ַ
	public static int ProbeHash(int vlAlpha, int vlBeta, int nDepth, Integer mv) {
	  boolean bMate; // ɱ���־�������ɱ�壬��ô����Ҫ�����������
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
		    return -Search.MATE_VALUE; // ���ܵ��������Ĳ��ȶ��ԣ������˳���������ŷ������õ�
		}
	    hsh.svl -= ChessPanel.pos.nDistance;
	    bMate = true;
	  } else if (hsh.svl < -Search.WIN_VALUE) {
		  if (hsh.svl > -Search.BAN_VALUE) {
		      return -Search.MATE_VALUE; // ͬ��
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

	// �����û�����
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
		    return; // ���ܵ��������Ĳ��ȶ��ԣ�����û������ŷ��������˳�
		}
	    hsh.svl = (short) (vl + ChessPanel.pos.nDistance);
	  } else if (vl < -Search.WIN_VALUE) {
		if (mv == 0 && vl >= -Search.BAN_VALUE) {
		    return; // ͬ��
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
