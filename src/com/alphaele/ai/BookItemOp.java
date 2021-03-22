package com.alphaele.ai;

import java.io.InputStream;
import com.alphaele.panel.ChessPanel;
import com.alphaele.panel.Position;
import com.alphaele.panel.Rule;

/**
 * ���뿪�ֿ⣬�Լ��Կ��ֿ���е�һϵ�в���
 *
 */
public class BookItemOp {
	
	//װ�뿪�ֿ�
	 public static void LoadBook(RC4 rc4) {
		 InputStream in = null;
		  int index = 0;
			try {
				in = rc4.getClass().getResourceAsStream("/res/BOOK.DAT");//���ֿ��ļ�
				if(in!=null){
					Search.nBookSize = 12080;
					for (; index < 12080; index++)
					{
						Search.BookTable[index] = new BookItem();
						Search.BookTable[index].dwLock =RC4.readInt(in)>>> 1;//��ȡ4���ֽ�
						Search.BookTable[index].wmv = (short) RC4.readShort(in);//��ȡ2���ֽ�
						Search.BookTable[index].wvl = (short) RC4.readShort(in);//��ȡ2���ֽ�
					}
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	  }
	 
	// �������ֿ�
	public static int SearchBook() {
	   int i, vl, nBookMoves, mv;
	   int mvs[] = new int[Rule.MAX_GEN_MOVES], vls[] = new int[Rule.MAX_GEN_MOVES];
	   boolean bMirror;
	   BookItem bkToSearch = new BookItem(), lpbk = null;
	   int index = 0;//��¼lpbk��BookTable�е��±�
	   Position posMirror = new Position();
	   // �������ֿ�Ĺ��������¼�������

	   // 1. ���û�п��ֿ⣬����������
	   if (Search.nBookSize == 0) {
	     return 0;
	   }
	   // 2. ������ǰ����
	   bMirror = false;
	   bkToSearch.dwLock = ChessPanel.pos.zobr.dwLock1>>> 1;
	   //���Ҿ���
	   index = RC4.binarySearch(bkToSearch.dwLock, 0, Search.nBookSize);
	   if(index>0){//���ж��Ƿ���HashMap��
		   lpbk = Search.BookTable[index];
	   }
	   // 3. ���û���ҵ�����ô������ǰ����ľ������
	   if (lpbk == null) {
	     bMirror = true;
	     ChessPanel.pos.Mirror(posMirror);
	     bkToSearch.dwLock = posMirror.zobr.dwLock1>>> 1;
	     //���Ҿ���
	     index = RC4.binarySearch(bkToSearch.dwLock, 0, Search.nBookSize);
	     if(index>0){//���ж��Ƿ���HashMap��
	    	 lpbk = Search.BookTable[index];
	     }
	   }
	   // 4. ����������Ҳû�ҵ�������������
	   if (lpbk == null) {
		   return 0;
	   }
	   // 5. ����ҵ�������ǰ���һ�����ֿ���
	   while (index >= 0 && lpbk.dwLock == bkToSearch.dwLock) {
	     index --;
	     lpbk = Search.BookTable[index];
	   }
	   index ++;
	   lpbk = Search.BookTable[index];
	   // 6. ���߷��ͷ�ֵд�뵽"mvs"��"vls"������
	   vl = nBookMoves = 0;
	   while (index < Search.nBookSize && lpbk.dwLock == bkToSearch.dwLock) {
		 mv = 0xffff & Search.BookTable[index].wmv;//mvҪ���⴦��
	     mv = (bMirror ? Rule.MIRROR_MOVE(mv) : mv);
	     if (ChessPanel.pos.LegalMove(mv)) {
	       mvs[nBookMoves] = mv;
	       vls[nBookMoves] = lpbk.wvl;
	       vl += vls[nBookMoves];
	       nBookMoves ++;
	       if (nBookMoves == Rule.MAX_GEN_MOVES) {
	         break; // ��ֹ"BOOK.DAT"�к����쳣����
	       }
	     }
	     index ++;
	     lpbk = Search.BookTable[index];
	   }
	   
	   if (vl == 0) {
	     return 0; // ��ֹ"BOOK.DAT"�к����쳣����
	   }
	   
	   // 7. ����Ȩ�����ѡ��һ���߷�
	   vl = ((int)(Math.random()*32767))% vl;
	   for (i = 0; i < nBookMoves; i ++) {
	     vl -= vls[i];
	     if (vl < 0) {
	       break;
	     }
	   }
	   return mvs[i];
	 }
}