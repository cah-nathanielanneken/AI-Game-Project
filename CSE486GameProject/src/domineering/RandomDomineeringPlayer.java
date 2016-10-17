package domineering;
import game.*;

import java.util.*;


public class RandomDomineeringPlayer extends GamePlayer {
	public RandomDomineeringPlayer(String n) 
	{
		super(n, "Domineering");
	}
	public GameMove getMove(GameState state, String lastMove)
	{
		DomineeringState board = (DomineeringState)state;
		ArrayList<DomineeringMove> list = new ArrayList<DomineeringMove>();  
		DomineeringMove mv = new DomineeringMove();
		
		int dr, dc;
		if (state.who == GameState.Who.HOME) {
			dr = 0;
			dc = 1;
		} else {
			dr = 1;
			dc = 0;
		}
		for (int r=0; r<DomineeringState.ROWS; r++) {
			for (int c=0; c<DomineeringState.COLS; c++) {
				mv.row1 = r;
				mv.col1 = c;
				mv.row2 = r+dr; mv.col2 = c+dc;
				if (board.moveOK(mv)) {
					list.add((DomineeringMove)mv.clone());
				}
			}
		}
		int which = Util.randInt(0, list.size()-1);
		return list.get(which);
	}
	public static void main(String [] args)
	{
		GamePlayer p = new RandomDomineeringPlayer("Random+");
		p.compete(args, 1);
	}
}
