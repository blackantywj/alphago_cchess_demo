package com.alphaele.panel;

import java.util.Arrays;

import com.alphaele.ai.Move;
import com.alphaele.ai.PieceValue;
import com.alphaele.ai.Search;
import com.alphaele.ai.Zobrist;
import com.alphaele.ai.ZobristStruct;
import java.io.*;
/*
 * 本类是存储局面状态
 * 这个类中包含了整个棋盘的属性和方法
 * 比如：当前棋局数组，当前落字方
 * 以及各种改变棋局状态的方法
 * 对棋子的各种基本操作
 * */
public class Position {
	public int sdPlayer;                   // 轮到谁走，0=红方，1=黑方
	public byte ucpcSquares[] = new byte[256];          // 棋盘上的棋子
	public int vlWhite;           // 红、黑双方的子力价值
	public int vlBlack;
	public int nDistance, nMoveNum;                  // 距离根节点的步数，历史走法数
	public static final int MAX_MOVES = 256;     // 最大的历史走法数
	public static final int DRAW_VALUE = 20;     // 和棋时返回的分数(取负值)
	public static final int NULL_MARGIN = 400;   // 空步裁剪的子力边界
	public static boolean GEN_CAPTURE = true;
	
	Move mvsList[] = new Move[MAX_MOVES];  // 历史走法信息列表
	public ZobristStruct zobr = new ZobristStruct();             // Zobrist
	
	public void ClearBoard() {         // 清空棋盘
		sdPlayer = vlWhite = vlBlack = nDistance = 0;
		byte zero[] = new byte[256];
		ucpcSquares = Arrays.copyOf(zero, ucpcSquares.length);//ucpcSquares数组清零
		zobr.InitZero();
	}
	
	public void SetIrrev() {           // 清空(初始化)历史走法信息
		for(int i=0; i<MAX_MOVES; i++){
			mvsList[i] = new Move();
		}
	    mvsList[0].Set(0, 0, Checked(), zobr.dwKey);
	    nMoveNum = 1;
	}
	
	public void Startup() {            // 初始化棋盘
		int sq, pc;
		ClearBoard();
	    for (sq = 0; sq < 256; sq ++) {
	        pc = BoardOperation.cucpcStartup[sq];
	        if (pc != 0) {
	          AddPiece(sq, pc);
	        }
	   }
	    SetIrrev();
	}

	public void ChangeSide() {         // 交换走子方
	    sdPlayer = 1 - sdPlayer;
	    zobr.Xor(Zobrist.Player);
	}
	  
	public void AddPiece(int sq, int pc) { // 在棋盘上放一枚棋子
	   ucpcSquares[sq] = (byte)pc;
	// 红方加分，黑方(注意"cucvlPiecePos"取值要颠倒)减分
	    if (pc < 16) {
	      vlWhite += PieceValue.cucvlPiecePos[pc - 8][sq];
	      zobr.Xor(Zobrist.Table[pc - 8][sq]);
	    } else {
	      vlBlack += PieceValue.cucvlPiecePos[pc - 16][BoardOperation.SQUARE_FLIP(sq)];//黑方
	      zobr.Xor(Zobrist.Table[pc - 9][sq]);
	    }
	}
	  
	public void DelPiece(int sq, int pc) {         // 从棋盘上拿走一枚棋子
		ucpcSquares[sq] = 0;
		if (pc < 16) {
		  vlWhite -= PieceValue.cucvlPiecePos[pc - 8][sq];
		  zobr.Xor(Zobrist.Table[pc - 8][sq]);
	    } else {
	      vlBlack -= PieceValue.cucvlPiecePos[pc - 16][BoardOperation.SQUARE_FLIP(sq)];
	      zobr.Xor(Zobrist.Table[pc - 9][sq]);
	    }
	}
	
	// 局面评价函数
	public int Evaluate(){      
		return (sdPlayer == 0 ? vlWhite - vlBlack : vlBlack - vlWhite) + Search.ADVANCED_VALUE;//局面评价函数，目前就是双方分值之差
	}	
	
	public boolean InCheck(){      // 是否被将军
	    return mvsList[nMoveNum - 1].ucbCheck;
	}
	
	public boolean Captured(){     // 上一步是否吃子
	    return mvsList[nMoveNum - 1].ucpcCaptured != 0;
	}
	
	// 搬一步棋的棋子
	public int MovePiece(int mv){         // 搬一步棋的棋子
		int sqSrc, sqDst, pc, pcCaptured;
		sqSrc = BoardOperation.SRC(mv);
		sqDst = BoardOperation.DST(mv);
		pcCaptured = ucpcSquares[sqDst];//记录下原来终点的棋子
		if (pcCaptured != 0) {
		    DelPiece(sqDst, pcCaptured);
		}
		pc = ucpcSquares[sqSrc];
		DelPiece(sqSrc, pc);
		AddPiece(sqDst, pc);
		
		return pcCaptured;
	}
	
	// 撤消搬一步棋的棋子
	public void UndoMovePiece(int mv, int pcCaptured) {
		int sqSrc, sqDst, pc;
		sqSrc = BoardOperation.SRC(mv);
		sqDst = BoardOperation.DST(mv);
		pc = ucpcSquares[sqDst];
		DelPiece(sqDst, pc);//撤销终点的棋子
		AddPiece(sqSrc, pc);//终点的棋子放回原处
		if (pcCaptured != 0) {
			AddPiece(sqDst, pcCaptured);//恢复终点的棋子
		}
	}
	
	public void UndoMakeMove() { // 撤消走一步棋
	    nDistance --;
	    nMoveNum --;
	    ChangeSide();//交换棋方
	    UndoMovePiece(mvsList[nMoveNum].wmv, mvsList[nMoveNum].ucpcCaptured);
	}
	
	public void NullMove() {                       // 走一步空步
		long dwKey;
		dwKey = zobr.dwKey;
		ChangeSide();
		mvsList[nMoveNum].Set(0, 0, false, dwKey);
		nMoveNum ++;
		nDistance ++;
	}
	
	public void UndoNullMove() {                   // 撤消走一步空步
		nDistance --;
		nMoveNum --;
		ChangeSide();
	}	
	
	// 走一步棋
	public boolean MakeMove(int mv) {//用一个数组来传递int的引用，长度为1
		int pcCaptured;
		long dwKey;
		dwKey = zobr.dwKey;
		pcCaptured = MovePiece(mv);
		if (Checked()) {//如果被将军
		    UndoMovePiece(mv, pcCaptured);//撤销一步走法
		    return false;
		}
		ChangeSide();
		mvsList[nMoveNum].Set(mv, pcCaptured, Checked(), dwKey);
		nMoveNum ++;
		nDistance ++;
	
		return true;
	}
	
	// 生成所有走法
	public int GenerateMoves(int mvs[], boolean bCapture) {//mvs是一个走法数组,bCapture默认为false
		  int i, j, nGenMoves, nDelta, sqSrc, sqDst;//sq记录的是棋子子数组中的下标
		  int pcSelfSide, pcOppSide, pcSrc, pcDst;//pc记录的是棋子的种类
		  // 生成所有走法，需要经过以下几个步骤：

		  nGenMoves = 0;//计算该子的所有走法
		  pcSelfSide = BoardOperation.SIDE_TAG(sdPlayer);
		  pcOppSide = BoardOperation.OPP_SIDE_TAG(sdPlayer);
		  for (sqSrc = 0; sqSrc < 256; sqSrc ++) {

		    // 1. 找到一个本方棋子，再做以下判断：
		    pcSrc = ucpcSquares[sqSrc];
		    if ((pcSrc & pcSelfSide) == 0) {//找到的棋子不是当前落字方，跳过本次循环
		      continue;
		    }

		    // 2. 根据棋子确定走法
		    switch (pcSrc - pcSelfSide) {
		    case Rule.PIECE_KING://将，帅
		      for (i = 0; i < 4; i ++) {
		        sqDst = sqSrc + Rule.ccKingDelta[i];
		        if (!Rule.IN_FORT(sqDst)) {//如果不在九宫格中
		          continue;
		        }
		        pcDst = ucpcSquares[sqDst];//得到终点处的棋子类型
		        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {//该走法的终点处不是自己的子，也就是这一种走法可以走
		          mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);//将该走法加入数组
		          nGenMoves ++;//走法数量增加1
		         
		        }
		      }
		      break;
		    case Rule.PIECE_ADVISOR://士，仕
		      for (i = 0; i < 4; i ++) {
		        sqDst = sqSrc + Rule.ccAdvisorDelta[i];
		        if (!Rule.IN_FORT(sqDst)) {//在九宫格中
		          continue;
		        }
		        pcDst = ucpcSquares[sqDst];
		        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
		          mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		          nGenMoves ++;
		         
		        }
		      }
		      break;
		    case Rule.PIECE_BISHOP://相，象
		      for (i = 0; i < 4; i ++) {
		        sqDst = sqSrc + Rule.ccAdvisorDelta[i];//按照仕的走法可以算出相心是否被占
		        if (!(Rule.IN_BOARD(sqDst) && Rule.HOME_HALF(sqDst, sdPlayer) && ucpcSquares[sqDst] == 0)) {//在棋盘内，相不能过河，被压象心
		          continue;
		        }
		        sqDst += Rule.ccAdvisorDelta[i];//sqDst再加上一次仕的走法就是象的走法
		        pcDst = ucpcSquares[sqDst];
		        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
		          mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		          nGenMoves ++;
		         
		        }
		      }
		      break;
		    case Rule.PIECE_KNIGHT://马
		      for (i = 0; i < 4; i ++) {
		        sqDst = sqSrc + Rule.ccKingDelta[i];//根据将的走法，可以判定压马腿的位置
		        if (ucpcSquares[sqDst] != 0) {//如果压马腿的位置有子，也就是被压马腿了，这个走法就无效
		          continue;
		        }
		        for (j = 0; j < 2; j ++) {//马一共有8个落脚点8=4*2
		          sqDst = sqSrc + Rule.ccKnightDelta[i][j];
		          if (!Rule.IN_BOARD(sqDst)) {//不在棋盘内，跳过本次循环
		            continue;
		          }
		          pcDst = ucpcSquares[sqDst];
		          if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
		            mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		            nGenMoves ++;
		           
		          }
		        }
		      }
		      break;
		    case Rule.PIECE_ROOK://车
		      for (i = 0; i < 4; i ++) {//车的四个方向循环，每个方向都一格一格向外推，看车前进的位置上是否有子阻拦
		        nDelta = Rule.ccKingDelta[i];
		        sqDst = sqSrc + nDelta;
		        while (Rule.IN_BOARD(sqDst)) {//在棋盘内
		          pcDst = ucpcSquares[sqDst];
		          if (pcDst == 0) {//没子阻拦
		        	  if (!bCapture) {
		        		  mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		        		  nGenMoves ++;
		        	  }
		          } else {
		            if ((pcDst & pcOppSide) != 0) {
		              mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		              nGenMoves ++;
		             
		            }
		            break;
		          }
		          sqDst += nDelta;//往外推一格
		        }
		      }
		      break;
		    case Rule.PIECE_CANNON://炮
		      for (i = 0; i < 4; i ++) {
		        nDelta = Rule.ccKingDelta[i];
		        sqDst = sqSrc + nDelta;
		        while (Rule.IN_BOARD(sqDst)) {//普通的移动走法
		          pcDst = ucpcSquares[sqDst];
		          if (pcDst == 0) {
		        	  if (!bCapture) {
		        		  mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		        		  nGenMoves ++;
		        		 
		        	  }
		          } else {
		            break;
		          }
		          sqDst += nDelta;
		        }
		        sqDst += nDelta;
		        while (Rule.IN_BOARD(sqDst)) {//吃子时的走法
		          pcDst = ucpcSquares[sqDst];
		          if (pcDst != 0) {
		            if ((pcDst & pcOppSide) != 0) {
		              mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		              nGenMoves ++;
		            
		            }
		            break;
		          }
		          sqDst += nDelta;
		        }
		      }
		      break;
		    case Rule.PIECE_PAWN://兵，卒
		      sqDst = Rule.SQUARE_FORWARD(sqSrc, sdPlayer);
		      if (Rule.IN_BOARD(sqDst)) {//没过河的走法，只能向前
		        pcDst = ucpcSquares[sqDst];
		        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
		          mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		          nGenMoves ++;
		          
		        }
		      }
		      if (Rule.AWAY_HALF(sqSrc, sdPlayer)) {//如果过了河，还可以左右移动
		        for (nDelta = -1; nDelta <= 1; nDelta += 2) {
		          sqDst = sqSrc + nDelta;
		          if (Rule.IN_BOARD(sqDst)) {
		            pcDst = ucpcSquares[sqDst];
		            if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
		              mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		              nGenMoves ++;
		              
		            }
		          }
		        }
		      }
		      break;
		    }
		  }
		  return nGenMoves;
		}
	//将走法输出至TXT文件
	public void OutputFile(int pc ,int sq) 
	{
		try{
			String str = "E:\\杂文件\\output.txt";
			File file = new File(str);
			String line = System.getProperty("line.separator");
			FileWriter wout = new FileWriter(file,true);
			wout.write(pc+"     ");
			wout.write(sq);
			wout.write(line);
			wout.flush();
			wout.close();
		}catch(IOException e){
			e.getStackTrace();
		}
	}
	// 判断走法是否合理
	public boolean LegalMove(int mv){
		  int sqSrc, sqDst, sqPin;
		  int pcSelfSide, pcSrc, pcDst, nDelta;
		  // 判断走法是否合法，需要经过以下的判断过程：

		  // 1. 判断起始格是否有自己的棋子
		  sqSrc = BoardOperation.SRC(mv);
		  pcSrc = ucpcSquares[sqSrc];
		  pcSelfSide = BoardOperation.SIDE_TAG(sdPlayer);
		  if ((pcSrc & pcSelfSide) == 0) {
		    return false;
		  }

		  // 2. 判断目标格是否有自己的棋子
		  sqDst = BoardOperation.DST(mv);
		  pcDst = ucpcSquares[sqDst];
		  if ((pcDst & pcSelfSide) != 0) {
		    return false;
		  }

		  // 3. 根据棋子的类型检查走法是否合理
		  switch (pcSrc - pcSelfSide) {
		  case Rule.PIECE_KING://将，帅
		    return Rule.IN_FORT(sqDst) && Rule.KING_SPAN(sqSrc, sqDst);//在九宫格中并且符合将走法
		  case Rule.PIECE_ADVISOR://仕
		    return Rule.IN_FORT(sqDst) && Rule.ADVISOR_SPAN(sqSrc, sqDst);
		  case Rule.PIECE_BISHOP://象
		    return Rule.SAME_HALF(sqSrc, sqDst) && Rule.BISHOP_SPAN(sqSrc, sqDst) &&
		        ucpcSquares[Rule.BISHOP_PIN(sqSrc, sqDst)] == 0;//没有被压相心
		  case Rule.PIECE_KNIGHT://马
		    sqPin = Rule.KNIGHT_PIN(sqSrc, sqDst);
		    return sqPin != sqSrc && ucpcSquares[sqPin] == 0;//起始位置没有被压马腿
		  case Rule.PIECE_ROOK://车
		  case Rule.PIECE_CANNON://炮
		    if (Rule.SAME_RANK(sqSrc, sqDst)) {//同一行
		      nDelta = (sqDst < sqSrc ? -1 : 1);
		    } else if (Rule.SAME_FILE(sqSrc, sqDst)) {//同一列
		      nDelta = (sqDst < sqSrc ? -16 : 16);
		    } else {
		      return false;
		    }
		    sqPin = sqSrc + nDelta;
		    while (sqPin != sqDst && ucpcSquares[sqPin] == 0) {
		      sqPin += nDelta;
		    }
		    if (sqPin == sqDst) {
		      return pcDst == 0 || pcSrc - pcSelfSide == Rule.PIECE_ROOK;
		    } else if (pcDst != 0 && pcSrc - pcSelfSide == Rule.PIECE_CANNON) {
		      sqPin += nDelta;
		      while (sqPin != sqDst && ucpcSquares[sqPin] == 0) {
		        sqPin += nDelta;
		      }
		      return sqPin == sqDst;
		    } else {
		      return false;
		    }
		  case Rule.PIECE_PAWN://兵
		    if (Rule.AWAY_HALF(sqDst, sdPlayer) && (sqDst == sqSrc - 1 || sqDst == sqSrc + 1)) {
		      return true;
		    }
		    return sqDst == Rule.SQUARE_FORWARD(sqSrc, sdPlayer);
		  default:
		    return false;
		  }
		}
	
	// 判断是否被将军
	public boolean Checked(){
	  int i, j, sqSrc, sqDst;
	  int pcSelfSide, pcOppSide, pcDst, nDelta;
	  pcSelfSide = BoardOperation.SIDE_TAG(sdPlayer);
	  pcOppSide = BoardOperation.OPP_SIDE_TAG(sdPlayer);
	  // 找到棋盘上的帅(将)，再做以下判断：

	  for (sqSrc = 0; sqSrc < 256; sqSrc ++) {
	    if (ucpcSquares[sqSrc] != pcSelfSide + Rule.PIECE_KING) {
	      continue;
	    }
	    // 1. 判断是否被对方的兵(卒)将军
	    if (ucpcSquares[Rule.SQUARE_FORWARD(sqSrc, sdPlayer)] == pcOppSide + Rule.PIECE_PAWN) {
	      return true;
	    }
	    for (nDelta = -1; nDelta <= 1; nDelta += 2) {
	      if (ucpcSquares[sqSrc + nDelta] == pcOppSide + Rule.PIECE_PAWN) {
	        return true;
	      }
	    }
	    
	    // 2. 判断是否被对方的马将军(以仕(士)的步长当作马腿)
	    for (i = 0; i < 4; i ++) {
	      if (ucpcSquares[sqSrc + Rule.ccAdvisorDelta[i]] != 0) {
	        continue;
	      }
	      for (j = 0; j < 2; j ++) {
	        pcDst = ucpcSquares[sqSrc + Rule.ccKnightCheckDelta[i][j]];
	        if (pcDst == pcOppSide + Rule.PIECE_KNIGHT) {
	          return true;
	        }
	      }
	    }
	    
	    // 3. 判断是否被对方的车或炮将军(包括将帅对脸)
	    for (i = 0; i < 4; i ++) {
	      nDelta = Rule.ccKingDelta[i];
	      sqDst = sqSrc + nDelta;
	      while (Rule.IN_BOARD(sqDst)) {
	        pcDst = ucpcSquares[sqDst];
	        if (pcDst != 0) {
	          if (pcDst == pcOppSide + Rule.PIECE_ROOK || pcDst == pcOppSide + Rule.PIECE_KING) {
	            return true;
	          }
	          break;
	        }
	        sqDst += nDelta;
	      }
	      sqDst += nDelta;
	      while (Rule.IN_BOARD(sqDst)) {
	        pcDst = ucpcSquares[sqDst];
	        if (pcDst != 0) {
	          if (pcDst == pcOppSide + Rule.PIECE_CANNON) {
	            return true;
	          }
	          break;
	        }
	        sqDst += nDelta;
	      }
	    }
	    return false;
	  }
	  return false;
	}
	
	// 判断是否被杀
	public boolean IsMate() {
	  int i, nGenMoveNum, pcCaptured;
	  int mvs[] = new int[Rule.MAX_GEN_MOVES];//走法的数组
	  
	  nGenMoveNum = GenerateMoves(mvs,false);//返回一共的走法数目,没有第二个参数，默认传false
	  for (i = 0; i < nGenMoveNum; i ++) {//让将，帅把每种棋子的每种走法都尝试一遍判断是否被将军了
	    pcCaptured = MovePiece(mvs[i]);
	    if (!Checked()) {//如果没被将军，那目前就还没杀棋，撤销走法，返回false
	      UndoMovePiece(mvs[i], pcCaptured);
	      return false;
	    } else {//被将军了就撤销这一步走法
	      UndoMovePiece(mvs[i], pcCaptured);
	    }
	  }
	  return true;
	}
	
	public int DrawValue(){                 // 和棋分值
		return (nDistance & 1) == 0 ? -DRAW_VALUE : DRAW_VALUE;
	}
	
	// 检测重复局面,默认值为1
	public int RepStatus(int nRecur){
	  boolean bSelfSide, bPerpCheck, bOppPerpCheck;
	  Move lpmvs;//原本是指针

	  bSelfSide = false;
	  bPerpCheck = bOppPerpCheck = true;
	  int index = nMoveNum - 1;
	  lpmvs = mvsList[index];
	  while (lpmvs.wmv != 0 && lpmvs.ucpcCaptured == 0) {
	    if (bSelfSide) {
	      bPerpCheck = bPerpCheck && lpmvs.ucbCheck;
	      if (lpmvs.dwKey == zobr.dwKey) {
	        nRecur --;
	        if (nRecur == 0) {
	          return 1 + (bPerpCheck ? 2 : 0) + (bOppPerpCheck ? 4 : 0);
	        }
	      }
	    } else {
	      bOppPerpCheck = bOppPerpCheck && lpmvs.ucbCheck;
	    }
	    bSelfSide = !bSelfSide;
	    index --;
	    lpmvs = mvsList[index];
	  }
	  return 0;
	}
	
	public int RepValue(int nRepStatus){        // 重复局面分值
	    int vlReturn;
	    vlReturn = ((nRepStatus & 2) == 0 ? 0 : nDistance - Search.BAN_VALUE) +
	            ((nRepStatus & 4) == 0 ? 0 : Search.BAN_VALUE - nDistance);
	    return vlReturn == 0 ? DrawValue() : vlReturn;
	}
	
	public boolean NullOkay(){                 // 判断是否允许空步裁剪
	    return (sdPlayer == 0 ? vlWhite : vlBlack) > NULL_MARGIN;
	}
	
	// 对局面镜像
	public void Mirror(Position posMirror){
	  int sq, pc;
	  posMirror.ClearBoard();
	  for (sq = 0; sq < 256; sq ++) {
	    pc = ucpcSquares[sq];
	    if (pc != 0) {
	      posMirror.AddPiece(Rule.MIRROR_SQUARE(sq), pc);
	    }
	  }
	  if (sdPlayer == 1) {
	    posMirror.ChangeSide();
	  }
	  posMirror.SetIrrev();
	}
}
