/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package hijara;
import java.util.*;

import game.*;

public class HijaraState extends GameState {
	public static final Params gameParams = new Params(CONFIG_DIR + "Hijara.txt");
	public static final int ROWS = 4;
	public static final int COLS = 4;
	public static final char homeSym = gameParams.character("HOMESYM");
	public static final char awaySym = gameParams.character("AWAYSYM");
	public static final char emptySym = gameParams.character("EMPTYSYM");

	public StringBuffer [][] board;
	public int [] score;
	
	final private static String [] fourOfAKind = { "0000", "1111", "2222", "3333" };
	final private static String straight = "0123";
	final private static String straightRev = "3210";
	
	public HijaraState()
	{
		super();
		board = new StringBuffer [ROWS][COLS];
		score = new int [2];
 		for (int r=0; r<ROWS; r++) {
			for (int c=0; c<COLS; c++) {
				board[r][c] = new StringBuffer(4);
			}
		}
		reset();
	}
	public Object clone()
	{
		HijaraState res = new HijaraState();
		res.copyInfo(this);
		for (int r=0; r<ROWS; r++) {
			for (int c=0; c<COLS; c++) {
				res.board[r][c] = new StringBuffer(board[r][c]);
			}
		}
		
		for (int i=0; i<2; i++) {
			res.score[i] = score[i];
		}

		return res;
	}
	public void reset()
	{
		clear();
		String cleared = "" + emptySym + emptySym + emptySym + emptySym;
		for (int r=0; r<ROWS; r++) {
			for (int c=0; c<COLS; c++) {
				board[r][c].replace(0, 4, cleared);
			}
		}
		score[0] = score[1] = 0;
	}
	public boolean moveOK(GameMove m)
	{
		HijaraMove mv = (HijaraMove)m;
		return status == Status.GAME_ON && mv != null &&
			Util.inrange(mv.row, 0, ROWS-1) && Util.inrange(mv.col, 0, COLS-1) &&
			board[mv.row][mv.col].charAt(3) == emptySym;
	}
	private boolean match(int sr, int sc, int dr, int dc, char sym, StringBuffer match)
	{
		for (int i=0; i<4; i++) {
			int whichNum = match.charAt(i) - '0';
			if (board[sr+i*dr][sc+i*dc].charAt(whichNum) != sym) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Returns the largest number (minus 1) covered in grid location (r,c).
	 * @param r Row - 0, 1, 2, 3
	 * @param c Column - 0, 1, 2, 3
	 * @return The index of the largest number covered in grid location (r,c). -1 is
	 *         returned if no stones have been placed in that location.
	 */
	public int lastUsedSpot(int r, int c)
	{
		int whichNum;
		for (whichNum=3; whichNum>=0; whichNum--) {
			if (board[r][c].charAt(whichNum) != emptySym) {
				break;
			}
		}
		return whichNum;
	}
	/**
	 * Computes the points obtained by a player placing their stone in (r,c). 
	 * @param r Row where the stone was placed 0, 1, ...  ROWS-1
	 * @param c Column where stone was placed 0, 1, ... COLS-1
	 * @param sym The symbol of the player that just placed their stone - 'B' or 'Y'
	 * @return The points awarded by this move.
	 */
	public int computeNewPoints(int r, int c, char sym)
	{
		int cnt = 0;
		int whichNum = lastUsedSpot(r, c);
		StringBuffer fok = new StringBuffer(fourOfAKind[whichNum]);
		StringBuffer str = new StringBuffer(straight);
		StringBuffer strRev = new StringBuffer(straightRev);
		
		if (match(r, 0, 0, +1, sym, fok)) cnt += 10;
		if (match(0, c, +1, 0, sym, fok)) cnt += 10;
		if (r == c && match(0, 0, +1, +1, sym, fok)) cnt += 10;
		if (r == 3-c && match(3, 0, -1, +1, sym, fok)) cnt += 10;

		if (whichNum == c && 	match(r, 0, 0, +1, sym, str)) cnt += 15;
		if (whichNum == 3-c && 	match(r, 0, 0, +1, sym, strRev)) cnt += 15;
		if (whichNum == r && 	match(0, c, +1, 0, sym, str)) cnt += 15;
		if (whichNum == 3-r && 	match(0, c, +1, 0, sym, strRev)) cnt += 15;
		
		if (r == c && whichNum == r && match(0, 0, +1, +1, sym, str)) cnt += 15;
		if (r == c && whichNum == 3-r && match(0, 0, +1, +1, sym, strRev)) cnt += 15;
		if (r == 3-c && whichNum == r && match(3, 0, -1, +1, sym, strRev)) cnt += 15;
		if (r == 3-c && whichNum == 3-r && match(3, 0, -1, +1, sym, str)) cnt += 15;

		if (board[r][c].length() == 4 &&
				board[r][c].charAt(0) == sym &&
				board[r][c].charAt(1) == sym &&
				board[r][c].charAt(2) == sym &&
				board[r][c].charAt(3) == sym) {
						cnt += 20;
		}
		
		return cnt;
	}
	public boolean makeMove(GameMove m)
	{
		HijaraMove mv = (HijaraMove)m;
		char sym = who == GameState.Who.HOME ? homeSym : awaySym;
		boolean OK = false;
		if (moveOK(m)) {
			OK = true;
			int nextSpot = lastUsedSpot(mv.row, mv.col) + 1;
			board[mv.row][mv.col].setCharAt(nextSpot, sym);
			int inc = computeNewPoints(mv.row, mv.col, sym);
			if (who == GameState.Who.HOME) {
				score[0] += inc;
			} else {
				score[1] += inc;
			}
			super.newMove();
			if (numMoves < ROWS*COLS*4) {
				status = GameState.Status.GAME_ON;
			} else if (score[0] > score[1]) {
				status = GameState.Status.HOME_WIN;
			} else if (score[1] > score[0]) {
				status = GameState.Status.AWAY_WIN;
			} else {
				status = GameState.Status.DRAW;
			}
		}
		return OK;
	}
	public void parseMsgString(String s)
	{
		reset();
		int i = s.indexOf("$");
		StringTokenizer toks = new StringTokenizer(s);
		score[0] = Integer.parseInt(toks.nextToken());
		score[1] = Integer.parseInt(toks.nextToken());
		Util.parseMsgString(s.substring(i+1), board);
		parseMsgSuffix(s.substring(s.indexOf('[')));
	}
	public boolean equals(Object other)
	{
		if (this == other) {
			return true;
		} else if (other == null) {
			return false;
		} else if (!(other instanceof HijaraState)) { 
			return false;
		} else {
			HijaraState hs = (HijaraState)other;
	 		for (int r=0; r<ROWS; r++) {
				for (int c=0; c<COLS; c++) {
					StringBuffer a = hs.board[r][c];
					StringBuffer b = board[r][c];
					for (int i=0; i<4; i++) {
						if (a.charAt(i) != b.charAt(i)) {
							return false;
						}
					}
				}
			}
			return true;
		}
	}
	public String toString()
	{ return "" + score[0] + " " + score[1] + "\n" +
				Util.toString(board) + msgSuffix();
	}
	public String msgString()
	{ return "" + score[0] + " " + score[1] + " $" +
			Util.msgString(board) + this.msgSuffix();
	}
}
