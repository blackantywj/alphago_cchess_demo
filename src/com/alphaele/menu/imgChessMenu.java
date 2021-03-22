package com.alphaele.menu;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.alphaele.ai.PieceValue;
import com.alphaele.panel.BoardOperation;
import com.alphaele.panel.ChessPanel;
import com.alphaele.util.FontUtil;
import com.alphaele.util.MusicUtil;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
/**
 * �����ǲ˵�
 */
public class imgChessMenu implements ActionListener{
	/**
	 * ��ʼ���
	 */
	ChessPanel cp = null;
	public static  int flag=1;
	public static  JButton musicButton=null;
	public static ArrayList <JButton> ModelButton=new ArrayList<JButton>();
	private Clip clip=null;

	//���췽���д���ChessPanel
	public imgChessMenu(ChessPanel cp){
		this.cp = cp;
	}

	/**
	 * ���ܣ������˵������˵����и�����ļ����¼�
	 * @throws IOException 
	 */
	public JMenuBar getChessMenu() throws IOException{
		// �����˵���
		JMenuBar menuBar = new JMenuBar();
		//menuBar.setBackground(Color.gray);
		// �����˵��һ��һ����ť����������ÿ����ť���������ԣ��Լ��¼������������ͳһ����������
		menuBar.setLayout(new FlowLayout(0,10,0));//��ʽ���֣�����룬ˮƽ���0����ֱ���0
		ArrayList <JButton>buttonlist=new ArrayList<JButton>();
		//�½�
		JButton newButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/new.png")));
		newButton.setToolTipText("�µĶԾ�");//��ʾ��Ϣ
		newButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restartGame();
			}
		});
		buttonlist.add(newButton);
		//��
		JButton openButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/open.png")));
		openButton.setToolTipText("�����");//��ʾ��Ϣ
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openGame();
			}
		});

		buttonlist.add(openButton);
		//����
		JButton saveButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/save.png")));
		saveButton.setToolTipText("�������");//��ʾ��Ϣ
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveGame();
			}
		});
		buttonlist.add(saveButton);
		//��������
		JButton changeButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/change.png")));
		changeButton.setToolTipText("��������");//��ʾ��Ϣ
		changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeFirst();
			}
		});
		buttonlist.add(changeButton);
		//Ԥ����һ��
		JButton nextButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/next.png")));
		nextButton.setToolTipText("Ԥ����һ��");//��ʾ��Ϣ
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Pre();
			}
		});
		buttonlist.add(nextButton);
		//���ƹ���
		JButton accessButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/access.png")));
		accessButton.setToolTipText("���ƹ���");//��ʾ��Ϣ
		accessButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Access();
			}
		});
		buttonlist.add(accessButton);
		//����ģʽ
		JButton newcommerButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level1.png")));
		newcommerButton.setToolTipText("����ģʽ");//��ʾ��Ϣ
		newcommerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newCommerMode();
			}
		});
		ModelButton.add(newcommerButton);
		buttonlist.add(newcommerButton);
		//�߼�ģʽ
		JButton not_newcommerButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level2_1.png")));
		not_newcommerButton.setToolTipText("�߼�ģʽ");//��ʾ��Ϣ
		not_newcommerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				notNewCommerMode();
			}
		});
		ModelButton.add(not_newcommerButton);
		buttonlist.add(not_newcommerButton);
		//��ϰģʽ
		JButton trainButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/train.png")));
		trainButton.setToolTipText("��ϰģʽ");//��ʾ��Ϣ
		trainButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TrainingMode();
			}
		});
		ModelButton.add(trainButton);
		buttonlist.add(trainButton);
		//��������
		ImageIcon icon1=new ImageIcon(imgChessMenu.class.getResource("/menuImg/bgmusic1.png"));//��ͣͼ��
		ImageIcon icon2=new ImageIcon(imgChessMenu.class.getResource("/menuImg/bgmusic2.png"));	//����ͼ��		
		musicButton=new JButton(icon1);
		musicButton.setActionCommand("�ر�");
		musicButton.setToolTipText("��ͣ��������");//��ʾ��Ϣ
		musicButton.addActionListener(this);
		buttonlist.add(musicButton);
		//����
		JButton aboutButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/about.png")));
		aboutButton.setToolTipText("����");//��ʾ��Ϣ
		aboutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aboutGame();
			}
		});
		buttonlist.add(aboutButton);
		//��menuBar��Ӱ�ť��������ͳһ����
		for(int i=0;i<buttonlist.size();i++)
		{
			buttonlist.get(i).setMaximumSize(new Dimension(64,64));//ͼ�����
			buttonlist.get(i).setBorderPainted(false);//��ť�߿���ʾ
			buttonlist.get(i).setFocusable(false);
			buttonlist.get(i).setContentAreaFilled(false);
			buttonlist.get(i).setMinimumSize(new Dimension(64,64));
			menuBar.add(buttonlist.get(i));
		}
		// �����˵���
		return menuBar;	
	}


	/**
	 * ���ܣ���ʾ���ڱ�����������Ϣ
	 */
	public void aboutGame(){
		try {
			// ����ͼƬ��ǩ
			JLabel label = new JLabel(new ImageIcon(ImageIO.read(imgChessMenu.class.getResourceAsStream("/music/chess.jpg"))));
			// �����ı���
			JTextArea ablutText = new JTextArea();
			// ���ò��ɱ༭
			ablutText.setEditable(false);
			// ��������
			ablutText.setFont(FontUtil.myFont1);
			// ��������
			ablutText.setText("AlphaEle Chinese Chess \n\n\n�Ӻ���ѧ���������ϢѧԺ14��������");
			// �����Ի���
			JDialog dialog = new JDialog();			
			// ��ͼƬ��ǩ��ӵ��Ի�������
			dialog.add(label,BorderLayout.WEST);
			// ���ı�����ӵ��Ի����м�
			dialog.add(new JScrollPane(ablutText),BorderLayout.CENTER);
			// ���ô���ͼ��
			dialog.setIconImage(ImageIO.read(imgChessMenu.class.getResourceAsStream("/music/chess.jpg")));
			dialog.setTitle("����AlphaEle1.0");
			// ���ô��ڴ�С
			dialog.setSize(480, 160);
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

	/**
	 * ���ܣ�ר�����ڿ���������ͣ�벥��
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		clip=MusicUtil.clip;
		if(clip!=null)
			if(this.flag++%2==1)//ֹͣ����
			{musicButton.setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/bgmusic2.png")));
			musicButton.setActionCommand("��");
			musicButton.setToolTipText("��������");//��ʾ��Ϣ

			clip.stop();	
			}
			else
			{
				musicButton.setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/bgmusic1.png")));
				musicButton.setActionCommand("�ر�");
				musicButton.setToolTipText("��ͣ��������");//��ʾ��Ϣ
				clip.loop(-1);;
			}

		else
		{

		}
	}

	/**
	 * ���ܣ�ʵ����ϰģʽ�趨���Լ�ѡ��Ͱ�ť��״̬�ı�
	 * ��ϰģʽ�±�Ϊ��2��
	 */
	private void TrainingMode() {
		//��ϰ
		ChessMenu.LevelGroup.get(2).setSelected(true);
		imgChessMenu.ModelButton.get(2).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/train_1.png")));
		//����
		imgChessMenu.ModelButton.get(0).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level1.png")));
		ChessMenu.LevelGroup.get(0).setSelected(false);
		//�߼�
		imgChessMenu.ModelButton.get(1).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level2.png")));
		ChessMenu.LevelGroup.get(1).setSelected(false);
		//ִ����ϰģʽ
		ChessPanel.trainingMode = true;
	}

	/**
	 * ���ܣ�ʵ�ָ߼�ˮƽ�趨���Լ�ѡ��Ͱ�ť��״̬�ı�
	 * �߼�AIˮƽ�±�Ϊ��1��
	 */
	private void notNewCommerMode() {
		//�߼�
		ChessMenu.LevelGroup.get(1).setSelected(true);
		imgChessMenu.ModelButton.get(1).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level2_1.png")));
		//����
		imgChessMenu.ModelButton.get(0).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level1.png")));
		ChessMenu.LevelGroup.get(0).setSelected(false);
		//��ϰ
		imgChessMenu.ModelButton.get(2).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/train.png")));
		ChessMenu.LevelGroup.get(2).setSelected(false);
		//ִ�и߼�ģʽ
		ChessPanel.newcommer = false;
		ChessPanel.trainingMode = false;
		//restartGame();
	}

	/**
	 *���ܣ�ʵ�ֳ���ˮƽ�趨���Լ�ѡ��Ͱ�ť��״̬�ı�
	 * ����AIˮƽ�±�Ϊ��0��
	 */
	private void newCommerMode() {
		//����
		ChessMenu.LevelGroup.get(0).setSelected(true);
		imgChessMenu.ModelButton.get(0).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level1_1.png")));
		//�߼�
		imgChessMenu.ModelButton.get(1).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level2.png")));
		ChessMenu.LevelGroup.get(1).setSelected(false);
		//��ϰ
		imgChessMenu.ModelButton.get(2).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/train.png")));
		ChessMenu.LevelGroup.get(2).setSelected(false);
		//ִ�г���
		ChessPanel.newcommer = true;
		ChessPanel.trainingMode = false;
		//restartGame();
	}
	/**
	 * ���ܣ�Ԥ����һ��
	 * */
	private void Pre() {
		if(!ChessPanel.bGameOver){
			Graphics g =  cp.getGraphics();
			ChessPanel.ResponseMove(g);
			cp.repaint();
		}
	}

	/**
	 * ���ܣ����ƹ���
	 */
	private void Access() {
		for(int i=0; i<256; i++){
			if(ChessPanel.pos.ucpcSquares[i]!=0){
				int pc = ChessPanel.pos.ucpcSquares[i];//������ӵ�����
				if (pc < 16) {
					ChessPanel.pos.vlWhite += PieceValue.cucvlPiecePos[pc - 8][i];
				} else {
					ChessPanel.pos.vlBlack += PieceValue.cucvlPiecePos[pc - 16][BoardOperation.SQUARE_FLIP(i)];//�ڷ�
				}
			}
		}

		if(ChessPanel.pos.vlWhite > ChessPanel.pos.vlBlack){
			JOptionPane.showMessageDialog(null, "�췽���֣�"+ChessPanel.pos.vlWhite+"\n"
					+"�ڷ����֣�"+ChessPanel.pos.vlBlack+"\n"+"�췽ռ�Ϸ�!");
		}
		else if(ChessPanel.pos.vlWhite < ChessPanel.pos.vlBlack){
			JOptionPane.showMessageDialog(null, "�췽���֣�"+ChessPanel.pos.vlWhite+"\n"
					+"�ڷ����֣�"+ChessPanel.pos.vlBlack+"\n"+"�ڷ�ռ�Ϸ�!");
		}
		else{
			JOptionPane.showMessageDialog(null, "�췽���֣�"+ChessPanel.pos.vlWhite+"\n"
					+"�ڷ����֣�"+ChessPanel.pos.vlBlack+"\n"+"���˫��ƽ����ɫ");
		}
	}

	/**
	 * ���¿�ʼ
	 */
	private void restartGame() {
		MusicUtil.playMusic("NEWGAME");
		ChessPanel.Startup();

		if(ChessPanel.bFlipped == true){//��������ʱai������
			Graphics g =  cp.getGraphics();
			ChessPanel.ResponseMove(g);
		}
		cp.repaint();
	}

	/**
	 * ��������
	 */
	private void changeFirst() {
		ChessPanel.bFlipped = !ChessPanel.bFlipped;
		ChessPanel.Startup();
		if(ChessPanel.bFlipped == true){//��������ʱai������
			Graphics g =  cp.getGraphics();
			ChessPanel.ResponseMove(g);
		}
		cp.repaint();
	}

	/**
	 * ���ܣ�����
	 */
	private void updateGame() {
		JOptionPane.showMessageDialog(null, "���޸��£�");
	}


	/**
	 * ���ܣ���������
	 */
	public void saveGame(){
		try {
			JOptionPane.showMessageDialog(null, "ѡ��һ���ļ�������������");
			// �����ļ�ѡ����
			JFileChooser fileChooser = new JFileChooser();
			// ��ʾ�����ļ��Ի���
			fileChooser.showSaveDialog(null);
			// ��ȡ������ļ���
			String filename = fileChooser.getSelectedFile().getAbsolutePath();
			FileWriter fw=new FileWriter(filename);
			PrintWriter pw=new PrintWriter(fw,true);
			pw.println(ChessPanel.bFlipped);//д���Ƿ�ת����
			pw.println(ChessPanel.pos.sdPlayer);//д������
			for(int i=0; i<256; i++){
				pw.print(ChessPanel.pos.ucpcSquares[i]+" ");
				if(i%16 == 0 && i!=0){
					pw.println();
				}
			}
			pw.close();
			fw.close();
			JOptionPane.showMessageDialog(null, "���̱���ɹ���");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "����ʧ�ܣ�");
		} 
	}


	/**
	 * ���ܣ������̣�Ԥ����һ��
	 */
	private void openGame() {
		try {
			JOptionPane.showMessageDialog(null, "�������ļ����ļ�Ҫ��\n��һ��Ϊ�����Ƿ�ת��\n�ڶ���Ϊ���ַ���\n����������ʮ����Ϊ16x16���������飬�ո�Ϊ����ķָ�����");
			// �����ļ�ѡ����
			JFileChooser fileChooser = new JFileChooser();
			// ��ʾ���ļ��Ի���
			fileChooser.showOpenDialog(null);
			// ��ȡ�򿪵��ļ�����·��
			String filepath = fileChooser.getSelectedFile().getAbsolutePath();
			FileReader fr=new FileReader(filepath);
			BufferedReader br=new BufferedReader(fr);
			String qipan="";
			ChessPanel.Startup();
			ChessPanel.bFlipped = Boolean.getBoolean(br.readLine());
			ChessPanel.pos.sdPlayer = Integer.parseInt(br.readLine());
			while(br.ready()){
				qipan += br.readLine();
			}
			String qpStr[] = qipan.split(" "); 
			for(int i=0; i<256; i++){
				ChessPanel.pos.ucpcSquares[i] = (byte)Integer.parseInt(qpStr[i]);
			}
			br.close();
			fr.close();
			cp.repaint();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "��ȡ����ʧ�ܣ�");
		} 
	}
}
