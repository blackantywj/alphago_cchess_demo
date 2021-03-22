package com.alphaele.ai;

/*
 * 历史走法信息(占4字节)
 * */
public class Move {
	public int wmv;//本位WORD类型
	public int ucpcCaptured;//本为byte
	public boolean ucbCheck;//本为byte
	public long dwKey;

	public void Set(int mv, int pcCaptured, boolean bCheck, long dwKey_) {
	  wmv =  mv;
	  ucpcCaptured =  pcCaptured;
	  ucbCheck = bCheck;
	  dwKey = dwKey_;
	}
}
