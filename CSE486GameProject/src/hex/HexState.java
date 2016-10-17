/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package hex;

import game.*;

public class HexState extends GameState {
	public static final Params gameParams = new Params(CONFIG_DIR + "Hex.txt");
	public static final int N = gameParams.integer("N");
	public static final char homeSym = gameParams.character("HOMESYM");
	public static final char awaySym = gameParams.character("AWAYSYM");
	public static final char emptySym = gameParams.character("EMPTYSYM");

	public char [][] board;
	public boolean [][][][][] connected;
	private final int [] neighsDeltaR = {-1,-1, 0, 0,+1,+1};
	private final int [] neighsDeltaC = { 0,+1,-1,+1,-1, 0};
	
	public HexState()
	{
		super();
		board = new char [N][N];
		connected = new boolean [2][N][N][N][N];
		reset();
	}
	public Object clone()
	{
		HexState res = new HexState();
		res.copyInfo(this);
		for (int r=0; r<N; r++) {
			for (int c=0; c<N; c++) {
				res.board[r][c] = board[r][c];
				for (int r2=0; r2<N; r2++) {
					for (int c2=0; c2<N; c2++) {
						res.connected[0][r][c][r2][c2] = connected[0][r][c][r2][c2];
						res.connected[1][r][c][r2][c2] = connected[1][r][c][r2][c2];
					}
				}
			}
		}
		return res;
	}
	public void reset()
	{
		clear();
		for (int r=0; r<N; r++) {
			for (int c=0; c<N; c++) {
				board[r][c] = emptySym;
				for (int r2=0; r2<N; r2++) {
					for (int c2=0; c2<N; c2++) {
						connected[0][r][c][r2][c2] = false;
						connected[1][r][c][r2][c2] = false;
					}
				}
			}
		}
	}
	public boolean moveOK(GameMove m)
	{
		HexMove mv = (HexMove)m;
		return status == Status.GAME_ON && mv != null &&
			Util.inrange(mv.row, 0, N-1) && Util.inrange(mv.col, 0, N-1) &&
			board[mv.row][mv.col] == emptySym &&
			!(numMoves == 0 && mv.row == N/2 && mv.col == N/2);
	}
	private GameState.Status gameOver()
	{
		for (int c1=0; c1<N; c1++) {
			for (int c2=0; c2<N; c2++) {
				if (connected[0][0][c1][N-1][c2]) {
//					System.out.printf("####%d %d%n", c1, c2);
//					System.out.printf("%s%n", toString());
					return GameState.Status.HOME_WIN; 
				}
			}
		}
		for (int r1=0; r1<N; r1++) {
			for (int r2=0; r2<N; r2++) {
				if (connected[1][r1][0][r2][N-1]) {
//					System.out.printf("!!!!!%d %d%n", r1, r2);
//					System.out.printf("%s%n", toString());
					return GameState.Status.AWAY_WIN; 
				}
			}
		}
		return GameState.Status.GAME_ON;
	}
	private void update(int which, int r1, int c1, int r2, int c2)
	{
		connected[which][r1][c1][r2][c2] = true; 
		connected[which][r2][c2][r1][c1] = true;
		for (int r=0; r<HexState.N; r++) {
			for (int c=0; c<HexState.N; c++) {
				if (connected[which][r][c][r1][c1] && !connected[which][r][c][r2][c2]) {
					update(which, r, c, r2, c2);
				}
				if (connected[which][r][c][r2][c2] && !connected[which][r][c][r1][c1]) {
					update(which, r, c, r1, c1);
				}
			}
		}
	}
	public void printConnectivity()
	{
		for (int r=0; r<HexState.N; r++) {
			for (int c=0; c<HexState.N; c++) {
				for (int r2=0; r2<HexState.N; r2++) {
					for (int c2=0; c2<HexState.N; c2++) {
						System.out.print(connected[0][r][c][r2][c2]?1:0);
					}
				}
				System.out.println();
			}
		}
		System.out.println();
	}
	private void updateConnectivity(HexMove mv, GameState.Who who)
	{
		int which = (who == GameState.Who.HOME ? 0 : 1);
		char sym = (who == GameState.Who.HOME ? HexState.homeSym : HexState.awaySym);
		int R = mv.row;
		int C = mv.col;
		for (int i=0; i<6; i++) {
			int nr = R + neighsDeltaR[i];
			int nc = C + neighsDeltaC[i];
			if (HexMove.posOK(nr, nc) && !connected[which][R][C][nr][nc] &&
					board[nr][nc] == sym) {
				update(which, R, C, nr, nc);
			}
		}
	}
	public boolean makeMove(GameMove m)
	{
		HexMove mv = (HexMove)m;
		char sym = who == GameState.Who.HOME ? homeSym : awaySym;
		boolean OK = false;
		if (moveOK(m)) {
			OK = true;
			board[mv.row][mv.col] = sym;
			updateConnectivity(mv, who);
			status = gameOver();
			super.newMove();
		}
		return OK;
	}
	public void parseMsgString(String s)
	{
		reset();
		Util.parseMsgString(s, board, emptySym);
		parseConn(s, s.indexOf('(')+1);
		parseMsgSuffix(s.substring(s.indexOf('[')));
	}
	public boolean equals(Object other)
	{
		if (this == other) {
			return true;
		} else if (other == null) {
			return false;
		} else if (!(other instanceof HexState)) { 
			return false;
		} else {
			HexState hs = (HexState)other;
	 		for (int r=0; r<N; r++) {
				for (int c=0; c<N; c++) {
					if (board[r][c] != hs.board[r][c]) {
						return false;
					}
				}
			}
			return true;
		}
	}
	public void parseConn(String s, int start)
	{
		int N = HexState.N;
		int pos = start;
		for (int i=0; i<2; i++) {
			for (int a=0; a<N; a++) {
				for (int b=0; b<N; b++) {
					for (int c=0; c<N; c++) {
						for (int d=0; d<N; d++) {
							char ch = s.charAt(pos++);
							if (ch == '1')
								connected[i][a][b][c][d] = true;
							else if (ch == '0')
								connected[i][a][b][c][d] = false;
							else
								System.err.println("PROBLEM");
						}
					}
				}
			}
		}
	}
	public String connString()
	{
		int N = HexState.N;
		int sz = 2 * N * N * N * N + 2;
		StringBuffer buf = new StringBuffer(sz);
		buf.append('(');
		for (int i=0; i<2; i++) {
			for (int a=0; a<N; a++) {
				for (int b=0; b<N; b++) {
					for (int c=0; c<N; c++) {
						for (int d=0; d<N; d++) {
							buf.append(connected[i][a][b][c][d]?'1':'0');
						}
					}
				}
			}
		}
		buf.append(')');
		return buf.toString();
	}
	public String toString()
	{ return "" + Util.toString(board) + msgSuffix();
	}
	public String msgString()
	{ return "" + Util.msgString(board) + connString() + this.msgSuffix(); }
}
