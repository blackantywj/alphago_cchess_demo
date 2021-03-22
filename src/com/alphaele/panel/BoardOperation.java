package com.alphaele.panel;

/*
 *�����̵ĸ��ֻ������� 
 * */
public class BoardOperation {
	// ���̳�ʼ���ã��ϲ��Ǻ��壬�²��Ǻ���
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
		
		// ��ø����������еĺ����꣬����4λ�൱�ڳ���16
		public static int RANK_Y(int sq) {
		  return sq >> 4;
		}

		// ��ø����������е�������
		public static int FILE_X(int sq) {
		  return sq & 15;
		}
		
		// ����������ͺ������ø�����256�����е��±�
		public static int COORD_XY(int x, int y) {
		  return x + (y << 4);
		}
		
		// ��ת����
		public static int SQUARE_FLIP(int sq) {
		  return 254 - sq;
		}

		// ������ˮƽ����
		public static int FILE_FLIP(int x) {
		  return 14 - x;
		}

		// �����괹ֱ����
		public static int RANK_FLIP(int y) {
		  return 15 - y;
		}

		// ��ú�ڱ��(������8��������16)
		public static int SIDE_TAG(int sd) {
		  return 8 + (sd << 3);
		}

		// ��öԷ���ڱ��
		public static int OPP_SIDE_TAG(int sd) {
		  return 16 - (sd << 3);
		}

		// ����߷������
		public static int SRC(int mv) {
		  return mv & 255;
		}

		// ����߷����յ�
		public static int DST(int mv) {
		  return mv >> 8;
		}

		// ���������յ����߷�
		public static int MOVE(int sqSrc, int sqDst) {
		  return sqSrc + sqDst * 256;
		}
}
