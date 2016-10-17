/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package clobber;
import game.*;

import java.util.*;


public class SystematicClobberPlayer extends GamePlayer {
	public SystematicClobberPlayer(String n) 
	{
		super(n, "Clobber");
	}
	public GameMove getMove(GameState state, String lastMove)
	{
		ClobberState board = (ClobberState)state;
		ArrayList<ClobberMove> list = new ArrayList<ClobberMove>();  
		ClobberMove mv = new ClobberMove();
		for (int r=ClobberState.ROWS-1; r>=0; r--) {
			for (int c=ClobberState.COLS-1; c>=0; c--) {
				mv.row1 = r;
				mv.col1 = c;
				mv.row2 = r-1; mv.col2 = c;
				if (board.moveOK(mv)) {
					return (ClobberMove)mv.clone();
				}
				mv.row2 = r+1; mv.col2 = c;
				if (board.moveOK(mv)) {
					return (ClobberMove)mv.clone();
				}
				mv.row2 = r; mv.col2 = c-1;
				if (board.moveOK(mv)) {
					return (ClobberMove)mv.clone();
				}
				mv.row2 = r; mv.col2 = c+1;
				if (board.moveOK(mv)) {
					return (ClobberMove)mv.clone();
				}
			}
		}
		int which = Util.randInt(0, list.size()-1);
		return list.get(which);
	}
	public static void main(String [] args)
	{
		GamePlayer p = new RandomClobberPlayer("Systematic+");
		p.compete(args, 1);
	}
}
