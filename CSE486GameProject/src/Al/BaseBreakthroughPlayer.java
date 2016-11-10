/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package Al;

import breakthrough.BreakthroughMove;
import breakthrough.BreakthroughState;
import game.GamePlayer;

public abstract class BaseBreakthroughPlayer extends GamePlayer {
	public static int ROWS = BreakthroughState.N;
	public static int COLS = BreakthroughState.N;
	public static final int MAX_SCORE = 20*BreakthroughState.N*BreakthroughState.N + 1;

	public BaseBreakthroughPlayer(String nname)
	{ super(nname, "Breakthrough");	}

	public static int eval(BreakthroughState brd, char who) {
		double weight1 = 20;
		double weight2 = 1;
		double weight3 = 5;
		double weight4 = 4;
		int count1 = 0, count2 = 0, count3 = 0, count4 = 0;

		int dir = who == BreakthroughState.homeSym ? 1 : -1;

		for (int i = 0; i < BreakthroughState.N; i++) {
			for (int j = 0; j < BreakthroughState.N; j++) {

				// count # pieces
				if (brd.board[i][j] == who) {
					count1++;
				}

				// find supported pieces
				if (brd.board[i][j] == who) {
					// check if the places we're going to check are valid
					// ensures row is valid
					if (BreakthroughMove.indexOK(i - dir)) {
						//if (i + dir >= 0 && i + dir <= BreakthroughState.N) {
						// ensure col valid
						if (BreakthroughMove.indexOK(j + 1) && brd.board[i - dir][j + 1] == who) {
							count2++;
						}
						if (BreakthroughMove.indexOK(j - 1) && brd.board[i - dir][j - 1] == who) {
							count2++;
						}
					}
				}

				// find number of spaces being attacked
				if (BreakthroughMove.indexOK(i + dir)) {
					if (BreakthroughMove.indexOK(j + 1) && brd.board[i + dir][j + 1] != who) {
						count3++;
					}
					if (BreakthroughMove.indexOK(j - 1) && brd.board[i + dir][j - 1] != who) {
						count3++;
					}
				}

				// count horizontal pieces
				if (BreakthroughMove.indexOK(j + 1)) {
					if (brd.board[i][j+1] == who) {
						count4++;
					}
				}
			}
		}
		int total = (int) Math.round((count1 * weight1) + (count2 * weight2) +
				(count3 * weight3) + (count4 * weight4));

		return total;
	}

	public static int evalBoard(BreakthroughState brd)
	{
		int score = eval(brd, BreakthroughState.homeSym) - eval(brd, BreakthroughState.awaySym);
		if (Math.abs(score) > MAX_SCORE) {
			System.err.println("Problem with eval");
			System.exit(0);
		}
		return score;
	}

}
