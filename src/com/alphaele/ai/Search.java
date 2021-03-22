package com.alphaele.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alphaele.panel.ChessPanel;
import com.alphaele.panel.Rule;

/*
 * 本类为初级的ai类
 * */
/**
 * @author John
 *
 */
public class Search {
	public static final int LIMIT_DEPTH = 64;    // 最大的搜索深度
	public static final int MATE_VALUE = 10000;  // 最高分值，即将死的分值
	public static final int BAN_VALUE = MATE_VALUE - 100; // 长将判负的分值，低于该值将不写入置换表
	public static final int WIN_VALUE = MATE_VALUE - 200; // 搜索出胜负的分值界限，超出此值就说明已经搜索出杀棋了
	public static final int ADVANCED_VALUE = 3;  // 先行权分值
	public static final int RANDOM_MASK = 7;     // 随机性分值
	public static final int NULL_DEPTH = 2;      // 空步裁剪的裁剪深度
	public static final boolean GEN_CAPTURE = true;
	public static final boolean NO_NULL = true;
	public static final int BOOK_SIZE = 16384;   // 开局库大小
	private static final List<Integer> array = new ArrayList<Integer>();//用于比较函数，排序历史表，mvv/lva 
	
	public static int mvResult;             // 电脑走的棋
	public static int nHistoryTable[] = new int[65536]; // 历史表65535
	
	public static int mvKillers[][] = new int[LIMIT_DEPTH][2]; // 杀手走法表
	public static HashItem HashTable[] = new HashItem[HashItem.HASH_SIZE]; // 置换表,还需要对置换表中的每一项都new一个对象
	
	public static int nBookSize;                 // 开局库大小
	public static BookItem BookTable[] = new BookItem[BOOK_SIZE]; // 开局库,记得new 
	
	private static long CLOCKS_PER_SEC = 200 ;//时间常数,允许ai思考的最大时间
	
	// 静态(Quiescence)搜索过程
	/**
	 * @param vlAlpha
	 * @param vlBeta
	 * @return
	 * 高级走法
	 */
	static int SearchQuiesc(int vlAlpha, int vlBeta) {
	  int i, nGenMoves;
	  int vl, vlBest;
	  int mvs[] = new int[Rule.MAX_GEN_MOVES];
	  // 一个静态搜索分为以下几个阶段

	  // 1. 检查重复局面
	  vl = ChessPanel.pos.RepStatus(1);
	  if (vl != 0) {
	    return ChessPanel.pos.RepValue(vl);
	  }

	  // 2. 到达极限深度就返回局面评价
	  if (ChessPanel.pos.nDistance == LIMIT_DEPTH) {
	    return ChessPanel.pos.Evaluate();
	  }

	  // 3. 初始化最佳值
	  vlBest = -MATE_VALUE; // 这样可以知道，是否一个走法都没走过(杀棋)

	  if (ChessPanel.pos.InCheck()) {
	    // 4. 如果被将军，则生成全部走法
	    nGenMoves = ChessPanel.pos.GenerateMoves(mvs, false);//默认false
	    
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
	    
	  } else {

	    // 5. 如果不被将军，先做局面评价
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

	    // 6. 如果局面评价没有截断，再生成吃子走法
	    nGenMoves = ChessPanel.pos.GenerateMoves(mvs, GEN_CAPTURE);
	    array.clear();//清除原走法
	    for(int item:mvs){//走法加入list
    		Integer k = new Integer(item);
    		array.add(k);
    	}
	    
	    Comparator<Integer> MvvLvaCmp = new MvvLvaComparator(); 
	    Collections.sort(array, MvvLvaCmp); //按Mvv/Lva值从大到小排序
	    
	    for(int t=0; t<array.size(); t++){//list转int,存到mvs中
	    	mvs[t] = array.get(t);//
	    }
	  }

	  // 7. 逐一走这些走法，并进行递归
	  for (i = 0; i < nGenMoves; i ++) {
	    if (ChessPanel.pos.MakeMove(mvs[i])) {
	      vl = -SearchQuiesc(-vlBeta, -vlAlpha);
	      ChessPanel.pos.UndoMakeMove();

	      // 8. 进行Alpha-Beta大小判断和截断
	      if (vl > vlBest) {    // 找到最佳值(但不能确定是Alpha、PV还是Beta走法)
	        vlBest = vl;        // "vlBest"就是目前要返回的最佳值，可能超出Alpha-Beta边界
	        if (vl >= vlBeta) { // 找到一个Beta走法
	          return vl;        // Beta截断
	        }
	        if (vl > vlAlpha) { // 找到一个PV走法
	          vlAlpha = vl;     // 缩小Alpha-Beta边界
	        }
	      }
	    }
	  }

	  // 9. 所有走法都搜索完了，返回最佳值
	  return vlBest == -MATE_VALUE ? ChessPanel.pos.nDistance - MATE_VALUE : vlBest;
	}

	
	/**
	 * @param vlAlpha
	 * @param vlBeta
	 * @param nDepth
	 * @param bNoNull
	 * @return
	 * 高级走法
	 */
	public static int SearchFull(int vlAlpha, int vlBeta, int nDepth, boolean bNoNull) {//默认false
		  int nHashFlag, vl, vlBest;
		  int mv, mvBest, mvHash = 0, nNewDepth;
		  Sort Sort = new Sort();
		  // 一个Alpha-Beta完全搜索分为以下几个阶段

		  // 1. 到达水平线，则调用静态搜索(注意：由于空步裁剪，深度可能小于零)
		    if (nDepth <= 0) {
		      return SearchQuiesc(vlAlpha, vlBeta);
		    }

		    // 1-1. 检查重复局面(注意：不要在根节点检查，否则就没有走法了)
		    vl = ChessPanel.pos.RepStatus(1);//默认为1
		    if (vl != 0) {
		      return ChessPanel.pos.RepValue(vl);
		    }

		    // 1-2. 到达极限深度就返回局面评价
		    if (ChessPanel.pos.nDistance == LIMIT_DEPTH) {
		      return ChessPanel.pos.Evaluate();
		    }

		    // 1-3. 尝试置换表裁剪，并得到置换表走法
		    Integer tempmvHash = new Integer(mvHash);//为传递引用而设
		    vl = HashItem.ProbeHash(vlAlpha, vlBeta, nDepth, tempmvHash);
		    mvHash = tempmvHash.intValue();
		    if (vl > -MATE_VALUE) {
		      return vl;
		    }

		    // 1-4. 尝试空步裁剪(根节点的Beta值是"MATE_VALUE"，所以不可能发生空步裁剪)
		    if (!bNoNull && !ChessPanel.pos.InCheck() && ChessPanel.pos.NullOkay()) {
		    	ChessPanel.pos.NullMove();
		      vl = -SearchFull(-vlBeta, 1 - vlBeta, nDepth - NULL_DEPTH - 1, NO_NULL);
		      ChessPanel.pos.UndoNullMove();
		      if (vl >= vlBeta) {
		        return vl;
		      }
		    }

		  // 2. 初始化最佳值和最佳走法
		  nHashFlag = HashItem.HASH_ALPHA;
		  vlBest = -MATE_VALUE; // 这样可以知道，是否一个走法都没走过(杀棋)
		  mvBest = 0;           // 这样可以知道，是否搜索到了Beta走法或PV走法，以便保存到历史表

		  // 3. 初始化走法排序结构
		  Sort.Init(mvHash);

		  // 4. 逐一走这些走法，并进行递归
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

		      // 5. 进行Alpha-Beta大小判断和截断
		      if (vl > vlBest) {    // 找到最佳值(但不能确定是Alpha、PV还是Beta走法)
		        vlBest = vl;        // "vlBest"就是目前要返回的最佳值，可能超出Alpha-Beta边界
		        if (vl >= vlBeta) { // 找到一个Beta走法，即更好的走法
		          nHashFlag = HashItem.HASH_BETA;
		          mvBest = mv;      // Beta走法要保存到历史表
		          break;            // Beta截断
		        }
		        if (vl > vlAlpha) { // 找到一个PV走法,比V1更小的可以不用搜索
		          nHashFlag = HashItem.HASH_PV;
		          mvBest = mv;      // PV走法要保存到历史表
		          vlAlpha = vl;     // 缩小Alpha-Beta边界
		        }
		      }
		    }
		  }

		  // 5. 所有走法都搜索完了，把最佳走法(不能是Alpha走法)保存到历史表，返回最佳值
		  if (vlBest == -MATE_VALUE) {
		    // 如果是杀棋，就根据杀棋步数给出评价
		    return ChessPanel.pos.nDistance - MATE_VALUE;
		  }
		  // 记录到置换表
		  HashItem.RecordHash(nHashFlag, vlBest, nDepth, mvBest);
		  if (mvBest != 0) {
			  // 如果不是Alpha走法，就将最佳走法保存到历史表
			  SetBestMove(mvBest, nDepth);
		  }
		    return vlBest;
	}

	// 根节点的Alpha-Beta搜索过程
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

	// 迭代加深搜索过程
	/**
	 * 高级走法
	 */
	public static void SearchMain() {
		  int i, vl, nGenMoves;
		  int mvs[] = new int[Rule.MAX_GEN_MOVES];
		  long t;
		  // 初始化
		  Search.nHistoryTable = Arrays.copyOf(new int[65535], 65535);// 清空历史表
		  Search.mvKillers = Arrays.copyOf(new int[LIMIT_DEPTH][2], LIMIT_DEPTH*2);// 清空杀手走法表
		  Search.HashTable = Arrays.copyOf(new HashItem[HashItem.HASH_SIZE], HashItem.HASH_SIZE);// 清空置换表
		  
		  //把HashTable中的每个元素都实例化
		  for(int index=0; index<HashItem.HASH_SIZE; index++){
			  Search.HashTable[index] = new HashItem(); 
		  }
		  
		  t = System.currentTimeMillis();       // 初始化定时器
		  ChessPanel.pos.nDistance = 0; // 初始步数
		  
		// 搜索开局库
		  Search.mvResult = BookItemOp.SearchBook();
		  if (Search.mvResult != 0) {
		    ChessPanel.pos.MakeMove(Search.mvResult);
		    if (ChessPanel.pos.RepStatus(3) == 0) {
		    	ChessPanel.pos.UndoMakeMove();
		      return;
		    }
		    ChessPanel.pos.UndoMakeMove();
		  }

		  // 检查是否只有唯一走法
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

		  // 迭代加深过程
		  for (i = 1; i <= LIMIT_DEPTH; i ++) {
			vl = SearchRoot(i);
		    // 搜索到杀棋，就终止搜索
		    if (vl > WIN_VALUE || vl < -WIN_VALUE) {
		      break;
		    }
		    // 超过一秒，就终止搜索
		    if (System.currentTimeMillis() - t > CLOCKS_PER_SEC) {
		      break;
		    }
		  }
	}
	
	// 对最佳走法的处理
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
	 * 中级智商
	 */
	// 静态(Quiescence)搜索过程
	static int SearchQuiesc_1(int vlAlpha, int vlBeta) {
	  int i, nGenMoves;
	  int vl, vlBest;
	  int mvs[] = new int[Rule.MAX_GEN_MOVES];
	  // 一个静态搜索分为以下几个阶段

	  // 1. 检查重复局面
	  vl = ChessPanel.pos.RepStatus(1);
	  if (vl != 0) {
	    return ChessPanel.pos.RepValue(vl);
	  }

	  // 2. 到达极限深度就返回局面评价
	  if (ChessPanel.pos.nDistance == LIMIT_DEPTH) {
	    return ChessPanel.pos.Evaluate();
	  }

	  // 3. 初始化最佳值
	  vlBest = -MATE_VALUE; // 这样可以知道，是否一个走法都没走过(杀棋)

	  if (ChessPanel.pos.InCheck()) {
	    // 4. 如果被将军，则生成全部走法
	    nGenMoves = ChessPanel.pos.GenerateMoves(mvs, false);//默认false
	    
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
	    
	  } else {

	    // 5. 如果不被将军，先做局面评价
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

	    // 6. 如果局面评价没有截断，再生成吃子走法
	    nGenMoves = ChessPanel.pos.GenerateMoves(mvs, GEN_CAPTURE);
	    array.clear();//清除原走法
	    for(int item:mvs){//走法加入list
    		Integer k = new Integer(item);
    		array.add(k);
    	}
	    
	    Comparator<Integer> MvvLvaCmp = new MvvLvaComparator(); 
	    Collections.sort(array, MvvLvaCmp); //按Mvv/Lva值从大到小排序
	    
	    for(int t=0; t<array.size(); t++){//list转int,存到mvs中
	    	mvs[t] = array.get(t);//
	    }
	  }

	  // 7. 逐一走这些走法，并进行递归
	  for (i = 0; i < nGenMoves; i ++) {
	    if (ChessPanel.pos.MakeMove(mvs[i])) {
	      vl = -SearchQuiesc_1(-vlBeta, -vlAlpha);
	      ChessPanel.pos.UndoMakeMove();

	      // 8. 进行Alpha-Beta大小判断和截断
	      if (vl > vlBest) {    // 找到最佳值(但不能确定是Alpha、PV还是Beta走法)
	        vlBest = vl;        // "vlBest"就是目前要返回的最佳值，可能超出Alpha-Beta边界
	        if (vl >= vlBeta) { // 找到一个Beta走法
	          return vl;        // Beta截断
	        }
	        if (vl > vlAlpha) { // 找到一个PV走法
	          vlAlpha = vl;     // 缩小Alpha-Beta边界
	        }
	      }
	    }
	  }

	  // 9. 所有走法都搜索完了，返回最佳值
	  return vlBest == -MATE_VALUE ? ChessPanel.pos.nDistance - MATE_VALUE : vlBest;
	}

	
	/**
	 * @param vlAlpha
	 * @param vlBeta
	 * @param nDepth
	 * @param bNoNull
	 * @return
	 * 中级走棋法
	 */
	public static int SearchFull_1(int vlAlpha, int vlBeta, int nDepth, boolean bNoNull) {//默认false
		  int i, nGenMoves;
		  int vl, vlBest, mvBest;
		  int mvs[] = new int[Rule.MAX_GEN_MOVES];//走法的数组
		  // 一个Alpha-Beta完全搜索分为以下几个阶段

		  if (ChessPanel.pos.nDistance > 0) {
			    // 1. 到达水平线，则调用静态搜索(注意：由于空步裁剪，深度可能小于零)
			    if (nDepth <= 0) {
			      return SearchQuiesc_1(vlAlpha, vlBeta);
			    }

			    // 1-1. 检查重复局面(注意：不要在根节点检查，否则就没有走法了)
			    vl = ChessPanel.pos.RepStatus(1);//默认为1
			    if (vl != 0) {
			      return ChessPanel.pos.RepValue(vl);
			    }

			    // 1-2. 到达极限深度就返回局面评价
			    if (ChessPanel.pos.nDistance == LIMIT_DEPTH) {
			      return ChessPanel.pos.Evaluate();
			    }

			    // 1-3. 尝试空步裁剪(根节点的Beta值是"MATE_VALUE"，所以不可能发生空步裁剪)
			    if (!bNoNull && !ChessPanel.pos.InCheck() && ChessPanel.pos.NullOkay()) {
			    	ChessPanel.pos.NullMove();
			      vl = -SearchFull_1(-vlBeta, 1 - vlBeta, nDepth - NULL_DEPTH - 1, NO_NULL);
			      ChessPanel.pos.UndoNullMove();
			      if (vl >= vlBeta) {
			        return vl;
			      }
			    }
		  }

		  // 2. 初始化最佳值和最佳走法
		  vlBest = -MATE_VALUE; // 这样可以知道，是否一个走法都没走过(杀棋)
		  mvBest = 0;           // 这样可以知道，是否搜索到了Beta走法或PV走法，以便保存到历史表

		  // 3. 生成全部走法，并根据历史表排序,历史表本应该是从大到小排序
		  nGenMoves = ChessPanel.pos.GenerateMoves(mvs, false);
		  array.clear();//清除原走法
		    for(int item:mvs){//走法加入list
	    		Integer k = new Integer(item);
	    		array.add(k);
	    	}
		    
		   Comparator<Integer> HistoryCmp = new HistoryComparator(); 
		   Collections.sort(array, HistoryCmp); //按HistoryTable值从大到小排序
		   
		    for(int t=0; t<array.size(); t++){//list转int,存到mvs中
		    	mvs[t] = array.get(t);//
		    }

		  // 4. 逐一走这些走法，并进行递归
		   for (i = 0; i < nGenMoves; i ++) {
		    if (ChessPanel.pos.MakeMove(mvs[i])) {
		      vl = -SearchFull_1(-vlBeta, -vlAlpha, ChessPanel.pos.InCheck() ? nDepth : nDepth - 1, false);//极大极小值迭代
		      ChessPanel.pos.UndoMakeMove();

		      // 5. 进行Alpha-Beta大小判断和截断
		      if (vl > vlBest) {    // 找到最佳值(但不能确定是Alpha、PV还是Beta走法)
		        vlBest = vl;        // "vlBest"就是目前要返回的最佳值，可能超出Alpha-Beta边界
		        if (vl >= vlBeta) { // 找到一个Beta走法，即更好的走法
		          mvBest = mvs[i];  // Beta走法要保存到历史表
		          break;            // Beta截断
		        }
		        if (vl > vlAlpha) { // 找到一个PV走法,比V1更小的可以不用搜索
		          mvBest = mvs[i];  // PV走法要保存到历史表
		          vlAlpha = vl;     // 缩小Alpha-Beta边界
		        }
		      }
		    }
		  }

		  // 5. 所有走法都搜索完了，把最佳走法(不能是Alpha走法)保存到历史表，返回最佳值
		  if (vlBest == -MATE_VALUE) {
		    // 如果是杀棋，就根据杀棋步数给出评价
		    return ChessPanel.pos.nDistance - MATE_VALUE;
		  }
		  if (mvBest != 0) {
		    // 如果不是Alpha走法，就将最佳走法保存到历史表
		    Search.nHistoryTable[mvBest] += nDepth * nDepth;
		    if (ChessPanel.pos.nDistance == 0) {
		      // 搜索根节点时，总是有一个最佳走法(因为全窗口搜索不会超出边界)，将这个走法保存下来
		      Search.mvResult = mvBest;
		    }
		  }
		  return vlBest;
		}

	// 迭代加深搜索过程
	/**
	 * 中级走棋法
	 */
	public static void SearchMain_1() {
		  int i, vl;
		  long t;
		  // 初始化
		  Search.nHistoryTable = Arrays.copyOf(new int[65535], 65535);// 清空历史表
		  t = System.currentTimeMillis();       // 初始化定时器
		  ChessPanel.pos.nDistance = 0; // 初始步数

		  // 迭代加深过程
		  for (i = 1; i <= LIMIT_DEPTH; i ++) {
		    vl = SearchFull_1(-MATE_VALUE, MATE_VALUE, i, false);
		    // 搜索到杀棋，就终止搜索
		    if (vl > WIN_VALUE || vl < -WIN_VALUE) {
		      break;
		    }
		    // 超过一秒，就终止搜索
		    if (System.currentTimeMillis() - t > CLOCKS_PER_SEC) {
		      break;
		    }
		  }
	}
	
}
