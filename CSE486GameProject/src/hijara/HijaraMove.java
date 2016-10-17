/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package hijara;
import java.util.StringTokenizer;

import game.*;


public class HijaraMove extends GameMove {
	public int row, col;

	public HijaraMove()
	{
		super();
	}
	public HijaraMove(HijaraMove m)
	{
		row = m.row;
		col = m.col;
	}
	public static boolean posOK(int r, int c)
	{ return Util.inrange(r, 0, HijaraState.ROWS-1) && Util.inrange(c, 0, HijaraState.COLS-1); }
	public HijaraMove(int r, int c)
	{
		row = r; col = c;
		if (!posOK(row, col)) {
			System.err.printf("problem in Hajara ctor: %d %d", row, col);
		}
	}
    public Object clone()
    { return new HijaraMove(row, col); }
	public String toString()
	{ return row + " " + col; }
	public int hashCode()
	{ return row << 4 + col; }
	public boolean equals(Object other)
	{
		if (other == null) {
			return false;
		} else if (!(other instanceof HijaraMove)) {
			return false;
		} else {
			HijaraMove mv = (HijaraMove)other;
			return row == mv.row && col == mv.col;
		}
	}
	public void parseMove(String s)
	{
		StringTokenizer toks = new StringTokenizer(s);
		row = Integer.parseInt(toks.nextToken());
		col = Integer.parseInt(toks.nextToken());
	}
}
