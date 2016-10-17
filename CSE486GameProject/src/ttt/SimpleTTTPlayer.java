/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package ttt;
import game.*;

public class SimpleTTTPlayer extends GamePlayer {
	public SimpleTTTPlayer(String nname)
	{ super(nname, "TTT"); }
	public GameMove getMove(GameState game, String lastMove)
	{
		TTTState brd = (TTTState)game;
		for (int r=0; r<TTTState.N; r++) {
			for (int c=0; c<TTTState.N; c++) {
				if (brd.board[r][c] == TTTState.emptySym) {
					return new TTTMove(r, c);
				}
			}
		}
		return null;
	}
	public static void main(String [] args)
	{
		GamePlayer p = new SimpleTTTPlayer("TTT simpleton");
		p.compete(args);
	}
}
