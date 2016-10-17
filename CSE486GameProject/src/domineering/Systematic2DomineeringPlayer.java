/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package domineering;
import game.*;

public class Systematic2DomineeringPlayer extends GamePlayer {
	public Systematic2DomineeringPlayer(String n) 
	{
		super(n, "Domineering");
	}
	public GameMove getMove(GameState state, String lastMove)
	{
		DomineeringState board = (DomineeringState)state;
		DomineeringMove mv = new DomineeringMove();
		int dr, dc;
		if (state.who == GameState.Who.HOME) {
			dr = 0;
			dc = 1;
		} else {
			dr = 1;
			dc = 0;
		}
		for (int c=0; c<DomineeringState.COLS; c++) {
			for (int r=0; r<DomineeringState.ROWS; r++) {
				mv.row1 = r;
				mv.col1 = c;
				mv.row2 = r+dr; mv.col2 = c+dc;
				if (board.moveOK(mv)) {
					return (DomineeringMove)mv.clone();
				}
			}
		}
		return new DomineeringMove();
	}
	public static void main(String [] args)
	{
		GamePlayer p = new Systematic2DomineeringPlayer("Sys2");
		p.compete(args, 1);
	}
}
