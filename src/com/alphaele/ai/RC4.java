package com.alphaele.ai;

import java.io.IOException;
import java.io.InputStream;

/*
 * RC4ÃÜÂëÁ÷Éú³ÉÆ÷
 * */
public class RC4 {
	public int state[];
	public int x;
	public int y;

	public void swap(int i, int j)
	{
		int t = state[i];
		state[i] = state[j];
		state[j] = t;
	}

	public int nextByte()
	{
		x = x + 1 & 0xff;
		y = y + state[x] & 0xff;
		swap(x, y);
		int t = state[x] + state[y] & 0xff;
		return state[t];
	}

	public int NextLong()
	{
		int n0 = nextByte();
		int n1 = nextByte();
		int n2 = nextByte();
		int n3 = nextByte();
		return n0 + (n1 << 8) + (n2 << 16) + (n3 << 24);
	}

	public RC4(byte key[])
	{
		state = new int[256];
		x = 0;
		y = 0;
		for (int i = 0; i < 256; i++)
			state[i] = i;

		int j = 0;
		for (int i = 0; i < 256; i++)
		{
			j = j + state[i] + key[i % key.length] & 0xff;
			swap(i, j);
		}

	}
	
	public static int readShort(InputStream in)
			throws IOException
		{
			int b0 = in.read();
			int b1 = in.read();
			if (b0 == -1 || b1 == -1)
				throw new IOException();
			else
				return b0 | b1 << 8;
		}

	public static int readInt(InputStream in)
			throws IOException
		{
			int b0 = in.read();
			int b1 = in.read();
			int b2 = in.read();
			int b3 = in.read();
			if (b0 == -1 || b1 == -1 || b2 == -1 || b3 == -1)
				throw new IOException();
			else
				return b0 | b1 << 8 | b2 << 16 | b3 << 24;
		}
	
	public static int binarySearch(int vl, int from, int to)
	{
		int low = from;
		for (int high = to - 1; low <= high;)
		{
			int mid = (low + high) / 2;
			if (Search.BookTable[mid].dwLock < vl)
				low = mid + 1;
			else if (Search.BookTable[mid].dwLock > vl)
				high = mid - 1;
			else
				return mid;
		}

		return -1;
	}
}
