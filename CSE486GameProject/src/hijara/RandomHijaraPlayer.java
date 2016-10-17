/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package hijara;
import game.*;
import java.util.*;

public class RandomHijaraPlayer extends GamePlayer {
	public RandomHijaraPlayer(String n) 
	{
		super(n, "Hijara");
	}
	public GameMove getMove(GameState state, String lastMove)
	{
		HijaraState board = (HijaraState)state;
		ArrayList<HijaraMove> list = new ArrayList<HijaraMove>();  
		HijaraMove mv = new HijaraMove();
		for (int r=0; r<HijaraState.ROWS; r++) {
			for (int c=0; c<HijaraState.COLS; c++) {
				mv.row = r;
				mv.col = c;
				if (board.moveOK(mv)) {
					list.add((HijaraMove)mv.clone());
				}
			}
		}
		int which = Util.randInt(0, list.size()-1);
		return list.get(which);
	}
	public static void main(String [] args)
	{
		GamePlayer p = new RandomHijaraPlayer("Random+");
		p.compete(args, 1);
	}
}
