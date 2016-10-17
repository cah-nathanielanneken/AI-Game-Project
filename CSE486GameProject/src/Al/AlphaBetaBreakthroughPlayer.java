/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package Al;

import breakthrough.BreakthroughState;
import game.GameMove;
import game.GamePlayer;
import game.GameState;


// AlphaBetaConnect4Player is identical to MiniMaxConnect4Player
// except for the search process, which uses alpha beta pruning.

public class AlphaBetaBreakthroughPlayer extends MiniMaxBreakthroughPlayer {
	public AlphaBetaBreakthroughPlayer(String nname, int d)
	{ super(nname, d); }

	/**
	 * Performs alpha beta pruning.
	 * @param brd
	 * @param currDepth
	 * @param alpha
	 * @param beta
	 */
	private void alphaBeta(BreakthroughState brd, int currDepth,
										double alpha, double beta)
	{
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
//			for (int i = BreakthroughState.N - 1; i >= 0; i--) {
//				for (int c = BreakthroughState.N - 1; c >= 0; c--) {
					if (brd.board[i][c] == (currTurn.equals(GameState.Who.HOME) ? BreakthroughState.homeSym
							: BreakthroughState.awaySym)) {
						// Make move on board
						for (int horizDir = -1; horizDir < 2; horizDir++) {
							tempMv.set(i, c, i + dir, c + horizDir, 0);
							if (brd.moveOK(tempMv)) {
								char val = brd.board[i + dir][c + horizDir];
								brd.makeMove(tempMv);
								int localScore = evalBoard(brd);
								alphaBeta(brd, currDepth + 1, alpha, beta);

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

								// Update alpha and beta. Perform pruning, if possible.
								if (toMinimize) {
									beta = Math.min(bestMove.score, beta);
									if (bestMove.score <= alpha || bestMove.score == -MAX_SCORE) {
										return;
									}
								} else {
									alpha = Math.max(bestMove.score, alpha);
									if (bestMove.score >= beta || bestMove.score == MAX_SCORE) {
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}
		
	public GameMove getMove(GameState brd, String lastMove)
	{ 
		alphaBeta((BreakthroughState)brd, 0, Double.NEGATIVE_INFINITY,
										 Double.POSITIVE_INFINITY);
		System.out.println(mvStack[0].score + " -> " + mvStack[0].toString());
		return mvStack[0];
	}
	
	public static void main(String [] args)
	{
		int depth = 6;
		GamePlayer p = new AlphaBetaBreakthroughPlayer("AlphaBeta", depth);
//		GamePlayer p2 = new AlphaBetaBreakthroughPlayer("AlphaBeta", depth);
//		((BaseBreakthroughPlayer)p2).WEIGHT_TWO = ((BaseBreakthroughPlayer)p2).WEIGHT_THREE =
//				((BaseBreakthroughPlayer)p2).WEIGHT_FOUR = 0;
//		((BaseBreakthroughPlayer)p2).WEIGHT_ONE = 1.00;

		p.compete(args);
//		p2.compete(args);

//		p.init();
//		String brd =
//				"BBBBBBBB" +
//				"BBBBB..B" +
//				".....BB." +
//				"........" +
//				".......W" +
//				"........" +
//				"WWWWWWW." +
//				"WWWWWWWW" +
//				"[HOME 4 GAME_ON]";
//
//		BreakthroughState state = new BreakthroughState();
//		state.parseMsgString(brd);
//		GameMove mv = p.getMove(state, "");
//		System.out.println("Original board");
//		System.out.println(state.toString());
//		System.out.println("Move: " + mv.toString());
//		System.out.println("Board after move");
//		state.makeMove(mv);
//		System.out.println(state.toString());
	}
}
