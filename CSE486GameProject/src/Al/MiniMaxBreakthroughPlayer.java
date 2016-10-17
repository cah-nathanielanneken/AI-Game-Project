/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package Al;

import breakthrough.BreakthroughMove;
import breakthrough.BreakthroughState;
import game.GameMove;
import game.GamePlayer;
import game.GameState;

public class MiniMaxBreakthroughPlayer extends BaseBreakthroughPlayer {
	public final int MAX_DEPTH = 50;
	public int depthLimit;

	// mvStack is where the search procedure places it's move recommendation.
	// If the search is at depth, d, the move is stored on mvStack[d].
	// This was done to help efficiency (i.e., reduce number constructor calls)
	// (Not sure how much it improves things.)
	protected ScoredBreakthroughMove[] mvStack;

	// A Connect4Move with a scored (how well it evaluates)
	protected class ScoredBreakthroughMove extends BreakthroughMove {
		public ScoredBreakthroughMove(int r1, int c1, int r2, int c2, double s) {
			super(r1, c1, r2, c2);
			score = s;
		}

		public void set(int r1, int c1, int r2, int c2, double s) {
			startRow = r1; startCol = c1; endingRow = r2; endingCol = c2;
			score = s;
		}

		public double score;
	}

	public MiniMaxBreakthroughPlayer(String nname, int d) {
		super(nname);
		depthLimit = d;
	}

	/**
	 * Initializes the stack of Moves.
	 */
	public void init() {
		mvStack = new ScoredBreakthroughMove[MAX_DEPTH];
		for (int i = 0; i < MAX_DEPTH; i++) {
			mvStack[i] = new ScoredBreakthroughMove(0, 0, 0, 0, 0);
		}
	}

	protected boolean terminalValue(GameState brd, ScoredBreakthroughMove mv) {
		GameState.Status status = brd.getStatus();
		boolean isTerminal = true;

		if (status == GameState.Status.HOME_WIN) {
			mv.set(0, 0, 0, 0, MAX_SCORE);
		} else if (status == GameState.Status.AWAY_WIN) {
			mv.set(0, 0, 0, 0, -MAX_SCORE);
		} else {
			isTerminal = false;
		}
		return isTerminal;
	}

	private void minimax(BreakthroughState brd, int currDepth) {
		boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
		boolean toMinimize = !toMaximize;

		boolean isTerminal = terminalValue(brd, mvStack[currDepth]);

		if (isTerminal) {
			;
		} else if (currDepth == depthLimit) {
			mvStack[currDepth].set(0, 0, 0, 0, evalBoard(brd));
		} else {
			ScoredBreakthroughMove tempMv = new ScoredBreakthroughMove(0, 0, 0, 0, 0);
			int dir = brd.who == GameState.Who.HOME ? +1 : -1;

			double bestScore = (toMaximize ?
					Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			ScoredBreakthroughMove bestMove = mvStack[currDepth];
			ScoredBreakthroughMove nextMove = mvStack[currDepth + 1];

			bestMove.set(0, 0, 0, 0, bestScore);
			GameState.Who currTurn = brd.getWho();

			for (int i = 0; i < BreakthroughState.N; i++) {
				for (int c = 0; c < BreakthroughState.N; c++) {
					if (brd.board[i][c] == (currTurn.equals(GameState.Who.HOME) ? BreakthroughState.homeSym
							: BreakthroughState.awaySym)) {
						// Make move on board
						for (int horizDir = -1; horizDir < 2; horizDir++) {
							tempMv.set(i, c, i + dir, c + horizDir, 0);
							if (brd.moveOK(tempMv)) {
								char val = brd.board[i + dir][c + horizDir];
								brd.makeMove(tempMv);
								minimax(brd, currDepth + 1);

								// Undo move
								brd.board[i + dir][c + horizDir] = val;
								brd.board[i][c] = (currTurn.equals(GameState.Who.HOME) ? BreakthroughState.homeSym
										: BreakthroughState.awaySym);
								brd.numMoves--;
								brd.status = GameState.Status.GAME_ON;
								brd.who = currTurn;

								// Check out the results, relative to what we've seen before
								if (toMaximize && nextMove.score > bestMove.score) {
									bestMove.set(i, c, i + dir, c + horizDir, nextMove.score);
								} else if (!toMaximize && nextMove.score < bestMove.score) {
									bestMove.set(i, c, i + dir, c + horizDir, nextMove.score);
								}
							}
						}
					}
				}
			}
		}
	}

	public GameMove getMove(GameState brd, String lastMove) {
		minimax((BreakthroughState) brd, 0);
		return mvStack[0];
	}

	public static void main(String[] args) {
		int depth = 2;
		GamePlayer p = new MiniMaxBreakthroughPlayer("MiniMax " + depth, depth);
		p.compete(args);
	}
}
