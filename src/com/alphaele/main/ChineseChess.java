package com.alphaele.main;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.button.ClassicButtonShaper;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.EmeraldDuskSkin;
import org.jvnet.substance.theme.SubstanceTerracottaTheme;
import org.jvnet.substance.watermark.SubstanceBubblesWatermark;

import com.alphaele.menu.ChessMenu;
import com.alphaele.menu.imgChessMenu;
import com.alphaele.panel.ChessPanel;
import com.alphaele.serialPort.Serial_Port;

//import gnu.io.SerialPort;

/*
 * 本类是程序的入口
 * */
public class ChineseChess extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//顶部29，左边4,menu高21
	public static final int BOARD_TOP = 29+42;
	public static final int BOARD_LEFT = 4;

	public static final int SQUARE_SIZE = 56;		//一个格子的宽度
	public static final int BOARD_EDGE = 13;		//边界宽度

	public static final int BOARD_WIDTH = BOARD_LEFT + BOARD_EDGE + SQUARE_SIZE * 9 + 8;	//棋盘宽度等于：左边界宽+9*格子+右边界宽
	public static final int BOARD_HEIGHT = BOARD_TOP + BOARD_EDGE + SQUARE_SIZE * 10 + BOARD_EDGE;	//棋盘高度等于：上边界宽+10*格子+下边界宽

//	public static SerialPort port;
	public ChineseChess() throws IOException {
		//指定面板以及获取容器
		ChessPanel chessPanel = new ChessPanel();
		Container  contentPane=this.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		// 添加游戏面板
		add(contentPane,chessPanel,0,2,1,1);
     	// 设置菜单栏
		add(contentPane,new ChessMenu(chessPanel).getChessMenu(),0,0,1,0);//第一行菜单
		add(contentPane,new imgChessMenu(chessPanel).getChessMenu(),0,1,1,0);//第二行菜单
		// 把进入窗口的鼠标设置为手型
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		// 设置标题
		this.setTitle("AlphaEle6——中国象棋");
		// 添加鼠标监听
		// 设置窗口的图标
		this.setIconImage(ImageIO.read(ChineseChess.class.getResourceAsStream("/music/chess.jpg")));
		// 设置窗口大小
		this.setSize(BOARD_WIDTH, BOARD_HEIGHT);
		// 设置窗口默认关闭方式
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//让窗口关闭时，后台程序也一起关闭
		// 设置窗口大小不可改变
		this.setResizable(false);
		// 设置窗口居中
		this.setLocationRelativeTo(null);
		// 设置窗口显示
		this.setVisible(true);
	}
	/**
	 * 功能：往指定的父级容器添加子级内容
	 * @param main 父级容器
	 * @param child 子级
	 * @param gridx 横坐标
	 * @param gridy 纵坐标
	 * @param weightx 
	 * @param weighty
	 */
	public void add(Container main,Container child,int gridx,int gridy,double weightx,double weighty){
		GridBagConstraints c=new GridBagConstraints();
		c.gridx=gridx;//组件的横坐标
		c.gridy=gridy;   //组件的纵坐标
		c.weightx=weightx;    //行的权重，通过此参数来决定如何分配行的剩余空间
		c.weighty=weighty;    //列的权重，通过此参数来决定如何分配列的剩余空间
		c.fill=GridBagConstraints.BOTH;  //当组件在内格而不能撑满其格时，用fill来填充
		main.add(child,c);
	}
	/*
	 * 功能：Main函数
	 */
	public static void main(String[] args) {
		try {
			//Serial_Port sport = new Serial_Port();
			//port = Serial_Port.connect("COM4");
           UIManager.setLookAndFeel(new SubstanceLookAndFeel());
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
            JFrame.setDefaultLookAndFeelDecorated(true);
           JDialog.setDefaultLookAndFeelDecorated(false);
            SubstanceLookAndFeel.setCurrentTheme(new SubstanceTerracottaTheme());
           //SubstanceLookAndFeel.setSkin(new EmeraldDuskSkin());
           // SubstanceLookAndFeel.setCurrentButtonShaper(new ClassicButtonShaper());
           // SubstanceLookAndFeel.setCurrentWatermark(new SubstanceBubblesWatermark());
           //SubstanceLookAndFeel.setCurrentBorderPainter(new StandardBorderPainter());
           //  SubstanceLookAndFeel.setCurrentGradientPainter(new StandardGradientPainter());
            //SubstanceLookAndFeel.setCurrentTitlePainter(new FlatTitePainter());
			new ChineseChess();
            
        } catch (Exception e) {
            System.err.println(e);
        }
	}
}
