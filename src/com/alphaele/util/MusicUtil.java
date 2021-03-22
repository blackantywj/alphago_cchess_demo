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
	 *  功能：播放音乐
	 *  参数str：代表要播放的音乐文件名
	 */
	public static void playMusic(String str) {
		try {
			inputStream = AudioSystem.getAudioInputStream(new File("src/music/" + str + ".WAV"));
			clip2 = AudioSystem.getClip();
			clip2.open(inputStream);
				
			// 如果是背景音乐则循环播放
			if(str.equals("bgmusic")){
				System.out.println("背景音乐："+clip.hashCode());
				clip2.loop(-1);
			}else{
				clip2.start();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 功能：关闭正在播放的音乐
	 */
	public static void closeMusic(){
		if(clip!=null)
		{
		System.out.println("关闭音乐："+clip.hashCode());
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
			//背景音乐则循环播放
				System.out.println("背景音乐："+clip.hashCode());
				clip.loop(-1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
