package com.alphaele.ai;

/*
 * Zobrist表
 * */
public class Zobrist {
	public static ZobristStruct Player = new ZobristStruct();
	//申请了数组空间，但还需要实例化
	public static ZobristStruct Table[][] = new ZobristStruct[14][256];
}
