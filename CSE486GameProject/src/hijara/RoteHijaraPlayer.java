/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package hijara;
import game.*;

public class RoteHijaraPlayer extends GamePlayer {
	public RoteHijaraPlayer(String n) 
	{
		super(n, "Hijara");
	}
	public GameMove getMove(GameState state, String lastMove)
	{
		HijaraState board = (HijaraState)state;
		HijaraMove mv = new HijaraMove();
		for (int c=0; c<HijaraState.COLS; c++) {
			for (int r=0; r<HijaraState.ROWS; r++) {
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
		HijaraState h = new HijaraState();
		//HijaraMove mv1 = new HijaraMove(0,0);
		HijaraMove mv2 = new HijaraMove(1,1);
		h.makeMove(new HijaraMove(0,0)); h.makeMove(mv2);
		h.makeMove(new HijaraMove(0,1)); h.makeMove(mv2);
		h.makeMove(new HijaraMove(0,2)); h.makeMove(mv2);
		h.makeMove(new HijaraMove(0,3));
		h.makeMove(mv2);
	}

	public static void main(String [] args)
	{
		GamePlayer p = new RoteHijaraPlayer("Rote+");
		p.compete(args, 1);
	}
}
