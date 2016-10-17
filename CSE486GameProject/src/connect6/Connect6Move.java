/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package connect6;
import game.GameMove;

import java.util.*;

public class Connect6Move extends GameMove {
	public int row1, col1, row2, col2;

	public static boolean indexOK(int v)
	{ return v >= 0 && v < Connect6State.N; }
	public Connect6Move()
	{
	}
	public Connect6Move(int r1, int c1, int r2, int c2)
	{
		row1 = r1; col1 = c1; row2 = r2; col2 = c2;
		if (!indexOK(row1) || !indexOK(col1) ||
			!indexOK(row2) || !indexOK(col2)) {
				System.err.println("problem");
		}
	}
    public Object clone()
    { return new Connect6Move(row1, col1, row2, col2); }
	
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
