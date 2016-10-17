/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package clobber;

import game.*;


public class ClobberState extends GameState {
	public static final Params gameParams = new Params(CONFIG_DIR + "Clobber.txt");
	public static final int ROWS = gameParams.integer("ROWS");
	public static final int COLS = gameParams.integer("COLS");
	public static final char homeSym = gameParams.character("HOMESYM");
	public static final char awaySym = gameParams.character("AWAYSYM");
	public static final char emptySym = gameParams.character("EMPTYSYM");

	public char [][] board;

	public ClobberState()
	{
		super();
		board = new char [ROWS][COLS];
		reset();
	}
	public Object clone()
	{
		ClobberState res = new ClobberState();
		res.copyInfo(this);
		Util.copy(res.board, board);
		return res;
	}
	public void reset()
	{
		clear();
		char [] syms = { homeSym, awaySym };
		int which = 0;
		for (int r=0; r<ROWS; r++) {
			for (int c=0; c<COLS; c++) {
				board[r][c] = syms[which];
				which = (which + 1) % 2;
			}
		}
	}
	public boolean moveOK(GameMove m)
	{
		ClobberMove mv = (ClobberMove)m;
		boolean OK = false;
		char PLAYER = who == GameState.Who.HOME ? homeSym : awaySym;
		char OPP = who == GameState.Who.HOME ? awaySym : homeSym;
		int rowDiff = mv.row1 - mv.row2;
		int colDiff = mv.col1 - mv.col2;
		if (status == Status.GAME_ON && mv != null &&
			Util.inrange(mv.row1, 0, ROWS-1) && Util.inrange(mv.row2, 0, ROWS-1) &&
			Util.inrange(mv.col1, 0, COLS-1) && Util.inrange(mv.col2, 0, COLS-1) &&
			((Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 0) || 
			 (Math.abs(rowDiff) == 0 && Math.abs(colDiff) == 1)) && 
			board[mv.row1][mv.col1] == PLAYER &&
			board[mv.row2][mv.col2] == OPP) {
					OK = true;
		}
		return OK;
	}
	private boolean noMoves(char who, char opp)
	{
		for (int r=0; r<ROWS; r++) {
			for (int c=0; c<COLS; c++) {
				char ch = board[r][c];
				if (ch == opp) {
					for (int j=-1; j<=+1; j+=2) {
						if ((ClobberMove.posOK(r+j, c) && board[r+j][c] == who) ||
							(ClobberMove.posOK(r, c+j) && board[r][c+j] == who)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	public boolean makeMove(GameMove m)
	{
		ClobberMove mv = (ClobberMove)m;
		boolean OK = false;
		char PLAYER = who == GameState.Who.HOME ? homeSym : awaySym;
		char OPP = who == GameState.Who.HOME ? awaySym : homeSym;
		GameState.Status possibleStatus = 
				who == GameState.Who.HOME ? GameState.Status.HOME_WIN: GameState.Status.AWAY_WIN;
		if (moveOK(m)) {
			board[mv.row1][mv.col1] = emptySym;
			board[mv.row2][mv.col2] = PLAYER;
			OK = true;
			super.newMove();
			status = (noMoves(PLAYER, OPP)) ?
					possibleStatus :  GameState.Status.GAME_ON;
		}
		return OK;
	}
	public void parseMsgString(String s)
	{
		reset();
		Util.parseMsgString(s, board, emptySym);
		parseMsgSuffix(s.substring(s.indexOf('[')));
	}
	public String toString()
	{ return Util.toString(board) + msgSuffix(); }
	public String msgString()
	{ return Util.msgString(board) + this.msgSuffix(); }
}
