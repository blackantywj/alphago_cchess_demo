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
 * �����ǳ�������
 * */
public class ChineseChess extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//����29�����4,menu��21
	public static final int BOARD_TOP = 29+42;
	public static final int BOARD_LEFT = 4;

	public static final int SQUARE_SIZE = 56;		//һ�����ӵĿ��
	public static final int BOARD_EDGE = 13;		//�߽���

	public static final int BOARD_WIDTH = BOARD_LEFT + BOARD_EDGE + SQUARE_SIZE * 9 + 8;	//���̿�ȵ��ڣ���߽��+9*����+�ұ߽��
	public static final int BOARD_HEIGHT = BOARD_TOP + BOARD_EDGE + SQUARE_SIZE * 10 + BOARD_EDGE;	//���̸߶ȵ��ڣ��ϱ߽��+10*����+�±߽��

//	public static SerialPort port;
	public ChineseChess() throws IOException {
		//ָ������Լ���ȡ����
		ChessPanel chessPanel = new ChessPanel();
		Container  contentPane=this.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		// �����Ϸ���
		add(contentPane,chessPanel,0,2,1,1);
     	// ���ò˵���
		add(contentPane,new ChessMenu(chessPanel).getChessMenu(),0,0,1,0);//��һ�в˵�
		add(contentPane,new imgChessMenu(chessPanel).getChessMenu(),0,1,1,0);//�ڶ��в˵�
		// �ѽ��봰�ڵ��������Ϊ����
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		// ���ñ���
		this.setTitle("AlphaEle6�����й�����");
		// ���������
		// ���ô��ڵ�ͼ��
		this.setIconImage(ImageIO.read(ChineseChess.class.getResourceAsStream("/music/chess.jpg")));
		// ���ô��ڴ�С
		this.setSize(BOARD_WIDTH, BOARD_HEIGHT);
		// ���ô���Ĭ�Ϲرշ�ʽ
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�ô��ڹر�ʱ����̨����Ҳһ��ر�
		// ���ô��ڴ�С���ɸı�
		this.setResizable(false);
		// ���ô��ھ���
		this.setLocationRelativeTo(null);
		// ���ô�����ʾ
		this.setVisible(true);
	}
	/**
	 * ���ܣ���ָ���ĸ�����������Ӽ�����
	 * @param main ��������
	 * @param child �Ӽ�
	 * @param gridx ������
	 * @param gridy ������
	 * @param weightx 
	 * @param weighty
	 */
	public void add(Container main,Container child,int gridx,int gridy,double weightx,double weighty){
		GridBagConstraints c=new GridBagConstraints();
		c.gridx=gridx;//����ĺ�����
		c.gridy=gridy;   //�����������
		c.weightx=weightx;    //�е�Ȩ�أ�ͨ���˲�����������η����е�ʣ��ռ�
		c.weighty=weighty;    //�е�Ȩ�أ�ͨ���˲�����������η����е�ʣ��ռ�
		c.fill=GridBagConstraints.BOTH;  //��������ڸ�����ܳ������ʱ����fill�����
		main.add(child,c);
	}
	/*
	 * ���ܣ�Main����
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
