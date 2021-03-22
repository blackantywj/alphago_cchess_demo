package com.alphaele.panel;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.alphaele.ai.BookItemOp;
import com.alphaele.ai.RC4;
import com.alphaele.ai.Search;
import com.alphaele.ai.Zobrist;
import com.alphaele.ai.ZobristStruct;
import com.alphaele.main.ChineseChess;
import com.alphaele.serialPort.Serial_Port;
import com.alphaele.util.FontUtil;
import com.alphaele.util.MusicUtil;

/*
 * ����Ӧ�ô�Ż�ͼ������ͼƬ���غ�������Ҫ��������
 * Xqwl�ṹ��ı������Դ�ŵ�����
 * */

public class ChessPanel extends JPanel{
	public static Image board = null;
	
	public static int sqSelected;                       // ѡ�еĸ��ӣ���һ����
	public static int mvLast;
	public static int str;
	
	public static boolean bFlipped = false, bGameOver;    // �Ƿ�ת����
	public static boolean newcommer = false;		//����Ƿ�ai�����ܵȼ�
	public static boolean nomusic = false;			//�������ֿ���
	public static boolean trainingMode = false;
	
	public static Image bmpBoard, bmpSelected, bmpPieces[] = new Image[24]; // ��ԴͼƬ
	
	public static final int SQUARE_SIZE = 56;		//һ�����ӵĿ��
	public static final int BOARD_EDGE = 14;		//�߽���
	public static final int BOARD_WIDTH = BOARD_EDGE + SQUARE_SIZE * 9 + BOARD_EDGE;	//���̿�ȵ��ڣ���߽��+9*����+�ұ߽��
	public static final int BOARD_HEIGHT = BOARD_EDGE + SQUARE_SIZE * 10 + BOARD_EDGE;	//���̸߶ȵ��ڣ��ϱ߽��+10*����+�±߽��
	
	// ���̷�Χ
	public static final int RANK_TOP = 3;			//�����±��3��ʼ
	public static final int RANK_BOTTOM = 12;		//�ײ��±��12��ʼ
	public static final int FILE_LEFT = 3;		//����±��3��ʼ
	public static final int FILE_RIGHT = 11;		//�ұ��±��11��ʼ
	
	public static RC4 rc4 = new RC4(new byte[1]);
	
	//��������ͼƬ
	static{
		try {
			bmpBoard = ImageIO.read(new File("src/boards/wood.gif"));//����
			bmpSelected = ImageIO.read(new File("src/wood/oos.gif"));//ѡ���
			
			//��ɫ
			bmpPieces[8] = ImageIO.read(new File("src/wood/rk.gif"));
			bmpPieces[9] = ImageIO.read(new File("src/wood/ra.gif"));
			bmpPieces[10] = ImageIO.read(new File("src/wood/rb.gif"));
			bmpPieces[11] = ImageIO.read(new File("src/wood/rn.gif"));
			bmpPieces[12] = ImageIO.read(new File("src/wood/rr.gif"));
			bmpPieces[13] = ImageIO.read(new File("src/wood/rc.gif"));
			bmpPieces[14] = ImageIO.read(new File("src/wood/rp.gif"));
			
			//��ɫ
			bmpPieces[16] = ImageIO.read(new File("src/wood/bk.gif"));
			bmpPieces[17] = ImageIO.read(new File("src/wood/ba.gif"));
			bmpPieces[18] = ImageIO.read(new File("src/wood/bb.gif"));
			bmpPieces[19] = ImageIO.read(new File("src/wood/bn.gif"));
			bmpPieces[20] = ImageIO.read(new File("src/wood/br.gif"));
			bmpPieces[21] = ImageIO.read(new File("src/wood/bc.gif"));
			bmpPieces[22] = ImageIO.read(new File("src/wood/bp.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Position pos = new Position();//��¼���������̵Ľ���״̬
	
	public static boolean DRAW_SELECTED = true;//����һ������
	
	//���췽���г�ʼ������
	public ChessPanel(){
		InitZobrist();
		bFlipped = false;
		Startup();
		
		//������¼�
		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				int x, y;
				Graphics g =  getGraphics();//�õ�һ������
			    x = FILE_LEFT + (e.getX() - BOARD_EDGE) / SQUARE_SIZE;//��õ��������
			    y = RANK_TOP + (e.getY() - BOARD_EDGE) / SQUARE_SIZE;
			    if (x >= FILE_LEFT && x <= FILE_RIGHT && y >= RANK_TOP && y <= RANK_BOTTOM) {//�����������
			      ClickSquare(g, BoardOperation.COORD_XY(x, y));
			    }
			    repaint();
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
		});
	}
	
	// �������ʼ������
	public static void DrawBoard(Graphics g) {
	  int x, y, xx, yy, sq, pc;
	  // ������
	  g.drawImage(bmpBoard ,0 ,0 ,null);
	  // ������
	  for (x = FILE_LEFT; x <= FILE_RIGHT; x ++) {//���̴�����
	    for (y = RANK_TOP; y <= RANK_BOTTOM; y ++) {//���̴��ϵ���
	      if (bFlipped) {	//ȷ�����ӵĺᣬ������
	        xx = BOARD_EDGE + (BoardOperation.FILE_FLIP(x) - FILE_LEFT) * SQUARE_SIZE;
	        yy = BOARD_EDGE + (BoardOperation.RANK_FLIP(y) - RANK_TOP) * SQUARE_SIZE;
	      } else {
	        xx = BOARD_EDGE + (x - FILE_LEFT) * SQUARE_SIZE;
	        yy = BOARD_EDGE + (y - RANK_TOP) * SQUARE_SIZE;
	      }
	      sq = BoardOperation.COORD_XY(x, y);	//���������256�����е��±�
	      pc = pos.ucpcSquares[sq];	//��ø�λ�õ����ӱ�ʶ
	      if (pc != 0) {//���ǿ�λ�ͻ�����
	        //��������
	    	g.drawImage(bmpPieces[pc] ,xx ,yy ,null);
	      }
	      if (sq == sqSelected || sq == BoardOperation.SRC(mvLast) || sq == BoardOperation.DST(mvLast)) {//�����ѡ�е��ӣ���������һ������ʼ����ֹλ��
	        //�ͻ���ѡ�еļǺ�
	    	g.drawImage(bmpSelected ,xx ,yy ,null);
	      }
	    }
	  }
	}
	
	// ���Ƹ���
	public static void DrawSquare(Graphics g, int sq, boolean bSelected) {
	  int sqFlipped, xx, yy, pc;
	  
	  sqFlipped = bFlipped ? BoardOperation.SQUARE_FLIP(sq) : sq;//��ñ�ѡ�е�����256�е��±�
	  xx = BOARD_EDGE + (BoardOperation.FILE_X(sqFlipped) - FILE_LEFT) * SQUARE_SIZE;
	  yy = BOARD_EDGE + (BoardOperation.RANK_Y(sqFlipped) - RANK_TOP) * SQUARE_SIZE;
	  pc = pos.ucpcSquares[sq];//�õ���λ�����ӵ�����
	  if (pc != 0) {
		//��������
		g.drawImage(bmpPieces[pc] ,xx ,yy ,null);
	  }
	  if (bSelected) {
		//�ͻ���ѡ�еļǺ�
		g.drawImage(bmpSelected ,xx ,yy ,null);
	  }
	}
	

	// ��������¼�����
	public static void ClickSquare(Graphics g, int sq) {
	  int pc,mv, vlRep;
	  sq = bFlipped ? BoardOperation.SQUARE_FLIP(sq) : sq;//�Ƿ�ת���̣��õ�������ӵ��±�
	  pc = pos.ucpcSquares[sq];	//��õ�����ӵ�����

	  if ((pc & BoardOperation.SIDE_TAG(pos.sdPlayer)) != 0) {
	    // �������Լ����ӣ���ôֱ��ѡ�и���
	    if (sqSelected != 0) {
	      DrawSquare(g,sqSelected, false);
	    }
	    sqSelected = sq;
	    DrawSquare(g, sq, DRAW_SELECTED);//����ѡ�еĿ��
	    if (mvLast != 0) {//��һ����ѡ����
	      DrawSquare(g, BoardOperation.SRC(mvLast),false);//�����յ㶼���Ͽ��
	      DrawSquare(g, BoardOperation.DST(mvLast),false);
	    }
	     // ���ŵ��������
	    if(!nomusic)
	    	MusicUtil.playMusic("CLICK");

	  } else if (sqSelected != 0 && !bGameOver) {
		// �������Ĳ����Լ����ӣ�������ѡ����(һ�����Լ�����)����ô�������
		mv = BoardOperation.MOVE(sqSelected, sq);
	    if (pos.LegalMove(mv)) {//�Ϸ����߷�
			if (pos.MakeMove(mv)) {//����
				mvLast = mv;
				DrawSquare(g, sqSelected, DRAW_SELECTED);//�ػ�Ų��ǰ��λ��
				DrawSquare(g, sq, DRAW_SELECTED);//�ػ�Ų�����λ��
				sqSelected = 0;//�ƶ���֮�����ѡ�е���
				
				// ����ظ�����
		        vlRep = pos.RepStatus(3);
		        
				if (pos.IsMate()) {//�ֳ�ʤ��
			       // ����ֳ�ʤ������ô����ʤ�������������ҵ���������������ʾ��
					if(!nomusic)
						MusicUtil.playMusic("WIN");
					GameOver("��Ӯ�ˣ�");//������ʾ��
					bGameOver = true;
			    } else if (vlRep > 0) {
			          vlRep = pos.RepValue(vlRep);
			          // ע�⣺"vlRep"�ǶԵ�����˵�ķ�ֵ
			          if(!nomusic)
			        	  MusicUtil.playMusic(vlRep > Search.WIN_VALUE ? "LOSS" : vlRep < -Search.WIN_VALUE ? "WIN" : "DRAW");
			          GameOver(vlRep > Search.WIN_VALUE ? "�����������벻Ҫ���٣�" :
			              vlRep < -Search.WIN_VALUE ? "���Գ���������ף����ȡ��ʤ����" : "˫���������ͣ������ˣ�");
			          bGameOver = true;
			    } else if (pos.nMoveNum > 100) {
			    	if(!nomusic)
			    		MusicUtil.playMusic("DRAW");
			    	GameOver("������Ȼ�������ͣ������ˣ�");
			        bGameOver = true;
			    } else {
			       // ���û�зֳ�ʤ������ô���Ž��������ӻ�һ�����ӵ�����
			       if(pos.InCheck()){//���Ž���������
			    	   if(!nomusic)
			    		   MusicUtil.playMusic("CHECK");
			       }
			       else{
			    	   if(pos.Captured()){//���ų��ӵ�����
			    		   if(!nomusic)
			    			   MusicUtil.playMusic("CAPTURE");
			    	   }else{//�����ƶ��ӵ�����
			    		   if(!nomusic)
			    			   MusicUtil.playMusic("MOVE");
			    	   }
			       }
			       
			       if (pos.Captured()) {
			            pos.SetIrrev();
			       }
			       DrawBoard(g);//���»�һ������
			       if(!trainingMode)
			    	   ResponseMove(g); // �ֵ���������
			    }
			}
			else{//�߷��Ƿ�������ҪӦ������
				if(!nomusic)
					MusicUtil.playMusic("ILLEGAL");
			}
		}
	    // ��������Ͳ������߷�(������������)����ô���������
	  }
	}
	
	// ��ʼ�����
	public static void Startup() {
	  pos.Startup();
	  sqSelected = mvLast = 0;
	  bGameOver = false; 
	}
	
	// ������ʾ��Ϸ����
	public void paint(Graphics g) {
		DrawBoard(g);//�������ʼ������
	}
	
	// ���Ի�Ӧһ����
	public static void ResponseMove(Graphics g) {
	  int vlRep;
	  Serial_Port sport = new Serial_Port();
	  // ������һ����
	  if(newcommer){
		  Search.SearchMain_1();//����ai�㷨
	  }
	  else{
		  Search.SearchMain();
	  }
	  pos.MakeMove(Search.mvResult);
	  // �����һ�����ѡ����
	  DrawSquare(g, BoardOperation.SRC(mvLast), !DRAW_SELECTED);
	  DrawSquare(g, BoardOperation.DST(mvLast), !DRAW_SELECTED);
	  // �ѵ����ߵ����ǳ���
	  mvLast = Search.mvResult;
	  DrawSquare(g, BoardOperation.SRC(mvLast), DRAW_SELECTED);
	  DrawSquare(g, BoardOperation.DST(mvLast), DRAW_SELECTED);
	  str = BoardOperation.SRC(mvLast) + BoardOperation.DST(mvLast);
//	  try{
//		  sport.sendMessage(ChineseChess.port , Integer.toString(str));
//	  }
//	  catch(Exception e)
//	  {
//		  e.printStackTrace();
//	  }
	  // ����ظ�����
	  vlRep = pos.RepStatus(3);
	  if (pos.IsMate()) {
	    // ����ֳ�ʤ������ô����ʤ�������������ҵ���������������ʾ��
		if(!nomusic)
			MusicUtil.playMusic("LOSS");
	    GameOver("�����ˣ�");
	    bGameOver = true;
	  }
	  else if (vlRep > 0) {
		    vlRep = pos.RepValue(vlRep);
		    // ע�⣺"vlRep"�Ƕ������˵�ķ�ֵ
		    if(vlRep < -Search.WIN_VALUE){
		    	if(!nomusic)
		    		MusicUtil.playMusic("LOSS");
		    }
		    else{
		    	if(vlRep > Search.WIN_VALUE){
		    		if(!nomusic)
		    			MusicUtil.playMusic("WIN");
		    	}
		    	else{
		    		if(!nomusic)
		    			MusicUtil.playMusic("DRAW");
		    	}
		    }
		    
		    GameOver(vlRep < -Search.WIN_VALUE ? "�����������벻Ҫ���٣�" :
		        vlRep > Search.WIN_VALUE ? "���Գ���������ף����ȡ��ʤ����" : "˫���������ͣ������ˣ�");
		    bGameOver = true;
	  } else if (pos.nMoveNum > 100) {
		    if(!nomusic)
			  MusicUtil.playMusic("DRAW");
		    GameOver("������Ȼ�������ͣ������ˣ�");
		    bGameOver = true;
	  } else {
	    // ���û�зֳ�ʤ������ô���Ž��������ӻ�һ�����ӵ�����
		  	if(pos.InCheck()){//���Ž���������
		  		if(!nomusic)
		  			MusicUtil.playMusic("CHECK2");
	       }
	       else{
	    	   if(pos.Captured()){//���ų��ӵ�����
	    		   if(!nomusic)
	    			   MusicUtil.playMusic("CAPTURE2");
	    	   }else{//�����ƶ��ӵ�����
	    		   if(!nomusic)
	    			   MusicUtil.playMusic("MOVE2");
	    	   }
	     }
	  }
	  if (pos.Captured()) {
	      pos.SetIrrev();
	  }
	}
	
	// ��ʼ��Zobrist��
	public static void InitZobrist() {
	  int i, j;
	  Zobrist.Player.InitRC4(rc4);
	  for (i = 0; i < 14; i ++) {
	    for (j = 0; j < 256; j ++) {
	    	Zobrist.Table[i][j] = new ZobristStruct();
	    	Zobrist.Table[i][j].InitRC4(rc4);
	    }
	  }
	  BookItemOp.LoadBook(rc4);
	}
	
	//��Ϸ����ʱ������ʾ��
	public static void GameOver(String msg){
		try {
			// �����ı���
			JTextArea ablutText = new JTextArea();
			// ���ò��ɱ༭
			ablutText.setEditable(false);
			// ��������
			ablutText.setFont(FontUtil.myFont1);
			// ��������
			ablutText.setText(msg+"��ѡ�����¿�ʼ�򽻻����֣�");
			// �����Ի���
			JDialog dialog = new JDialog();			
			// ���ı�����ӵ��Ի����м�
			dialog.add(new JScrollPane(ablutText),BorderLayout.CENTER);
			// ���ô���ͼ��
			dialog.setIconImage(ImageIO.read(new File("src/music/chess.jpg")));
			dialog.setTitle("��ֽ���");
			// ���ô��ڴ�С
			dialog.setSize(300, 100);
			// ���ô�����ʾ
			dialog.setVisible(true);
			// ���ô����ö�
			dialog.setAlwaysOnTop(true);
			// ���ô��ھ���
			dialog.setLocationRelativeTo(null);
			// ���ô���Ĭ�Ϲرշ�ʽ
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
