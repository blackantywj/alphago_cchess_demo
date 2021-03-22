package com.alphaele.menu;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.alphaele.ai.PieceValue;
import com.alphaele.panel.BoardOperation;
import com.alphaele.panel.ChessPanel;
import com.alphaele.util.FontUtil;
import com.alphaele.util.MusicUtil;
/**
 * �����ǲ˵�
 */
public class ChessMenu implements ActionListener{
	/*
	 * ��ʼ���
	 */
	/**
	 * ģʽ��
	 */
	public static ArrayList <JCheckBox> LevelGroup = new ArrayList<JCheckBox>();//ģʽ��
	/**
	 * ����������
	 */
	ArrayList <JCheckBox> MusicGroup =new ArrayList<JCheckBox>();//����
	/**
	 * ����ѡ����
	 */
	ArrayList <JCheckBox> BoaGroup =new ArrayList<JCheckBox>();//����
	/**
	 * ����ѡ����
	 */
	ArrayList <JCheckBox> PieGroup =new ArrayList<JCheckBox>();//����
	ChessPanel cp = null;
	//���췽���д���ChessPanel
	public ChessMenu(ChessPanel cp){
		this.cp = cp;
	}

	/**
	 * ���ܣ������˵������˵����и�����ļ����¼�
	 */
	public JMenuBar getChessMenu(){
		// �����˵���
		JMenuBar menuBar = new JMenuBar();
		// �����˵�
		JMenu fileMenu =new JMenu("�ļ�(F)");
		fileMenu.setMnemonic('F');
		JMenu jumianMenu =new JMenu("����(A)");
		jumianMenu.setMnemonic('A');
		JMenu computerMenu =new JMenu("����(C)");
		computerMenu.setMnemonic('C');
		JMenu chooseMenu =new JMenu("ѡ��(O)");
		chooseMenu.setMnemonic('O');
		JMenu helpMenu = new JMenu("����(H)");
		helpMenu.setMnemonic('H');
		// �����˵���
		//�ļ�*********
		JMenuItem restartItem = new JMenuItem("�µľ���(N)");
		restartItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N,java.awt.Event.CTRL_MASK));  
		restartItem.setMnemonic('N');
		restartItem.addActionListener(this);
		JMenuItem openItem = new JMenuItem("��(O)");
		openItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,java.awt.Event.CTRL_MASK));
		openItem.setMnemonic('O');
		openItem.addActionListener(this);
		JMenuItem saveItem = new JMenuItem("����(S)");
		saveItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,java.awt.Event.CTRL_MASK));
		saveItem.setMnemonic('S');
		saveItem.addActionListener(this);
		JMenuItem exitItem = new JMenuItem("�˳�(X)");
		exitItem.setMnemonic('X');
		exitItem.addActionListener(this);
		//����******
		JMenuItem changeFirstItem = new JMenuItem("��������(E)");
		changeFirstItem.setMnemonic('E');
		changeFirstItem.addActionListener(this);
		JMenuItem preItem = new JMenuItem("Ԥ����һ��(P)");
		preItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P,java.awt.Event.CTRL_MASK));
		preItem.setMnemonic('P');
		preItem.addActionListener(this);
		JMenuItem accessItem = new JMenuItem("���ƹ���(A)");
		accessItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,java.awt.Event.CTRL_MASK));
		accessItem.setMnemonic('A');
		accessItem.addActionListener(this);
		//����********
		JMenu levelSetMenu = new JMenu("�ȼ�����(L)");
		levelSetMenu.setMnemonic('L');
		JCheckBox newcommerItem = new JCheckBox("����ˮƽ(A)",false);
		newcommerItem.setMnemonic('A');
		LevelGroup.add(newcommerItem);
		JCheckBox not_newcommerItem = new JCheckBox("�߼�ˮƽ(B)",true);
		not_newcommerItem.setMnemonic('B');
		LevelGroup.add(not_newcommerItem);
		JCheckBox trainModeItem = new JCheckBox("��ϰģʽ(P)",false);
		trainModeItem.setMnemonic('P');
		LevelGroup.add(trainModeItem);
		//������Ĺ�������
		for(int i=0;i<LevelGroup.size();i++)
		{
			LevelGroup.get(i).setBorderPaintedFlat(false);
			LevelGroup.get(i).setBorderPainted(false);
			LevelGroup.get(i).addActionListener(this);
		}
		//ѡ��*********
		//����
		JMenu musicMenu = new JMenu("��������(M)");
		musicMenu.setMnemonic('M');
		JCheckBox xianjianItem=new JCheckBox("�ɽ�(A)");
		xianjianItem.setMnemonic('A');
		xianjianItem.addActionListener(this);
		MusicGroup.add(xianjianItem);
		JCheckBox gaoshanItem=new JCheckBox("��ɽ��ˮ(B)");
		gaoshanItem.setMnemonic('B');
		gaoshanItem.addActionListener(this);
		MusicGroup.add(gaoshanItem);
		//����
		JMenu boardMenu = new JMenu("����(B)");
		boardMenu.setMnemonic('B');
		JCheckBox canvasItem = new JCheckBox("����(A)");
		canvasItem.setMnemonic('A');
		BoaGroup.add(canvasItem);
		JCheckBox dropsItem = new JCheckBox("ˮ��(B)");
		dropsItem.setMnemonic('B');
		BoaGroup.add(dropsItem);
		JCheckBox greenItem = new JCheckBox("ī��(C)");
		greenItem.setMnemonic('C');
		BoaGroup.add(greenItem);
		JCheckBox qianhongItem = new JCheckBox("����(D)");
		qianhongItem.setMnemonic('D');
		BoaGroup.add(qianhongItem);
		JCheckBox sheetItem = new JCheckBox("����(E)");
		sheetItem.setMnemonic('E');
		BoaGroup.add(sheetItem);
		JCheckBox whiteItem = new JCheckBox("����ʯ(F)");
		whiteItem.setMnemonic('F');
		BoaGroup.add(whiteItem);
		JCheckBox woodItem = new JCheckBox("ľ��(G)");
		woodItem.setMnemonic('G');
		woodItem.setSelected(true);
		BoaGroup.add(woodItem);
		//��������
		for(int i=0;i<BoaGroup.size();i++)
		{
			BoaGroup.get(i).setBorderPaintedFlat(false);
			BoaGroup.get(i).setBorderPainted(false);
			BoaGroup.get(i).addActionListener(this);
		}
		//����
		JMenu pieceMenu = new JMenu("����(P)");
		pieceMenu.setMnemonic('P');
		JCheckBox woodpItem = new JCheckBox("ľ��(A)");
		woodItem.setMnemonic('A');
		woodpItem.setSelected(true);
		PieGroup.add(woodpItem);
		JCheckBox delicateItem = new JCheckBox("����(B)");
		delicateItem.setMnemonic('B');
		PieGroup.add(delicateItem);
		JCheckBox polishItem = new JCheckBox("�⻬(C)");
		polishItem.setMnemonic('C');
		PieGroup.add(polishItem);
		//��������
		for(int i=0;i<PieGroup.size();i++)
		{
			PieGroup.get(i).setBorderPaintedFlat(false);
			PieGroup.get(i).setBorderPainted(false);
			PieGroup.get(i).addActionListener(this);
		}
		//����*******
		JMenuItem updateItem = new JMenuItem("����(U)");
		updateItem.setMnemonic('U');
		updateItem.addActionListener(this);
		JMenuItem aboutItem = new JMenuItem("����(A)");
		aboutItem.setMnemonic('A');
		aboutItem.addActionListener(this);
		// �Ѳ˵�����ӵ��˵�
		//�ļ�
		fileMenu.add(restartItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(exitItem);
		fileMenu.addSeparator();
		//����
		jumianMenu.add(changeFirstItem);
		jumianMenu.add(accessItem);
		jumianMenu.add(preItem);
		//ģʽ
		//�ȼ�
		computerMenu.add(levelSetMenu);
		levelSetMenu.add(newcommerItem);
		levelSetMenu.add(not_newcommerItem);
		computerMenu.add(trainModeItem);
		//ѡ��
		chooseMenu.add(musicMenu);//����
		musicMenu.add(xianjianItem);
		musicMenu.add(gaoshanItem);
		chooseMenu.add(boardMenu);//����
		boardMenu.add(canvasItem);
		boardMenu.add(dropsItem);
		boardMenu.add(greenItem);
		boardMenu.add(qianhongItem);
		boardMenu.add(sheetItem);
		boardMenu.add(whiteItem);
		boardMenu.add(woodItem);
		chooseMenu.add(pieceMenu);//����
		pieceMenu.add(woodpItem);
		pieceMenu.add(delicateItem);
		pieceMenu.add(polishItem);
		helpMenu.add(updateItem);
		helpMenu.add(aboutItem);
		// �Ѳ˵���ӵ��˵���
		menuBar.add(fileMenu);
		menuBar.add(jumianMenu);
		menuBar.add(computerMenu);
		menuBar.add(chooseMenu);
		menuBar.add(helpMenu);
		// ���ز˵���
		return menuBar;	
	}


	/**
	 * ���ܣ���ʾ���ڱ�����������Ϣ
	 */
	public void aboutGame(){
		try {
			// ����ͼƬ��ǩ
			JLabel label = new JLabel(new ImageIcon(ImageIO.read(ChessMenu.class.getResourceAsStream("/music/chess.jpg"))));
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
			dialog.setIconImage(ImageIO.read(ChessMenu.class.getResourceAsStream("/music/chess.jpg")));
			dialog.setTitle("�����й�����");
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
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("��������(E)")){	
			changeFirst();
		}else if(e.getActionCommand().equals("�µľ���(N)")){	
			restartGame();
		}else if(e.getActionCommand().equals("��(O)")){	
			openGame();
		}else if(e.getActionCommand().equals("����(S)")){
			saveGame();
		}else if(e.getActionCommand().equals("���ƹ���(A)")){
			Access();
		}else if(e.getActionCommand().equals("Ԥ����һ��(P)")){
			Pre();
		}else if(e.getActionCommand().equals("�˳�(X)")){
			System.exit(0);
		}else if(e.getActionCommand().equals("����(U)")){
			updateGame();
		}else if(e.getActionCommand().equals("����(A)")){
			aboutGame();
		}
		else if(e.getActionCommand().equals("����ˮƽ(A)")){
			newCommerMode();
		}else if(e.getActionCommand().equals("�߼�ˮƽ(B)")){
			notNewCommerMode();
		}else if(e.getActionCommand().equals("��ϰģʽ(P)")){
			TrainingMode();
		}
		//��������
		else if(e.getActionCommand().equals("�ɽ�(A)")){
			playbgMusic("�ɽ�(A)","xianjian");
		}else if(e.getActionCommand().equals("��ɽ��ˮ(B)")){
			playbgMusic("��ɽ��ˮ(B)","GAOSHAN");
		}
		//����ѡ������
		else if(e.getActionCommand().equals("����(A)")){
			setBoard("����(A)","canvas");
		}
		else if(e.getActionCommand().equals("ˮ��(B)")){
			setBoard("ˮ��(B)","drops");
		}
		else if(e.getActionCommand().equals("ī��(C)")){
			setBoard("ī��(C)","green");
		}
		else if(e.getActionCommand().equals("����(D)")){
			setBoard("����(D)","qianhong");
		}
		else if(e.getActionCommand().equals("����(E)")){
			setBoard("����(E)","sheet");
		}
		else if(e.getActionCommand().equals("����ʯ(F)")){
			setBoard("����ʯ(F)","white");
		}
		else if(e.getActionCommand().equals("ľ��(G)")){
			setBoard("ľ��(G)","wood");
		}

		//ѡ������
		else if(e.getActionCommand().equals("ľ��(A)")){
			setPiece("ľ��(A)","wood");
		}
		else if(e.getActionCommand().equals("����(B)")){
			setPiece("����(B)","delicate");
		}
		else if(e.getActionCommand().equals("�⻬(C)")){
			setPiece("�⻬(C)","polish");
		}
	}
	/**
	 * ���ܣ����ű������֣��Լ�����ѡ�ͼ����ʾ�趨
	 * @param string1 ����ѡ���־
	 * @param string2 ���ֲ��ŵ��ļ���
	 */
	private void playbgMusic(String string1,String string2) {
		for(int i=0;i<MusicGroup.size();i++)
		{
			//ѡ��
			if(MusicGroup.get(i).getText().equals(string1))
			{
				MusicGroup.get(i).setSelected(true);

			}
			//��ѡ��
			else
			{
				MusicGroup.get(i).setSelected(false);
			}
		}
		imgChessMenu.flag=1;
		imgChessMenu.musicButton.setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/bgmusic1.png")));
		imgChessMenu.musicButton.setActionCommand("�ر�����");
		imgChessMenu.musicButton.setToolTipText("��ͣ��������");//��ʾ��Ϣ
		MusicUtil.playbgMusic(string2);
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
	 *���ܣ� ����ѡ��
	 * */
	private void setBoard(String string1,String string2) {
		for(int i=0;i<BoaGroup.size();i++)
		{
			if(BoaGroup.get(i).getText().equals(string1))
			{
				BoaGroup.get(i).setSelected(true);

			}
			else
			{
				BoaGroup.get(i).setSelected(false);
			}
		}
		try {
			ChessPanel.bmpBoard = ImageIO.read(ChessMenu.class.getResourceAsStream("/boards/"+string2+".gif"));
			cp.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ܣ�����ͼƬѡ��
	 * @param string1 ͼƬ��־
	 * @param string2 ͼƬ�ļ���
	 */
	private void setPiece(String string1,String string2) {
		for(int i=0;i<PieGroup.size();i++)
		{
			if(PieGroup.get(i).getText().equals(string1))
			{
				PieGroup.get(i).setSelected(true);

			}
			else
			{
				PieGroup.get(i).setSelected(false);
			}
		}
		try{
			ChessPanel.bmpSelected = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/oos.gif"));//ѡ���

			//��ɫ
			ChessPanel.bmpPieces[8] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rk.gif"));
			ChessPanel.bmpPieces[9] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/ra.gif"));
			ChessPanel.bmpPieces[10] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rb.gif"));
			ChessPanel.bmpPieces[11] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rn.gif"));
			ChessPanel.bmpPieces[12] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rr.gif"));
			ChessPanel.bmpPieces[13] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rc.gif"));
			ChessPanel.bmpPieces[14] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rp.gif"));

			//��ɫ
			ChessPanel.bmpPieces[16] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/bk.gif"));
			ChessPanel.bmpPieces[17] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/ba.gif"));
			ChessPanel.bmpPieces[18] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/bb.gif"));
			ChessPanel.bmpPieces[19] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/bn.gif"));
			ChessPanel.bmpPieces[20] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/br.gif"));
			ChessPanel.bmpPieces[21] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/bc.gif"));
			ChessPanel.bmpPieces[22] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/bp.gif"));
		}catch(Exception e){}
		cp.repaint();
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
