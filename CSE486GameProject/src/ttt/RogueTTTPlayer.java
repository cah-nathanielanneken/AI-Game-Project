/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package ttt;
import game.*;

public class RogueTTTPlayer extends RandomTTTPlayer {
	public void init()
	{
		try {
			Thread.sleep(30*1000);
		}
		catch (Exception e) {
		}
	}
	public RogueTTTPlayer(String nname)
	{
		super(nname);
		gameState = new TTTState();
	}
	public GameMove getMove(GameState game, String lastMove)
	{
		try {
			//Thread.sleep(4970*1000);
		}
		catch (Exception e) {
		}
		return super.getMove(game, lastMove);
	}
	public static void main(String [] args)
	{
		GamePlayer p = new RogueTTTPlayer("Rogue player");
		p.compete(args);
	}
}
