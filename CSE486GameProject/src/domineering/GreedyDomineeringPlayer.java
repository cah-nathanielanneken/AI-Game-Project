/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package domineering;
import game.*;

public class GreedyDomineeringPlayer extends GamePlayer {
	public GreedyDomineeringPlayer(String n) 
	{
		super(n, "Domineering");
	}
	public static int legalMoves(DomineeringState state, int dr, int dc)
	{
		int cnt = 0;
		for (int r=0; r<DomineeringState.ROWS; r++) {
			for (int c=0; c<DomineeringState.COLS; c++) {
				int R = r + dr;
				int C = c + dc;
				if (R == DomineeringState.ROWS || C == DomineeringState.COLS)
					continue;
				if (state.board[r][c] == DomineeringState.emptySym && 
					state.board[R][C] == DomineeringState.emptySym)
						cnt++;
			}
		}
		return cnt;
	}
	public static int horzMoves(DomineeringState state)
	{ return legalMoves(state, 0, 1); }
	public static int vertMoves(DomineeringState state)
	{ return legalMoves(state, 1, 0); }
	public static int eval(DomineeringState state)
	{ return horzMoves(state) - vertMoves(state); }
	public GameMove getMove(GameState state, String lastMove)
	{
		DomineeringState board = (DomineeringState)state;
		DomineeringMove mv = new DomineeringMove();
		boolean toMaximize, toMinimize;
		int dr, dc;
		if (state.who == GameState.Who.HOME) {
			toMaximize = true;
			dr = 0;
			dc = 1;
		} else {
			toMaximize = false;
			dr = 1;
			dc = 0;
		}
		toMinimize = !toMaximize;
		DomineeringMove bestMove = null;
		int bestScore = toMaximize ? -Integer.MIN_VALUE : Integer.MAX_VALUE;
		for (int c=0; c<DomineeringState.COLS; c++) {
			for (int r=0; r<DomineeringState.ROWS; r++) {
				mv.row1 = r;
				mv.col1 = c;
				mv.row2 = r+dr; mv.col2 = c+dc;
				if (board.moveOK(mv)) {
					DomineeringState copyBrd = (DomineeringState)board.clone();
					copyBrd.makeMove(mv);
					int perf = eval(copyBrd);
					if (toMaximize && perf > bestScore) {
						bestScore = perf;
						bestMove = (DomineeringMove)mv.clone();
					} else if (toMinimize && perf < bestScore) {
						bestScore = perf;
						bestMove = (DomineeringMove)mv.clone();
					}
				}
			}
		}
		return bestMove;
	}
	public static void main(String [] args)
	{
		GamePlayer p = new GreedyDomineeringPlayer("Greedy");
		p.compete(args, 1);
		//p.solvePuzzles(new String [] {"DomPuzzle1", "DomPuzzle2"});
	}
}
