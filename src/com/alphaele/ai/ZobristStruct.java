package com.alphaele.ai;

/*
 * Zobrist结构
 * */
public class ZobristStruct {
	public int dwKey;//本位DWORD类型，这里用int替代
	public int dwLock0;
	public int dwLock1;

	  public void InitZero() {                 // 用零填充Zobrist
	    dwKey = dwLock0 = dwLock1 = 0;
	  }
	  public void InitRC4(RC4 rc4) {        // 用密码流填充Zobrist
	    dwKey = rc4.NextLong();
	    dwLock0 = rc4.NextLong();
	    dwLock1 = rc4.NextLong();
	  }
	  public void Xor(final ZobristStruct zobr) { // 执行XOR操作,^按位异或
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
