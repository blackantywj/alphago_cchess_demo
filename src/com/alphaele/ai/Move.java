package com.alphaele.ai;

/*
 * ��ʷ�߷���Ϣ(ռ4�ֽ�)
 * */
public class Move {
	public int wmv;//��λWORD����
	public int ucpcCaptured;//��Ϊbyte
	public boolean ucbCheck;//��Ϊbyte
	public long dwKey;

	public void Set(int mv, int pcCaptured, boolean bCheck, long dwKey_) {
	  wmv =  mv;
	  ucpcCaptured =  pcCaptured;
	  ucbCheck = bCheck;
	  dwKey = dwKey_;
	}
}
