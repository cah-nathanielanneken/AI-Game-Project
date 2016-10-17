/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package hex;
import game.*;
import java.util.*;

public class HexMove extends GameMove {
	public int row, col;

	public HexMove()
	{
		super();
	}
	public HexMove(HexMove m)
	{
		row = m.row;
		col = m.col;
	}
	public static boolean posOK(int r, int c)
	{ return Util.inrange(r, 0, HexState.N-1) && Util.inrange(c, 0, HexState.N-1); }
	public static boolean connected(int r1, int c1, int r2, int c2)
	{
		return false;
	}
	public HexMove(int r, int c)
	{
		row = r; col = c;
		if (!posOK(row, col)) {
			System.err.printf("problem in Hex ctor: %s\n", this);
		}
	}
    public Object clone()
    { return new HexMove(row, col); }
	public String toString()
	{ return "" + row + "-" + col; }
	public int hashCode()
	{ return row + col; }
	public boolean equals(Object other)
	{
		if (other == null) {
			return false;
		} else if (!(other instanceof HexMove)) {
			return false;
		} else {
			HexMove mv = (HexMove)other;
			return row == mv.row && col == mv.col;
		}
	}
	public void parseMove(String s)
	{
		StringTokenizer toks = new StringTokenizer(s, "-");
		row = Integer.parseInt(toks.nextToken());
		col = Integer.parseInt(toks.nextToken());
	}
}
