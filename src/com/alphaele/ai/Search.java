package com.alphaele.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alphaele.panel.ChessPanel;
import com.alphaele.panel.Rule;

/*
 * ����Ϊ������ai��
 * */
/**
 * @author John
 *
 */
public class Search {
	public static final int LIMIT_DEPTH = 64;    // �����������
	public static final int MATE_VALUE = 10000;  // ��߷�ֵ���������ķ�ֵ
	public static final int BAN_VALUE = MATE_VALUE - 100; // �����и��ķ�ֵ�����ڸ�ֵ����д���û���
	public static final int WIN_VALUE = MATE_VALUE - 200; // ������ʤ���ķ�ֵ���ޣ�������ֵ��˵���Ѿ�������ɱ����
	public static final int ADVANCED_VALUE = 3;  // ����Ȩ��ֵ
	public static final int RANDOM_MASK = 7;     // ����Է�ֵ
	public static final int NULL_DEPTH = 2;      // �ղ��ü��Ĳü����
	public static final boolean GEN_CAPTURE = true;
	public static final boolean NO_NULL = true;
	public static final int BOOK_SIZE = 16384;   // ���ֿ��С
	private static final List<Integer> array = new ArrayList<Integer>();//���ڱȽϺ�����������ʷ��mvv/lva 
	
	public static int mvResult;             // �����ߵ���
	public static int nHistoryTable[] = new int[65536]; // ��ʷ��65535
	
	public static int mvKillers[][] = new int[LIMIT_DEPTH][2]; // ɱ���߷���
	public static HashItem HashTable[] = new HashItem[HashItem.HASH_SIZE]; // �û���,����Ҫ���û����е�ÿһ�newһ������
	
	public static int nBookSize;                 // ���ֿ��С
	public static BookItem BookTable[] = new BookItem[BOOK_SIZE]; // ���ֿ�,�ǵ�new 
	
	private static long CLOCKS_PER_SEC = 200 ;//ʱ�䳣��,����ai˼�������ʱ��
	
	// ��̬(Quiescence)��������
	/**
	 * @param vlAlpha
	 * @param vlBeta
	 * @return
	 * �߼��߷�
	 */
	static int SearchQuiesc(int vlAlpha, int vlBeta) {
	  int i, nGenMoves;
	  int vl, vlBest;
	  int mvs[] = new int[Rule.MAX_GEN_MOVES];
	  // һ����̬������Ϊ���¼����׶�

	  // 1. ����ظ�����
	  vl = ChessPanel.pos.RepStatus(1);
	  if (vl != 0) {
	    return ChessPanel.pos.RepValue(vl);
	  }

	  // 2. ���Ｋ����Ⱦͷ��ؾ�������
	  if (ChessPanel.pos.nDistance == LIMIT_DEPTH) {
	    return ChessPanel.pos.Evaluate();
	  }

	  // 3. ��ʼ�����ֵ
	  vlBest = -MATE_VALUE; // ��������֪�����Ƿ�һ���߷���û�߹�(ɱ��)

	  if (ChessPanel.pos.InCheck()) {
	    // 4. �����������������ȫ���߷�
	    nGenMoves = ChessPanel.pos.GenerateMoves(mvs, false);//Ĭ��false
	    
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
	    
	  } else {

	    // 5. �������������������������
	    vl = ChessPanel.pos.Evaluate();
	    if (vl > vlBest) {
	      vlBest = vl;
	      if (vl >= vlBeta) {
	        return vl;
	      }
	      if (vl > vlAlpha) {
	        vlAlpha = vl;
	      }
	    }

	    // 6. �����������û�нضϣ������ɳ����߷�
	    nGenMoves = ChessPanel.pos.GenerateMoves(mvs, GEN_CAPTURE);
	    array.clear();//���ԭ�߷�
	    for(int item:mvs){//�߷�����list
    		Integer k = new Integer(item);
    		array.add(k);
    	}
	    
	    Comparator<Integer> MvvLvaCmp = new MvvLvaComparator(); 
	    Collections.sort(array, MvvLvaCmp); //��Mvv/Lvaֵ�Ӵ�С����
	    
	    for(int t=0; t<array.size(); t++){//listתint,�浽mvs��
	    	mvs[t] = array.get(t);//
	    }
	  }

	  // 7. ��һ����Щ�߷��������еݹ�
	  for (i = 0; i < nGenMoves; i ++) {
	    if (ChessPanel.pos.MakeMove(mvs[i])) {
	      vl = -SearchQuiesc(-vlBeta, -vlAlpha);
	      ChessPanel.pos.UndoMakeMove();

	      // 8. ����Alpha-Beta��С�жϺͽض�
	      if (vl > vlBest) {    // �ҵ����ֵ(������ȷ����Alpha��PV����Beta�߷�)
	        vlBest = vl;        // "vlBest"����ĿǰҪ���ص����ֵ�����ܳ���Alpha-Beta�߽�
	        if (vl >= vlBeta) { // �ҵ�һ��Beta�߷�
	          return vl;        // Beta�ض�
	        }
	        if (vl > vlAlpha) { // �ҵ�һ��PV�߷�
	          vlAlpha = vl;     // ��СAlpha-Beta�߽�
	        }
	      }
	    }
	  }

	  // 9. �����߷����������ˣ��������ֵ
	  return vlBest == -MATE_VALUE ? ChessPanel.pos.nDistance - MATE_VALUE : vlBest;
	}

	
	/**
	 * @param vlAlpha
	 * @param vlBeta
	 * @param nDepth
	 * @param bNoNull
	 * @return
	 * �߼��߷�
	 */
	public static int SearchFull(int vlAlpha, int vlBeta, int nDepth, boolean bNoNull) {//Ĭ��false
		  int nHashFlag, vl, vlBest;
		  int mv, mvBest, mvHash = 0, nNewDepth;
		  Sort Sort = new Sort();
		  // һ��Alpha-Beta��ȫ������Ϊ���¼����׶�

		  // 1. ����ˮƽ�ߣ�����þ�̬����(ע�⣺���ڿղ��ü�����ȿ���С����)
		    if (nDepth <= 0) {
		      return SearchQuiesc(vlAlpha, vlBeta);
		    }

		    // 1-1. ����ظ�����(ע�⣺��Ҫ�ڸ��ڵ��飬�����û���߷���)
		    vl = ChessPanel.pos.RepStatus(1);//Ĭ��Ϊ1
		    if (vl != 0) {
		      return ChessPanel.pos.RepValue(vl);
		    }

		    // 1-2. ���Ｋ����Ⱦͷ��ؾ�������
		    if (ChessPanel.pos.nDistance == LIMIT_DEPTH) {
		      return ChessPanel.pos.Evaluate();
		    }

		    // 1-3. �����û���ü������õ��û����߷�
		    Integer tempmvHash = new Integer(mvHash);//Ϊ�������ö���
		    vl = HashItem.ProbeHash(vlAlpha, vlBeta, nDepth, tempmvHash);
		    mvHash = tempmvHash.intValue();
		    if (vl > -MATE_VALUE) {
		      return vl;
		    }

		    // 1-4. ���Կղ��ü�(���ڵ��Betaֵ��"MATE_VALUE"�����Բ����ܷ����ղ��ü�)
		    if (!bNoNull && !ChessPanel.pos.InCheck() && ChessPanel.pos.NullOkay()) {
		    	ChessPanel.pos.NullMove();
		      vl = -SearchFull(-vlBeta, 1 - vlBeta, nDepth - NULL_DEPTH - 1, NO_NULL);
		      ChessPanel.pos.UndoNullMove();
		      if (vl >= vlBeta) {
		        return vl;
		      }
		    }

		  // 2. ��ʼ�����ֵ������߷�
		  nHashFlag = HashItem.HASH_ALPHA;
		  vlBest = -MATE_VALUE; // ��������֪�����Ƿ�һ���߷���û�߹�(ɱ��)
		  mvBest = 0;           // ��������֪�����Ƿ���������Beta�߷���PV�߷����Ա㱣�浽��ʷ��

		  // 3. ��ʼ���߷�����ṹ
		  Sort.Init(mvHash);

		  // 4. ��һ����Щ�߷��������еݹ�
		  while ((mv = Sort.Next()) != 0) {
			  if (ChessPanel.pos.MakeMove(mv)) {
		      nNewDepth = ChessPanel.pos.InCheck() ? nDepth : nDepth - 1;
		      // PVS
		      if (vlBest == -MATE_VALUE) {
		        vl = -SearchFull(-vlBeta, -vlAlpha, nNewDepth, false);
		      } else {
		        vl = -SearchFull(-vlAlpha - 1, -vlAlpha, nNewDepth, false);
		        if (vl > vlAlpha && vl < vlBeta) {
		          vl = -SearchFull(-vlBeta, -vlAlpha, nNewDepth, false);
		        }
		      }
		      ChessPanel.pos.UndoMakeMove();

		      // 5. ����Alpha-Beta��С�жϺͽض�
		      if (vl > vlBest) {    // �ҵ����ֵ(������ȷ����Alpha��PV����Beta�߷�)
		        vlBest = vl;        // "vlBest"����ĿǰҪ���ص����ֵ�����ܳ���Alpha-Beta�߽�
		        if (vl >= vlBeta) { // �ҵ�һ��Beta�߷��������õ��߷�
		          nHashFlag = HashItem.HASH_BETA;
		          mvBest = mv;      // Beta�߷�Ҫ���浽��ʷ��
		          break;            // Beta�ض�
		        }
		        if (vl > vlAlpha) { // �ҵ�һ��PV�߷�,��V1��С�Ŀ��Բ�������
		          nHashFlag = HashItem.HASH_PV;
		          mvBest = mv;      // PV�߷�Ҫ���浽��ʷ��
		          vlAlpha = vl;     // ��СAlpha-Beta�߽�
		        }
		      }
		    }
		  }

		  // 5. �����߷����������ˣ�������߷�(������Alpha�߷�)���浽��ʷ���������ֵ
		  if (vlBest == -MATE_VALUE) {
		    // �����ɱ�壬�͸���ɱ�岽����������
		    return ChessPanel.pos.nDistance - MATE_VALUE;
		  }
		  // ��¼���û���
		  HashItem.RecordHash(nHashFlag, vlBest, nDepth, mvBest);
		  if (mvBest != 0) {
			  // �������Alpha�߷����ͽ�����߷����浽��ʷ��
			  SetBestMove(mvBest, nDepth);
		  }
		    return vlBest;
	}

	// ���ڵ��Alpha-Beta��������
	public static int SearchRoot(int nDepth) {
		    int vl, vlBest, mv, nNewDepth;
		    Sort Sort = new Sort();

		    vlBest = -MATE_VALUE;
		    Sort.Init(Search.mvResult);
		    while ((mv = Sort.Next()) != 0) {
		      if (ChessPanel.pos.MakeMove(mv)) {
		        nNewDepth = ChessPanel.pos.InCheck() ? nDepth : nDepth - 1;
		        if (vlBest == -MATE_VALUE) {
		          vl = -SearchFull(-MATE_VALUE, MATE_VALUE, nNewDepth, NO_NULL);
		        } else {
		          vl = -SearchFull(-vlBest - 1, -vlBest, nNewDepth, false);
		          if (vl > vlBest) {
		            vl = -SearchFull(-MATE_VALUE, -vlBest, nNewDepth, NO_NULL);
		          }
		        }
		        ChessPanel.pos.UndoMakeMove();
		        if (vl > vlBest) {
		          vlBest = vl;
		          Search.mvResult = mv;
		          if (vlBest > -WIN_VALUE && vlBest < WIN_VALUE) {
		            vlBest += (((int)(Math.random()*32767)) & RANDOM_MASK) - (((int)(Math.random()*32767)) & RANDOM_MASK);
		          }
		        }
		      }
		    }
		HashItem.RecordHash(HashItem.HASH_PV, vlBest, nDepth, Search.mvResult);
		SetBestMove(Search.mvResult, nDepth);
		return vlBest;
	}

	// ����������������
	/**
	 * �߼��߷�
	 */
	public static void SearchMain() {
		  int i, vl, nGenMoves;
		  int mvs[] = new int[Rule.MAX_GEN_MOVES];
		  long t;
		  // ��ʼ��
		  Search.nHistoryTable = Arrays.copyOf(new int[65535], 65535);// �����ʷ��
		  Search.mvKillers = Arrays.copyOf(new int[LIMIT_DEPTH][2], LIMIT_DEPTH*2);// ���ɱ���߷���
		  Search.HashTable = Arrays.copyOf(new HashItem[HashItem.HASH_SIZE], HashItem.HASH_SIZE);// ����û���
		  
		  //��HashTable�е�ÿ��Ԫ�ض�ʵ����
		  for(int index=0; index<HashItem.HASH_SIZE; index++){
			  Search.HashTable[index] = new HashItem(); 
		  }
		  
		  t = System.currentTimeMillis();       // ��ʼ����ʱ��
		  ChessPanel.pos.nDistance = 0; // ��ʼ����
		  
		// �������ֿ�
		  Search.mvResult = BookItemOp.SearchBook();
		  if (Search.mvResult != 0) {
		    ChessPanel.pos.MakeMove(Search.mvResult);
		    if (ChessPanel.pos.RepStatus(3) == 0) {
		    	ChessPanel.pos.UndoMakeMove();
		      return;
		    }
		    ChessPanel.pos.UndoMakeMove();
		  }

		  // ����Ƿ�ֻ��Ψһ�߷�
		  vl = 0;
		  nGenMoves = ChessPanel.pos.GenerateMoves(mvs, false);
		  for (i = 0; i < nGenMoves; i ++) {
		    if (ChessPanel.pos.MakeMove(mvs[i])) {
		    	ChessPanel.pos.UndoMakeMove();
		      Search.mvResult = mvs[i];
		      vl ++;
		    }
		  }
		  if (vl == 1) {
		    return;
		  }

		  // �����������
		  for (i = 1; i <= LIMIT_DEPTH; i ++) {
			vl = SearchRoot(i);
		    // ������ɱ�壬����ֹ����
		    if (vl > WIN_VALUE || vl < -WIN_VALUE) {
		      break;
		    }
		    // ����һ�룬����ֹ����
		    if (System.currentTimeMillis() - t > CLOCKS_PER_SEC) {
		      break;
		    }
		  }
	}
	
	// ������߷��Ĵ���
	public static void SetBestMove(int mv, int nDepth) {
	  int lpmvKillers[] = new int[2];
	  Search.nHistoryTable[mv] += nDepth * nDepth;
	  lpmvKillers = Search.mvKillers[ChessPanel.pos.nDistance];
	  if (lpmvKillers[0] != mv) {
	    lpmvKillers[1] = lpmvKillers[0];
	    lpmvKillers[0] = mv;
	  }
	}

	
	
	/**
	 * @param vlAlpha
	 * @param vlBeta
	 * @return
	 * �м�����
	 */
	// ��̬(Quiescence)��������
	static int SearchQuiesc_1(int vlAlpha, int vlBeta) {
	  int i, nGenMoves;
	  int vl, vlBest;
	  int mvs[] = new int[Rule.MAX_GEN_MOVES];
	  // һ����̬������Ϊ���¼����׶�

	  // 1. ����ظ�����
	  vl = ChessPanel.pos.RepStatus(1);
	  if (vl != 0) {
	    return ChessPanel.pos.RepValue(vl);
	  }

	  // 2. ���Ｋ����Ⱦͷ��ؾ�������
	  if (ChessPanel.pos.nDistance == LIMIT_DEPTH) {
	    return ChessPanel.pos.Evaluate();
	  }

	  // 3. ��ʼ�����ֵ
	  vlBest = -MATE_VALUE; // ��������֪�����Ƿ�һ���߷���û�߹�(ɱ��)

	  if (ChessPanel.pos.InCheck()) {
	    // 4. �����������������ȫ���߷�
	    nGenMoves = ChessPanel.pos.GenerateMoves(mvs, false);//Ĭ��false
	    
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
	    
	  } else {

	    // 5. �������������������������
	    vl = ChessPanel.pos.Evaluate();
	    if (vl > vlBest) {
	      vlBest = vl;
	      if (vl >= vlBeta) {
	        return vl;
	      }
	      if (vl > vlAlpha) {
	        vlAlpha = vl;
	      }
	    }

	    // 6. �����������û�нضϣ������ɳ����߷�
	    nGenMoves = ChessPanel.pos.GenerateMoves(mvs, GEN_CAPTURE);
	    array.clear();//���ԭ�߷�
	    for(int item:mvs){//�߷�����list
    		Integer k = new Integer(item);
    		array.add(k);
    	}
	    
	    Comparator<Integer> MvvLvaCmp = new MvvLvaComparator(); 
	    Collections.sort(array, MvvLvaCmp); //��Mvv/Lvaֵ�Ӵ�С����
	    
	    for(int t=0; t<array.size(); t++){//listתint,�浽mvs��
	    	mvs[t] = array.get(t);//
	    }
	  }

	  // 7. ��һ����Щ�߷��������еݹ�
	  for (i = 0; i < nGenMoves; i ++) {
	    if (ChessPanel.pos.MakeMove(mvs[i])) {
	      vl = -SearchQuiesc_1(-vlBeta, -vlAlpha);
	      ChessPanel.pos.UndoMakeMove();

	      // 8. ����Alpha-Beta��С�жϺͽض�
	      if (vl > vlBest) {    // �ҵ����ֵ(������ȷ����Alpha��PV����Beta�߷�)
	        vlBest = vl;        // "vlBest"����ĿǰҪ���ص����ֵ�����ܳ���Alpha-Beta�߽�
	        if (vl >= vlBeta) { // �ҵ�һ��Beta�߷�
	          return vl;        // Beta�ض�
	        }
	        if (vl > vlAlpha) { // �ҵ�һ��PV�߷�
	          vlAlpha = vl;     // ��СAlpha-Beta�߽�
	        }
	      }
	    }
	  }

	  // 9. �����߷����������ˣ��������ֵ
	  return vlBest == -MATE_VALUE ? ChessPanel.pos.nDistance - MATE_VALUE : vlBest;
	}

	
	/**
	 * @param vlAlpha
	 * @param vlBeta
	 * @param nDepth
	 * @param bNoNull
	 * @return
	 * �м����巨
	 */
	public static int SearchFull_1(int vlAlpha, int vlBeta, int nDepth, boolean bNoNull) {//Ĭ��false
		  int i, nGenMoves;
		  int vl, vlBest, mvBest;
		  int mvs[] = new int[Rule.MAX_GEN_MOVES];//�߷�������
		  // һ��Alpha-Beta��ȫ������Ϊ���¼����׶�

		  if (ChessPanel.pos.nDistance > 0) {
			    // 1. ����ˮƽ�ߣ�����þ�̬����(ע�⣺���ڿղ��ü�����ȿ���С����)
			    if (nDepth <= 0) {
			      return SearchQuiesc_1(vlAlpha, vlBeta);
			    }

			    // 1-1. ����ظ�����(ע�⣺��Ҫ�ڸ��ڵ��飬�����û���߷���)
			    vl = ChessPanel.pos.RepStatus(1);//Ĭ��Ϊ1
			    if (vl != 0) {
			      return ChessPanel.pos.RepValue(vl);
			    }

			    // 1-2. ���Ｋ����Ⱦͷ��ؾ�������
			    if (ChessPanel.pos.nDistance == LIMIT_DEPTH) {
			      return ChessPanel.pos.Evaluate();
			    }

			    // 1-3. ���Կղ��ü�(���ڵ��Betaֵ��"MATE_VALUE"�����Բ����ܷ����ղ��ü�)
			    if (!bNoNull && !ChessPanel.pos.InCheck() && ChessPanel.pos.NullOkay()) {
			    	ChessPanel.pos.NullMove();
			      vl = -SearchFull_1(-vlBeta, 1 - vlBeta, nDepth - NULL_DEPTH - 1, NO_NULL);
			      ChessPanel.pos.UndoNullMove();
			      if (vl >= vlBeta) {
			        return vl;
			      }
			    }
		  }

		  // 2. ��ʼ�����ֵ������߷�
		  vlBest = -MATE_VALUE; // ��������֪�����Ƿ�һ���߷���û�߹�(ɱ��)
		  mvBest = 0;           // ��������֪�����Ƿ���������Beta�߷���PV�߷����Ա㱣�浽��ʷ��

		  // 3. ����ȫ���߷�����������ʷ������,��ʷ��Ӧ���ǴӴ�С����
		  nGenMoves = ChessPanel.pos.GenerateMoves(mvs, false);
		  array.clear();//���ԭ�߷�
		    for(int item:mvs){//�߷�����list
	    		Integer k = new Integer(item);
	    		array.add(k);
	    	}
		    
		   Comparator<Integer> HistoryCmp = new HistoryComparator(); 
		   Collections.sort(array, HistoryCmp); //��HistoryTableֵ�Ӵ�С����
		   
		    for(int t=0; t<array.size(); t++){//listתint,�浽mvs��
		    	mvs[t] = array.get(t);//
		    }

		  // 4. ��һ����Щ�߷��������еݹ�
		   for (i = 0; i < nGenMoves; i ++) {
		    if (ChessPanel.pos.MakeMove(mvs[i])) {
		      vl = -SearchFull_1(-vlBeta, -vlAlpha, ChessPanel.pos.InCheck() ? nDepth : nDepth - 1, false);//����Сֵ����
		      ChessPanel.pos.UndoMakeMove();

		      // 5. ����Alpha-Beta��С�жϺͽض�
		      if (vl > vlBest) {    // �ҵ����ֵ(������ȷ����Alpha��PV����Beta�߷�)
		        vlBest = vl;        // "vlBest"����ĿǰҪ���ص����ֵ�����ܳ���Alpha-Beta�߽�
		        if (vl >= vlBeta) { // �ҵ�һ��Beta�߷��������õ��߷�
		          mvBest = mvs[i];  // Beta�߷�Ҫ���浽��ʷ��
		          break;            // Beta�ض�
		        }
		        if (vl > vlAlpha) { // �ҵ�һ��PV�߷�,��V1��С�Ŀ��Բ�������
		          mvBest = mvs[i];  // PV�߷�Ҫ���浽��ʷ��
		          vlAlpha = vl;     // ��СAlpha-Beta�߽�
		        }
		      }
		    }
		  }

		  // 5. �����߷����������ˣ�������߷�(������Alpha�߷�)���浽��ʷ���������ֵ
		  if (vlBest == -MATE_VALUE) {
		    // �����ɱ�壬�͸���ɱ�岽����������
		    return ChessPanel.pos.nDistance - MATE_VALUE;
		  }
		  if (mvBest != 0) {
		    // �������Alpha�߷����ͽ�����߷����浽��ʷ��
		    Search.nHistoryTable[mvBest] += nDepth * nDepth;
		    if (ChessPanel.pos.nDistance == 0) {
		      // �������ڵ�ʱ��������һ������߷�(��Ϊȫ�����������ᳬ���߽�)��������߷���������
		      Search.mvResult = mvBest;
		    }
		  }
		  return vlBest;
		}

	// ����������������
	/**
	 * �м����巨
	 */
	public static void SearchMain_1() {
		  int i, vl;
		  long t;
		  // ��ʼ��
		  Search.nHistoryTable = Arrays.copyOf(new int[65535], 65535);// �����ʷ��
		  t = System.currentTimeMillis();       // ��ʼ����ʱ��
		  ChessPanel.pos.nDistance = 0; // ��ʼ����

		  // �����������
		  for (i = 1; i <= LIMIT_DEPTH; i ++) {
		    vl = SearchFull_1(-MATE_VALUE, MATE_VALUE, i, false);
		    // ������ɱ�壬����ֹ����
		    if (vl > WIN_VALUE || vl < -WIN_VALUE) {
		      break;
		    }
		    // ����һ�룬����ֹ����
		    if (System.currentTimeMillis() - t > CLOCKS_PER_SEC) {
		      break;
		    }
		  }
	}
	
}
