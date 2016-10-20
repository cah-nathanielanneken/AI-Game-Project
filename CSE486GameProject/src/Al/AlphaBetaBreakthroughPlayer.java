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

import java.util.concurrent.*;


// AlphaBetaConnect4Player is identical to MiniMaxConnect4Player
// except for the search process, which uses alpha beta pruning.

public class AlphaBetaBreakthroughPlayer extends MiniMaxBreakthroughPlayer {
	public AlphaBetaBreakthroughPlayer(String nname, int d)
	{ super(nname, d); }

	protected class AlphaBetaTask implements Callable<ScoredBreakthroughMove> {
		BreakthroughState brd;

		public AlphaBetaTask(BreakthroughState brd) {
			super();
			this.brd = brd;
		}

		@Override
		public ScoredBreakthroughMove call() throws Exception {
			alphaBeta(brd, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			return mvStack[0];
		}
	}

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
			;
		} else {
			ScoredBreakthroughMove tempMv = new ScoredBreakthroughMove(0, 0, 0, 0, 0);
			int dir = brd.who == GameState.Who.HOME ? +1 : -1;

			double bestScore = (toMaximize ? 
					Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			ScoredBreakthroughMove bestMove = mvStack[currDepth];
			ScoredBreakthroughMove nextMove = mvStack[currDepth + 1];

			bestMove.set(0, 0, 0, 0, bestScore);
			GameState.Who currTurn = brd.getWho();

			for (int i = (currTurn.equals(GameState.Who.HOME) ? BreakthroughState.N - 1 : 0);
				 (currTurn.equals(GameState.Who.AWAY) && i < BreakthroughState.N) ||
						 (currTurn.equals(GameState.Who.HOME) && i >= 0);) {
				for (int c = 0; c < BreakthroughState.N; c++) {
					if (brd.board[i][c] == (currTurn.equals(GameState.Who.HOME) ? BreakthroughState.homeSym
							: BreakthroughState.awaySym)) {
						// Make move on board
						for (int horizDir = -1; horizDir < 2; horizDir++) {
							tempMv.set(i, c, i + dir, c + horizDir, 0);
							if (brd.moveOK(tempMv)) {
								char val = brd.board[i + dir][c + horizDir];
								brd.makeMove(tempMv);
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
				i += currTurn.equals(GameState.Who.HOME) ? -1 : 1;
			}
		}
	}
		
	public GameMove getMove(GameState brd, String lastMove)
	{
		ScoredBreakthroughMove finalBestMove = new ScoredBreakthroughMove(0, 0, 0, 0, 0), temp;
		int dLimit = this.depthLimit;

		// Set iterative deepening time limit to 3 seconds
		long endTime = System.currentTimeMillis() + 3000;
		// Create thread to run alpha beta searches
		ExecutorService service = Executors.newSingleThreadExecutor(new ThreadFactory(){
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}
		});

		// While we are less then our maximum depth limit
		for (int i = 1; i <= dLimit; i++) {
			// Set search's limit to the current depth
			this.depthLimit = i;
			// Create asynchronous object to be resolved to the best move possible at the current depth limit
			Future<ScoredBreakthroughMove> bestMove = service.submit(
					new AlphaBetaTask((BreakthroughState)brd.clone()));
			try {
				// Resolve asynchronous object
				temp = bestMove.get(endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
				// Set deepest best move to the most recently resolved best move
				finalBestMove.set(temp.startRow, temp.startCol, temp.endingRow, temp.endingCol, temp.score);
			} catch (Exception ex) {
				// Kill the asynchronous execution
				bestMove.cancel(true);
				break;
			}
			// If best possible solution, no need to continue searching deeper
			if (Math.abs(finalBestMove.score) == MAX_SCORE) {
				break;
			}
		}
		// Shutdown extra thread
		service.shutdownNow();
		// Reset the original depth limit
		this.depthLimit = dLimit;

		return finalBestMove;
	}
	
	public static void main(String [] args)
	{
		int depth = 20;
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
//				"BBBB.BB." +
//				"....B..B" +
//				"WB......" +
//				"........" +
//				"........" +
//				"WWWWWWWW" +
//				".WWWWWWW" +
//				"[AWAY 5 GAME_ON]";
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
