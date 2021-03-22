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
 * 本类是菜单
 */
public class ChessMenu implements ActionListener{
	/*
	 * 初始面板
	 */
	/**
	 * 模式组
	 */
	public static ArrayList <JCheckBox> LevelGroup = new ArrayList<JCheckBox>();//模式组
	/**
	 * 背景音乐组
	 */
	ArrayList <JCheckBox> MusicGroup =new ArrayList<JCheckBox>();//音乐
	/**
	 * 棋盘选项组
	 */
	ArrayList <JCheckBox> BoaGroup =new ArrayList<JCheckBox>();//棋子
	/**
	 * 棋子选项组
	 */
	ArrayList <JCheckBox> PieGroup =new ArrayList<JCheckBox>();//棋盘
	ChessPanel cp = null;
	//构造方法中传入ChessPanel
	public ChessMenu(ChessPanel cp){
		this.cp = cp;
	}

	/**
	 * 功能：创建菜单栏及菜单栏中各组件的监听事件
	 */
	public JMenuBar getChessMenu(){
		// 创建菜单栏
		JMenuBar menuBar = new JMenuBar();
		// 创建菜单
		JMenu fileMenu =new JMenu("文件(F)");
		fileMenu.setMnemonic('F');
		JMenu jumianMenu =new JMenu("局面(A)");
		jumianMenu.setMnemonic('A');
		JMenu computerMenu =new JMenu("电脑(C)");
		computerMenu.setMnemonic('C');
		JMenu chooseMenu =new JMenu("选项(O)");
		chooseMenu.setMnemonic('O');
		JMenu helpMenu = new JMenu("帮助(H)");
		helpMenu.setMnemonic('H');
		// 创建菜单项
		//文件*********
		JMenuItem restartItem = new JMenuItem("新的局面(N)");
		restartItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N,java.awt.Event.CTRL_MASK));  
		restartItem.setMnemonic('N');
		restartItem.addActionListener(this);
		JMenuItem openItem = new JMenuItem("打开(O)");
		openItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,java.awt.Event.CTRL_MASK));
		openItem.setMnemonic('O');
		openItem.addActionListener(this);
		JMenuItem saveItem = new JMenuItem("保存(S)");
		saveItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,java.awt.Event.CTRL_MASK));
		saveItem.setMnemonic('S');
		saveItem.addActionListener(this);
		JMenuItem exitItem = new JMenuItem("退出(X)");
		exitItem.setMnemonic('X');
		exitItem.addActionListener(this);
		//局面******
		JMenuItem changeFirstItem = new JMenuItem("交换先手(E)");
		changeFirstItem.setMnemonic('E');
		changeFirstItem.addActionListener(this);
		JMenuItem preItem = new JMenuItem("预测下一步(P)");
		preItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P,java.awt.Event.CTRL_MASK));
		preItem.setMnemonic('P');
		preItem.addActionListener(this);
		JMenuItem accessItem = new JMenuItem("局势估计(A)");
		accessItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,java.awt.Event.CTRL_MASK));
		accessItem.setMnemonic('A');
		accessItem.addActionListener(this);
		//电脑********
		JMenu levelSetMenu = new JMenu("等级设置(L)");
		levelSetMenu.setMnemonic('L');
		JCheckBox newcommerItem = new JCheckBox("初级水平(A)",false);
		newcommerItem.setMnemonic('A');
		LevelGroup.add(newcommerItem);
		JCheckBox not_newcommerItem = new JCheckBox("高级水平(B)",true);
		not_newcommerItem.setMnemonic('B');
		LevelGroup.add(not_newcommerItem);
		JCheckBox trainModeItem = new JCheckBox("练习模式(P)",false);
		trainModeItem.setMnemonic('P');
		LevelGroup.add(trainModeItem);
		//设置组的共有属性
		for(int i=0;i<LevelGroup.size();i++)
		{
			LevelGroup.get(i).setBorderPaintedFlat(false);
			LevelGroup.get(i).setBorderPainted(false);
			LevelGroup.get(i).addActionListener(this);
		}
		//选项*********
		//音乐
		JMenu musicMenu = new JMenu("背景音乐(M)");
		musicMenu.setMnemonic('M');
		JCheckBox xianjianItem=new JCheckBox("仙剑(A)");
		xianjianItem.setMnemonic('A');
		xianjianItem.addActionListener(this);
		MusicGroup.add(xianjianItem);
		JCheckBox gaoshanItem=new JCheckBox("高山流水(B)");
		gaoshanItem.setMnemonic('B');
		gaoshanItem.addActionListener(this);
		MusicGroup.add(gaoshanItem);
		//棋盘
		JMenu boardMenu = new JMenu("棋盘(B)");
		boardMenu.setMnemonic('B');
		JCheckBox canvasItem = new JCheckBox("帆布(A)");
		canvasItem.setMnemonic('A');
		BoaGroup.add(canvasItem);
		JCheckBox dropsItem = new JCheckBox("水蓝(B)");
		dropsItem.setMnemonic('B');
		BoaGroup.add(dropsItem);
		JCheckBox greenItem = new JCheckBox("墨绿(C)");
		greenItem.setMnemonic('C');
		BoaGroup.add(greenItem);
		JCheckBox qianhongItem = new JCheckBox("灰绿(D)");
		qianhongItem.setMnemonic('D');
		BoaGroup.add(qianhongItem);
		JCheckBox sheetItem = new JCheckBox("银白(E)");
		sheetItem.setMnemonic('E');
		BoaGroup.add(sheetItem);
		JCheckBox whiteItem = new JCheckBox("大理石(F)");
		whiteItem.setMnemonic('F');
		BoaGroup.add(whiteItem);
		JCheckBox woodItem = new JCheckBox("木制(G)");
		woodItem.setMnemonic('G');
		woodItem.setSelected(true);
		BoaGroup.add(woodItem);
		//共有属性
		for(int i=0;i<BoaGroup.size();i++)
		{
			BoaGroup.get(i).setBorderPaintedFlat(false);
			BoaGroup.get(i).setBorderPainted(false);
			BoaGroup.get(i).addActionListener(this);
		}
		//棋子
		JMenu pieceMenu = new JMenu("棋子(P)");
		pieceMenu.setMnemonic('P');
		JCheckBox woodpItem = new JCheckBox("木制(A)");
		woodItem.setMnemonic('A');
		woodpItem.setSelected(true);
		PieGroup.add(woodpItem);
		JCheckBox delicateItem = new JCheckBox("精致(B)");
		delicateItem.setMnemonic('B');
		PieGroup.add(delicateItem);
		JCheckBox polishItem = new JCheckBox("光滑(C)");
		polishItem.setMnemonic('C');
		PieGroup.add(polishItem);
		//共有属性
		for(int i=0;i<PieGroup.size();i++)
		{
			PieGroup.get(i).setBorderPaintedFlat(false);
			PieGroup.get(i).setBorderPainted(false);
			PieGroup.get(i).addActionListener(this);
		}
		//帮助*******
		JMenuItem updateItem = new JMenuItem("更新(U)");
		updateItem.setMnemonic('U');
		updateItem.addActionListener(this);
		JMenuItem aboutItem = new JMenuItem("关于(A)");
		aboutItem.setMnemonic('A');
		aboutItem.addActionListener(this);
		// 把菜单项添加到菜单
		//文件
		fileMenu.add(restartItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(exitItem);
		fileMenu.addSeparator();
		//局面
		jumianMenu.add(changeFirstItem);
		jumianMenu.add(accessItem);
		jumianMenu.add(preItem);
		//模式
		//等级
		computerMenu.add(levelSetMenu);
		levelSetMenu.add(newcommerItem);
		levelSetMenu.add(not_newcommerItem);
		computerMenu.add(trainModeItem);
		//选项
		chooseMenu.add(musicMenu);//音乐
		musicMenu.add(xianjianItem);
		musicMenu.add(gaoshanItem);
		chooseMenu.add(boardMenu);//棋盘
		boardMenu.add(canvasItem);
		boardMenu.add(dropsItem);
		boardMenu.add(greenItem);
		boardMenu.add(qianhongItem);
		boardMenu.add(sheetItem);
		boardMenu.add(whiteItem);
		boardMenu.add(woodItem);
		chooseMenu.add(pieceMenu);//棋子
		pieceMenu.add(woodpItem);
		pieceMenu.add(delicateItem);
		pieceMenu.add(polishItem);
		helpMenu.add(updateItem);
		helpMenu.add(aboutItem);
		// 把菜单添加到菜单栏
		menuBar.add(fileMenu);
		menuBar.add(jumianMenu);
		menuBar.add(computerMenu);
		menuBar.add(chooseMenu);
		menuBar.add(helpMenu);
		// 返回菜单栏
		return menuBar;	
	}


	/**
	 * 功能：显示关于本程序的相关信息
	 */
	public void aboutGame(){
		try {
			// 创建图片标签
			JLabel label = new JLabel(new ImageIcon(ImageIO.read(ChessMenu.class.getResourceAsStream("/music/chess.jpg"))));
			// 创建文本域
			JTextArea ablutText = new JTextArea();
			// 设置不可编辑
			ablutText.setEditable(false);
			// 设置字体
			ablutText.setFont(FontUtil.myFont1);
			// 设置内容
			ablutText.setText("AlphaEle Chinese Chess \n\n\n河海大学计算机与信息学院14级本科生");
			// 创建对话框
			JDialog dialog = new JDialog();			
			// 把图片标签添加到对话框西边
			dialog.add(label,BorderLayout.WEST);
			// 把文本域添加到对话框中间
			dialog.add(new JScrollPane(ablutText),BorderLayout.CENTER);
			// 设置窗口图标
			dialog.setIconImage(ImageIO.read(ChessMenu.class.getResourceAsStream("/music/chess.jpg")));
			dialog.setTitle("关于中国象棋");
			// 设置窗口大小
			dialog.setSize(480, 160);
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
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("交换先手(E)")){	
			changeFirst();
		}else if(e.getActionCommand().equals("新的局面(N)")){	
			restartGame();
		}else if(e.getActionCommand().equals("打开(O)")){	
			openGame();
		}else if(e.getActionCommand().equals("保存(S)")){
			saveGame();
		}else if(e.getActionCommand().equals("局势估计(A)")){
			Access();
		}else if(e.getActionCommand().equals("预测下一步(P)")){
			Pre();
		}else if(e.getActionCommand().equals("退出(X)")){
			System.exit(0);
		}else if(e.getActionCommand().equals("更新(U)")){
			updateGame();
		}else if(e.getActionCommand().equals("关于(A)")){
			aboutGame();
		}
		else if(e.getActionCommand().equals("初级水平(A)")){
			newCommerMode();
		}else if(e.getActionCommand().equals("高级水平(B)")){
			notNewCommerMode();
		}else if(e.getActionCommand().equals("练习模式(P)")){
			TrainingMode();
		}
		//背景音乐
		else if(e.getActionCommand().equals("仙剑(A)")){
			playbgMusic("仙剑(A)","xianjian");
		}else if(e.getActionCommand().equals("高山流水(B)")){
			playbgMusic("高山流水(B)","GAOSHAN");
		}
		//界面选择，棋盘
		else if(e.getActionCommand().equals("帆布(A)")){
			setBoard("帆布(A)","canvas");
		}
		else if(e.getActionCommand().equals("水蓝(B)")){
			setBoard("水蓝(B)","drops");
		}
		else if(e.getActionCommand().equals("墨绿(C)")){
			setBoard("墨绿(C)","green");
		}
		else if(e.getActionCommand().equals("灰绿(D)")){
			setBoard("灰绿(D)","qianhong");
		}
		else if(e.getActionCommand().equals("银白(E)")){
			setBoard("银白(E)","sheet");
		}
		else if(e.getActionCommand().equals("大理石(F)")){
			setBoard("大理石(F)","white");
		}
		else if(e.getActionCommand().equals("木制(G)")){
			setBoard("木制(G)","wood");
		}

		//选择棋子
		else if(e.getActionCommand().equals("木制(A)")){
			setPiece("木制(A)","wood");
		}
		else if(e.getActionCommand().equals("精致(B)")){
			setPiece("精致(B)","delicate");
		}
		else if(e.getActionCommand().equals("光滑(C)")){
			setPiece("光滑(C)","polish");
		}
	}
	/**
	 * 功能：播放背景音乐，以及音乐选项，图标显示设定
	 * @param string1 音乐选项标志
	 * @param string2 音乐播放的文件名
	 */
	private void playbgMusic(String string1,String string2) {
		for(int i=0;i<MusicGroup.size();i++)
		{
			//选中
			if(MusicGroup.get(i).getText().equals(string1))
			{
				MusicGroup.get(i).setSelected(true);

			}
			//不选中
			else
			{
				MusicGroup.get(i).setSelected(false);
			}
		}
		imgChessMenu.flag=1;
		imgChessMenu.musicButton.setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/bgmusic1.png")));
		imgChessMenu.musicButton.setActionCommand("关闭音乐");
		imgChessMenu.musicButton.setToolTipText("暂停背景音乐");//提示信息
		MusicUtil.playbgMusic(string2);
	}
	/**
	 * 功能：实现练习模式设定，以及选项和按钮的状态改变
	 * 练习模式下标为（2）
	 */
	private void TrainingMode() {
		//练习
		ChessMenu.LevelGroup.get(2).setSelected(true);
		imgChessMenu.ModelButton.get(2).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/train_1.png")));
		//初级
		imgChessMenu.ModelButton.get(0).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level1.png")));
		ChessMenu.LevelGroup.get(0).setSelected(false);
		//高级
		imgChessMenu.ModelButton.get(1).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level2.png")));
		ChessMenu.LevelGroup.get(1).setSelected(false);
		//执行练习模式
		ChessPanel.trainingMode = true;
	}

	/**
	 * 功能：实现高级水平设定，以及选项和按钮的状态改变
	 * 高级AI水平下标为（1）
	 */
	private void notNewCommerMode() {
		//高级
		ChessMenu.LevelGroup.get(1).setSelected(true);
		imgChessMenu.ModelButton.get(1).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level2_1.png")));
		//初级
		imgChessMenu.ModelButton.get(0).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level1.png")));
		ChessMenu.LevelGroup.get(0).setSelected(false);
		//练习
		imgChessMenu.ModelButton.get(2).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/train.png")));
		ChessMenu.LevelGroup.get(2).setSelected(false);
		//执行高级模式
		ChessPanel.newcommer = false;
		ChessPanel.trainingMode = false;
		//restartGame();
	}

	/**
	 *功能：实现初级水平设定，以及选项和按钮的状态改变
	 * 初级AI水平下标为（0）
	 */
	private void newCommerMode() {
		//初级
		ChessMenu.LevelGroup.get(0).setSelected(true);
		imgChessMenu.ModelButton.get(0).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level1_1.png")));
		//高级
		imgChessMenu.ModelButton.get(1).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level2.png")));
		ChessMenu.LevelGroup.get(1).setSelected(false);
		//练习
		imgChessMenu.ModelButton.get(2).setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/train.png")));
		ChessMenu.LevelGroup.get(2).setSelected(false);
		//执行初级
		ChessPanel.newcommer = true;
		ChessPanel.trainingMode = false;
		//restartGame();
	}
	/**
	 *功能： 棋盘选择
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
	 * 功能：棋子图片选择
	 * @param string1 图片标志
	 * @param string2 图片文件名
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
			ChessPanel.bmpSelected = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/oos.gif"));//选择框

			//红色
			ChessPanel.bmpPieces[8] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rk.gif"));
			ChessPanel.bmpPieces[9] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/ra.gif"));
			ChessPanel.bmpPieces[10] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rb.gif"));
			ChessPanel.bmpPieces[11] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rn.gif"));
			ChessPanel.bmpPieces[12] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rr.gif"));
			ChessPanel.bmpPieces[13] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rc.gif"));
			ChessPanel.bmpPieces[14] = ImageIO.read(ChessMenu.class.getResourceAsStream("/"+string2+"/rp.gif"));

			//蓝色
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
	 * 功能：预测下一步
	 * */
	private void Pre() {
		if(!ChessPanel.bGameOver){
			Graphics g =  cp.getGraphics();
			ChessPanel.ResponseMove(g);
			cp.repaint();
		}
	}

	/**
	 * 功能：局势估计
	 */
	private void Access() {
		for(int i=0; i<256; i++){
			if(ChessPanel.pos.ucpcSquares[i]!=0){
				int pc = ChessPanel.pos.ucpcSquares[i];//获得棋子的种类
				if (pc < 16) {
					ChessPanel.pos.vlWhite += PieceValue.cucvlPiecePos[pc - 8][i];
				} else {
					ChessPanel.pos.vlBlack += PieceValue.cucvlPiecePos[pc - 16][BoardOperation.SQUARE_FLIP(i)];//黑方
				}
			}
		}

		if(ChessPanel.pos.vlWhite > ChessPanel.pos.vlBlack){
			JOptionPane.showMessageDialog(null, "红方估分："+ChessPanel.pos.vlWhite+"\n"
					+"黑方估分："+ChessPanel.pos.vlBlack+"\n"+"红方占上风!");
		}
		else if(ChessPanel.pos.vlWhite < ChessPanel.pos.vlBlack){
			JOptionPane.showMessageDialog(null, "红方估分："+ChessPanel.pos.vlWhite+"\n"
					+"黑方估分："+ChessPanel.pos.vlBlack+"\n"+"黑方占上风!");
		}
		else{
			JOptionPane.showMessageDialog(null, "红方估分："+ChessPanel.pos.vlWhite+"\n"
					+"黑方估分："+ChessPanel.pos.vlBlack+"\n"+"红黑双方平分秋色");
		}
	}

	/**
	 * 重新开始
	 */
	private void restartGame() {
		MusicUtil.playMusic("NEWGAME");
		ChessPanel.Startup();

		if(ChessPanel.bFlipped == true){//交换先手时ai先落子
			Graphics g =  cp.getGraphics();
			ChessPanel.ResponseMove(g);
		}
		cp.repaint();
	}

	/**
	 * 交换先手
	 */
	private void changeFirst() {
		ChessPanel.bFlipped = !ChessPanel.bFlipped;
		ChessPanel.Startup();
		if(ChessPanel.bFlipped == true){//交换先手时ai先落子
			Graphics g =  cp.getGraphics();
			ChessPanel.ResponseMove(g);
		}
		cp.repaint();
	}

	/**
	 * 功能：更新
	 */
	private void updateGame() {
		JOptionPane.showMessageDialog(null, "暂无更新！");
	}


	/**
	 * 功能：保存棋盘
	 */
	public void saveGame(){
		try {
			JOptionPane.showMessageDialog(null, "选择一个文件保存棋盘数据");
			// 创建文件选择器
			JFileChooser fileChooser = new JFileChooser();
			// 显示保存文件对话框
			fileChooser.showSaveDialog(null);
			// 获取保存的文件名
			String filename = fileChooser.getSelectedFile().getAbsolutePath();
			FileWriter fw=new FileWriter(filename);
			PrintWriter pw=new PrintWriter(fw,true);
			pw.println(ChessPanel.bFlipped);//写入是否翻转棋盘
			pw.println(ChessPanel.pos.sdPlayer);//写入先手
			for(int i=0; i<256; i++){
				pw.print(ChessPanel.pos.ucpcSquares[i]+" ");
				if(i%16 == 0 && i!=0){
					pw.println();
				}
			}
			pw.close();
			fw.close();
			JOptionPane.showMessageDialog(null, "棋盘保存成功！");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "存盘失败！");
		} 
	}


	/**
	 * 功能：打开棋盘，预测下一步
	 */
	private void openGame() {
		try {
			JOptionPane.showMessageDialog(null, "打开棋盘文件，文件要求：\n第一行为棋盘是否翻转；\n第二行为先手方；\n第三行至第十九行为16x16的棋盘数组，空格为数组的分隔符。");
			// 创建文件选择器
			JFileChooser fileChooser = new JFileChooser();
			// 显示打开文件对话框
			fileChooser.showOpenDialog(null);
			// 获取打开的文件绝对路径
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
			JOptionPane.showMessageDialog(null, "读取存盘失败！");
		} 
	}
}
