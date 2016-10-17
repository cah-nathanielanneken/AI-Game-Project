/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package domineering;
import game.*;

public class DomineeringState extends GameState {
	public static final Params gameParams = new Params(CONFIG_DIR + "domineering.txt");
	public static final int ROWS = gameParams.integer("ROWS");
	public static final int COLS = gameParams.integer("COLS");
	public static final char homeSym = gameParams.character("HOMESYM");
	public static final char awaySym = gameParams.character("AWAYSYM");
	public static final char emptySym = gameParams.character("EMPTYSYM");

	public char [][] board;

	public DomineeringState()
	{
		super();
		board = new char [ROWS][COLS];
		reset();
	}
	public Object clone()
	{
		DomineeringState res = new DomineeringState();
		res.copyInfo(this);
		Util.copy(res.board, board);
		return res;
	}
	public void reset()
	{
		clear();
		for (int r=0; r<ROWS; r++) {
			for (int c=0; c<COLS; c++) {
				board[r][c] = emptySym;
			}
		}
	}
	public boolean moveOK(GameMove m)
	{
		DomineeringMove mv = (DomineeringMove)m;
		int rowDiff = mv.row1 - mv.row2;
		int colDiff = mv.col1 - mv.col2;
		if (status == Status.GAME_ON && mv != null &&
			Util.inrange(mv.row1, 0, ROWS-1) && Util.inrange(mv.row2, 0, ROWS-1) &&
			Util.inrange(mv.col1, 0, COLS-1) && Util.inrange(mv.col2, 0, COLS-1) &&
			board[mv.row1][mv.col1] == emptySym &&
			board[mv.row2][mv.col2] == emptySym &&
			((who == GameState.Who.HOME && rowDiff == 0 && Math.abs(colDiff) == 1) ||
			(who == GameState.Who.AWAY && Math.abs(rowDiff) == 1 && colDiff == 0))) {
				return true;
		}
		return false;
	}
	private boolean noMoves(GameState.Who who)
	{
		for (int r=0; r<ROWS; r++) {
			for (int c=0; c<COLS; c++) {
				char ch = board[r][c];
				if (who == GameState.Who.HOME && ch == emptySym 
					&& Util.inrange(c+1, 0, COLS-1) && board[r][c+1] == emptySym) {
					return false;
				} else if (who == GameState.Who.AWAY && ch == emptySym 
					&& Util.inrange(r+1, 0, ROWS-1) && board[r+1][c] == emptySym) {
					return false;
				}
			}
		}
		return true;
	}
	public boolean makeMove(GameMove m)
	{
		DomineeringMove mv = (DomineeringMove)m;
		char PLAYER = who == GameState.Who.HOME ? homeSym : awaySym;
		GameState.Status possibleStatus = who == GameState.Who.HOME 
				? GameState.Status.HOME_WIN: GameState.Status.AWAY_WIN;
		if (moveOK(m)) {
			board[mv.row1][mv.col1] = PLAYER;
			board[mv.row2][mv.col2] = PLAYER;
			super.newMove();
			status = noMoves(who) ? possibleStatus : GameState.Status.GAME_ON;
			return true;
		}
		return false;
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
