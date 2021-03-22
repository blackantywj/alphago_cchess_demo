package com.alphaele.ai;

import java.io.InputStream;
import com.alphaele.panel.ChessPanel;
import com.alphaele.panel.Position;
import com.alphaele.panel.Rule;

/**
 * 载入开局库，以及对开局库进行的一系列操作
 *
 */
public class BookItemOp {
	
	//装入开局库
	 public static void LoadBook(RC4 rc4) {
		 InputStream in = null;
		  int index = 0;
			try {
				in = rc4.getClass().getResourceAsStream("/res/BOOK.DAT");//开局库文件
				if(in!=null){
					Search.nBookSize = 12080;
					for (; index < 12080; index++)
					{
						Search.BookTable[index] = new BookItem();
						Search.BookTable[index].dwLock =RC4.readInt(in)>>> 1;//读取4个字节
						Search.BookTable[index].wmv = (short) RC4.readShort(in);//读取2个字节
						Search.BookTable[index].wvl = (short) RC4.readShort(in);//读取2个字节
					}
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	  }
	 
	// 搜索开局库
	public static int SearchBook() {
	   int i, vl, nBookMoves, mv;
	   int mvs[] = new int[Rule.MAX_GEN_MOVES], vls[] = new int[Rule.MAX_GEN_MOVES];
	   boolean bMirror;
	   BookItem bkToSearch = new BookItem(), lpbk = null;
	   int index = 0;//记录lpbk在BookTable中的下标
	   Position posMirror = new Position();
	   // 搜索开局库的过程有以下几个步骤

	   // 1. 如果没有开局库，则立即返回
	   if (Search.nBookSize == 0) {
	     return 0;
	   }
	   // 2. 搜索当前局面
	   bMirror = false;
	   bkToSearch.dwLock = ChessPanel.pos.zobr.dwLock1>>> 1;
	   //查找局面
	   index = RC4.binarySearch(bkToSearch.dwLock, 0, Search.nBookSize);
	   if(index>0){//先判断是否在HashMap中
		   lpbk = Search.BookTable[index];
	   }
	   // 3. 如果没有找到，那么搜索当前局面的镜像局面
	   if (lpbk == null) {
	     bMirror = true;
	     ChessPanel.pos.Mirror(posMirror);
	     bkToSearch.dwLock = posMirror.zobr.dwLock1>>> 1;
	     //查找局面
	     index = RC4.binarySearch(bkToSearch.dwLock, 0, Search.nBookSize);
	     if(index>0){//先判断是否在HashMap中
	    	 lpbk = Search.BookTable[index];
	     }
	   }
	   // 4. 如果镜像局面也没找到，则立即返回
	   if (lpbk == null) {
		   return 0;
	   }
	   // 5. 如果找到，则向前查第一个开局库项
	   while (index >= 0 && lpbk.dwLock == bkToSearch.dwLock) {
	     index --;
	     lpbk = Search.BookTable[index];
	   }
	   index ++;
	   lpbk = Search.BookTable[index];
	   // 6. 把走法和分值写入到"mvs"和"vls"数组中
	   vl = nBookMoves = 0;
	   while (index < Search.nBookSize && lpbk.dwLock == bkToSearch.dwLock) {
		 mv = 0xffff & Search.BookTable[index].wmv;//mv要特殊处理
	     mv = (bMirror ? Rule.MIRROR_MOVE(mv) : mv);
	     if (ChessPanel.pos.LegalMove(mv)) {
	       mvs[nBookMoves] = mv;
	       vls[nBookMoves] = lpbk.wvl;
	       vl += vls[nBookMoves];
	       nBookMoves ++;
	       if (nBookMoves == Rule.MAX_GEN_MOVES) {
	         break; // 防止"BOOK.DAT"中含有异常数据
	       }
	     }
	     index ++;
	     lpbk = Search.BookTable[index];
	   }
	   
	   if (vl == 0) {
	     return 0; // 防止"BOOK.DAT"中含有异常数据
	   }
	   
	   // 7. 根据权重随机选择一个走法
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