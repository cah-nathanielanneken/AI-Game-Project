/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package Al;

import breakthrough.BreakthroughState;
import game.GamePlayer;

public abstract class BaseBreakthroughPlayer extends GamePlayer {
	public static int ROWS = BreakthroughState.N;
	public static int COLS = BreakthroughState.N;
	public static final int MAX_SCORE = 20*BreakthroughState.N*BreakthroughState.N + 1;

	public static double WEIGHT_ONE = 1.0;
	public static double WEIGHT_TWO = 0;
	public static double WEIGHT_THREE = 0;
	public static double WEIGHT_FOUR = 0;

	public BaseBreakthroughPlayer(String nname)
	{ super(nname, "Breakthrough");	}

	// Count number of who's pieces left on the board
	private static int eval(BreakthroughState brd, char who)
	{
		int cnt = 0;
		for (int i = 0; i < BreakthroughState.N; i++) {
			for (int c = 0; c < BreakthroughState.N; c++) {
				if (brd.board[i][c] == who) {
					cnt++;
				}
			}
		}

		return cnt;
	}

	// Weight the number of pieces on the board to how close they are to the opponents side
	private static int eval2(BreakthroughState brd, char who) {
		int cnt = 0;
		for (int i = 0; i < BreakthroughState.N; i++) {
			for (int c = 0; c < BreakthroughState.N; c++) {
				if (brd.board[i][c] == who) {
					cnt += (who == BreakthroughState.homeSym ? i + 1 : BreakthroughState.N - i);
				}
			}
		}

		return cnt;
	}

	// Count number of friendly pieces adjacent (horizontally) to one another
	private static int eval3(BreakthroughState brd, char who) {
		int cnt = 0;
		for (int i = 0; i < BreakthroughState.N; i++) {
			for (int c = 0; c < BreakthroughState.N - 1; c++) {
				if (brd.board[i][c] == who && brd.board[i][c + 1] == who) {
					cnt++;
				}
			}
		}

		return cnt;
	}

	// Count number of friendly pieces adjacent (vertically) to one another
	private static int eval4(BreakthroughState brd, char who) {
		int cnt = 0;
		for (int i = 0; i < BreakthroughState.N - 1; i++) {
			for (int c = 0; c < BreakthroughState.N; c++) {
				if (brd.board[i][c] == who && brd.board[i + 1][c] == who) {
					cnt++;
				}
			}
		}

		return cnt;
	}

	public static int evalBoard(BreakthroughState brd)
	{
		int score = eval(brd, BreakthroughState.homeSym) - eval(brd, BreakthroughState.awaySym);
//		double sc = WEIGHT_ONE * (eval(brd, BreakthroughState.homeSym) - eval(brd, BreakthroughState.awaySym));
		//sc += WEIGHT_TWO * (eval2(brd, BreakthroughState.homeSym) - eval2(brd, BreakthroughState.awaySym));
		//sc += WEIGHT_THREE * (eval3(brd, BreakthroughState.homeSym) - eval3(brd, BreakthroughState.awaySym));
		//sc += WEIGHT_FOUR * (eval4(brd, BreakthroughState.homeSym) - eval4(brd, BreakthroughState.awaySym));
//		int score = (int)Math.round(sc);
		if (Math.abs(score) > MAX_SCORE) {
			System.err.println("Problem with eval");
			System.exit(0);
		}
		return score;
	}
	
}
