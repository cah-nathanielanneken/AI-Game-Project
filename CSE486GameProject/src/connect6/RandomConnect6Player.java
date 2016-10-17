/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package connect6;
import game.*;

import java.util.ArrayList;

public class RandomConnect6Player extends GamePlayer {
	public RandomConnect6Player(String nname)
	{
		super(nname, "Connect6");
		gameState = new Connect6State();
	}
	public void init() { }
	public void timeOfLastMove(double secs) { }
	public void endGame(int rs) { }
	public void startGame(String opp) { }
	public void done() { }
	public GameMove getMove(GameState game, String lastMove)
	{
		if (game.getNumMoves() == 0) {	// black gets only one piece, intially
			int r = (int)(Math.random() * Connect6State.N);
			int c = (int)(Math.random() * Connect6State.N);
			return new Connect6Move(r, c, 0, 0);
		}
		ArrayList<Integer> possibleRow  = new ArrayList<Integer>();
		ArrayList<Integer> possibleCol = new ArrayList<Integer>();
		Connect6State brd = (Connect6State)game;
		for (int r=0; r<Connect6State.N; r++) {
			for (int c=0; c<Connect6State.N; c++) {
				if (brd.board[r][c] == Connect6State.EMPTY) {
					possibleRow.add(r);
					possibleCol.add(c);
				}
			}
		}

		int numPossibleMoves = possibleRow.size();
		if (numPossibleMoves == 0) {
			return null;
		} else if (numPossibleMoves == 1) {
			return new Connect6Move(possibleRow.get(0), possibleCol.get(0),
									0, 0);
		} else {
			int w = (int)(Math.random() * numPossibleMoves);
			int r1 = (Integer)possibleRow.get(w);
			int c1 = (Integer)possibleCol.get(w);
			possibleRow.remove(w);
			possibleCol.remove(w);
			w = (int)(Math.random() * (numPossibleMoves-1));
			int r2 = (Integer)possibleRow.get(w);
			int c2 = (Integer)possibleCol.get(w);
			return new Connect6Move(r1, c1, r2, c2);
		}
	}
	public static void main(String [] args)
	{
		GamePlayer p = new RandomConnect6Player("C6 randomizer");
		p.compete(args);
	}
}
