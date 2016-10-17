/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package hex;
import game.*;

public class RoteHexPlayer extends GamePlayer {
	public RoteHexPlayer(String n) 
	{
		super(n, "Hex");
	}
	public GameMove getMove(GameState state, String lastMove)
	{
		HexState board = (HexState)state;
		HexMove mv = new HexMove();
		for (int c=0; c<HexState.N; c++) {
			for (int r=0; r<HexState.N; r++) {
				mv.row = r;
				mv.col = c;
				if (board.moveOK(mv)) {
					return mv;
				}
			}
		}
		return null;
	}
	public static void test()
	{
		//GamePlayer p = new RoteHijaraPlayer("Rote+");
		HexState h = new HexState();
		//HijaraMove mv1 = new HijaraMove(0,0);
		HexMove mv2 = new HexMove('A',1);
		h.makeMove(new HexMove(0,0)); h.makeMove(mv2);
		h.makeMove(new HexMove(0,1)); h.makeMove(mv2);
		h.makeMove(new HexMove(3,2)); h.makeMove(mv2);
		h.makeMove(new HexMove(3,3));
		h.makeMove(mv2);
	}

	public static void main(String [] args)
	{
		GamePlayer p = new RoteHexPlayer("Rote+");
		p.compete(args, 1);
	}
}
