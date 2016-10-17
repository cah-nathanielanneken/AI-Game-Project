/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package connect6;
import game.*;

public class SimpleConnect6Player extends GamePlayer {
	public void init() { }
	public void timeOfLastMove(double secs) { }
	public void endGame(int rs) { }
	public void startGame(String opp) { }
	public void done() { }
	public SimpleConnect6Player(String nname)
	{
		super(nname, "Connect6");
		gameState = new Connect6State();
	}
	public GameMove getMove(GameState game, String lastMove)
	{
		Connect6State brd = (Connect6State)game;
		
		if (game.getNumMoves() == 0) {	// black gets only one piece, intially
			return new Connect6Move(0, 0, 0, 0);
		}
		int r1=-1, c1=-1, r2=-1, c2=-1;
TOP:
		for (int r=0; r<Connect6State.N; r++) {
			for (int c=0; c<Connect6State.N; c++) {
				if (brd.board[r][c] == Connect6State.EMPTY) {
					if (r1 == -1) {
						r1 = r;
						c1 = c;
						if (game.getNumMoves() == Connect6State.NSQR-1) {
							break;
						}
					} else {
						r2 = r;
						c2 = c;
						break TOP;
					}
				}
			}
		}

		return new Connect6Move(r1, c1, r2, c2);
	}
	public static void main(String [] args)
	{
		GamePlayer p = new SimpleConnect6Player("C6 simpleton");
		p.compete(args);
	}
}
