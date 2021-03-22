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
 * 本类应该存放绘图函数和图片加载函数，主要用来绘制
 * Xqwl结构体的变量可以存放到本类
 * */

public class ChessPanel extends JPanel{
	public static Image board = null;
	
	public static int sqSelected;                       // 选中的格子，上一步棋
	public static int mvLast;
	public static int str;
	
	public static boolean bFlipped = false, bGameOver;    // 是否翻转棋盘
	public static boolean newcommer = false;		//标记是否ai的智能等级
	public static boolean nomusic = false;			//控制音乐开关
	public static boolean trainingMode = false;
	
	public static Image bmpBoard, bmpSelected, bmpPieces[] = new Image[24]; // 资源图片
	
	public static final int SQUARE_SIZE = 56;		//一个格子的宽度
	public static final int BOARD_EDGE = 14;		//边界宽度
	public static final int BOARD_WIDTH = BOARD_EDGE + SQUARE_SIZE * 9 + BOARD_EDGE;	//棋盘宽度等于：左边界宽+9*格子+右边界宽
	public static final int BOARD_HEIGHT = BOARD_EDGE + SQUARE_SIZE * 10 + BOARD_EDGE;	//棋盘高度等于：上边界宽+10*格子+下边界宽
	
	// 棋盘范围
	public static final int RANK_TOP = 3;			//顶部下标从3开始
	public static final int RANK_BOTTOM = 12;		//底部下标从12开始
	public static final int FILE_LEFT = 3;		//左边下标从3开始
	public static final int FILE_RIGHT = 11;		//右边下标从11开始
	
	public static RC4 rc4 = new RC4(new byte[1]);
	
	//加载棋盘图片
	static{
		try {
			bmpBoard = ImageIO.read(new File("src/boards/wood.gif"));//棋盘
			bmpSelected = ImageIO.read(new File("src/wood/oos.gif"));//选择框
			
			//红色
			bmpPieces[8] = ImageIO.read(new File("src/wood/rk.gif"));
			bmpPieces[9] = ImageIO.read(new File("src/wood/ra.gif"));
			bmpPieces[10] = ImageIO.read(new File("src/wood/rb.gif"));
			bmpPieces[11] = ImageIO.read(new File("src/wood/rn.gif"));
			bmpPieces[12] = ImageIO.read(new File("src/wood/rr.gif"));
			bmpPieces[13] = ImageIO.read(new File("src/wood/rc.gif"));
			bmpPieces[14] = ImageIO.read(new File("src/wood/rp.gif"));
			
			//蓝色
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
	public static Position pos = new Position();//记录了整个棋盘的进行状态
	
	public static boolean DRAW_SELECTED = true;//就是一个代号
	
	//构造方法中初始化棋盘
	public ChessPanel(){
		InitZobrist();
		bFlipped = false;
		Startup();
		
		//鼠标点击事件
		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				int x, y;
				Graphics g =  getGraphics();//得到一个画笔
			    x = FILE_LEFT + (e.getX() - BOARD_EDGE) / SQUARE_SIZE;//获得点击的坐标
			    y = RANK_TOP + (e.getY() - BOARD_EDGE) / SQUARE_SIZE;
			    if (x >= FILE_LEFT && x <= FILE_RIGHT && y >= RANK_TOP && y <= RANK_BOTTOM) {//点击在棋盘内
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
	
	// 绘制最初始的棋盘
	public static void DrawBoard(Graphics g) {
	  int x, y, xx, yy, sq, pc;
	  // 画棋盘
	  g.drawImage(bmpBoard ,0 ,0 ,null);
	  // 画棋子
	  for (x = FILE_LEFT; x <= FILE_RIGHT; x ++) {//棋盘从左到右
	    for (y = RANK_TOP; y <= RANK_BOTTOM; y ++) {//棋盘从上到下
	      if (bFlipped) {	//确定棋子的横，纵坐标
	        xx = BOARD_EDGE + (BoardOperation.FILE_FLIP(x) - FILE_LEFT) * SQUARE_SIZE;
	        yy = BOARD_EDGE + (BoardOperation.RANK_FLIP(y) - RANK_TOP) * SQUARE_SIZE;
	      } else {
	        xx = BOARD_EDGE + (x - FILE_LEFT) * SQUARE_SIZE;
	        yy = BOARD_EDGE + (y - RANK_TOP) * SQUARE_SIZE;
	      }
	      sq = BoardOperation.COORD_XY(x, y);	//获得棋子在256数组中的下标
	      pc = pos.ucpcSquares[sq];	//获得该位置的棋子标识
	      if (pc != 0) {//不是空位就画棋子
	        //画上棋子
	    	g.drawImage(bmpPieces[pc] ,xx ,yy ,null);
	      }
	      if (sq == sqSelected || sq == BoardOperation.SRC(mvLast) || sq == BoardOperation.DST(mvLast)) {//如果是选中的子，或者是上一步的起始或终止位置
	        //就画上选中的记号
	    	g.drawImage(bmpSelected ,xx ,yy ,null);
	      }
	    }
	  }
	}
	
	// 绘制格子
	public static void DrawSquare(Graphics g, int sq, boolean bSelected) {
	  int sqFlipped, xx, yy, pc;
	  
	  sqFlipped = bFlipped ? BoardOperation.SQUARE_FLIP(sq) : sq;//获得被选中的子在256中的下标
	  xx = BOARD_EDGE + (BoardOperation.FILE_X(sqFlipped) - FILE_LEFT) * SQUARE_SIZE;
	  yy = BOARD_EDGE + (BoardOperation.RANK_Y(sqFlipped) - RANK_TOP) * SQUARE_SIZE;
	  pc = pos.ucpcSquares[sq];//得到该位置棋子的类型
	  if (pc != 0) {
		//画上棋子
		g.drawImage(bmpPieces[pc] ,xx ,yy ,null);
	  }
	  if (bSelected) {
		//就画上选中的记号
		g.drawImage(bmpSelected ,xx ,yy ,null);
	  }
	}
	

	// 点击格子事件处理
	public static void ClickSquare(Graphics g, int sq) {
	  int pc,mv, vlRep;
	  sq = bFlipped ? BoardOperation.SQUARE_FLIP(sq) : sq;//是否翻转棋盘，得到点击格子的下标
	  pc = pos.ucpcSquares[sq];	//获得点击格子的类型

	  if ((pc & BoardOperation.SIDE_TAG(pos.sdPlayer)) != 0) {
	    // 如果点击自己的子，那么直接选中该子
	    if (sqSelected != 0) {
	      DrawSquare(g,sqSelected, false);
	    }
	    sqSelected = sq;
	    DrawSquare(g, sq, DRAW_SELECTED);//画上选中的框框
	    if (mvLast != 0) {//上一步有选中子
	      DrawSquare(g, BoardOperation.SRC(mvLast),false);//起点和终点都画上框框
	      DrawSquare(g, BoardOperation.DST(mvLast),false);
	    }
	     // 播放点击的声音
	    if(!nomusic)
	    	MusicUtil.playMusic("CLICK");

	  } else if (sqSelected != 0 && !bGameOver) {
		// 如果点击的不是自己的子，但有子选中了(一定是自己的子)，那么走这个子
		mv = BoardOperation.MOVE(sqSelected, sq);
	    if (pos.LegalMove(mv)) {//合法的走法
			if (pos.MakeMove(mv)) {//能走
				mvLast = mv;
				DrawSquare(g, sqSelected, DRAW_SELECTED);//重画挪动前的位置
				DrawSquare(g, sq, DRAW_SELECTED);//重画挪动后的位置
				sqSelected = 0;//移动子之后清除选中的子
				
				// 检查重复局面
		        vlRep = pos.RepStatus(3);
		        
				if (pos.IsMate()) {//分出胜负
			       // 如果分出胜负，那么播放胜负的声音，并且弹出不带声音的提示框
					if(!nomusic)
						MusicUtil.playMusic("WIN");
					GameOver("你赢了！");//弹出提示框
					bGameOver = true;
			    } else if (vlRep > 0) {
			          vlRep = pos.RepValue(vlRep);
			          // 注意："vlRep"是对电脑来说的分值
			          if(!nomusic)
			        	  MusicUtil.playMusic(vlRep > Search.WIN_VALUE ? "LOSS" : vlRep < -Search.WIN_VALUE ? "WIN" : "DRAW");
			          GameOver(vlRep > Search.WIN_VALUE ? "长打作负，请不要气馁！" :
			              vlRep < -Search.WIN_VALUE ? "电脑长打作负，祝贺你取得胜利！" : "双方不变作和，辛苦了！");
			          bGameOver = true;
			    } else if (pos.nMoveNum > 100) {
			    	if(!nomusic)
			    		MusicUtil.playMusic("DRAW");
			    	GameOver("超过自然限着作和，辛苦了！");
			        bGameOver = true;
			    } else {
			       // 如果没有分出胜负，那么播放将军、吃子或一般走子的声音
			       if(pos.InCheck()){//播放将军的声音
			    	   if(!nomusic)
			    		   MusicUtil.playMusic("CHECK");
			       }
			       else{
			    	   if(pos.Captured()){//播放吃子的声音
			    		   if(!nomusic)
			    			   MusicUtil.playMusic("CAPTURE");
			    	   }else{//播放移动子的声音
			    		   if(!nomusic)
			    			   MusicUtil.playMusic("MOVE");
			    	   }
			       }
			       
			       if (pos.Captured()) {
			            pos.SetIrrev();
			       }
			       DrawBoard(g);//重新画一次棋盘
			       if(!trainingMode)
			    	   ResponseMove(g); // 轮到电脑走棋
			    }
			}
			else{//走法非法，必须要应“将”
				if(!nomusic)
					MusicUtil.playMusic("ILLEGAL");
			}
		}
	    // 如果根本就不符合走法(例如马不走日字)，那么程序不予理会
	  }
	}
	
	// 初始化棋局
	public static void Startup() {
	  pos.Startup();
	  sqSelected = mvLast = 0;
	  bGameOver = false; 
	}
	
	// 重新显示游戏界面
	public void paint(Graphics g) {
		DrawBoard(g);//画上最初始的棋盘
	}
	
	// 电脑回应一步棋
	public static void ResponseMove(Graphics g) {
	  int vlRep;
	  Serial_Port sport = new Serial_Port();
	  // 电脑走一步棋
	  if(newcommer){
		  Search.SearchMain_1();//调用ai算法
	  }
	  else{
		  Search.SearchMain();
	  }
	  pos.MakeMove(Search.mvResult);
	  // 清除上一步棋的选择标记
	  DrawSquare(g, BoardOperation.SRC(mvLast), !DRAW_SELECTED);
	  DrawSquare(g, BoardOperation.DST(mvLast), !DRAW_SELECTED);
	  // 把电脑走的棋标记出来
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
	  // 检查重复局面
	  vlRep = pos.RepStatus(3);
	  if (pos.IsMate()) {
	    // 如果分出胜负，那么播放胜负的声音，并且弹出不带声音的提示框
		if(!nomusic)
			MusicUtil.playMusic("LOSS");
	    GameOver("你输了！");
	    bGameOver = true;
	  }
	  else if (vlRep > 0) {
		    vlRep = pos.RepValue(vlRep);
		    // 注意："vlRep"是对玩家来说的分值
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
		    
		    GameOver(vlRep < -Search.WIN_VALUE ? "长打作负，请不要气馁！" :
		        vlRep > Search.WIN_VALUE ? "电脑长打作负，祝贺你取得胜利！" : "双方不变作和，辛苦了！");
		    bGameOver = true;
	  } else if (pos.nMoveNum > 100) {
		    if(!nomusic)
			  MusicUtil.playMusic("DRAW");
		    GameOver("超过自然限着作和，辛苦了！");
		    bGameOver = true;
	  } else {
	    // 如果没有分出胜负，那么播放将军、吃子或一般走子的声音
		  	if(pos.InCheck()){//播放将军的声音
		  		if(!nomusic)
		  			MusicUtil.playMusic("CHECK2");
	       }
	       else{
	    	   if(pos.Captured()){//播放吃子的声音
	    		   if(!nomusic)
	    			   MusicUtil.playMusic("CAPTURE2");
	    	   }else{//播放移动子的声音
	    		   if(!nomusic)
	    			   MusicUtil.playMusic("MOVE2");
	    	   }
	     }
	  }
	  if (pos.Captured()) {
	      pos.SetIrrev();
	  }
	}
	
	// 初始化Zobrist表
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
	
	//游戏结束时弹出提示框
	public static void GameOver(String msg){
		try {
			// 创建文本域
			JTextArea ablutText = new JTextArea();
			// 设置不可编辑
			ablutText.setEditable(false);
			// 设置字体
			ablutText.setFont(FontUtil.myFont1);
			// 设置内容
			ablutText.setText(msg+"请选择重新开始或交换先手！");
			// 创建对话框
			JDialog dialog = new JDialog();			
			// 把文本域添加到对话框中间
			dialog.add(new JScrollPane(ablutText),BorderLayout.CENTER);
			// 设置窗口图标
			dialog.setIconImage(ImageIO.read(new File("src/music/chess.jpg")));
			dialog.setTitle("棋局结束");
			// 设置窗口大小
			dialog.setSize(300, 100);
			// 设置窗口显示
			dialog.setVisible(true);
			// 设置窗口置顶
			dialog.setAlwaysOnTop(true);
			// 设置窗口居中
			dialog.setLocationRelativeTo(null);
			// 设置窗口默认关闭方式
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
