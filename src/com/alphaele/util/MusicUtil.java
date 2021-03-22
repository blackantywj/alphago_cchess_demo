package com.alphaele.util;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MusicUtil {

	private static boolean bgMusic = false;
	private static boolean gameMusic = true;
	private static AudioInputStream inputStream;
	public  static Clip clip = null;
	public static Clip clip2=null;
	
	/*
	 *  ���ܣ���������
	 *  ����str������Ҫ���ŵ������ļ���
	 */
	public static void playMusic(String str) {
		try {
			inputStream = AudioSystem.getAudioInputStream(new File("src/music/" + str + ".WAV"));
			clip2 = AudioSystem.getClip();
			clip2.open(inputStream);
				
			// ����Ǳ���������ѭ������
			if(str.equals("bgmusic")){
				System.out.println("�������֣�"+clip.hashCode());
				clip2.loop(-1);
			}else{
				clip2.start();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * ���ܣ��ر����ڲ��ŵ�����
	 */
	public static void closeMusic(){
		if(clip!=null)
		{
		System.out.println("�ر����֣�"+clip.hashCode());
		clip.close();
		}
		else
		{}
	}
	
	
	public static boolean isBgMusic() {
		return bgMusic;
	}

	public static void setBgMusic(boolean bgMusic) {
		MusicUtil.bgMusic = bgMusic;
	}

	public static boolean isGameMusic() {
		return gameMusic;
	}

	public static void setGameMusic(boolean gameMusic) {
		MusicUtil.gameMusic = gameMusic;
	}

	public static void playbgMusic(String string) {
		try {
			closeMusic();
			System.out.println("/bgmusic/" + string + ".WAV");
			inputStream = AudioSystem.getAudioInputStream(new File("src/bgmusic/" + string + ".wav"));
			clip = AudioSystem.getClip();
			clip.open(inputStream);
			//����������ѭ������
				System.out.println("�������֣�"+clip.hashCode());
				clip.loop(-1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
