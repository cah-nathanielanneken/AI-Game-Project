/* Copyright (C) Nathan Anneken, Alex Rinehart, Max Anderson - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Nathan Anneken, Alex Rinehart, Max Anderson, 2016
 */

package Al;

import breakthrough.BreakthroughMove;
import breakthrough.BreakthroughState;
import game.GameMove;
import game.GamePlayer;
import game.GameState;

import java.util.concurrent.*;
import java.util.Hashtable;


// AlphaBetaConnect4Player is identical to MiniMaxConnect4Player
// except for the search process, which uses alpha beta pruning.

public class AlphaBetaBreakthroughPlayer extends MiniMaxBreakthroughPlayer {
	public ScoredBreakthroughMove predictedOpponentMove, opponentMoveResponse, tempPredictedOpponentMove,
			currentPredictedOppMove;
	public FutureTask<ScoredBreakthroughMove> opponentThinkTask;
	public Thread thread;
	public long timeLeft;
	public Hashtable<String, String> openingPlayBook;

	public AlphaBetaBreakthroughPlayer(String nname, int d)
	{
		super(nname, d);
		opponentMoveResponse = new ScoredBreakthroughMove(0, 0, 0, 0, 0);
		predictedOpponentMove = new ScoredBreakthroughMove(0, 0, 0, 0, 0);
		tempPredictedOpponentMove = new ScoredBreakthroughMove(0, 0, 0, 0, 0);
		currentPredictedOppMove = new ScoredBreakthroughMove(0, 0, 0, 0, 0);
	}

	protected class AlphaBetaTask implements Callable<ScoredBreakthroughMove> {
		BreakthroughState brd;
		int dLimit;

		public AlphaBetaTask(BreakthroughState brd, int dLimit) {
			super();
			this.brd = brd;
			this.dLimit = dLimit;
		}

		@Override
		public ScoredBreakthroughMove call() throws Exception {
			init();
			alphaBeta(brd, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, dLimit);
			boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
			// Check to see if current predicted opponents move is a worse move for the opponent than most recent
			if ((toMaximize && tempPredictedOpponentMove.score <= predictedOpponentMove.score) || (!toMaximize &&
					tempPredictedOpponentMove.score >= predictedOpponentMove.score)) {
				predictedOpponentMove.set(tempPredictedOpponentMove);
			}
			return mvStack[0];
		}
	}

	protected class ThinkOnOpponentsMove extends AlphaBetaTask {

		public ThinkOnOpponentsMove(BreakthroughState brd, int dLimit) {
			super(brd, dLimit);
		}

		@Override
		public ScoredBreakthroughMove call() throws Exception {
			// While we are less then our maximum depth limit
			for (int i = 1; i <= dLimit; i++) {
				init();

				alphaBeta(brd, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, i);

				// Set my response should the opponent make predicted move
				opponentMoveResponse.set(mvStack[0]);

				boolean toMaximize = (brd.getWho() == GameState.Who.HOME);

				// Check to see if current predicted opponents move is a worse move for the opponent than most recent
				if ((toMaximize && tempPredictedOpponentMove.score <= currentPredictedOppMove.score) || (!toMaximize &&
						tempPredictedOpponentMove.score >= currentPredictedOppMove.score)) {
					currentPredictedOppMove.set(tempPredictedOpponentMove);
				}

				// If best possible solution or less time left to solve next depth than it took to solve the previous
				// depth, no need to continue searching deeper
				if (Math.abs(opponentMoveResponse.score) == MAX_SCORE) {
					break;
				}
			}
			return opponentMoveResponse;
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
										double alpha, double beta, int dLimit)
	{
		if (Thread.currentThread().isInterrupted()) {
			throw new RuntimeException();
		}
		boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
		boolean toMinimize = !toMaximize;

		boolean isTerminal = terminalValue(brd, mvStack[currDepth]);
		
		if (isTerminal) {
			;
		} else if (currDepth == dLimit) {
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

								alphaBeta(brd, currDepth + 1, alpha, beta, dLimit);

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
									if (currDepth == 0) {
										tempPredictedOpponentMove.set(nextMove);
									}
								} else if (!toMaximize && nextMove.score < bestMove.score) {
									bestMove.set(i, c, i + dir, c + horizDir, nextMove.score);
									if (currDepth == 0) {
										tempPredictedOpponentMove.set(nextMove);
									}
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

	private ScoredBreakthroughMove iterateDeepeningSearch(GameState brd, long timePerMove) {
		ScoredBreakthroughMove finalBestMove = new ScoredBreakthroughMove(0, 0, 0, 0, 0), temp;

		// Set iterative deepening time limit
		long endTime = System.currentTimeMillis() + timePerMove, curTime;
		// Create thread to run alpha beta searches
		ExecutorService service = Executors.newSingleThreadExecutor(new ThreadFactory(){
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}
		});

		// While we are less then our maximum depth limit
		for (int i = 1; i <= this.depthLimit; i++) {
			// Create asynchronous object to be resolved to the best move possible at the current depth limit
			Future<ScoredBreakthroughMove> bestMove = service.submit(
					new AlphaBetaTask((BreakthroughState)brd.clone(), i));
			try {
				// Resolve asynchronous object
				temp = bestMove.get(endTime - (curTime = System.currentTimeMillis()), TimeUnit.MILLISECONDS);
				// Set deepest best move to the most recently resolved best move
				finalBestMove.set(temp);
			} catch (Exception ex) {
				// Kill the asynchronous execution
				bestMove.cancel(true);
				break;
			}
			// If best possible solution or less time left to solve next depth than it took to solve the previous
			// depth, no need to continue searching deeper
			if (Math.abs(finalBestMove.score) == MAX_SCORE || (2*System.currentTimeMillis()) - curTime >= endTime) {
				break;
			}
		}

		// Shutdown extra thread
		service.shutdownNow();

		// Start new search assuming opponent makes predicted move on opponents turn
		searchOnOpponentTurn((BreakthroughState)brd, this.depthLimit, finalBestMove);

		return finalBestMove;
	}

	private void searchOnOpponentTurn(BreakthroughState brd, int dLimit, BreakthroughMove finalBestMove) {
		BreakthroughState oppBrd = (BreakthroughState)brd.clone();
		// Make current move and predicted opponent move
		oppBrd.makeMove(finalBestMove);
		oppBrd.makeMove(predictedOpponentMove);
		// Create task for thinking on opponents turn
		opponentThinkTask = new FutureTask<>(new ThinkOnOpponentsMove(oppBrd, dLimit));
		thread = new Thread(opponentThinkTask);
		thread.setDaemon(true);
		thread.start();
	}

	private void killSearch() {
		if (opponentThinkTask != null) {
			// Kill search
			opponentThinkTask.cancel(true);
			thread.interrupt();
		}
	}

		
	public GameMove getMove(GameState brd, String lastMove)
	{
		// 0 - 175 seconds used (175 seconds), 7 second moves
		// 175 - 230 seconds used (55 seconds), 5 second moves
		// 230 - 240 seconds used (10 seconds), 5 depth search

		long startTime = System.currentTimeMillis();
		// New board game, reset predicted moves
		if (brd.getNumMoves() <= 1) {
			predictedOpponentMove.set(0, 0, 0, 0, 0);
			timeLeft = 240000;
		}
		// Set turn time
		int timeForMove;
		System.out.println(timeLeft);
		if (timeLeft >= 65000) {
			// 7 second turns
			System.out.println("7 second turn!!");
			timeForMove = 7000;
		} else if (timeLeft >= 10000) {
			// 5 second turns
			System.out.println("5 second turn!!");
			timeForMove = 5000;
		} else {
			// Panic mode
			System.out.println("PANIC TIME");
			killSearch();
			init();
			alphaBeta((BreakthroughState)brd, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 5);
			return mvStack[0];
		}

		// If AI correctly guessed opponents move
		if (predictedOpponentMove != null && predictedOpponentMove.toString().equals(lastMove)) {
			// Allow AI to continue processing for rest of turn
			try {
				Thread.sleep(timeForMove);
			} catch (InterruptedException exception){}
			killSearch();
			ScoredBreakthroughMove response = new ScoredBreakthroughMove(opponentMoveResponse);
			// Set predicted opponent move to new prediction
			predictedOpponentMove.set(currentPredictedOppMove);
			// Assume opponent makes this move and start new search
			searchOnOpponentTurn((BreakthroughState)brd, this.depthLimit, response);
			timeLeft = timeLeft - (System.currentTimeMillis() - startTime);
			return response;
		}
		killSearch();
		// Perform new search and return results with actual opponent move
		GameMove result = iterateDeepeningSearch(brd, timeForMove);
		timeLeft = timeLeft - (System.currentTimeMillis() - startTime);
		return result;
	}
	
	public static void main(String [] args)
	{
		int depth = 20;
		GamePlayer p = new AlphaBetaBreakthroughPlayer("AlphaBeta", depth);

		p.compete(args);
	}
}
