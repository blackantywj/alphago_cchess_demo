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
 * 本类是菜单
 */
public class imgChessMenu implements ActionListener{
	/**
	 * 初始面板
	 */
	ChessPanel cp = null;
	public static  int flag=1;
	public static  JButton musicButton=null;
	public static ArrayList <JButton> ModelButton=new ArrayList<JButton>();
	private Clip clip=null;

	//构造方法中传入ChessPanel
	public imgChessMenu(ChessPanel cp){
		this.cp = cp;
	}

	/**
	 * 功能：创建菜单栏及菜单栏中各组件的监听事件
	 * @throws IOException 
	 */
	public JMenuBar getChessMenu() throws IOException{
		// 创建菜单栏
		JMenuBar menuBar = new JMenuBar();
		//menuBar.setBackground(Color.gray);
		// 创建菜单项，一个一个按钮，首先设置每个按钮的特有属性，以及事件处理，完了最后统一处理共有属性
		menuBar.setLayout(new FlowLayout(0,10,0));//流式布局，左对齐，水平间距0，垂直间距0
		ArrayList <JButton>buttonlist=new ArrayList<JButton>();
		//新建
		JButton newButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/new.png")));
		newButton.setToolTipText("新的对局");//提示信息
		newButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restartGame();
			}
		});
		buttonlist.add(newButton);
		//打开
		JButton openButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/open.png")));
		openButton.setToolTipText("打开棋局");//提示信息
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openGame();
			}
		});

		buttonlist.add(openButton);
		//保存
		JButton saveButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/save.png")));
		saveButton.setToolTipText("保存棋局");//提示信息
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveGame();
			}
		});
		buttonlist.add(saveButton);
		//交换先手
		JButton changeButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/change.png")));
		changeButton.setToolTipText("交换先手");//提示信息
		changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeFirst();
			}
		});
		buttonlist.add(changeButton);
		//预测下一步
		JButton nextButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/next.png")));
		nextButton.setToolTipText("预测下一步");//提示信息
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Pre();
			}
		});
		buttonlist.add(nextButton);
		//局势估计
		JButton accessButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/access.png")));
		accessButton.setToolTipText("局势估计");//提示信息
		accessButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Access();
			}
		});
		buttonlist.add(accessButton);
		//初级模式
		JButton newcommerButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level1.png")));
		newcommerButton.setToolTipText("初级模式");//提示信息
		newcommerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newCommerMode();
			}
		});
		ModelButton.add(newcommerButton);
		buttonlist.add(newcommerButton);
		//高级模式
		JButton not_newcommerButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/level2_1.png")));
		not_newcommerButton.setToolTipText("高级模式");//提示信息
		not_newcommerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				notNewCommerMode();
			}
		});
		ModelButton.add(not_newcommerButton);
		buttonlist.add(not_newcommerButton);
		//练习模式
		JButton trainButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/train.png")));
		trainButton.setToolTipText("练习模式");//提示信息
		trainButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TrainingMode();
			}
		});
		ModelButton.add(trainButton);
		buttonlist.add(trainButton);
		//背景音乐
		ImageIcon icon1=new ImageIcon(imgChessMenu.class.getResource("/menuImg/bgmusic1.png"));//暂停图标
		ImageIcon icon2=new ImageIcon(imgChessMenu.class.getResource("/menuImg/bgmusic2.png"));	//播放图标		
		musicButton=new JButton(icon1);
		musicButton.setActionCommand("关闭");
		musicButton.setToolTipText("暂停背景音乐");//提示信息
		musicButton.addActionListener(this);
		buttonlist.add(musicButton);
		//关于
		JButton aboutButton=new JButton(new ImageIcon(imgChessMenu.class.getResource("/menuImg/about.png")));
		aboutButton.setToolTipText("关于");//提示信息
		aboutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aboutGame();
			}
		});
		buttonlist.add(aboutButton);
		//往menuBar添加按钮，并设置统一属性
		for(int i=0;i<buttonlist.size();i++)
		{
			buttonlist.get(i).setMaximumSize(new Dimension(64,64));//图标最大
			buttonlist.get(i).setBorderPainted(false);//按钮边框不显示
			buttonlist.get(i).setFocusable(false);
			buttonlist.get(i).setContentAreaFilled(false);
			buttonlist.get(i).setMinimumSize(new Dimension(64,64));
			menuBar.add(buttonlist.get(i));
		}
		// 创建菜单项
		return menuBar;	
	}


	/**
	 * 功能：显示关于本程序的相关信息
	 */
	public void aboutGame(){
		try {
			// 创建图片标签
			JLabel label = new JLabel(new ImageIcon(ImageIO.read(imgChessMenu.class.getResourceAsStream("/music/chess.jpg"))));
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
			dialog.setIconImage(ImageIO.read(imgChessMenu.class.getResourceAsStream("/music/chess.jpg")));
			dialog.setTitle("关于AlphaEle1.0");
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

	/**
	 * 功能：专门用于控制音乐暂停与播放
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		clip=MusicUtil.clip;
		if(clip!=null)
			if(this.flag++%2==1)//停止播放
			{musicButton.setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/bgmusic2.png")));
			musicButton.setActionCommand("打开");
			musicButton.setToolTipText("继续播放");//提示信息

			clip.stop();	
			}
			else
			{
				musicButton.setIcon(new ImageIcon(imgChessMenu.class.getResource("/menuImg/bgmusic1.png")));
				musicButton.setActionCommand("关闭");
				musicButton.setToolTipText("暂停背景音乐");//提示信息
				clip.loop(-1);;
			}

		else
		{

		}
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
