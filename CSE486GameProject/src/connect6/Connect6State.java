/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package connect6;
import game.GameMove;
import game.GameState;
import game.Params;

import java.util.*;

public class Connect6State extends GameState {
	public static final Params gameParams = new Params(CONFIG_DIR + "connect6.txt");
	public static final int N = gameParams.integer("ROWS");
	public static final int NSQR = N*N;	
	public static final char EMPTY = '.';
	public char [][] board = new char [N][N];

	public Connect6State()
	{ reset(); }
	
	public Object clone()
	{
		Connect6State copy = new Connect6State();

		copy.copyInfo(this);
		for (int r=0; r<N; r++) {
			for (int c=0; c<N; c++) {
				copy.board[r][c] = board[r][c];
			}
		}
		
		return copy;
	}
	public void reset()
	{
		clear();
		for (int r=0; r<N; r++) {
			Arrays.fill(board[r], EMPTY);
		}
	}
	private int check(int startR, int startC, int dr, int dc)
	{
		int r = startR + dr;
		int c = startC + dc;
		int cnt = 0;
		while (Connect6Move.indexOK(r) && Connect6Move.indexOK(c)) {
			if (board[r][c] == board[startR][startC]) {
				cnt++;
				r += dr;
				c += dc;
			} else {
				break;
			}
		}
		return cnt;
	}

	private boolean check(int startR, int startC, int dr1, int dc1, int dr2, int dc2)
	{
		int cnt1 = check(startR, startC, dr1, dc1);
		int cnt2 = check(startR, startC, dr2, dc2);
		return cnt1 + cnt2 + 1 >= 6;
	}
	private boolean makeMove(int r, int c)
	{
		if (board[r][c] != EMPTY) {
			return false;
		} else {
			board[r][c] = (who == GameState.Who.HOME ? 'B' : 'W');
			numMoves++;
			if (status == GameState.Status.GAME_ON) {
				if (check(r, c, -1, -1, +1, +1) ||
					check(r, c, -1, +1, +1, -1) ||
					check(r, c, 0, -1, 0, +1) ||
					check(r, c, -1, 0, +1, 0)) {
						status = (who == GameState.Who.HOME ? GameState.Status.HOME_WIN:
																GameState.Status.AWAY_WIN);
				}
			}
			if (status == GameState.Status.GAME_ON && numMoves == NSQR) {
				status = GameState.Status.DRAW;
			}
			return true;
		}
	}
	private boolean moveOnBoard(int r, int c)
	{
		return r >= 0 && r < N && c >= 0 && c < N;
	}
	public boolean moveOK(GameMove mv)
	{
		Connect6Move mv6 = (Connect6Move)mv;
		boolean OK;
		
		if (getNumMoves() == 0 || getNumMoves() == NSQR-1) {
			OK = moveOnBoard(mv6.row1, mv6.col1) && board[mv6.row1][mv6.col1] == EMPTY; 
		} else {
			OK = moveOnBoard(mv6.row1, mv6.col1) && board[mv6.row1][mv6.col1] == EMPTY &&
				moveOnBoard(mv6.row2, mv6.col2) && board[mv6.row2][mv6.col2] == EMPTY &&
				!(mv6.row1 == mv6.row2 && mv6.col1 == mv6.col2);
		}
		
		return OK;
	}
	public boolean makeMove(GameMove mv)
	{
		Connect6Move mv6 = (Connect6Move)mv;
		int r1 = mv6.row1;
		int c1 = mv6.col1;
		int r2 = mv6.row2;
		int c2 = mv6.col2;
		
		if (getNumMoves() == 0 || getNumMoves() == NSQR-1) {
			boolean result = makeMove(r1, c1);
			togglePlayer();
			return result;
		} else if (makeMove(r1, c1) && makeMove(r2, c2)) {
			togglePlayer();
			return true;
		} else {
			return false;
		}
	}
	private void computeStatus()
	{
		
	}
	public void parseMsgString(String s)
	{
		reset();
		int cnt = 0;
		for (int r=0; r<N; r++) {
			for (int c=0; c<N; c++) {
				board[r][c] = s.charAt(cnt++);
				if (board[r][c] != EMPTY) {
					numMoves++;
				}
			}
		}
		computeStatus();
		who = ((numMoves+1)/2) % 2 == 0 ? Who.HOME: Who.AWAY;
	}
	public String toString()
	{
		StringBuffer buf = new StringBuffer(NSQR + N);
		for (int r=0; r<N; r++) {
			for (int c=0; c<N; c++) {
				buf.append(board[r][c]);
			}
			buf.append('\n');
		}
		return buf.toString();
	}
	public String msgString()
	{
		StringBuffer buf = new StringBuffer(NSQR);
		for (int r=0; r<N; r++) {
			for (int c=0; c<N; c++) {
				buf.append(board[r][c]);
			}
		}
		return buf.toString();
	}
}
