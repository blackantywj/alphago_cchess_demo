package com.alphaele.ai;

/*
 * Zobrist�ṹ
 * */
public class ZobristStruct {
	public int dwKey;//��λDWORD���ͣ�������int���
	public int dwLock0;
	public int dwLock1;

	  public void InitZero() {                 // �������Zobrist
	    dwKey = dwLock0 = dwLock1 = 0;
	  }
	  public void InitRC4(RC4 rc4) {        // �����������Zobrist
	    dwKey = rc4.NextLong();
	    dwLock0 = rc4.NextLong();
	    dwLock1 = rc4.NextLong();
	  }
	  public void Xor(final ZobristStruct zobr) { // ִ��XOR����,^��λ���
	    dwKey ^= zobr.dwKey;
	    dwLock0 ^= zobr.dwLock0;
	    dwLock1 ^= zobr.dwLock1;
	  }
	  public void Xor(final ZobristStruct zobr1, final ZobristStruct zobr2) {
	    dwKey ^= zobr1.dwKey ^ zobr2.dwKey;
	    dwLock0 ^= zobr1.dwLock0 ^ zobr2.dwLock0;
	    dwLock1 ^= zobr1.dwLock1 ^ zobr2.dwLock1;
	  }
}
