package com.alphaele.panel;

import java.util.Arrays;

import com.alphaele.ai.Move;
import com.alphaele.ai.PieceValue;
import com.alphaele.ai.Search;
import com.alphaele.ai.Zobrist;
import com.alphaele.ai.ZobristStruct;
import java.io.*;
/*
 * �����Ǵ洢����״̬
 * ������а������������̵����Ժͷ���
 * ���磺��ǰ������飬��ǰ���ַ�
 * �Լ����ָı����״̬�ķ���
 * �����ӵĸ��ֻ�������
 * */
public class Position {
	public int sdPlayer;                   // �ֵ�˭�ߣ�0=�췽��1=�ڷ�
	public byte ucpcSquares[] = new byte[256];          // �����ϵ�����
	public int vlWhite;           // �졢��˫����������ֵ
	public int vlBlack;
	public int nDistance, nMoveNum;                  // ������ڵ�Ĳ�������ʷ�߷���
	public static final int MAX_MOVES = 256;     // ������ʷ�߷���
	public static final int DRAW_VALUE = 20;     // ����ʱ���صķ���(ȡ��ֵ)
	public static final int NULL_MARGIN = 400;   // �ղ��ü��������߽�
	public static boolean GEN_CAPTURE = true;
	
	Move mvsList[] = new Move[MAX_MOVES];  // ��ʷ�߷���Ϣ�б�
	public ZobristStruct zobr = new ZobristStruct();             // Zobrist
	
	public void ClearBoard() {         // �������
		sdPlayer = vlWhite = vlBlack = nDistance = 0;
		byte zero[] = new byte[256];
		ucpcSquares = Arrays.copyOf(zero, ucpcSquares.length);//ucpcSquares��������
		zobr.InitZero();
	}
	
	public void SetIrrev() {           // ���(��ʼ��)��ʷ�߷���Ϣ
		for(int i=0; i<MAX_MOVES; i++){
			mvsList[i] = new Move();
		}
	    mvsList[0].Set(0, 0, Checked(), zobr.dwKey);
	    nMoveNum = 1;
	}
	
	public void Startup() {            // ��ʼ������
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

	public void ChangeSide() {         // �������ӷ�
	    sdPlayer = 1 - sdPlayer;
	    zobr.Xor(Zobrist.Player);
	}
	  
	public void AddPiece(int sq, int pc) { // �������Ϸ�һö����
	   ucpcSquares[sq] = (byte)pc;
	// �췽�ӷ֣��ڷ�(ע��"cucvlPiecePos"ȡֵҪ�ߵ�)����
	    if (pc < 16) {
	      vlWhite += PieceValue.cucvlPiecePos[pc - 8][sq];
	      zobr.Xor(Zobrist.Table[pc - 8][sq]);
	    } else {
	      vlBlack += PieceValue.cucvlPiecePos[pc - 16][BoardOperation.SQUARE_FLIP(sq)];//�ڷ�
	      zobr.Xor(Zobrist.Table[pc - 9][sq]);
	    }
	}
	  
	public void DelPiece(int sq, int pc) {         // ������������һö����
		ucpcSquares[sq] = 0;
		if (pc < 16) {
		  vlWhite -= PieceValue.cucvlPiecePos[pc - 8][sq];
		  zobr.Xor(Zobrist.Table[pc - 8][sq]);
	    } else {
	      vlBlack -= PieceValue.cucvlPiecePos[pc - 16][BoardOperation.SQUARE_FLIP(sq)];
	      zobr.Xor(Zobrist.Table[pc - 9][sq]);
	    }
	}
	
	// �������ۺ���
	public int Evaluate(){      
		return (sdPlayer == 0 ? vlWhite - vlBlack : vlBlack - vlWhite) + Search.ADVANCED_VALUE;//�������ۺ�����Ŀǰ����˫����ֵ֮��
	}	
	
	public boolean InCheck(){      // �Ƿ񱻽���
	    return mvsList[nMoveNum - 1].ucbCheck;
	}
	
	public boolean Captured(){     // ��һ���Ƿ����
	    return mvsList[nMoveNum - 1].ucpcCaptured != 0;
	}
	
	// ��һ���������
	public int MovePiece(int mv){         // ��һ���������
		int sqSrc, sqDst, pc, pcCaptured;
		sqSrc = BoardOperation.SRC(mv);
		sqDst = BoardOperation.DST(mv);
		pcCaptured = ucpcSquares[sqDst];//��¼��ԭ���յ������
		if (pcCaptured != 0) {
		    DelPiece(sqDst, pcCaptured);
		}
		pc = ucpcSquares[sqSrc];
		DelPiece(sqSrc, pc);
		AddPiece(sqDst, pc);
		
		return pcCaptured;
	}
	
	// ������һ���������
	public void UndoMovePiece(int mv, int pcCaptured) {
		int sqSrc, sqDst, pc;
		sqSrc = BoardOperation.SRC(mv);
		sqDst = BoardOperation.DST(mv);
		pc = ucpcSquares[sqDst];
		DelPiece(sqDst, pc);//�����յ������
		AddPiece(sqSrc, pc);//�յ�����ӷŻ�ԭ��
		if (pcCaptured != 0) {
			AddPiece(sqDst, pcCaptured);//�ָ��յ������
		}
	}
	
	public void UndoMakeMove() { // ������һ����
	    nDistance --;
	    nMoveNum --;
	    ChangeSide();//�����巽
	    UndoMovePiece(mvsList[nMoveNum].wmv, mvsList[nMoveNum].ucpcCaptured);
	}
	
	public void NullMove() {                       // ��һ���ղ�
		long dwKey;
		dwKey = zobr.dwKey;
		ChangeSide();
		mvsList[nMoveNum].Set(0, 0, false, dwKey);
		nMoveNum ++;
		nDistance ++;
	}
	
	public void UndoNullMove() {                   // ������һ���ղ�
		nDistance --;
		nMoveNum --;
		ChangeSide();
	}	
	
	// ��һ����
	public boolean MakeMove(int mv) {//��һ������������int�����ã�����Ϊ1
		int pcCaptured;
		long dwKey;
		dwKey = zobr.dwKey;
		pcCaptured = MovePiece(mv);
		if (Checked()) {//���������
		    UndoMovePiece(mv, pcCaptured);//����һ���߷�
		    return false;
		}
		ChangeSide();
		mvsList[nMoveNum].Set(mv, pcCaptured, Checked(), dwKey);
		nMoveNum ++;
		nDistance ++;
	
		return true;
	}
	
	// ���������߷�
	public int GenerateMoves(int mvs[], boolean bCapture) {//mvs��һ���߷�����,bCaptureĬ��Ϊfalse
		  int i, j, nGenMoves, nDelta, sqSrc, sqDst;//sq��¼���������������е��±�
		  int pcSelfSide, pcOppSide, pcSrc, pcDst;//pc��¼�������ӵ�����
		  // ���������߷�����Ҫ�������¼������裺

		  nGenMoves = 0;//������ӵ������߷�
		  pcSelfSide = BoardOperation.SIDE_TAG(sdPlayer);
		  pcOppSide = BoardOperation.OPP_SIDE_TAG(sdPlayer);
		  for (sqSrc = 0; sqSrc < 256; sqSrc ++) {

		    // 1. �ҵ�һ���������ӣ����������жϣ�
		    pcSrc = ucpcSquares[sqSrc];
		    if ((pcSrc & pcSelfSide) == 0) {//�ҵ������Ӳ��ǵ�ǰ���ַ�����������ѭ��
		      continue;
		    }

		    // 2. ��������ȷ���߷�
		    switch (pcSrc - pcSelfSide) {
		    case Rule.PIECE_KING://����˧
		      for (i = 0; i < 4; i ++) {
		        sqDst = sqSrc + Rule.ccKingDelta[i];
		        if (!Rule.IN_FORT(sqDst)) {//������ھŹ�����
		          continue;
		        }
		        pcDst = ucpcSquares[sqDst];//�õ��յ㴦����������
		        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {//���߷����յ㴦�����Լ����ӣ�Ҳ������һ���߷�������
		          mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);//�����߷���������
		          nGenMoves ++;//�߷���������1
		         
		        }
		      }
		      break;
		    case Rule.PIECE_ADVISOR://ʿ����
		      for (i = 0; i < 4; i ++) {
		        sqDst = sqSrc + Rule.ccAdvisorDelta[i];
		        if (!Rule.IN_FORT(sqDst)) {//�ھŹ�����
		          continue;
		        }
		        pcDst = ucpcSquares[sqDst];
		        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
		          mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		          nGenMoves ++;
		         
		        }
		      }
		      break;
		    case Rule.PIECE_BISHOP://�࣬��
		      for (i = 0; i < 4; i ++) {
		        sqDst = sqSrc + Rule.ccAdvisorDelta[i];//�����˵��߷�������������Ƿ�ռ
		        if (!(Rule.IN_BOARD(sqDst) && Rule.HOME_HALF(sqDst, sdPlayer) && ucpcSquares[sqDst] == 0)) {//�������ڣ��಻�ܹ��ӣ���ѹ����
		          continue;
		        }
		        sqDst += Rule.ccAdvisorDelta[i];//sqDst�ټ���һ���˵��߷���������߷�
		        pcDst = ucpcSquares[sqDst];
		        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
		          mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		          nGenMoves ++;
		         
		        }
		      }
		      break;
		    case Rule.PIECE_KNIGHT://��
		      for (i = 0; i < 4; i ++) {
		        sqDst = sqSrc + Rule.ccKingDelta[i];//���ݽ����߷��������ж�ѹ���ȵ�λ��
		        if (ucpcSquares[sqDst] != 0) {//���ѹ���ȵ�λ�����ӣ�Ҳ���Ǳ�ѹ�����ˣ�����߷�����Ч
		          continue;
		        }
		        for (j = 0; j < 2; j ++) {//��һ����8����ŵ�8=4*2
		          sqDst = sqSrc + Rule.ccKnightDelta[i][j];
		          if (!Rule.IN_BOARD(sqDst)) {//���������ڣ���������ѭ��
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
		    case Rule.PIECE_ROOK://��
		      for (i = 0; i < 4; i ++) {//�����ĸ�����ѭ����ÿ������һ��һ�������ƣ�����ǰ����λ�����Ƿ���������
		        nDelta = Rule.ccKingDelta[i];
		        sqDst = sqSrc + nDelta;
		        while (Rule.IN_BOARD(sqDst)) {//��������
		          pcDst = ucpcSquares[sqDst];
		          if (pcDst == 0) {//û������
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
		          sqDst += nDelta;//������һ��
		        }
		      }
		      break;
		    case Rule.PIECE_CANNON://��
		      for (i = 0; i < 4; i ++) {
		        nDelta = Rule.ccKingDelta[i];
		        sqDst = sqSrc + nDelta;
		        while (Rule.IN_BOARD(sqDst)) {//��ͨ���ƶ��߷�
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
		        while (Rule.IN_BOARD(sqDst)) {//����ʱ���߷�
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
		    case Rule.PIECE_PAWN://������
		      sqDst = Rule.SQUARE_FORWARD(sqSrc, sdPlayer);
		      if (Rule.IN_BOARD(sqDst)) {//û���ӵ��߷���ֻ����ǰ
		        pcDst = ucpcSquares[sqDst];
		        if (bCapture ? (pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
		          mvs[nGenMoves] = BoardOperation.MOVE(sqSrc, sqDst);
		          nGenMoves ++;
		          
		        }
		      }
		      if (Rule.AWAY_HALF(sqSrc, sdPlayer)) {//������˺ӣ������������ƶ�
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
	//���߷������TXT�ļ�
	public void OutputFile(int pc ,int sq) 
	{
		try{
			String str = "E:\\���ļ�\\output.txt";
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
	// �ж��߷��Ƿ����
	public boolean LegalMove(int mv){
		  int sqSrc, sqDst, sqPin;
		  int pcSelfSide, pcSrc, pcDst, nDelta;
		  // �ж��߷��Ƿ�Ϸ�����Ҫ�������µ��жϹ��̣�

		  // 1. �ж���ʼ���Ƿ����Լ�������
		  sqSrc = BoardOperation.SRC(mv);
		  pcSrc = ucpcSquares[sqSrc];
		  pcSelfSide = BoardOperation.SIDE_TAG(sdPlayer);
		  if ((pcSrc & pcSelfSide) == 0) {
		    return false;
		  }

		  // 2. �ж�Ŀ����Ƿ����Լ�������
		  sqDst = BoardOperation.DST(mv);
		  pcDst = ucpcSquares[sqDst];
		  if ((pcDst & pcSelfSide) != 0) {
		    return false;
		  }

		  // 3. �������ӵ����ͼ���߷��Ƿ����
		  switch (pcSrc - pcSelfSide) {
		  case Rule.PIECE_KING://����˧
		    return Rule.IN_FORT(sqDst) && Rule.KING_SPAN(sqSrc, sqDst);//�ھŹ����в��ҷ��Ͻ��߷�
		  case Rule.PIECE_ADVISOR://��
		    return Rule.IN_FORT(sqDst) && Rule.ADVISOR_SPAN(sqSrc, sqDst);
		  case Rule.PIECE_BISHOP://��
		    return Rule.SAME_HALF(sqSrc, sqDst) && Rule.BISHOP_SPAN(sqSrc, sqDst) &&
		        ucpcSquares[Rule.BISHOP_PIN(sqSrc, sqDst)] == 0;//û�б�ѹ����
		  case Rule.PIECE_KNIGHT://��
		    sqPin = Rule.KNIGHT_PIN(sqSrc, sqDst);
		    return sqPin != sqSrc && ucpcSquares[sqPin] == 0;//��ʼλ��û�б�ѹ����
		  case Rule.PIECE_ROOK://��
		  case Rule.PIECE_CANNON://��
		    if (Rule.SAME_RANK(sqSrc, sqDst)) {//ͬһ��
		      nDelta = (sqDst < sqSrc ? -1 : 1);
		    } else if (Rule.SAME_FILE(sqSrc, sqDst)) {//ͬһ��
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
		  case Rule.PIECE_PAWN://��
		    if (Rule.AWAY_HALF(sqDst, sdPlayer) && (sqDst == sqSrc - 1 || sqDst == sqSrc + 1)) {
		      return true;
		    }
		    return sqDst == Rule.SQUARE_FORWARD(sqSrc, sdPlayer);
		  default:
		    return false;
		  }
		}
	
	// �ж��Ƿ񱻽���
	public boolean Checked(){
	  int i, j, sqSrc, sqDst;
	  int pcSelfSide, pcOppSide, pcDst, nDelta;
	  pcSelfSide = BoardOperation.SIDE_TAG(sdPlayer);
	  pcOppSide = BoardOperation.OPP_SIDE_TAG(sdPlayer);
	  // �ҵ������ϵ�˧(��)�����������жϣ�

	  for (sqSrc = 0; sqSrc < 256; sqSrc ++) {
	    if (ucpcSquares[sqSrc] != pcSelfSide + Rule.PIECE_KING) {
	      continue;
	    }
	    // 1. �ж��Ƿ񱻶Է��ı�(��)����
	    if (ucpcSquares[Rule.SQUARE_FORWARD(sqSrc, sdPlayer)] == pcOppSide + Rule.PIECE_PAWN) {
	      return true;
	    }
	    for (nDelta = -1; nDelta <= 1; nDelta += 2) {
	      if (ucpcSquares[sqSrc + nDelta] == pcOppSide + Rule.PIECE_PAWN) {
	        return true;
	      }
	    }
	    
	    // 2. �ж��Ƿ񱻶Է�������(����(ʿ)�Ĳ�����������)
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
	    
	    // 3. �ж��Ƿ񱻶Է��ĳ����ڽ���(������˧����)
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
	
	// �ж��Ƿ�ɱ
	public boolean IsMate() {
	  int i, nGenMoveNum, pcCaptured;
	  int mvs[] = new int[Rule.MAX_GEN_MOVES];//�߷�������
	  
	  nGenMoveNum = GenerateMoves(mvs,false);//����һ�����߷���Ŀ,û�еڶ���������Ĭ�ϴ�false
	  for (i = 0; i < nGenMoveNum; i ++) {//�ý���˧��ÿ�����ӵ�ÿ���߷�������һ���ж��Ƿ񱻽�����
	    pcCaptured = MovePiece(mvs[i]);
	    if (!Checked()) {//���û����������Ŀǰ�ͻ�ûɱ�壬�����߷�������false
	      UndoMovePiece(mvs[i], pcCaptured);
	      return false;
	    } else {//�������˾ͳ�����һ���߷�
	      UndoMovePiece(mvs[i], pcCaptured);
	    }
	  }
	  return true;
	}
	
	public int DrawValue(){                 // �����ֵ
		return (nDistance & 1) == 0 ? -DRAW_VALUE : DRAW_VALUE;
	}
	
	// ����ظ�����,Ĭ��ֵΪ1
	public int RepStatus(int nRecur){
	  boolean bSelfSide, bPerpCheck, bOppPerpCheck;
	  Move lpmvs;//ԭ����ָ��

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
	
	public int RepValue(int nRepStatus){        // �ظ������ֵ
	    int vlReturn;
	    vlReturn = ((nRepStatus & 2) == 0 ? 0 : nDistance - Search.BAN_VALUE) +
	            ((nRepStatus & 4) == 0 ? 0 : Search.BAN_VALUE - nDistance);
	    return vlReturn == 0 ? DrawValue() : vlReturn;
	}
	
	public boolean NullOkay(){                 // �ж��Ƿ�����ղ��ü�
	    return (sdPlayer == 0 ? vlWhite : vlBlack) > NULL_MARGIN;
	}
	
	// �Ծ��澵��
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
