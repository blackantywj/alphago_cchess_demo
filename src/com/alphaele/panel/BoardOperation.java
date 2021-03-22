package com.alphaele.panel;

/*
 *对棋盘的各种基本操作 
 * */
public class BoardOperation {
	// 棋盘初始设置，上部是黑棋，下部是红棋
		public static final byte cucpcStartup[] = {
		  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		  0,  0,  0, 20, 19, 18, 17, 16, 17, 18, 19, 20,  0,  0,  0,  0,
		  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0,
		  0,  0,  0, 22,  0, 22,  0, 22,  0, 22,  0, 22,  0,  0,  0,  0,
		  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		  0,  0,  0, 14,  0, 14,  0, 14,  0, 14,  0, 14,  0,  0,  0,  0,
		  0,  0,  0,  0, 13,  0,  0,  0,  0,  0, 13,  0,  0,  0,  0,  0,
		  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		  0,  0,  0, 12, 11, 10,  9,  8,  9, 10, 11, 12,  0,  0,  0,  0,
		  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0
		};
		
		// 获得格子在棋盘中的横坐标，右移4位相当于除以16
		public static int RANK_Y(int sq) {
		  return sq >> 4;
		}

		// 获得格子在棋盘中的纵坐标
		public static int FILE_X(int sq) {
		  return sq & 15;
		}
		
		// 根据纵坐标和横坐标获得格子在256数组中的下标
		public static int COORD_XY(int x, int y) {
		  return x + (y << 4);
		}
		
		// 翻转格子
		public static int SQUARE_FLIP(int sq) {
		  return 254 - sq;
		}

		// 纵坐标水平镜像
		public static int FILE_FLIP(int x) {
		  return 14 - x;
		}

		// 横坐标垂直镜像
		public static int RANK_FLIP(int y) {
		  return 15 - y;
		}

		// 获得红黑标记(红子是8，黑子是16)
		public static int SIDE_TAG(int sd) {
		  return 8 + (sd << 3);
		}

		// 获得对方红黑标记
		public static int OPP_SIDE_TAG(int sd) {
		  return 16 - (sd << 3);
		}

		// 获得走法的起点
		public static int SRC(int mv) {
		  return mv & 255;
		}

		// 获得走法的终点
		public static int DST(int mv) {
		  return mv >> 8;
		}

		// 根据起点和终点获得走法
		public static int MOVE(int sqSrc, int sqDst) {
		  return sqSrc + sqDst * 256;
		}
}
