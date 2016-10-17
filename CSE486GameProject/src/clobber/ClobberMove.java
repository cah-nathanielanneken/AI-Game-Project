/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package clobber;
import java.util.StringTokenizer;

import game.*;


public class ClobberMove extends GameMove {
	public int row1, col1;
	public int row2, col2;

	public ClobberMove()
	{
		super();
	}
	public ClobberMove(ClobberMove m)
	{
		row1 = m.row1;
		col1 = m.col1;
		row2 = m.row2;
		col2 = m.col2;
	}
	public static boolean posOK(int r, int c)
	{ return Util.inrange(r, 0, ClobberState.ROWS-1) && Util.inrange(c, 0, ClobberState.COLS-1); }
	public ClobberMove(int r1, int c1, int r2, int c2)
	{
		row1 = r1; col1 = c1; row2 = r2; col2 = c2;
		if (!posOK(row1, col2) || !posOK(row2, col2)) {
			System.err.printf("problem in Clobber ctor: %d %d %d %d", r1, c1, r2, c2);
		}
	}
    public Object clone()
    { return new ClobberMove(row1, col1, row2, col2); }
	public String toString()
	{ return row1 + " " + col1 + " " + row2 + " " + col2; }
	public void parseMove(String s)
	{
		StringTokenizer toks = new StringTokenizer(s);
		row1 = Integer.parseInt(toks.nextToken());
		col1 = Integer.parseInt(toks.nextToken());
		row2 = Integer.parseInt(toks.nextToken());
		col2 = Integer.parseInt(toks.nextToken());
	}
}
